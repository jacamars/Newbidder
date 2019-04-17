package com.jacamars.dsp.rtb.common;

import com.jacamars.dsp.crosstalk.budget.AtomicBigDecimal;
import com.jacamars.dsp.crosstalk.budget.DayPart;

public class Budget {

	/** The current cost incurred by this campaign */
	public volatile AtomicBigDecimal totalCost = new AtomicBigDecimal(0); 
	
	/** Total budget of this object */
	public AtomicBigDecimal totalBudget = new AtomicBigDecimal(0);
	
	/** The daily budget of this campaign */
	public AtomicBigDecimal dailyBudget = new AtomicBigDecimal(0);
	
	/** The hourly budget of this campaign */
	public AtomicBigDecimal hourlyBudget = new AtomicBigDecimal(0);
	
	public long expire_time ;
	public long activate_time;
	
	// Not used in Creatives
	public DayPart daypart;

	/** The current daily cost */
	public transient AtomicBigDecimal dailyCost = new AtomicBigDecimal(0);
	
	/** The current hourly cost */
	public transient AtomicBigDecimal hourlyCost = new AtomicBigDecimal(0);

	public Budget() {
		
	}
	
	public void setTotalBudget(double d) {
		totalBudget = new AtomicBigDecimal(d);
	}
	
	public void setHourlyBudget(double d) {
		hourlyBudget = new AtomicBigDecimal(d);
	}
	
	public void setDailyBudget(double d) {
		dailyBudget = new AtomicBigDecimal(d);
	}
}
