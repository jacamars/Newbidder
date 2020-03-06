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
import com.jacamars.dsp.rtb.common.Node;
import com.jacamars.dsp.rtb.tools.JdbcTools;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;

/**
 * Returns an empty campaign object.
 * @author Ben M. Faul
 *
 */
public class SQLAddNewUserCmd extends ApiCommand {
	
	ResultSet rs = null;
	public String user;

	/**
	 * Default constructor
	 */
	public SQLAddNewUserCmd() {

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
				System.out.println("NEW User: " + user);
				UserRecord u = mapper.readValue(user,UserRecord.class);
				u.toSql();
				return;
			} catch (Exception err) {
				err.printStackTrace();
				error = true;
				message = err.toString();
			}
		}
	
}
