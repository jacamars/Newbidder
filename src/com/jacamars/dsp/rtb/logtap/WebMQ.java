package com.jacamars.dsp.rtb.logtap;

import java.io.BufferedReader;

import java.io.ByteArrayOutputStream;



import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.session.SessionHandler;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.jmq.Pull;
import com.jacamars.dsp.rtb.jmq.Push;


/**
 * Creates the HTTP handler for the Minimal server. This is an adaptation of the
 * Jacamar's Inc. prototype web server. This source code is licensed under
 * Apache Commons 2.
 * 
 * @author Ben M. Faul.
 *
 */

public class WebMQ implements Runnable {
	/** The thread the server runs on */
	Thread me;
	/** The default HTTP port */
	static protected int port = 7379;
	static protected String password;
	static final ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");
	
	/**
	 * Creates the default Server
	 * 
	 * @param args
	 *            . String[]. Args[0] contains the name of the users file, if
	 *            not, presume "users.json"
	 * @throws Exception
	 *             on network or JSON parsing errors.
	 */
	public static void main(String[] args) throws Exception {
		new WebMQ(7379,null);
	}

	/**
	 * Creates the WebMQ instance and starts it. The Jetty side receives
	 * ajax calls for Mosaic actions.
	 * 
	 * @param configFile
	 *            String. The name of the users JSON file.
	 * @throws Exception
	 *             or network errors.
	 */
	public WebMQ(int port, String password) throws Exception {
		this.port = port;
		this.password = password;
				
		me = new Thread(this);
		me.start();
		
		Thread.sleep(500);
	}
	
	/**
	 * Starts the JETTY server
	 */
	 public void run() {
		Server server = new Server(port);
		Handler handler = new Handler();
		SessionHandler sh = new SessionHandler(); // org.eclipse.jetty.server.session.SessionHandler
		sh.setHandler(handler);

		server.setHandler(sh); // set session handle
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
}

@MultipartConfig
class Handler extends AbstractHandler {
	/**
	 * The property for temp files.
	 */
	
	static final ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	SessionHandler sh = new SessionHandler(); // org.eclipse.jetty.server.session.SessionHandler

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Headers","Content-Type");

		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);

		HttpSession session = request.getSession();
		String userAgent = request.getHeader("User-Agent");
		
		
		int code = 200;
		String html = "";

		InputStream body = request.getInputStream();
		baseRequest.setHandled(true);

		if (target.equals("/"))
			target = "/index.html";

		// ///////////////////////////////////////////////////////

		session.setAttribute("path", target);
		String type = request.getContentType();
		String ipAddress = getIpAddress(request);

		try {
			
			if (target.startsWith("/publish")) {
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json;charset=utf-8");
				baseRequest.setHandled(true);
				String result = null;
				String port = request.getParameter("port");
				String topic = request.getParameter("topic");
				String message = request.getParameter("message");
				if (port == null) {
					response.getWriter().println("{\"error\":\"no port specified\"}");
					return;
				}
				if (topic == null) {
					response.getWriter().println("{\"error\":\"no topic specified\"}");
					return;
				}
				if (message == null) {
					message = getStringFromInputStream(body);
				}
				
				
				try {
					new WebMQPublisher(port,topic,message);
					response.getWriter().println("{\"status\":\"ok\"}");
				} catch (Exception error) {
					response.getWriter().println("{\"error\":\"" + error.toString() + "\"}");
				}
				
				return;
			}
			
			/**
			 * Warning, never returns
			 *  /subscribe?topic=xxx
			 */
			if (target.startsWith("/subscribe")) {
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json;charset=utf-8");
				baseRequest.setHandled(true);
				
				String topics = request.getParameter("topic");
	
				if (topics == null) {
					response.getWriter().println("{\"error\":\"no topic specified\"}");
					return;
				}
				
				
				var wmq = new WebMQSubscriber(response,topics);       // does not return
				System.out.println(wmq + " has initialized for: " + topics);
				wmq.run();
				System.out.println("Client disconnected: " + wmq);
				
			}
			
			if (target.startsWith("/shortsub")) {
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json;charset=utf-8");
				baseRequest.setHandled(true);
				
				String topics = request.getParameter("topic");
	
				if (topics == null) {
					response.getWriter().println("{\"error\":\"no topic specified\"}");
					return;
				}
				
				
				var ss = new ShortSubscriber(response,topics);       // does not return
				System.out.println(ss + " has initialized for: " + topics);
				ss.run();
				System.out.println("Client disconnected: " + ss);
				
			}

			if (target.contains("favicon")) {
				response.setStatus(HttpServletResponse.SC_OK);
				baseRequest.setHandled(true);
				response.getWriter().println("");
				return;
			}
			
			type = null;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println(e.toString());
		}

		baseRequest.setHandled(true);
		response.getWriter().println(html);
		response.setStatus(code);
	}

	/**
	 * Return the IP address of this
	 * 
	 * @param request
	 *            HttpServletRequest. The web browser's request object.
	 * @return String the ip:remote-port of this browswer's connection.
	 */
	public String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		ip += ":" + request.getRemotePort();
		return ip;
	}
	
	  public static byte[] compressGZip(String uncompressed) throws Exception {
	            ByteArrayOutputStream baos  = new ByteArrayOutputStream();
	            GZIPOutputStream gzos       = new GZIPOutputStream(baos);
	     
	            byte [] uncompressedBytes   = uncompressed.getBytes();
	     
	            gzos.write(uncompressedBytes, 0, uncompressedBytes.length);
	            gzos.close();
	     
	            return baos.toByteArray();
	    }

	  public static String getStringFromInputStream(InputStream is) {

			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();

			String line;
			try {

				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return sb.toString();

		}
}