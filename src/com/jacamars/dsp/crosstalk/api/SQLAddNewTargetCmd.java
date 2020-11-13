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
import com.jacamars.dsp.crosstalk.budget.Targeting;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Node;
import com.jacamars.dsp.rtb.tools.JdbcTools;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;

/**
 * Returns an empty campaign object.
 * @author Ben M. Faul
 *
 */
public class SQLAddNewTargetCmd extends ApiCommand {
	
	ResultSet rs = null;
	public String target;
	int id;

	/**
	 * Default constructor
	 */
	public SQLAddNewTargetCmd() {

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
				System.out.println("NEW TARGET: " + target);
				ObjectNode x = mapper.readValue(target,ObjectNode.class);
				x.put("customer_id",tokenData.customer);
				Targeting n = new Targeting(x);
	
				PreparedStatement st = Targeting.toSql(n, CrosstalkConfig.getInstance().getConnection());
				st.execute();
				if (n.id == 0) {
					ResultSet updated = st.getResultSet();
					if (updated.next()) {
						id = updated.getInt("id");
					}
				}
				st.close();
				
				n.updateAttachedCampaigns();
				return;
			} catch (Exception err) {
				err.printStackTrace();
				error = true;
				message = err.toString();
			}
		}
	
}
