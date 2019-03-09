package com.jacamars.dsp.crosstalk.api;


import java.util.List;

import java.util.Random;

import com.jacamars.dsp.rtb.blocks.AwsCommander;
import com.jacamars.dsp.rtb.commands.ConfigureAwsObject;
import com.jacamars.dsp.rtb.commands.SetPrice;
import com.jacamars.dsp.rtb.common.Configuration;

/**
 * Web API access to configure an object from AWS S3, a bloom filter, etc.
 * @author Ben M. Faul
 *
 */
public class ConfigureAwsObjectCmd extends ApiCommand {

	/** The results of the command */
	public String updated;
	
	/** The command to execute */
	public String command;
	
	/**
	 * Default constructor
	 */

	public ConfigureAwsObjectCmd() {

	}

	/**
	 * Configures an S3 object.
	 * 
	 * @param username
	 *            String. User authorization for command.
	 * @param password
	 *            String. Password authorization for command.
	 */
	public ConfigureAwsObjectCmd(String username, String password) {
		super(username, password);
		type = ConfigureAws;
	}

	/**
	 * Targeted form of command. starts a specific bidder.
	 * 
	 * @param username
	 *            String. User authorizatiom.
	 * @param password
	 *            String. Password authorization.
	 * @param target
	 *            String. The bidder to start.
	 */
	public ConfigureAwsObjectCmd(String username, String password, String target) {
		super(username, password);
		command = target;
		type = ConfigureAws;
	}

	/**
	 * Convert to JSON
	 */
	public String toJson() throws Exception {
		return WebAccess.mapper.writeValueAsString(this);
	}

	/**
	 * Execute the command, msrshall the results.
	 */
	@Override
	public void execute() {
			super.execute();	
			try {
				logger.debug("EXECUTING THE CONFIGURATION COMMAND: " + command);
				ConfigureAwsObject sp = new ConfigureAwsObject("","",command);
				sp.from = WebAccess.uuid + "-" + new Random().nextLong();
				
				 AwsCommander aws = new AwsCommander(command);
			     if (aws.errored()) {
			    	 error = true;
			         message = "AWS Object load failed: " + aws.getMessage();
			         return;
			     }
			     updated = "AWS Object " + command + " loaded ok";
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
	}
}
