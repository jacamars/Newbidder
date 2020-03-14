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
public class SQLListAffiliatesCmd extends ApiCommand {

	/** The list of deletions/updates */
	public List<Map> affiliates;

	/**
	 * Default constructor
	 */
	public SQLListAffiliatesCmd() {

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
					select = "select * from companies";
				else
					select = "select * from companies where customer_id='"+tokenData.customer+"'";
				var conn = CrosstalkConfig.getInstance().getConnection();
				var stmt = conn.createStatement();
				var prep = conn.prepareStatement(select);
				ResultSet rs = prep.executeQuery();
				
				affiliates = convertToJson(rs); 
							
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
			String customer = rs.getString("customer_id");
			String email = rs.getString("email");
			String telephone = rs.getString("telephone");
			String firstname = rs.getString("firstname");
			String lastname = rs.getString("lastname");
			String address = rs.getString("address");
			String citystate = rs.getString("citystate");
			String  country = rs.getString("country");
			String postalcode = rs.getString("postalcode");
			Map m = new HashMap<>();
			m.put("id", id);
			m.put("customer_id", customer);
			m.put("telephone",telephone);
			m.put("firstname",firstname);
			m.put("lastname",lastname);
			m.put("address",address);
			m.put("citystate",citystate);
			m.put("country",country);
			m.put("postalcode",postalcode);
			
			
			list.add(m);
		}
		return list;
	}
}
