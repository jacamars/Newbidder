package com.jacamars.dsp.crosstalk.api;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hazelcast.core.HazelcastInstance;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.shared.CampaignCache;
import com.jacamars.dsp.rtb.tools.MemoryAccounting;

/**
 * Web API to list all campaigns known by crosstalk
 * @author Ben M. Faul
 *
 */
public class GetAccountingCmd extends ApiCommand {

	/** The list of campaigns */
	public Map<String,Long> accounting;

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
				
				accounting = MemoryAccounting.getInstance().getValues();
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
