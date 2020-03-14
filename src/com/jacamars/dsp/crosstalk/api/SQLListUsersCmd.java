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
public class SQLListUsersCmd extends ApiCommand {

	/** The list of deletions/updates */
	public List<Map> users;

	/**
	 * Default constructor
	 */
	public SQLListUsersCmd() {

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
				String select;
				if (tokenData.isRtb4FreeSuperUser()) 
					select = "select * from users";
				else
					select = "select * from users where customer_id='"+tokenData.customer+"'";
				var conn = CrosstalkConfig.getInstance().getConnection();
				var stmt = conn.createStatement();
				var prep = conn.prepareStatement(select);
				ResultSet rs = prep.executeQuery();
				
				users = convertToJson(rs); 
							
				return;
			} catch (Exception err) {
				err.printStackTrace();
				error = true;
				message = err.toString();
			}
			message = "Timed out";
		}
	
	List<Map> convertToJson(ResultSet rs) throws Exception {
		List<Map> list = new ArrayList<>();
		while(rs.next()) {
			int id = rs.getInt("id");
			String username = rs.getString("username");
			String customer = rs.getString("customer_id");
			String role = rs.getString("sub_id");
			String password = rs.getString("password");
			Map m = new HashMap<>();
			m.put("id", id);
			m.put("customer_id", customer);
			m.put("username", username);
			m.put("sub_id", role);
			m.put("password", password);
			list.add(m);
		}
		return list;
	}
}
