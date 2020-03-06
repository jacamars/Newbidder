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
			creatives = new ArrayList<>();
			try {
				String select, selectbv, selectn, selecta;
				if (tokenData.isRtb4FreeSuperUser()) {
					select = "select * from banners";
					selectbv = "select * from banner_videos";
					selectn = "select * from banner_natives";
					selecta = "select * from banner_audios";
				} else {
					select = "select * from banners where customer_id='"+tokenData.customer + "'";
					selectbv = "select * from banner_videos where customer_id='"+tokenData.customer + "'";
					selectn = "select * from banner_natives where customer_id='"+tokenData.customer + "'";
					selecta = "select * from banner_audios where customer_id='"+tokenData.customer + "'";
				}
				var conn = CrosstalkConfig.getInstance().getConnection();
				var prep = conn.prepareStatement(select);
				ResultSet rs = prep.executeQuery();	
				banners = convertToJson(rs,"banner");	
				
				rs = conn.prepareStatement(selectbv).executeQuery();
				videos = convertToJson(rs,"video"); 
			
				rs = conn.prepareStatement(selectn).executeQuery();
				natives = convertToJson(rs,"native"); 
				
				rs = conn.prepareStatement(selecta).executeQuery();
				audios =  convertToJson(rs,"audio");
				
				creatives.addAll(banners);
				creatives.addAll(videos);
				creatives.addAll(audios);
				creatives.addAll(natives);
				
				return;
			} catch (Exception err) {
				err.printStackTrace();
				error = true;
				message = err.toString();
			}
		}
	
	List<Map> convertToJson(ResultSet rs, String key) throws Exception {
		List<Map> list = new ArrayList<>();
		while(rs.next()) {
			int id = rs.getInt("id");
			String name = rs.getString("name");
			long start = rs.getTimestamp("interval_start").getTime();
			long end = rs.getTimestamp("interval_end").getTime();
			Map m= new HashMap();
			m.put("id", id);
			m.put("name", name);
			m.put("start", start);
			m.put("end", end);
			m.put("type", key);
			
			if (tokenData.isRtb4FreeSuperUser()) {
				m.put("customer_id", rs.getString("customer_id"));		
			}
			list.add(m);
		}
		return list;
	}
}
