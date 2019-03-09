package com.jacamars.dsp.crosstalk.api;

import java.util.Random;

import com.jacamars.dsp.crosstalk.budget.AccountingCampaign;
import com.jacamars.dsp.crosstalk.budget.AccountingCreative;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;

/**
 * Wen API to set the price of a campaign/creative
 * @author Ben M. Faul
 *
 */
public class SetBudgetCmd extends ApiCommand {

	/** THe price to set.*/
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
	 * Basic form of the command.
	 * @param username String. The username to use for authorization.
	 * @param password String. The password to use for authorization.
	 */
	public SetBudgetCmd(String username, String password) {
		super(username, password);
		type = SetBudget;

	}

	/**
	 * Targeted form of the command.
	 * @param username String. The user authorization.
	 * @param password String. THe password authorization.
	 * @param campaign String. The target campaign.
	 * @param creative String. The target creative.
	 * @param hourly double. The hourly limit to set.
	 * @param daily double. The daily limit to set.
	 * @param total double. The total limit to set.
	 */
	public SetBudgetCmd(String username, String password, String campaign, String creative, double hourly, double daily, double total) {
		super(username, password);
		this.campaign = campaign;
		this.creative = creative;
		this.daily = daily;
		this.hourly = hourly;
		this.total = total;
		type = SetBudget;
	}

	/**
	 * Targeted form of the command.
	 * @param username String. The user authorization.
	 * @param password String. THe password authorization.
	 * @param campaign String. The target campaign.
	 */
	public SetBudgetCmd(String username, String password, String campaign, double hourly, double daily, double total) {
		super(username, password);
		this.campaign = campaign;
		this.creative = creative;
		this.daily = daily;
		this.hourly = hourly;
		this.total = total;
		type = SetBudget;
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
		AccountingCampaign c = Crosstalk.getInstance().getKnownCampaign(campaign);
		if (c == null) {
			error = true;
			message = "No campaign defined: " + campaign;
			return;
		}
		
		if (creative == null) {
			c.setTotalBudget(total);
			c.setDailyBudget(daily);
			c.setHourlyBudget(hourly);
			
			Crosstalk.getInstance().update(c.campaign,true);
			return;
		}
		
		AccountingCreative cc = c.getCreative(this.creative);
		if (cc == null) {
			error = true;
			message = "No creative defined: " + creative + " in " + campaign;
			return;
		}

		cc.setTotalBudget(total);
		cc.setDailyBudget(daily);
		cc.setHourlyBudget(hourly);
		
		Crosstalk.getInstance().update(c.campaign,true);
		} catch (Exception error) {
			message = error.getMessage();
			this.error = true;
		}
	}
}
