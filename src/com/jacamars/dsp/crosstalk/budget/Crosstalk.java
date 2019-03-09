package com.jacamars.dsp.crosstalk.budget;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.config.Config;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;

import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.shared.CampaignCache;
import com.jacamars.dsp.rtb.tools.Performance;

public enum Crosstalk {

	INSTANCE;

	protected static final Logger logger = LoggerFactory.getLogger(Crosstalk.class);

	private static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/** The cache to contain the general context info on runing campaigns */
	public static volatile IMap<String, AccountingCampaign> campaigns;

	/** The cache to contain the general context info on runing campaigns */
	public static volatile IMap<String, AccountingCampaign> deletedCampaigns;

	/** The default backup count if you dont set it */
	static public int backupCount = 3;

	/** The default value to read backups, is true */
	static public boolean readBackup = true;
	
	/**
     * The /log in memory queues
     */
    public static final List<Deque<String>> deqeues = new ArrayList<Deque<String>>();

	public static Crosstalk getInstance() {
		if (campaigns != null)
			return INSTANCE;
		
		// Start the connection to elastic search
		BudgetController.getInstance();

		Config config = RTBServer.getSharedInstance().getConfig();
		String name = "deletedCampaigns";
		deletedCampaigns = RTBServer.getSharedInstance().getMap(name);
		config.getMapConfig(name).setAsyncBackupCount(backupCount).setReadBackupData(readBackup);
		
		name = "accountingCampaigns";
		campaigns = RTBServer.getSharedInstance().getMap(name);
		config.getMapConfig(name).setAsyncBackupCount(backupCount).setReadBackupData(readBackup);

		ScheduledExecutorService execService = Executors.newScheduledThreadPool(1);
		execService.scheduleAtFixedRate(() -> {
			try {
				updateBudgets();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}, 0L, 1L, TimeUnit.MINUTES);
		
		return INSTANCE;

	}

	static void updateBudgets() {
		if (! RTBServer.isLeader())
			return;
		
		logger.info("CROSSTALK budgeting has started");
		
		campaigns.entrySet().forEach(e->{
			e.getValue().runUsingElk();
		});
		
		
		logger.info("CROSSTALK budgeting has completed");
	}
	
	////////////////////////////////////
	
	/**
	 * Add a single campaign back into the system (usually from the API.
	 * 
	 * @param campaign
	 *            String. The campaign id.
	 * @return String. The response to send back.
	 * @throws Exception
	 *             on SQL errors.
	 */
	public String add(String json) throws Exception {
		Campaign c = new Campaign(json);
		return update(c,true);

		
	}

	public List<String> deleteCampaign(String campaign) throws Exception {
		AccountingCampaign c = getKnownCampaign(campaign);
		c.setStatus("offline");
		parkCampaign(c);	
		return null;
	}
	
	/**
	 * Park a campaign. This causes it to get unloaded from the bidders
	 * @param camp Campaign. The campaign to park.
	 * @return boolean. Returns true.
	 * @throws Exception if there was an error.
	 */
	boolean parkCampaign(AccountingCampaign camp) throws Exception {
		if (camp.campaign == null || deletedCampaigns.get(camp.campaign.adId) != null)
			return false;

		CampaignCache.getClientInstance(RTBServer.getSharedInstance()).deleteCampaign(camp.campaign.adId);
		deletedCampaigns.put(camp.campaign.adId, camp); // add to the deleted campaigns map
		campaigns.remove(camp);							// remove from the campaigns set, used on refresh
		return true;
	}
	
	/**
	 * Return a list of all the deleted (parked) campaigns
	 * 
	 * @return List. The deleted campaign.
	 */
	public List<AccountingCampaign> getDeletedCampaigns() {
		List<AccountingCampaign> list = new ArrayList<AccountingCampaign>();
		for (Map.Entry<String, AccountingCampaign> entry : deletedCampaigns.entrySet()) {
			list.add(entry.getValue());
		}
		return list;
	}

	public AccountingCampaign getKnownCampaign(String id) {
		AccountingCampaign camp = deletedCampaigns.get(id);
		if (camp != null)
			return camp;

		camp = campaigns.get(id);
		return camp;
	}
	
	public String update(Campaign campaign, boolean add) throws Exception {
		String msg = null;
		
		AccountingCampaign c = getKnownCampaign(campaign.adId);

		// New campaign
		if (c == null) {
			c = makeNewCampaign(campaign);
			if (c.isActive()) {
				campaigns.put(campaign.adId,c);
				Configuration.getInstance().addCampaign(campaign);
				c.runUsingElk();
				logger.info("New campaign {} going active",campaign);
				msg = "NEW CAMPAIGN GOING ACTIVE: " + campaign;
			} else {
				logger.info("New campaign is inactive {}, reason: {}", campaign, c.report());
				deleteCampaign(campaign.adId);
			}
		} else {
			// A previously known campaign is updated.
			c.update(campaign);
			if (c.isActive()) {
				logger.info("Previously inactive campaign going active: {}",campaign);
				if (deletedCampaigns.get(campaign) != null) {
					deletedCampaigns.remove(campaign);
				}
				msg = "CAMPAIGN GOING ACTIVE: " + campaign;
				try {
					c.addToRTB(); // notifies the bidder
				} catch (Exception err) {
					logger.error("Failed to load campaign {} into bidders, reason: {}", c.campaignid,err.toString());
				}
			} else {
				logger.info("New campaign going inactive:{}, reason: {}", campaign, c.report());
				msg = "CAMPAIGN GOING INACTIVE: " + campaign + ", reason: " + c.report();
				deleteCampaign(campaign.adId);
			}
		}

		return msg;
	}
	
	public AccountingCampaign makeNewCampaign(Campaign node) throws Exception {
		AccountingCampaign ac = new AccountingCampaign(node);
		return ac;
		
	}
	
	/**
	 * Update a command, note retrieves the campaign from the SQL database,
	 * 
	 * @param campaign
	 *            String.
	 * @return String. The message on return.
	 * @throws Exception
	 *             on SQL errors.
	 */
	public String update(String json) throws Exception {
		Campaign x = new Campaign(json);
		
		AccountingCampaign c = getKnownCampaign(x.adId);
		if (c == null) {
			throw new Exception("No such campaign: " + x.adId);
		}
		return update(x,true);
	}
	
}
