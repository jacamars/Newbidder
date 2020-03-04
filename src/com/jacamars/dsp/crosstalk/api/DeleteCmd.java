package com.jacamars.dsp.crosstalk.api;


import java.util.List;

import com.jacamars.dsp.crosstalk.budget.Crosstalk;

/**
 * Deletes a campaign
 * @author Ben M. Faul
 *
 */
public class DeleteCmd extends ApiCommand {

	/** The list of deletions/updates */
	public List<String> updated;

	/**
	 * Default constructor
	 */
	public DeleteCmd() {

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
				updated = Crosstalk.getInstance().deleteCampaign(campaign, tokenData);
				return;
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
			message = "Timed out";
		}
}
