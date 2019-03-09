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
	 * Basic form of the command.
	 * @param username String. The username to use for authorization.
	 * @param password String. The password to use for authorization.
	 */
	public GetWeightsCmd(String username, String password) {
		super(username, password);
		type = GetWeights;

	}

	/**
	 * Targeted form of the command.
	 * @param username String. The user authorization.
	 * @param password String. THe password authorization.
	 * @param campaign String. The target campaign.
	 */
	public GetWeightsCmd(String username, String password, String campaign) {
		super(username, password);
		this.campaign = campaign;
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
			pe = Configuration.getInstance().getWeights(campaign);
		} catch (Exception err) {
			error = true;
			message = err.getMessage();
		}
	}

}
