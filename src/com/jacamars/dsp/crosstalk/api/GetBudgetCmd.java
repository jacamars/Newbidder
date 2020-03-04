package com.jacamars.dsp.crosstalk.api;



import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Creative;

/**
 * Get the assigned budget for campaign/creatives
 * @author Ben M. Faul
 *
 */
public class GetBudgetCmd extends ApiCommand {

	/** The returned daily budget */
	public Double budget_limit_daily;  
	/** The returned budget limit hourly */
	public Double budget_limit_hourly;    
	/** returned total budget */
	public Double total_budget;                 
	
	/**
	 * Default constructor for the object.
	 */
	public GetBudgetCmd() {

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
		this.refreshList = null;
		
		if (creative == null)
			executeCampaignLevel();
		else
			executeCreativeLevel();
	}
		
	/**
	 * Execute at the campaign level
	 */
	void executeCampaignLevel() {
		
		Campaign c = null;
		try {
			c = Crosstalk.getInstance().getKnownCampaign(campaign);
		if (c == null) {
			error = true;
			message = "No campaign defined: " + campaign;
			return;
		} } catch (Exception e) {
			error = true;
			message = e.getMessage();
		}
		
		if (tokenData.isAuthorized(c.customer_id)==false) {
			error = true;
			message = "No campaign defined: " + campaign;
			return;
		}
		
		budget_limit_daily = c.budget.dailyBudget.getDoubleValue(); 
		budget_limit_hourly = c.budget.hourlyBudget.getDoubleValue();  
		total_budget = c. budget.totalBudget.getDoubleValue();
	}
	
	/**
	 * Execute at the creative level.
	 */
	void executeCreativeLevel() {
		Campaign c = null;
		Creative cr = null;
		try {
		c = Crosstalk.getInstance().getKnownCampaign(campaign);
		if (c == null) {
			error = true;
			message = "No campaign defined: " + campaign;
			return;
		}
		
		 cr = c.getCreative(this.creative);
		if (cr == null) {
			error = true;
			message = "No creative defined: " + creative + " in " + campaign;
			return;
		}

			
		budget_limit_daily = cr.budget.dailyBudget.getDoubleValue();  
		budget_limit_hourly = cr.budget.hourlyBudget.getDoubleValue();
		total_budget = cr.budget.totalBudget.getDoubleValue(); 
		} catch (Exception e) {
			error = true;
			message = "No creative defined: " + creative + " in " + campaign;
			return;
		}
			
	}
}
