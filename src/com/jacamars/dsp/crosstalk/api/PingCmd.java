package com.jacamars.dsp.crosstalk.api;

/**
 * Web API to send a ping command to crosstalk.
 * 
 * @author Ben M. Faul
 *
 */
public class PingCmd extends ApiCommand {
	
	/**
	 * Default constructor.
	 */
	public PingCmd() {
		
	}
	
	/**
	 * Convert to JSON
	 */
	@Override
	public String toJson() throws Exception {
		return WebAccess.mapper.writeValueAsString(this);
	}
	
	/**
	 * Execute the command. Just updates its timestamp.
	 */
	@Override
	public void execute() {
		 super.execute();
		 refreshList = null;
	}
}
