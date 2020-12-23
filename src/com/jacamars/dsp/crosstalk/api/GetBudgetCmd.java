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
	
	public String adtype;
	
	public List<Map> values;
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
		
		if (campaign != null)
			getCampaignData();
		else
			getAllData();
		
	}
	
	private void getAllData() {
		String select = null;
		values = new ArrayList<>();
		try {
			if (tokenData.isRtb4FreeSuperUser())
				select = "select * from campaigns";
			else
				select = "select * from campaigns where customer_id='" + tokenData.customer + "'";

			var conn = CrosstalkConfig.getInstance().getConnection();
			var prep = conn.prepareStatement(select);
			ResultSet rs = prep.executeQuery();

			while (rs.next()) {
				int cid = rs.getInt("id");
				Campaign c = Campaign.getInstance(cid,tokenData);
				Map m = new HashMap<>();
				double total = 0;
				double daily = 0;
				double hourly = 0;
				String name = rs.getString("name");
				String customer = rs.getString("customer_id");
				if (c.budget != null) {
					if (c.budget.totalBudget != null)
						total = c.budget.totalBudget.getDoubleValue();
					if (c.budget.dailyBudget != null)
						daily = c.budget.dailyBudget.getDoubleValue();
					if (c.budget.hourlyBudget != null)
						hourly = c.budget.hourlyBudget.getDoubleValue();
				}
				m.put("total_budget", total);
				m.put("budget_limit_daily", daily);
				m.put("budget_limit_hourly", hourly);
				m.put("id", cid);
				m.put("customer",customer);
				m.put("name", name);
				List creatives = new ArrayList();
				m.put("creatives",creatives);
				for (int i=0;i<c.creatives.size();i++) {
					Map mm = new HashMap();
					Creative cr = c.creatives.get(i);
					if (cr.budget.totalBudget != null)
						total = cr.budget.totalBudget.getDoubleValue();
					if (cr.budget.dailyBudget != null)
						daily = cr.budget.dailyBudget.getDoubleValue();
					if (cr.budget.hourlyBudget != null)
						hourly = cr.budget.hourlyBudget.getDoubleValue();
					mm.put("total_budget", total);
					mm.put("budget_limit_daily", daily);
					mm.put("budget_limit_hourly", hourly);
					mm.put("id", cr.id);
					mm.put("type", cr.type);
					creatives.add(mm);
				}
				values.add(m);
			}	
		} catch (Exception error) {
			this.error = true;
			message = error.toString();
		}
	}
	
	private void getCampaignData() {
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
			c = Campaign.getInstance(Integer.parseInt(campaign),tokenData);
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
		c = Campaign.getInstance(Integer.parseInt(campaign),tokenData);
		if (c == null) {
			error = true;
			message = "No campaign defined: " + campaign;
			return;
		}
		
		if (adtype != null)
			cr = c.getCreative(this.creative,adtype);
		else
			cr = c.getCreative(this.creative);
		if (cr == null) {
			error = true;
			message = "No creative defined: " + creative + " in " + campaign + " of type " + adtype;
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
