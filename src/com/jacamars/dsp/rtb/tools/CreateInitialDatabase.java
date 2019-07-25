package com.jacamars.dsp.rtb.tools;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateInitialDatabase {
	
	static String makeDb = "CREATE DATABASE RTB4FREE;";
	
	static Connection conn;
	static Statement stmt;

	public static void main(String [] args) throws Exception {
		boolean dbExists = false;
		try {
		conn = DriverManager.getConnection(
	                "jdbc:postgresql://localhost:5432/rtb4free", "postgres", "postgres");
		dbExists = true;
		} catch (Exception err) {
			if (err.getMessage().contains("does not exist")) {
				conn = DriverManager.getConnection(
		                "jdbc:postgresql://localhost:5432/", "postgres", "postgres");
				dbExists = false;
			} else
				throw err;
		}
		
	    if (conn != null) {
	    	System.out.println("Connected to the database!");
	    } else {
	        System.out.println("Failed to make connection!");
	   }
	    
	   stmt = conn.createStatement();
	    
	   if (!dbExists) {
		   stmt.execute(makeDb);
	   }
	   
	   make("data/postgres/targets.sql");
	   make("data/postgres/rtb_standards.sql");
	   make("data/postgres/banners_rtb_standards.sql");
	   make("data/postgres/banner_videos_rtb_standards.sql");
	   make("data/postgres/campaigns_rtb_standards.sql");
	   make("data/postgres/banners.sql");
	   make("data/postgres/banner_videos.sql");
	   make("data/postgres/campaigns.sql");
	   make("data/postgres/exchange_attributes.sql");
	   
	   make("data/postgres/sample.sql");
	   
	   System.out.println("Database created!");
	}
	
	public static void make(String str) throws Exception {
		var data = new String(Files.readAllBytes(Paths.get(str)));
		stmt.execute(data);
	}
}
