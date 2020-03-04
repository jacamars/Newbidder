package com.jacamars.dsp.crosstalk.api;


import java.sql.ResultSet;
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
public class SQLListCampaigns extends ApiCommand {

	/** The list of deletions/updates */
	public List<Map> campaigns;

	/**
	 * Default constructor
	 */
	public SQLListCampaigns() {

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
					select = "select * from campaigns";
				else
					select = "select * from campaigns where customer_id='"+tokenData.customer+"'";
				
				var conn = CrosstalkConfig.getInstance().getConnection();
				var stmt = conn.createStatement();
				var prep = conn.prepareStatement(select);
				ResultSet rs = prep.executeQuery();
				
				campaigns = new ArrayList<>();
				while(rs.next()) {
					int id = rs.getInt("id");
					String name = rs.getString("name");
					String status = rs.getString("status");
					if (status == null)
						status = "offline";
					Map m= new HashMap();
					m.put("id", id);
					m.put("name", name);
					m.put("status", status);
					campaigns.add(m);
				}
				
				return;
			} catch (Exception err) {
				err.printStackTrace();
				error = true;
				message = err.toString();
			}
			message = "Timed out";
		}
}
