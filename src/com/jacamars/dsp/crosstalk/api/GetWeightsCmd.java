package com.jacamars.dsp.crosstalk.api;

import com.jacamars.dsp.rtb.blocks.ProportionalEntry;
import com.jacamars.dsp.rtb.commands.GetWeights;
import com.jacamars.dsp.rtb.common.Configuration;


/**
 * Get the creative weights on this campaign.
 * @author Ben M. Faul
 *
 */
public class GetWeightsCmd extends ApiCommand {

	/**
	 * Default constructor for the object.
	 */
	
	public ProportionalEntry pe;

	public GetWeightsCmd() {

	}

	/**
	 * Convert to JSON
	 */
	public String toJson() throws Exception {
		return mapper.writeValueAsString(this);
	}

	/**
	 * Executes the command, marshalls the response.
	 */
	@Override
	public void execute() {
		super.execute();
		
		try {
			pe = Configuration.getInstance().getWeights(campaign, tokenData);
		} catch (Exception err) {
			error = true;
			message = err.getMessage();
		}
	}

}
