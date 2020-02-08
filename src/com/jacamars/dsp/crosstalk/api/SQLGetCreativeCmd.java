package com.jacamars.dsp.crosstalk.api;


import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.language.bm.Rule;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.crosstalk.budget.CrosstalkConfig;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.common.Node;
import com.jacamars.dsp.rtb.tools.JdbcTools;

/**
 * Deletes a campaign
 * @author Ben M. Faul
 *
 */
public class SQLGetCreativeCmd extends ApiCommand {

	/** The list of deletions/updates */
	public int id;
	public String key;
	public Creative data;

	/**
	 * Default constructor
	 */
	public SQLGetCreativeCmd() {

	}

	/**
	 * Deletes a campaign from the bidders.
	 * 
	 * @param username
	 *            String. User authorization for command.
	 * @param password
	 *            String. Password authorization for command.
	 */
	public SQLGetCreativeCmd(String username, String password) {
		super(username, password);
		type = SQLGET_CREATIVE;
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
	public SQLGetCreativeCmd(String username, String password, String target) {
		super(username, password);
		campaign = target;
		type = SQLGET_CREATIVE;
	}

	/**
	 * Convert to JSON
	 */
	public String toJson() throws Exception {
		return WebAccess.mapper.writeValueAsString(this);
	}

	/**
	 * Execute the command, msrshall the results.
	 */
	@Override
		public void execute() {
			super.execute();
			try {
				data = Creative.getInstance(id,key);
				return;
			} catch (Exception err) {
				err.printStackTrace();
				error = true;
				message = err.toString();
			}
		}
}
