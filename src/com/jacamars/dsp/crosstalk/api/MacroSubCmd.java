package com.jacamars.dsp.crosstalk.api;


import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.shared.CampaignCache;
import com.jacamars.dsp.rtb.tools.MacroProcessing;

import java.util.ArrayList;
import java.util.List;

import com.hazelcast.core.HazelcastInstance;

/**
 * Get a campaign in JSON form of the base SQL object.
 * @author Ben M. Faul
 *
 */
public class MacroSubCmd extends ApiCommand {

	/** The JSON node that represents the SQL of this campaign */
	public String data;
	
	/**
	 * Default constructor
	 */
	public MacroSubCmd() {

	}

	/**
	 * Basic form of the command..
	 * 
	 * @param username
	 *            String. User authorization for command.
	 * @param password
	 *            String. Password authorization for command.
	 */
	public MacroSubCmd(String username, String password) {
		super(username, password);
		type = MACROSUB;
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
	public MacroSubCmd(String username, String password, String target) {
		super(username, password);
		campaign = target;
		type = MACROSUB;
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
				List<String> macros = new ArrayList<>();
				MacroProcessing.findMacros(macros,data);
				StringBuilder sb = new StringBuilder(data);
				MacroProcessing.replace(macros,sb);
				data = new String(sb);
				return;
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
		}
}
