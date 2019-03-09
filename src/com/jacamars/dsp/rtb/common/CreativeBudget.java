package com.jacamars.dsp.rtb.common;

import com.jacamars.dsp.crosstalk.budget.AtomicBigDecimal;

public class CreativeBudget {
	/** Current cost of this creative */
	public volatile AtomicBigDecimal total_cost = new AtomicBigDecimal(0);
	
	/** Total budget of this creative */
	public volatile AtomicBigDecimal total_budget = new AtomicBigDecimal(0);
	
	/** The bid price of this creative */
	public volatile AtomicBigDecimal bid_ecpm = new AtomicBigDecimal(0);
	
	/** The daily budget for the creative */
	public volatile AtomicBigDecimal dailyBudget = null;
	
	/** The daily cost incurred today for this creative */
	public volatile AtomicBigDecimal dailyCost = null;

	/* The hourly budget for this creative */
	public volatile AtomicBigDecimal hourlyBudget = null;
	
	/** The current hourly cost for this creative */
	public volatile AtomicBigDecimal hourlyCost = null;


	/** The epoch time this creative is activated */
	public long activate_time = 0;
	
	/** The epoch time this creative expires */
	public long expire_time = 0;
	
	public CreativeBudget() {
		
	}
}
