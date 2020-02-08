package com.jacamars.dsp.crosstalk.api;


import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.crosstalk.budget.CrosstalkConfig;

/**
 * Deletes a campaign
 * @author Ben M. Faul
 *
 */
public class SQLListTargetsCmd extends ApiCommand {

	/** The list of deletions/updates */
	public List<Map> targets;

	/**
	 * Default constructor
	 */
	public SQLListTargetsCmd() {

	}

	/**
	 * Deletes a campaign from the bidders.
	 * 
	 * @param username
	 *            String. User authorization for command.
	 * @param password
	 *            String. Password authorization for command.
	 */
	public SQLListTargetsCmd(String username, String password) {
		super(username, password);
		type = SQLLIST_TARGETS;
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
	public SQLListTargetsCmd(String username, String password, String target) {
		super(username, password);
		campaign = target;
		type = SQLLIST_TARGETS;
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
				String select = "select * from targets";
				var conn = CrosstalkConfig.getInstance().getConnection();
				var stmt = conn.createStatement();
				var prep = conn.prepareStatement(select);
				ResultSet rs = prep.executeQuery();
				
				targets = convertToJson(rs); 
							
				return;
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
			message = "Timed out";
		}
	
	List<Map> convertToJson(ResultSet rs) throws Exception {
		List<Map> list = new ArrayList<>();
		while(rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");
			Map m = new HashMap<>();
			m.put("id", id);
			m.put("name", name);
			list.add(m);
		}
		return list;
	}
}
