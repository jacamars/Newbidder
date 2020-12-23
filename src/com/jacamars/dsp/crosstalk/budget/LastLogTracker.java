package com.jacamars.dsp.crosstalk.budget;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A class for tracking the bidlogs time lag.
 * @author Ben M. Faul
 *
 */
public class LastLogTracker {

	/** The last log query */
	static final String LASTLOG = "query/lastlog.json";

	/** The calendar used by the class */
	Calendar cal = Calendar.getInstance();
	/** The simple date formatter */
	SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
	/** Mapper for printing JSON stuff */
	public static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/** The resultant map from the query */
	Map map;
	/** ELK rest client */
	RestClient restClient;
	/** The contents of the data file containing the templated query */
	final String contents;
	/** The time of the call */
	long time;
	/** The lag in milliseconds of the last log */
	long deltaTime;

	/**
	 * Find the last log entry in ELK.
	 * @param host String. The hostname of the ELK.
	 * @param port int. The port of the ELK client.
	 * @throws Exception on network errors.
	 */
	public LastLogTracker(String host, int port) throws Exception {
		System.out.println("*** ELASTICCONFIG: h="+host+",p="+port);
		restClient = RestClient.builder(new HttpHost(host, port, "http")).build();
		contents = new String(Files.readAllBytes(Paths.get(LASTLOG)), StandardCharsets.UTF_8); 
	}
	
	public LastLogTracker(String host, int port, String username, String password, String certPath) throws Exception {
		System.out.println("*** ELASTICCONFIG: h="+host+",p="+port+"u="+username+",pass="+password+",ca="+certPath);
		contents = new String(Files.readAllBytes(Paths.get(LASTLOG)), StandardCharsets.UTF_8); 
		
		Path caCertificatePath = Paths.get(certPath);
		CertificateFactory factory =
		    CertificateFactory.getInstance("X.509");
		Certificate trustedCa;
		try (InputStream is = Files.newInputStream(caCertificatePath)) {
		    trustedCa = factory.generateCertificate(is);
		}
		KeyStore trustStore = KeyStore.getInstance("pkcs12");
		trustStore.load(null, null);
		trustStore.setCertificateEntry("ca", trustedCa);
		org.apache.http.conn.ssl.SSLContextBuilder sslContextBuilder = SSLContexts.custom()
		    .loadTrustMaterial(trustStore, null);
		final SSLContext sslContext = sslContextBuilder.build();
		RestClientBuilder builder = RestClient.builder(
		    new HttpHost(host, port, "https"))
		    .setHttpClientConfigCallback(new HttpClientConfigCallback() {
		        @Override
		        public HttpAsyncClientBuilder customizeHttpClient(
		            HttpAsyncClientBuilder httpClientBuilder) {
		            return httpClientBuilder.setSSLContext(sslContext);
		        }
		    });
		
		restClient = builder.build();
	}


	/**
	 * Query the Last log. Watch out, rolling over a day might mean no index for the day is available.
	 * @return long. The lag in milliseconds in the log.
	 * @throws Exception on indexing errors.
	 */
	public long query() throws Exception {
		cal.setTimeInMillis(System.currentTimeMillis());
		String index  = "/wins-" + format1.format(cal.getTime());
		String data = contents;
		HttpEntity entity = new NStringEntity(data, ContentType.APPLICATION_JSON);
		Response indexResponse = null;
		try { 
		    Request request = new Request("GET",  index + "/_search");
		    request.setEntity(new NStringEntity(
                    "{\"json\":\"text\"}",
                    ContentType.APPLICATION_JSON));
		    
			indexResponse = restClient.performRequest(request);
		} catch (Exception error) {
			// We might have just crossed the day, so go back 1
			if (error.toString().contains("404")) {
				cal = Calendar.getInstance();
			    cal.add(Calendar.DATE, -1);
				index  = "/wins-" + format1.format(cal.getTime());
				entity = new NStringEntity(data, ContentType.APPLICATION_JSON);
				
			    Request request = new Request("GET",  index + "/_search");
			    request.setEntity(new NStringEntity(
	                    "{\"json\":\"text\"}",
	                    ContentType.APPLICATION_JSON));
				indexResponse = restClient.performRequest(request);
				
			}
			else
				throw error;
		}
		// System.out.println(data);
		data = EntityUtils.toString(indexResponse.getEntity());
		map = mapper.readValue(data, Map.class);
		Map x = (Map) map.get("hits");
		List<Map> hits  = (List)x.get("hits");
		x = hits.get(0);
		List<Long> list = (List)x.get("sort");
		time = list.get(0);
		deltaTime = System.currentTimeMillis() - time;
		return time;
	}

	/**
	 * Returns the last delta time for the logging.
	 * @return long. The lag in milliseconds.
	 */
	public long deltaTime() {
		return deltaTime;
	}

	/**
	 * Close the log.
	 * @throws Exception on network errors.
	 */
	public void close() throws Exception {
		try {
			restClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * A simple tester.
	 * @param args String[]. Arguments.
	 * @throws Exception on Elastic Search errors.
	 */
	public static void main(String [] args) throws Exception  {
		String host = "54.164.51.156";
		if (args.length > 0)
			host = args[0];

		LastLogTracker log = new LastLogTracker(host,9200);
		log.query();
		System.out.println(log.deltaTime());
	}
}
