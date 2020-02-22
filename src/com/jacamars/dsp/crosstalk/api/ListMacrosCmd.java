package com.jacamars.dsp.crosstalk.api;


import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.shared.CampaignCache;
import com.jacamars.dsp.rtb.tools.MacroProcessing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hazelcast.core.HazelcastInstance;

/**
 * Get a campaign in JSON form of the base SQL object.
 * @author Ben M. Faul
 *
 */
public class ListMacrosCmd extends ApiCommand {

	/** The JSON node that represents the SQL of this campaign */
	public Map<String,String> macros;
	
	/**
	 * Default constructor
	 */
	public ListMacrosCmd() {

	}

	/**
	 * Basic form of the command..
	 * 
	 * @param username
	 *            String. User authorization for command.
	 * @param password
	 *            String. Password authorization for command.
	 */
	public ListMacrosCmd(String username, String password) {
		super(username, password);
		type = LIST_MACROS;
	}

	/**
	 * Targeted form of command. starts a specific bidder.
	 * 
	 * @param username
	 *            String. User authorization.
	 * @param password
	 *            String. Password authorization.
	 * @param target
	 *            String. The bidder to start.
	 */
	public ListMacrosCmd(String username, String password, String target) {
		super(username, password);
		campaign = target;
		type = LIST_MACROS;
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
				macros = Configuration.getInstance().getEnvironment();			
				return;
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
		}
}
