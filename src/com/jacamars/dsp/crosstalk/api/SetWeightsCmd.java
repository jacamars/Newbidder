package com.jacamars.dsp.crosstalk.api;

import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.rtb.blocks.ProportionalEntry;
import com.jacamars.dsp.rtb.common.Campaign;

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

		Campaign c = null;
		try {
			c = Crosstalk.getInstance().getKnownCampaign(campaign);
			if (c == null || tokenData.isAuthorized(c.customer_id)) {
				error = true;
				message = "No campaign defined: " + campaign;
				return;
			}
		} catch (Exception e) {
			error = true;
			message = e.getMessage();
			return;
		}

		c.weights = pe;
		try {
			Crosstalk.getInstance().update(c, true);
		} catch (Exception e) {
			error = true;
			message = e.getMessage();
		}
	}
}
