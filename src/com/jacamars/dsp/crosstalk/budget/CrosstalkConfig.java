package com.jacamars.dsp.crosstalk.budget;

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;
import java.util.Map;

public enum CrosstalkConfig {

	CONFIGURATION;
	
	static Statement statement;
	static Connection connect;
	static String region;
	static Elk elk;
	
	public static CrosstalkConfig getInstance(Map crosstalk) throws Exception {

		Map sql = (Map)crosstalk.get("sql");
		
		var driver = sql.get("jdbcdriver");
		if (driver == null)
			throw new Exception("Crosstalk subsystem requires a jdbcdriver");
		
		Class.forName((String)driver);
		DriverManager.setLoginTimeout(20);
		
		var jdbc = sql.get("login");
		if (jdbc == null)
			throw new Exception("Crosstalk subsystem requires jdbc login configuration");
		
		region = (String)crosstalk.get("region");
		if (region == null)
			throw new Exception("Crosstalk subsystem requires a region");
		
		connect = DriverManager.getConnection((String)jdbc);
		statement = connect.createStatement();
		
		if (crosstalk.get("elk")==null) 
			throw new Exception("ELK is not configured");
		
		elk = new Elk((Map<String,String>)crosstalk.get("elk"));
		return CONFIGURATION;
	}
	
	public static CrosstalkConfig getInstance() {
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
