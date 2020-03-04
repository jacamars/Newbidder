package com.jacamars.dsp.crosstalk.api;


import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.shared.CampaignCache;
import com.hazelcast.core.HazelcastInstance;

/**
 * Get a campaign in JSON form of the base SQL object.
 * @author Ben M. Faul
 *
 */
public class GetCampaignCmd extends ApiCommand {

	/** The JSON node that represents the SQL of this campaign */
	public Campaign node;
	
	/**
	 * Default constructor
	 */
	public GetCampaignCmd() {

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
				HazelcastInstance hz = RTBServer.getSharedInstance();
				var clist = CampaignCache.getClientInstance(hz).getCampaigns();
				for (Campaign c : clist) {
					if (c.name.equals(campaign)) {
						node = c;
						
						if (tokenData.isAuthorized(c.customer_id) == false) {
							error = true;
							message = "No such campaign";
							return;
						}
						
						return;
					}
				}
				error = true;
				message = "No such campaign";
				return;
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
		}
}
