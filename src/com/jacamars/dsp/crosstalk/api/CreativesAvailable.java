package com.jacamars.dsp.crosstalk.api;


import com.jacamars.dsp.crosstalk.budget.CrosstalkConfig;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Creative;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Lists campaigns in db
 * Checked for Multi-tenant
 * @author Ben M. Faul
 *
 */
public class CreativesAvailable extends ApiCommand {

	/**
	 * The list of deletions/updates
	 */
	public List<Map> creatives = new ArrayList<>();
	public int id;   // the campaign id
	public String customer_id; // the customer id

	/**
	 * Default constructor
	 */
	public CreativesAvailable() {

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
			
			get("banners");
			get("banner_videos");
			get("banner_audios");
			get("banner_natives");
			
			return;
		} catch (Exception err) {
			err.printStackTrace();
			error = true;
			message = err.toString();
		}
	}

	boolean checkBanners() {
		return false;
	}

	boolean checkVideos() {
		return false;
	}

	boolean checkAudios() {
		return false;
	}

	boolean checkNatives() {
		return false;
	}
	
	void get(String what) throws Exception {
		
		String select;
		if (tokenData.isRtb4FreeSuperUser())
			select = "select * from " + what + " where campaign_id = 0 OR campaign_id = " + id;
		else
			select = "select * from " + what + " where customer_id='" + customer_id + "' AND (campaign_id = 0  OR campaign_id = " + id + ")";

		var conn = CrosstalkConfig.getInstance().getConnection();
		var prep = conn.prepareStatement(select);
		ResultSet rs = prep.executeQuery();

		List<Creative> x = Creative.getInstances(rs);
		x.forEach(c->{
			if (customer_id.equals(c.customer_id)) { // dont let rtb4free include other customer creative
				Map m = new HashMap();
				m.put("name",c.name);
				m.put("id",c.id);
				m.put("campaign_id",c.campaign_id);
				m.put("type",c.type);
				creatives.add(m);
			}
		});

	}
}
