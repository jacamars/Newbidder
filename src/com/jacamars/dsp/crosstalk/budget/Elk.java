package com.jacamars.dsp.crosstalk.budget;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jacamars.dsp.rtb.tools.DbTools;

/**
 * Elastic Search configuration object
 * @author Ben M. Faul
 *
 */
public class Elk {

	/** The host of where to get daily and hourly spends */
	public String elastic_host;

	/** The port to use for web client access to ELK */
	public String elastic_port;
	
	public String elastic_ssl_enabled;
	
	public String elastic_user;
	
	public String elastic_password;
	
	public String elastic_ca_path;

	/** If specified, for debugging where to read the hourly campaign data from */
	public String simFile;

	/**
	 * Default constructor
	 */
	public Elk() {
		
	}
	
	public static Elk build(Map m) throws Exception { 
		String s = DbTools.mapper.writeValueAsString(m);
		Elk e = DbTools.mapper.readValue(s,Elk.class);
		return e;
	}
	
	@JsonIgnore
	public int getPort() {
		if (elastic_port == null){
			return 9200; // default port
		}

		if (elastic_port.startsWith("$")) {
			String test = System.getenv(elastic_port.substring(1));
			if (test == null)
				return 9200;
			
			return Integer.parseInt(System.getenv(elastic_port.substring(1)));
		}
		else return Integer.parseInt(elastic_port);
	}

	@JsonIgnore
	public String getHost() {
		if (elastic_host.startsWith("$")) {
			String name = elastic_host.substring(1);
			elastic_host = System.getenv(name);
		}
		return elastic_host;
	}

	
	public boolean getElasticSslEnabled() {
		if (elastic_ssl_enabled.startsWith("$")) {
			String name = elastic_ssl_enabled.substring(1);
			elastic_ssl_enabled = System.getenv(name);
		}
		
		if (elastic_ssl_enabled == null)
			return false;
		
		return Boolean.parseBoolean(elastic_ssl_enabled);
	}
	
	public String getElasticUser() {
		if (elastic_user.startsWith("$")) {
			String name = elastic_user.substring(1);
			elastic_user = System.getenv(name);
		}
		return elastic_user;
	}
	
	public String getElasticPassword() {
		if (elastic_password.startsWith("$")) {
			String name = elastic_password.substring(1);
			elastic_password = System.getenv(name);
		}
		return elastic_password;
	}
	
	public String getElasticCaPath() {
		if (elastic_ca_path.startsWith("$")) {
			String name = elastic_ca_path.substring(1);
			elastic_password = System.getenv(name);
		}
		return elastic_ca_path;
	}
	
}
