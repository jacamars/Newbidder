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
				
				// TBD Needs rewrite for multi tenant
				macros = Configuration.getInstance().getEnvironment();			
				return;
			} catch (Exception err) {
				err.printStackTrace();
				error = true;
				message = err.toString();
			}
		}
}
