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
				c.runUsingElk();
			}
			
			// Is this a known campaign? Null is not known
			if (check == null) {
				// Ok, it's not known, is the new one ready to run.
				if (c.isRunnable() && c.isActive()) {
					logger.info("New campaign {} going active", campaign);
					msg = "New campaign going active: " + campaign;
					CampaignCache.getInstance().addCampaign(c);
					Crosstalk.signaler.addString("load " + c.id);
					return;
				} else {
					logger.info("New campaign {} is not going active, reason: {}", campaign, c.report());
				}
			} else {
				if (c.isRunnable() && c.isActive()) {
					if (check.isRunnable() && check.isActive()) {
						logger.info("Previous running campaign {}, changed but is active", campaign);
						msg = "New campaign going active: " + campaign;
						CampaignCache.getInstance().addCampaign(c);
						Crosstalk.signaler.addString("load " + c.id);
						return;
					} else {
						logger.info("Previous paused campaign {}, is now active", campaign);
						msg = "New campaign going active: " + campaign;
						CampaignCache.getInstance().addCampaign(c);
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
			
		
/*			if (check == null && (c == null && jnode == null)) {
				msg = "Campaign is unknown; " + campaign;
				throw new Exception("Campaign is unknown: " + campaign);
			}
			
			if (c == null)
				c = new Campaign(node);

			// New campaign
			if (check == null) {
				c.runUsingElk();
				if (c.isActive()) {
					logger.info("New campaign {} going active", campaign);
					msg = "New campaign going active: " + campaign;
					CampaignCache.getInstance().addCampaign(c);
					Crosstalk.signaler.addString("load " + c.id);
				} else {
					logger.info("New campaign is inactive {}, reason: {}", campaign, c.report());
					Crosstalk.getInstance().parkCampaign(c); 
				}
			} else if (check == null && c != null) {                 // node is null, but c is already known
				logger.info("Deleting a campaign: {}", campaign);
				msg = "Deleted campaign: " + campaign;
				c.report();
				Crosstalk.getInstance().deletedCampaigns.put(campaign, c);
				Crosstalk.getInstance().parkCampaign(c);
				Crosstalk.signaler.addString("unload " + c.id);
			} else {                                               // both are known
				boolean old = check.isActive();
				boolean updated = checkUpdateTime(c);			
				if (c.isActive()) {
					if (old == false) {
						logger.info("Previously inactive campaign going active: {}", campaign);
						if (Crosstalk.getInstance().deletedCampaigns.get(campaign) != null) {
							Crosstalk.getInstance().deletedCampaigns.remove(campaign);
							Crosstalk.signaler.addString("load " + c.id);
							// campaigns.add(c);
						}
						if (updated) {
							logger.info("Active campaign was updated {}", campaign);
							try {
								CampaignCache.getInstance().addCampaign(c);
								Crosstalk.signaler.addString("load " + c.id);
							} catch (Exception err) {
								logger.error("Failed to load campaign {} into bidders, reason: {}", c.name,
										err.toString());
								msg = "Failed to load campaign: " + c.name + ", error"+ err.toString();
							}
						}
					} else {
						if (updated) { 
							if (c.isActive()) {
								logger.info("Active campaign was updated {}", campaign);
								msg = "Active campaign was updated: " + campaign;
								CampaignCache.getInstance().addCampaign(c);
								Crosstalk.signaler.addString("load " + c.id);
								Crosstalk.getInstance().setKnownCampaign(c);
							} else {
								logger.info("Active campaign was updated {}, and will now go offline", campaign);
								msg = "Active campaign was updated, but is now going offline: " + campaign;
								Crosstalk.signaler.addString("unload " + c.id);
								Crosstalk.getInstance().setKnownCampaign(c);
							}
						}
						if (old == true && !c.isActive()) {
							logger.info("Campaign going inactive:{}, reason: {}", campaign, c.report());
							msg = "Campaign going inactive: " + campaign + ", reason: " + c.report();
							Crosstalk.getInstance().parkCampaign(c); // notifies the bidder
							Crosstalk.signaler.addString("unload " + c.id);
						}
					}
				} else {
					if (old == false) {
						if (c.isRunnable()) {
							if (c.isActive()) {
								logger.info("Previously active campaign, modified, but still active:{}", campaign);
								msg = "Previously active campaign, modified, but still active:" +campaign;
								CampaignCache.getInstance().addCampaign(c);
								Crosstalk.signaler.addString("load " + c.id);
							}  else {
								logger.info("Previously inactive campaign updated, but is still inactive:{}, reason: {}", campaign, c.report());
								Crosstalk.signaler.addString("unload " + c.id);
							}
						}
					}
					else {
						logger.info("Previously active campaign going inactive:{}, reason: {}", campaign, c.report());
						msg = "Campaign going inactive: " + campaign + ", reason: " + c.report();
						Crosstalk.getInstance().parkCampaign(c); // notifies the bidder
						Crosstalk.signaler.addString("unload " + c.id);
					}
				}
			} */
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
System.out.println("NEW" + ts.getTime() + " OLD: " + c.updated_at);
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
