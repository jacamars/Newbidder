package com.jacamars.dsp.crosstalk.api;

import java.util.Map;
import java.util.Random;

import com.jacamars.dsp.rtb.commands.ConfigureAwsObject;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.tools.DbTools;

/**
 * Web API access to configure an object from AWS S3, a bloom filter, etc.
 * @author Ben M. Faul
 *
 */
public class ConfigureAwsObjectCmd extends ApiCommand {

	/** The results of the command */
	public String updated;
	
	
	/**
	 * Default constructor
	 */

	public ConfigureAwsObjectCmd() {

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
				
				var errored = Configuration.configureObject(map);
			     if (errored != null) {
			    	 error = true;
			         message = "AWS Object load failed: " + errored;
			         return;
			     }
			     updated = "AWS Object " + command + " loaded ok";
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
	}
}
