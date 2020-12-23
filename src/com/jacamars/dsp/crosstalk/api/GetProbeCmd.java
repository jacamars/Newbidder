package com.jacamars.dsp.crosstalk.api;


import java.util.ArrayList;

import java.util.List;

import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.rtb.bidder.CampaignProcessor;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.probe.Probe;

/**
 * Web API to return the reason a campaign is not loaded.
 * @author Ben M. Faul
 *
 */
public class GetProbeCmd extends ApiCommand {


	/** If only 1 reason, its here */
	public Probe probe;
	
	
	/**
	 * Default constructor
	 */
	public GetProbeCmd() {

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
			probe = CampaignProcessor.probe;
	
		}
}
