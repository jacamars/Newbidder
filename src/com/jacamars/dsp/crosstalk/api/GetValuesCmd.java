package com.jacamars.dsp.crosstalk.api;

import com.jacamars.dsp.crosstalk.budget.Aggregator;
import com.jacamars.dsp.crosstalk.budget.BudgetController;

/**
 * Web API to get the assigned budget for campaign/creatives
 * 
 * @author Ben M. Faul
 *
 */
public class GetValuesCmd extends ApiCommand {
	/** The daily budget value */
	public Double daily_value;

	/** The hourly budget value */
	public Double hourly_value;

	/** The total budget */
	public Double total_value;

	/** The adtype of the creative */
	public String adtype;

	/**
	 * Default constructor for the object.
	 */
	public GetValuesCmd() {

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
		refreshList = null;
		String type = null;
		
		// TBD Needs rewrite for multi tenant
		
		try {
			if (creative == null) {
				total_value = BudgetController.getInstance().getCampaignTotalSpend(campaign);
				daily_value = BudgetController.getInstance().getCampaignDailySpend(campaign);
				hourly_value = BudgetController.getInstance().getCampaignHourlySpend(campaign);
			} else {
				if (adtype != null) {
					type = adtype;
				} else {
					if (Aggregator.creativeExists(campaign, creative, "banner"))
						type = "banner";
					if (Aggregator.creativeExists(campaign, creative, "video"))
						type = "video";
					if (Aggregator.creativeExists(campaign, creative, "native"))
						type = "native";
					if (Aggregator.creativeExists(campaign, creative, "audio"))
						type = "audio";
				}
				total_value = BudgetController.getInstance().getCreativeTotalSpend(campaign, creative, type);
				daily_value = BudgetController.getInstance().getCreativeDailySpend(campaign, creative, type);
				hourly_value = BudgetController.getInstance().getCreativeHourlySpend(campaign, creative, type);
			}
		} catch (Exception error) {
			this.error = true;
			message = error.toString();
		}
	}
}
