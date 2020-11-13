package com.jacamars.dsp.crosstalk.api;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.jacamars.dsp.crosstalk.budget.CrosstalkConfig;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.tools.JdbcTools;
import com.jacamars.dsp.crosstalk.budget.CampaignBuilderWorker;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;

/**
 * Returns an empty campaign object.
 * @author Ben M. Faul
 *
 */
public class SQLAddNewCampaignCmd extends ApiCommand {
	
	ResultSet rs = null;
	
	public int id;

	/**
	 * Default constructor
	 */
	public SQLAddNewCampaignCmd() {

	}

	/**
	 * Convert to JSON
	 */
	public String toJson() throws Exception {
		return WebAccess.mapper.writeValueAsString(this);
	}

	/**
	 * Execute the command, masrshall the results.
	 */
	@Override
		public void execute() {
			super.execute();
			try {
				ObjectNode node = mapper.readValue(campaign,ObjectNode.class);
				
				node.put("customer_id", tokenData.customer);
				
				Campaign x = Crosstalk.getInstance().shadow.get(node.get("id").asText());
				
				Campaign c =  new Campaign(node);   // patch running totals back in
				if (x != null && x.budget != null) {
					c.budget.totalCost = x.budget.totalCost;
					c.budget.hourlyCost = x.budget.hourlyCost;
					c.budget.dailyCost = x.budget.dailyCost;
				}

	
				PreparedStatement st = Campaign.toSql(c, CrosstalkConfig.getInstance().getConnection());
				st.executeUpdate();
				if (c.id == 0) {
					ResultSet updated = st.getResultSet();
					if (updated.next()) {
						id = updated.getInt("id");
					}
				}
				st.close();
				
			//	Crosstalk.getInstance().scan(); 
						
				CampaignBuilderWorker w = new CampaignBuilderWorker(c);
				w.run();
				
				System.out.println(c.toJson());
				
				return;
			} catch (Exception err) {
				err.printStackTrace();
				error = true;
				message = err.toString();
			}
		}
	
}
