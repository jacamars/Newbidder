package com.jacamars.dsp.crosstalk.api;

import java.util.Random;

import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Creative;

/**
 * Wen API to set the price of a campaign/creative
 * 
 * @author Ben M. Faul
 *
 */
public class SetBudgetCmd extends ApiCommand {

	/** THe price to set. */
	public double hourly;
	public double daily;
	public double total;

	/** If setting a deal price, then indicate that here */
	public String deal;

	/**
	 * Default constructor for the object.
	 */
	public SetBudgetCmd() {

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
			Campaign c = Crosstalk.getInstance().getKnownCampaign(campaign);
			if (c == null) {
				error = true;
				message = "No campaign defined: " + campaign;
				return;
			}
			
			if (tokenData.isAuthorized(c.customer_id) == false) {
				message = "No campaign defined: " + campaign;
				return;
			}

			if (creative == null) {
				c.budget.setTotalBudget(total);
				c.budget.setDailyBudget(daily);
				c.budget.setHourlyBudget(hourly);

				Crosstalk.getInstance().update(c, true);
				return;
			}

			Creative cc = c.getCreative(this.creative);
			if (cc == null) {
				error = true;
				message = "No creative defined: " + creative + " in " + campaign;
				return;
			}

			cc.setTotalBudget(total);
			cc.setDailyBudget(daily);
			cc.setHourlyBudget(hourly);

			Crosstalk.getInstance().update(c, true);
		} catch (Exception error) {
			message = error.getMessage();
			this.error = true;
		}
	}
}
