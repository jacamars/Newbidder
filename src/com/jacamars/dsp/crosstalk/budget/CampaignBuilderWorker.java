package com.jacamars.dsp.crosstalk.budget;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jacamars.dsp.rtb.common.Campaign;

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

	public CampaignBuilderWorker(JsonNode jnode) {
		this.jnode = jnode;
	}
 
	@Override
	public void run() {
		String campaign = jnode.get("id").asText();
		msg = "No change required for campaign: " + campaign;
		try {
			ObjectNode node = (ObjectNode) jnode;

			Campaign c = Crosstalk.getInstance().getKnownCampaign(campaign);

			if (c == null && node == null) {
				msg = "Campaign is unknown; " + campaign;
				throw new Exception("Campaign is unknown: " + campaign);
			}

			// New campaign
			if (c == null && node != null) {
				c = Crosstalk.getInstance().makeNewCampaign(node);
				c.runUsingElk();
				if (c.isActive()) {
					logger.info("New campaign {} going active", campaign);
					msg = "New campaign going active: " + campaign;
					Crosstalk.getInstance().addCampaignToRTB(c);
					Crosstalk.commands.addString(msg);
				} else {
					logger.info("New campaign is inactive {}, reason: {}", campaign, c.report());
					Crosstalk.getInstance().parkCampaign(c); 
				}
			} else if (node == null && c != null) {                 // node is null, but c is already known
				logger.info("Deleting a campaign: {}", campaign);
				msg = "Deleted campaign: " + campaign;
				c.report();
				Crosstalk.getInstance().deletedCampaigns.put(campaign, c);
				Crosstalk.getInstance().parkCampaign(c);
			} else {                                               // both are known
				boolean old = c.isActive();
				boolean updated = false;
				if (c.updated_at != node.get("updated_at").asLong()) {
					c.update(node);
					updated = true;
				}

				if (c.isActive()) {
					if (old == false) {
						logger.info("Previously inactive campaign going active: {}", campaign);
						if (Crosstalk.getInstance().deletedCampaigns.get(campaign) != null) {
							Crosstalk.getInstance().deletedCampaigns.remove(campaign);
							// campaigns.add(c);
						}
						if (updated) {
							logger.info("Active campaign was updated {}", campaign);
							try {
								Crosstalk.getInstance().addCampaignToRTB(c);
							} catch (Exception err) {
								logger.error("Failed to load campaign {} into bidders, reason: {}", c.name,
										err.toString());
								msg = "Failed to load campaign: " + c.name + ", error"+ err.toString();
							}
						}
					} else {
						if (updated) {
							logger.info("Active campaign was updated {}", campaign);
							msg = "Active campaign was updated: " + campaign;
							Crosstalk.getInstance().addCampaignToRTB(c);
						}
						if (old == true && !c.isActive()) {
							logger.info("Campaign going inactive:{}, reason: {}", campaign, c.report());
							msg = "Campaign going inactive: " + campaign + ", reason: " + c.report();
							Crosstalk.getInstance().parkCampaign(c); // notifies the bidder
						}
					}
				} else {
					if (old == false)
						logger.info("Previously inactive campaign updated, but is still inactive:{}, reason: {}", campaign, c.report());
					else
						logger.info("Previously active campaign going inactive:{}, reason: {}", campaign, c.report());
					msg = "Campaign going inactive: " + campaign + ", reason: " + c.report();
					Crosstalk.getInstance().parkCampaign(c); // notifies the bidder
				}
			}
		} catch (Exception error) {
			error.printStackTrace();
			msg = "Error creating campaign: " + campaign + ", error: "+ error.toString();
			logger.error("Error creating campaign: {}", error.toString());
		}

	}

	@Override
	public String toString() {
		return msg;
	}
}
