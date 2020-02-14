package com.jacamars.dsp.crosstalk.api;


import java.util.ArrayList;
import java.util.List;

import com.hazelcast.core.HazelcastInstance;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.shared.CampaignCache;

/**
 * Web API to list all campaigns known by crosstalk
 * @author Ben M. Faul
 *
 */
public class ListCampaignsCmd extends ApiCommand {

	/** The list of campaigns */
	public List<String> campaigns;

	/**
	 * Default constructor
	 */
	public ListCampaignsCmd() {

	}

	/**
	 * Deletes a campaign from the bidders.
	 * 
	 * @param username
	 *            String. User authorization for command.
	 * @param password
	 *            String. Password authorization for command.
	 */
	public ListCampaignsCmd(String username, String password) {
		super(username, password);
		type = ListCampaigns;
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
				campaigns = new ArrayList<String>(); 
				var list = Configuration.getInstance().getCampaignsList();
				list.stream().forEach(e->{
					campaigns.add(e.name);
				});
				return;
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
			error = true;
			if (message == null)
				message = "Timed out";
		}
}
