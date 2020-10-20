package com.jacamars.dsp.crosstalk.api;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hazelcast.core.HazelcastInstance;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.shared.AccountingCache;
import com.jacamars.dsp.rtb.shared.CampaignCache;

/**
 * Web API to list all campaigns known by crosstalk
 * @author Ben M. Faul
 *
 */
public class GetAccountingCmd extends ApiCommand {

	/** The list of campaigns */
	public Map<String,Double> accounting;

	/**
	 * Default constructor
	 */
	public GetAccountingCmd() {

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
				
				// TBD rewrite for multi tenant
				
				var data = AccountingCache.getInstance().asMap();
				accounting = new HashMap<>();
				data.forEach((k,v)->{
					set(k + ".bids", accounting,v);
					set(k + ".wins", accounting,v);
					set(k + ".pixels", accounting,v);
					set(k + ".clicks", accounting,v);
					set(k + ".total", accounting,v);			
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
	
	void set(String key, Map<String,Double> m, Map<String,Double> v) {
		if (v.get(key) != null) 
			accounting.put(key,v.get(key));
		else
			accounting.put(key,0.0);
	}
}
