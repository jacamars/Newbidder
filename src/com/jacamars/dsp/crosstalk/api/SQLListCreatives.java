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
public class SQLListCreatives extends ApiCommand {

	/** The list of deletions/updates */
	public List<Map> banners;
	public List<Map> videos;
	public List<Map> natives;
	public List<Map> audios;

	/**
	 * Default constructor
	 */
	public SQLListCreatives() {

	}

	/**
	 * Deletes a campaign from the bidders.
	 * 
	 * @param username
	 *            String. User authorization for command.
	 * @param password
	 *            String. Password authorization for command.
	 */
	public SQLListCreatives(String username, String password) {
		super(username, password);
		type = SQLLIST_CREATIVES;
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
	public SQLListCreatives(String username, String password, String target) {
		super(username, password);
		campaign = target;
		type = SQLLIST_CREATIVES;
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
				String select = "select * from banners";
				var conn = CrosstalkConfig.getInstance().getConnection();
				var stmt = conn.createStatement();
				var prep = conn.prepareStatement(select);
				ResultSet rs = prep.executeQuery();
				
				banners = convertToJson(rs); 
				
				select = "select * from banner_videos";
				rs = prep.executeQuery();
				videos = convertToJson(rs); 
			
				natives = new ArrayList<>();
				audios = new ArrayList<>();
				
				
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
			Timestamp start = rs.getTimestamp("interval_start");
			Timestamp stop = rs.getTimestamp("interval_end");
			int campaign_id = rs.getInt("campaign_id");
			Map m= new HashMap();
			m.put("id", id);
			m.put("name", name);
			m.put("campaign_id", id);
			m.put("start", start.getTime());
			m.put("end", stop.getTime());
			list.add(m);
		}
		return list;
	}
}
