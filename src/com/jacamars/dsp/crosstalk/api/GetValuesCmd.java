package com.jacamars.dsp.crosstalk.api;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jacamars.dsp.crosstalk.budget.CrosstalkConfig;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.shared.CampaignCache;

/**
 * Web API to get the assigned budget for campaign/creatives
 * 
 * @author Ben M. Faul
 *
 */
public class GetValuesCmd extends ApiCommand {
	/** The daily budget value */
	public Double daily_value;

	/** The hourly budget value */
	public Double hourly_value;

	/** The total budget */
	public Double total_value;

	/** The adtype of the creative */
	public String adtype;
	
	public List<Map> values;

	/**
	 * Default constructor for the object.
	 */
	public GetValuesCmd() {

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
		refreshList = null;

		// TBD Needs rewrite for multi tenant

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
					if (c.budget.totalCost != null)
						total = c.budget.totalCost.getDoubleValue();
					if (c.budget.dailyCost != null)
						daily = c.budget.dailyCost.getDoubleValue();
					if (c.budget.hourlyCost != null)
						hourly = c.budget.hourlyCost.getDoubleValue();
				}
				m.put("total_value", total);
				m.put("daily_value", daily);
				m.put("hourly_value", hourly);
				m.put("id", cid);
				m.put("customer",customer);
				m.put("name", name);
				List creatives = new ArrayList();
				m.put("creatives",creatives);
				for (int i=0;i<c.creatives.size();i++) {
					Map mm = new HashMap();
					Creative cr = c.creatives.get(i);
					total = hourly = daily = 0;
					if (cr.budget.totalCost != null)
						total = cr.budget.totalCost.getDoubleValue();
					if (cr.budget.totalCost != null)
						daily = cr.budget.dailyCost.getDoubleValue();
					if (cr.budget.totalCost != null)
						hourly = cr.budget.hourlyCost.getDoubleValue();
					mm.put("total_value", total);
					mm.put("daily_value", daily);
					mm.put("hourly_value", hourly);
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
		try {
			Campaign c = Campaign.getInstance(Integer.parseInt(campaign),tokenData);
			if (tokenData.isRtb4FreeSuperUser() || tokenData.customer.equals(c.customer_id)) {
				if (creative == null) {
					total_value = c.budget.totalCost.getDoubleValue();
					daily_value = c.budget.dailyCost.getDoubleValue();
					hourly_value = c.budget.hourlyCost.getDoubleValue();
				} else {
					Creative cr;
					if (adtype == null)
						cr = c.getCreative(creative);
					else
						cr = c.getCreative(creative, adtype);
					total_value = cr.budget.totalCost.getDoubleValue();
					daily_value = cr.budget.dailyCost.getDoubleValue();
					hourly_value = cr.budget.hourlyCost.getDoubleValue();
				}
			}
		} catch (Exception error) {
			this.error = true;
			message = error.toString();
		}
	}
}
