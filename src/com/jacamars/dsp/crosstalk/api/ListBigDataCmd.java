package com.jacamars.dsp.crosstalk.api;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hazelcast.core.HazelcastInstance;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.blocks.LookingGlass;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.shared.CampaignCache;

/**
 * Web API to list all campaigns known by crosstalk
 * @author Ben M. Faul
 *
 */
public class ListBigDataCmd extends ApiCommand {

	/** The list of campaigns */
	public List<Map<String,String>> catalog;

	/**
	 * Default constructor
	 */
	public ListBigDataCmd() {

	}

	/**
	 * Deletes a campaign from the bidders.
	 * 
	 * @param username
	 *            String. User authorization for command.
	 * @param password
	 *            String. Password authorization for command.
	 */
	public ListBigDataCmd(String username, String password) {
		super(username, password);
		type = LIST_BIGDATA;
	}

	/**
	 * Convert to JSON
	 */
	public String toJson() throws Exception {
		return WebAccess.mapper.writeValueAsString(this);
	}

	/**
	 * Execute the command, msrshal the results.
	 */
	@Override
		public void execute() {
			super.execute();
			try {
				catalog = new ArrayList<>(); 
				LookingGlass.symbols.forEach((key,value)-> {
					String 	type = value.getClass().getName();
					Map<String,String> m = new HashMap<>();
					m.put("name", key);
					m.put("type",type);
					m.put("size", "" + ((LookingGlass)value).getMembers());
					catalog.add(m);
				});
				
				return;
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
			error = true;
		}
}