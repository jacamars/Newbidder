package com.jacamars.dsp.crosstalk.api;



import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.crosstalk.budget.CrosstalkConfig;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.shared.CampaignCache;

/**
 * Reset the assigned budget for campaign/creatives
 * @author Ben M. Faul
 *
 */
public class ResetBudgetCmd extends ApiCommand {

	/** The returned daily budget */
	public Double budget_limit_daily;  
	/** The returned budget limit hourly */
	public Double budget_limit_hourly;    
	/** returned total budget */
	public Double total_budget;
	
	public String adtype;
	
	public List<Map> values;
	/**
	 * Default constructor for the object.
	 */
	public ResetBudgetCmd() {

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
		Campaign c = null;
		try {
			c = Campaign.getInstance(Integer.parseInt(campaign),tokenData);
		if (c == null) {
			error = true;
			message = "No campaign defined: " + campaign;
			return;
		} } catch (Exception e) {
			error = true;
			message = e.getMessage();
		}
		try {
			Campaign.resetTotalCost(c);
		} catch (Exception e) {
			error = true;
			message = e.getMessage();
		}	
	}
	
}
