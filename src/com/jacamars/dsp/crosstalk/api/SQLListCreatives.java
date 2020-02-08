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
	public List<Map> creatives;
	
	List<Map> banners;
	List<Map> videos;
	List<Map> natives;
	List<Map> audios;

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
				banners = convertToJson(rs,"banner");	
				
				select = "select * from banner_videos";
				rs = prep.executeQuery();
				videos = convertToJson(rs,"video"); 
			
				select = "select * from banner_natives";
				rs = prep.executeQuery();
				natives = convertToJson(rs,"native"); 
				
				select = "select * from banner_audios";
				rs = prep.executeQuery();
				audios =  convertToJson(rs,"audio");
				
				creatives.addAll(banners);
				creatives.addAll(videos);
				creatives.addAll(audios);
				creatives.addAll(natives);
				
				return;
			} catch (Exception err) {
				error = true;
				message = err.toString();
			}
			message = "Timed out";
		}
	
	List<Map> convertToJson(ResultSet rs, String key) throws Exception {
		List<Map> list = new ArrayList<>();
		while(rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");
			long start = rs.getTimestamp("activate_time").getTime();
			long end = rs.getTimestamp("expire_time").getTime();
			Map m= new HashMap();
			m.put("id", id);
			m.put("name", name);
			m.put("start", start);
			m.put("end", end);
			m.put(type, key);
			list.add(m);
		}
		return list;
	}
}
