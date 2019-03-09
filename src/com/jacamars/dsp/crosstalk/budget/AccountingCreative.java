package com.jacamars.dsp.crosstalk.budget;

import java.math.BigDecimal;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.node.MissingNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.jacamars.dsp.rtb.bidder.MimeTypes;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.common.CreativeBudget;
import com.jacamars.dsp.rtb.common.Deal;
import com.jacamars.dsp.rtb.common.Deals;
import com.jacamars.dsp.rtb.common.Dimension;
import com.jacamars.dsp.rtb.common.Dimensions;
import com.jacamars.dsp.rtb.common.HttpPostGet;
import com.jacamars.dsp.rtb.common.Node;
import com.jacamars.dsp.rtb.exchanges.adx.AdxCreativeExtensions;

/**
 * A class that creates an RTB4FREE creative from the MySQL definition of a creative.
 * @author ben
 *
 */
public class AccountingCreative implements Comparable<Object> {

	/** This class's logging object */
	static final Logger logger = LoggerFactory.getLogger(AccountingCreative.class);

	public Creative creative;
	public CreativeBudget budget;
	
	String cid;
	String crid;
	String type;
	

	boolean runUsingElk() {

		try {
			budget.total_cost.set(BudgetController.getInstance().getCreativeTotalSpend(cid, crid, type));
			budget.dailyCost.set(BudgetController.getInstance().getCreativeDailySpend(cid, crid, type));
			budget.hourlyCost.set(BudgetController.getInstance().getCreativeHourlySpend(cid, crid, type));
		
			logger.debug("*** ELK TEST: Updating budgets: {}/{}/{}",cid, crid, type);
			logger.debug("Total cost: {} hourly cost: {}, daily_cost: {}",budget.total_cost.getDoubleValue(),
					budget.dailyCost.getDoubleValue(), budget.hourlyCost.getDoubleValue());
		} catch (Exception error) {
			error.printStackTrace();
		}
		return true;
	}
	
	String getType(Creative c) {
		if (c.isNative())
			return "native";
		if (c.isVideo())
			return "video";
		return "banner";
	}
	
	public boolean isActive() throws Exception {

		if (budgetExceeded())
			return false;
		return true;
	}

	public AccountingCreative() {

	}

	public AccountingCreative(String adid, Creative c) throws Exception {
		creative = c;
		budget = c.budget;
		this.cid = adid;
		this.crid = c.impid;
		type = getType(c);
	}

	public void stop() {

	}

	

	String clean(String data) {
		String[] lines = data.split("\n");
		String rc = "";
		for (String s : lines) {
			rc += s.trim();
		}
		return rc;
	}

	/**
	 * Determine if the budget was exceeded.
	 * @return boolean. Returns true if the budget was exceeded.
	 * @throws Exception on Elk errors.
	 */
	public boolean budgetExceeded() throws Exception {
			logger.debug("********* CHECKING BUDGET FOR CREATIVE {}",creative.impid);
			if (budget == null)
				return false;
			return BudgetController.getInstance().checkCreativeBudgets(cid, crid, type,
						budget.total_budget, budget.dailyBudget, budget.hourlyBudget);
	}




	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	protected List<String> getList(String text) {
		List<String> temp = new ArrayList<String>();
		if (text != null && text.length() > 0) {
			String[] parts = text.split(",");
			for (String part : parts) {
				temp.add(part);
			}
		}
		return temp;

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
}
