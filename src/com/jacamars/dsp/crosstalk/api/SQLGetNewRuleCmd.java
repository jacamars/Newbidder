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
public class SQLGetNewRuleCmd extends ApiCommand {
	
	ResultSet rs = null;
	/** The returned results from the campaign. */
	public String name;
	public Node data;

	/**
	 * Default constructor
	 */
	public SQLGetNewRuleCmd() {

	}

	/**
	 * Deletes a campaign from the bidders.
	 *
	 * @param username
	 *            String. User authorization for command.
	 * @param password
	 *            String. Password authorization for command.
	 */
	public SQLGetNewRuleCmd(String username, String password) {
		super(username, password);
		type = SQLGET_NEW_TARGET;
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
	public SQLGetNewRuleCmd(String username, String password, String target) {
		super(username, password);
		this.name = target;
		type = SQLGET_NEW_TARGET;
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
				data =  new Node();
				data.name = name;
				return;
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
		}
	
}