package com.jacamars.dsp.crosstalk.budget;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.shared.CampaignCache;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ben on 7/17/17.
 */

public class CampaignBuilderWorker implements Runnable {

	/** Logging object */
	static final Logger logger = LoggerFactory.getLogger(CampaignBuilderWorker.class);
	private JsonNode jnode;
	private String msg;
	private String campaign;
	private Campaign c;

	public CampaignBuilderWorker(JsonNode jnode) {
		this.jnode = jnode;
		campaign = jnode.get("id").asText();
	}
	
	public CampaignBuilderWorker(Campaign c) {
		campaign = "" +  c.id;
		this.c = c;
	}
 
	@Override
	public void run() {
		msg = "No change required for campaign: " + campaign;
		try {
			ObjectNode node = (ObjectNode) jnode;
			Campaign check = Crosstalk.getInstance().getKnownCampaign(campaign);   // check is the old one.
			if (c == null) {                                                         // c is the new one.
				c = new Campaign(node);
			}
			c.runUsingElk();
			if (c.isActive() && c.isRunnable())
				Crosstalk.getInstance().shadow.add(c);
			else
				Crosstalk.getInstance().shadow.remove(c);
			
			// Is this a known campaign? Null is not known
			if (check == null) {
				// Ok, it's not known, is the new one ready to run.
				if (c.isRunnable() && c.isActive()) {
					logger.info("New campaign {} going active", campaign);
					msg = "New campaign going active: " + campaign;
					Crosstalk.getInstance().shadow.add(c);
					Crosstalk.signaler.addString("load " + c.id);
					return;
				} else {
					logger.info("New campaign {} is not going active, reason: {}", campaign, c.report());
				}
			} else {
				if (c.isRunnable() && c.isActive()) {
					Crosstalk.getInstance().shadow.add(c);
					if (check.isRunnable() && check.isActive()) {
						logger.info("Previous running campaign {}, changed but is active", campaign);
						msg = "New campaign going active: " + campaign;
						Crosstalk.signaler.addString("load " + c.id);
						return;
					} else {
						logger.info("Previous paused campaign {}, is now active", campaign);
						msg = "New campaign going active: " + campaign;
						Crosstalk.signaler.addString("load " + c.id);
						return;
					}
				} else {
					if (check.isRunnable() && check.isActive()) {
						logger.info("Previous running campaign {}, is now inactive, reason: {}", campaign,c.report());
						msg = "Campaign going inactive: " + campaign;
						Crosstalk.getInstance().parkCampaign(c);
						Crosstalk.getInstance().deletedCampaigns.remove(campaign);
						Crosstalk.signaler.addString("unload " + c.id);
						Crosstalk.getInstance().shadow.remove(c);
						return;
					} else {
						logger.info("Previous paused campaign {}, changed, but is still inactive, reason: {}", campaign,c.report());
						msg = "New campaign going inactive: " + campaign;
						Crosstalk.getInstance().parkCampaign(c);
						Crosstalk.signaler.addString("unload " + c.id);
						return;
					}
				}
			}
			
		} catch (Exception error) {
			error.printStackTrace();
			msg = "Error creating campaign: " + campaign + ", error: "+ error.toString();
			logger.error("Error creating campaign: {}", error.toString());
		}

	}
	
	public static boolean checkUpdateTime(Campaign c) throws Exception {
		var rs = CrosstalkConfig.getInstance().getStatement().executeQuery("select updated_at from campaigns where id="+c.id);
		if (rs.next()) {
			var ts = rs.getTimestamp(1);
			if (ts.getTime() == c.updated_at)
				return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return msg;
	}
}
