package com.jacamars.dsp.crosstalk.budget;

import com.jacamars.dsp.rtb.common.CampaignBudget;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Creative;

import com.jacamars.dsp.rtb.tools.DbTools;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 
 * A class that builds the RTB4FREE representation of a campaign described in MySQL. A JSON representation of the MySQL definition of the
 * campaign that was defined in MySQL is converted to the RTB4FREE form. Also, the budgets and spends are monitored in this object. The object
 * also contains the Target object and an array of Creatives that implement the campaign.
 * 
 * @author Ben M. Faul
 *
 */
public class AccountingCampaign {

	/**  The campaign id for this campaign. This is the database key. */
	public int campaignid;

	/** The total budget for this campaign */
	protected volatile AtomicBigDecimal total_budget = new AtomicBigDecimal(0);
	
	/** The date time this campaign will be active */
	public long activate_time;
	
	/** The date time this campaign will expire */
	public long expire_time;
	
	/** All of the creatives of this campaign */
	protected Set<AccountingCreative> creatives = new HashSet<AccountingCreative>();

	/** The RTB4FREE campaign that was generated from this object */
	public Campaign campaign = null;
	
	/** The current hourly cost */
	protected AtomicBigDecimal hourlyCost = new AtomicBigDecimal(0);

	/** The SQL name for this campaign id */
	protected final String CAMPAIGN_ID = "id";
	
	/** Thew SQL name for the updated flag */
	protected final String UPDATED = "updated_at";
	
	/** The SQL name for the total budget */
	protected final String TOTAL_BUDGET = "total_budget";
	
	/** SQL name for the descriptive name */
	protected final String CAMPAIGN_NAME = "name";
	
	/** SQL name of Datetime of expiration */
	protected final String EXPIRE_TIME = "expire_time";
	
	/** SQL name for the date time to activate */
	protected final String ACTIVATE_TIME = "activate_time";
	
	/** The SQL name for the budget limit daily */
	protected final String DAILY_BUDGET = "budget_limit_daily";
	
	/** The SQL name for the hourly budget */
	protected final String HOURLY_BUDGET = "budget_limit_hourly";

	protected final String DAYPART = "day_parting_utc";
	
	Set<AccountingCreative> parkedCreatives = new HashSet<AccountingCreative>();

	/** If set to runnable then this will be allowed into the bidders (if budget is ok and not expired. Anything else means no loading */
	protected String status;

	/** This class's sl4j logger */
	static final Logger logger = LoggerFactory.getLogger(AccountingCampaign.class);
	
	public CampaignBudget budget;

	/**
	 * Default constructor.
	 */
	public AccountingCampaign() {

	}
	
	public AccountingCampaign(Campaign c) throws Exception {
		update(c);
	}
	
	public void update(Campaign c) throws Exception {
		this.campaign = c;
		this.budget = c.budget;
		if (c.budget == null)
			return;
		
		if (c.budget.daypart != null)
			c.budget.daypart.init();
		

		for (Creative cr : campaign.creatives)  {
			AccountingCreative creative = new AccountingCreative(campaign.adId,cr);
			if (!(creative.budgetExceeded())) {
				unpark(creative);
			} else {
				park(creative);
			}
		}
	}

	public void stop() {
	
	}

	public void runUsingElk() {
		try {

			budget.cost.set(BudgetController.getInstance().getCampaignTotalSpend("" + campaignid));
			budget.dailyCost.set(BudgetController.getInstance().getCampaignDailySpend("" + campaignid));
			budget.hourlyCost.set(BudgetController.getInstance().getCampaignHourlySpend("" + campaignid));

			logger.debug("*** ELK TEST: Updating budgets CAMPAIGN:{}", campaignid);
			logger.debug("Total cost: {}, daily cost: {}, hourly cost: {}", budget.cost.getDoubleValue(),
					budget.dailyCost.getDoubleValue(), budget.hourlyCost.getDoubleValue());

			for (AccountingCreative c : creatives) {
				c.runUsingElk();
			}

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public double costAsDouble() {
		return budget.cost.doubleValue();
	}


	/**
	 * Set the new total budget. Used by the api.
	 * @param amount double. The value to set.
	 */
	public void setTotalBudget(double amount) {
		budget.total_budget.set(amount);
	}

	/**
	 * Set the new total daily. Used by the api.
	 * @param amount double. The value to set.
	 */
	public void setDailyBudget(double amount) {
		budget.dailyBudget.set(amount);
	}

	/**
	 * Set the new hourly budget. Used by the api.
	 * @param amount double. The value to set.
	 */
	public void setHourlyBudget(double amount) {
		budget.hourlyBudget.set(amount);
	}


	public AccountingCreative getCreative(String id) {
		//
		// Check the active creatives
		//
		for (AccountingCreative c : creatives) {
			if (c.crid.equals(id))
				return c;
		}

		return null;
	}

	/**
	 * Report why the campaign is not runnable.
	 * @return String. The reasons why...
	 * @throws Exception on ES errors.
	 */
	public String report() throws Exception {
		if (campaignid == 1499) {
			System.out.println("HERE");
		}
		String reason = "";
		if (budgetExceeded()) {
			if (reason.length() != 0)
				reason += " ";
			if (BudgetController.getInstance().checkCampaignBudgetsTotal(""+campaignid, total_budget))
				reason += "Campaign total budget exceeded. ";
			if (BudgetController.getInstance().checkCampaignBudgetsDaily(""+campaignid, budget.dailyBudget))
				reason += "Campaign daily budget exceeded. ";
			if (BudgetController.getInstance().checkCampaignBudgetsHourly(""+campaignid, budget.hourlyBudget))
				reason += "Campaign hourly budget exceeded. ";
		}

		if (isExpired()) {
			if (reason.length() > 0)
				reason += " ";
			reason += "Bid window closed, expiry. ";
		} else if (!budgetExceeded()) {
			if (reason.length() > 0)
				reason += " ";

			if (budget.daypart != null) {
				if (budget.daypart.isActive() != true) {
					reason += "Daypart is not active ";
				}
			}
		}

		List<Map> xreasons = new ArrayList<Map>();
		if (creatives.size() != 0) {
			for (AccountingCreative p : parkedCreatives) {
				Map<String, Object> r = new HashMap<String, Object>();
				r.put("creative",p.crid);
				List<String> reasons = new ArrayList<String>();
				if (p.budgetExceeded()) {
					reasons.add("nobudget");
				}

				r.put("reasons",reasons);
			}
		}

		if (xreasons.size() != 0) {
			reason += DbTools.mapper.writeValueAsString(xreasons);
		}
		if (reason.length() > 0)
			logger.info("Campaign {} not loaded: {}",campaignid, reason);

		if (reason.length() == 0)
			reason = "Runnable";
		return reason;
	}

	public boolean process() throws Exception {
		boolean change = false;
		int n = creatives.size();
		List<AccountingCreative> list = new ArrayList<AccountingCreative>();

		for (AccountingCreative c : parkedCreatives) {
			if (!c.budgetExceeded()) {
				unpark(c);
				change = true;
			}
		}

		for (AccountingCreative creative : creatives) {
			if (creative.budgetExceeded()) {
				list.add(creative);
				change = true;
			}
		}

		for (AccountingCreative c : list) {
			park(c);
		}

		return change;
	}

	protected void park(AccountingCreative c) {
		creatives.remove(c);
		parkedCreatives.add(c);
	}

	protected void unpark(AccountingCreative c) {
		parkedCreatives.remove(c);
		creatives.add(c);
	}

	public boolean isExpired() {
		Date date = new Date();
		boolean expired = date.getTime() > expire_time;
		if (expired)
			return expired;
		expired = date.getTime() < activate_time;
		if (expired)
			return expired;
		return false;

	}

	public boolean isActive() throws Exception {
		Date date = new Date();

		if (creatives.size() == 0) {
			return false;
		}

		if (budgetExceeded()) {
			logger.debug("BUDGET EXCEEDED: {}", this.campaignid);
			return false;
		}

		if ((date.getTime() >= activate_time) && (date.getTime() <= expire_time)) {

			if (budget.daypart != null) {
				if (budget.daypart.isActive() != true) {
					logger.debug("Daypart is not active: {}", this.campaignid);
					return false;
				}
			}

			logger.debug("IS ACTIVE: {}", this.campaignid);
			return true;
		} else {
			logger.debug("ACTIVATION TIME NOT IN RANGE: {}", campaignid);
			return false;
		}
	}



	public boolean budgetExceeded() throws Exception {
		if (budget == null)
			return false;

		return BudgetController.getInstance().checkCampaignBudgets(""+campaignid, total_budget, budget.dailyBudget, budget.hourlyBudget);
	}

	public boolean compareTo(AccountingCampaign t) {
		return false;
	}

	/**
	 * Check and see if this campaign is deletable fron the system
	 * @return boolean. If campaign is expired or the total spend has been reached.
	 * @throws Exception on errors computing budgets.
	 */
	public boolean canBePurged() throws Exception {
		if (isExpired())
			return true;
		return BudgetController.getInstance().checkCampaignTotalBudgetExceeded(""+this.campaignid, total_budget);
	}

	public boolean addToRTB() throws Exception {
		return true;
	}

	public boolean addToRTB(String bidder) throws Exception {
		return true;
	}

	public Set<AccountingCreative> getCreatives() {
		return creatives;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}
