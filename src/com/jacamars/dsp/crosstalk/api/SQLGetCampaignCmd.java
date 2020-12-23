package com.jacamars.dsp.crosstalk.api;


import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.crosstalk.budget.CrosstalkConfig;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.tools.JdbcTools;

/**
 * Deletes a campaign
 * @author Ben M. Faul
 *
 */
public class SQLGetCampaignCmd extends ApiCommand {

	/** The list of deletions/updates */
	public int id;

	/**
	 * Default constructor
	 */
	public SQLGetCampaignCmd() {

	}

	/**
	 * Convert to JSON
	 */
	public String toJson() throws Exception {
		return WebAccess.mapper.writeValueAsString(this);
	}

	/**
	 * Execute the command, marshall the results.
	 */
	@Override
		public void execute() {
			super.execute();
			try {
				campaign = Campaign.getInstance(id, tokenData).toJson();

				return;
			} catch (Exception err) {
				error = true;
				message = err.toString();
				err.printStackTrace();
				return;
			}
		}
}
