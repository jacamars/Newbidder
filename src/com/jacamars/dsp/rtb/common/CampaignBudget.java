package com.jacamars.dsp.rtb.common;

import com.jacamars.dsp.crosstalk.budget.AtomicBigDecimal;
import com.jacamars.dsp.crosstalk.budget.DayPart;

public class CampaignBudget {

	/** The current cost incurred by this campaign */
	public volatile AtomicBigDecimal cost = new AtomicBigDecimal(0); 
	
	/** The daily budget of this campaign */
	public AtomicBigDecimal dailyBudget = null;
	
	/** The hourly budget of this campaign */
	public AtomicBigDecimal hourlyBudget = null;
	
	public AtomicBigDecimal total_budget;
	
	public long expire_time ;
	public long activate_time;
	
	public DayPart daypart;

	/** The current daily cost */
	public transient AtomicBigDecimal dailyCost = new AtomicBigDecimal(0);
	
	/** The current hourly cost */
	public transient AtomicBigDecimal hourlyCost = new AtomicBigDecimal(0);

	public CampaignBudget() {
		
	}
}
