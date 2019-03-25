package com.jacamars.dsp.crosstalk.budget;

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;
import java.util.Map;

public enum Configuration {

	CONFIGURATION;
	
	static Statement statement;
	static Connection connect;
	static String region;
	static Elk elk;
	
	public static Configuration getInstance(Map crosstalk) throws Exception {
		/**
		 * Now connect to SQL
		 */
		// load the MySQL driver
		Class.forName("com.mysql.jdbc.Driver");
		DriverManager.setLoginTimeout(20);
		
		String driver = (String)crosstalk.get("jdbcdriver");
		if (driver == null)
			throw new Exception("Crosstalk subsystem requires a jdbcdriver");
		
		String jdbc = (String)crosstalk.get("login");
		if (jdbc == null)
			throw new Exception("Crosstalk subsystem requires jdbc login configuration");
		
		region = (String)crosstalk.get("region");
		if (region == null)
			throw new Exception("Crosstalk subsystem requires a region");
		
		connect = DriverManager.getConnection(jdbc);
		statement = connect.createStatement();
		
		if (crosstalk.get("elk")==null) 
			throw new Exception("ELK is not configured");
		
		elk = new Elk((Map<String,String>)crosstalk.get("elk"));
		return CONFIGURATION;
	}
	
	public static Configuration getInstance() {
		return CONFIGURATION;
	}
	
	public static Connection getConnection() {
		return connect;
	}
	
	public static String getRegion() {
		return region;
	}
	
	public Statement getStatement() {
		return statement;
	}
}
