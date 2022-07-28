package com.jacamars.dsp.rtb.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jacamars.dsp.rtb.bidder.RTBServer;

public class Log {

	protected static final Logger logger = LoggerFactory.getLogger(Log.class);
	
	public static void main(String[] args) {
	
		logger.info("Helo World");
		
		System.out.println("Done");
	}
}
