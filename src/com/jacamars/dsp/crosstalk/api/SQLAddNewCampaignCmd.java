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
import com.jacamars.dsp.crosstalk.budget.Crosstalk;

/**
 * Returns an empty campaign object.
 * @author Ben M. Faul
 *
 */
public class SQLAddNewCampaignCmd extends ApiCommand {
	
	ResultSet rs = null;

	/**
	 * Default constructor
	 */
	public SQLAddNewCampaignCmd() {

	}

	/**
	 * Deletes a campaign from the bidders.
	 *
	 * @param username
	 *            String. User authorization for command.
	 * @param password
	 *            String. Password authorization for command.
	 */
	public SQLAddNewCampaignCmd(String username, String password) {
		super(username, password);
		type = SQLADD_NEW_CAMPAIGN;
	}

	/**
	 * Targeted form of command. starts a specific bidder.
	 *
	 * @param username
	 *            String. User authorizatiom.
	 * @param password
	 *            String. Password authorization.
	 * @param target
	 *            String. The bidder to start.
	 */
	public SQLAddNewCampaignCmd(String username, String password, String target) {
		super(username, password);
		type = SQLADD_NEW_CAMPAIGN;
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
				System.out.println("NEW CAMPAIGN: " + campaign);
				ObjectNode node = mapper.readValue(campaign,ObjectNode.class);
				node.put("ad_domain", node.get("adomain").asText()); // normalize to SQL origin
				
				Campaign c = new Campaign(node);
				PreparedStatement st = Campaign.toSql(c, CrosstalkConfig.getInstance().getConnection());
				st.executeUpdate();
				st.close();
				
				
				return;
			} catch (Exception err) {
				err.printStackTrace();
				error = true;
				message = err.toString();
			}
		}
	
}
