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
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.tools.JdbcTools;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;

/**
 * Returns an empty campaign object.
 * @author Ben M. Faul
 *
 */
public class SQLAddNewCreativeCmd extends ApiCommand {
	
	ResultSet rs = null;
	public int id;

	/**
	 * Default constructor
	 */
	public SQLAddNewCreativeCmd() {

	}

	/**
	 * Convert to JSON
	 */
	public String toJson() throws Exception {
		return WebAccess.mapper.writeValueAsString(this);
	}

	/**
	 * Execute the command, marshal the results.
	 */
	@Override
		public void execute() {
			super.execute();
			try {
				System.out.println("NEW Creative: " + creative);
				ObjectNode node = mapper.readValue(creative,ObjectNode.class);
				node.put("customer_id", tokenData.customer);
				String stype = node.get("type").asText();
				Creative c = new Creative(node);
				c.compile();
				id = c.saveToDatabase();
				if (c.id != 0) {
					Campaign.touchCampaignsWithCreative(c.getAttributeType(),c.id);
				}
				return;
			} catch (Exception err) {
				err.printStackTrace();
				error = true;
				message = err.toString();
			}
		}
	
}
