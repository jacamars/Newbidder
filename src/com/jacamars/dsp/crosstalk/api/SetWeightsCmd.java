package com.jacamars.dsp.crosstalk.api;

import com.jacamars.dsp.crosstalk.budget.AccountingCampaign;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.rtb.blocks.ProportionalEntry;
import com.jacamars.dsp.rtb.commands.SetWeights;
import com.jacamars.dsp.rtb.common.Configuration;

/**
 * Get the assigned price of a Campaign/creative
 * 
 * @author Ben M. Faul
 *
 */
public class SetWeightsCmd extends ApiCommand {

	public String weights;
	public ProportionalEntry pe;

	/**
	 * Default constructor for the object.
	 */

	public SetWeightsCmd() {

	}

	/**
	 * Basic form of the command.
	 * 
	 * @param username String. The username to use for authorization.
	 * @param password String. The password to use for authorization.
	 */
	public SetWeightsCmd(String username, String password) {
		super(username, password);
		type = GetPrice;

	}

	/**
	 * Targeted form of the command.
	 * 
	 * @param username String. The user authorization.
	 * @param password String. THe password authorization.
	 * @param campaign String. The target campaign.
	 * @param weights  String. The target creative.
	 */
	public SetWeightsCmd(String username, String password, String campaign, String weights) {
		super(username, password);
		this.campaign = campaign;
		this.weights = weights;
		type = SetWeights;
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
	public void execute() {
		super.execute();

		AccountingCampaign c = Crosstalk.getInstance().getKnownCampaign(campaign);
		if (c == null) {
			error = true;
			message = "No campaign defined: " + campaign;
			return;
		}

		c.campaign.weights = pe;
		try {
			Crosstalk.getInstance().update(c.campaign, true);
		} catch (Exception e) {
			error = true;
			message = e.getMessage();
		}
	}
}
