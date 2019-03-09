package com.jacamars.dsp.crosstalk.api;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jacamars.dsp.crosstalk.budget.AccountingCampaign;
import com.jacamars.dsp.crosstalk.budget.AccountingCreative;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.commands.Echo;
import com.jacamars.dsp.rtb.common.Campaign;

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
	 * Basic form of the command.
	 * @param username String. The username to use for authorization.
	 * @param password String. The password to use for authorization.
	 */
	public GetBudgetCmd(String username, String password) {
		super(username, password);
		type = GetBudget;

	}

	/**
	 * Targeted form of the command.
	 * @param username String. The user authorization.
	 * @param password String. THe password authorization.
	 * @param campaign String. The target campaign.
	 */
	public GetBudgetCmd(String username, String password, String campaign) {
		super(username, password);
		this.campaign = campaign;
		this.creative = creative;
		type = GetBudget;
	}
	
	/**
	 * Targeted form of the command.
	 * @param username String. The user authorization.
	 * @param password String. THe password authorization.
	 * @param campaign String. The target campaign.
	 * @param creative String. The target creative.
	 */
	public GetBudgetCmd(String username, String password, String campaign, String creative) {
		super(username, password);
		this.campaign = campaign;
		this.creative = creative;
		type = GetBudget;
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
		
		AccountingCampaign c = Crosstalk.getInstance().getKnownCampaign(campaign);
		if (c == null) {
			error = true;
			message = "No campaign defined: " + campaign;
			return;
		}
		
		budget_limit_daily = c.budget.dailyBudget.getDoubleValue(); 
		budget_limit_hourly = c.budget.hourlyBudget.getDoubleValue();  
		total_budget = c. budget.total_budget.getDoubleValue();
	}
	
	/**
	 * Execute at the creative level.
	 */
	void executeCreativeLevel() {
		
		AccountingCampaign c = Crosstalk.getInstance().getKnownCampaign(campaign);
		if (c == null) {
			error = true;
			message = "No campaign defined: " + campaign;
			return;
		}
		
		AccountingCreative cr = c.getCreative(this.creative);
		if (cr == null) {
			error = true;
			message = "No creative defined: " + creative + " in " + campaign;
			return;
		}

			
		budget_limit_daily = cr.budget.dailyBudget.getDoubleValue();  
		budget_limit_hourly = cr.budget.hourlyBudget.getDoubleValue();
		total_budget = cr.budget.total_budget.getDoubleValue(); 
			
	}
}
