package com.jacamars.dsp.crosstalk.budget;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
import com.hazelcast.config.Config;
import com.hazelcast.core.IMap;

import com.jacamars.dsp.crosstalk.api.ResultSetToJSON;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.shared.CampaignCache;
import com.jacamars.dsp.rtb.tools.DbTools;

public enum Crosstalk {

	INSTANCE;

	protected static final Logger logger = LoggerFactory.getLogger(Crosstalk.class);

	private static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/** The cache to contain the general context info on runing campaigns */
	public static volatile IMap<String, Campaign> campaigns;

	/** The cache to contain the general context info on runing campaigns */
	public static volatile IMap<String, Campaign> deletedCampaigns;
	
	/**
	 * creatives in a map, for fast lookup on win notifications and rollups.
	 * Note creatives are not deleted
	 */
	public static volatile Map<String, Creative> creativesMap = new ConcurrentHashMap<String, Creative>();


	/** The default backup count if you dont set it */
	static public int backupCount = 3;

	/** The default value to read backups, is true */
	static public boolean readBackup = true;
	
	public static Map<Integer, JsonNode> globalRtbSpecification;
	public static ArrayNode campaignRtbStd;
	public static ArrayNode bannerRtbStd;
	public static ArrayNode videoRtbStd;
	public static ArrayNode exchangeAttributes;
	
	/**
	 * The list of RTB rules not specified in campaigns, creatives and targets.
	 */
	public static final String RTB_STD = "rtb_standards";
	public static final String CAMP_RTB_STD = "campaigns_rtb_standards";
	public static final String BANNER_RTB_STD = "banners_rtb_standards";
	public static final String VIDEO_RTB_STD = "banner_videos_rtb_standards";
	
	
	static ResultSet rs;
	/**
     * The /log in memory queues
     */
    public static final List<Deque<String>> deqeues = new ArrayList<Deque<String>>();

	public static Crosstalk getInstance() throws Exception {
		if (campaigns != null)
			return INSTANCE;
		try {
			initialize();
		} catch (Exception error) {
			tryCreate();
		}
		
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
	
	static void tryCreate() throws Exception {
		var conn = CrosstalkConfig.getInstance().getConnection();
		var stmt = conn.createStatement();
		String content = new String(Files.readAllBytes(Paths.get("data/postgres/banner_videos.sql")), StandardCharsets.UTF_8);
		stmt.execute(content);
		content = new String(Files.readAllBytes(Paths.get("data/postgres/banners_rtb_standard.sql")), StandardCharsets.UTF_8);
		stmt.execute(content);
		content = new String(Files.readAllBytes(Paths.get("data/postgres/banners.sql")), StandardCharsets.UTF_8);
		stmt.execute(content);
		content = new String(Files.readAllBytes(Paths.get("data/postgres/campaigns_rtb_standard.sql")), StandardCharsets.UTF_8);
		stmt.execute(content);
		content = new String(Files.readAllBytes(Paths.get("data/postgres/campaigns.sql")), StandardCharsets.UTF_8);
		stmt.execute(content);
		content = new String(Files.readAllBytes(Paths.get("data/postgres/rtb_standards.sql")), StandardCharsets.UTF_8);
		stmt.execute(content);
		content = new String(Files.readAllBytes(Paths.get("data/postgres/exchange_attributes.sql")), StandardCharsets.UTF_8);
		stmt.execute(content);
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
	
	static void initialize() throws Exception {
		// /////////////////////////// GLOBAL rtb_spec
		globalRtbSpecification = new HashMap<Integer, JsonNode>();
		rs = CrosstalkConfig.getInstance().getStatement().executeQuery("select * from " + RTB_STD);
		ArrayNode std = ResultSetToJSON.convert(rs);
		Iterator<JsonNode> it = std.iterator();
		while (it.hasNext()) {
			JsonNode child = it.next();
			globalRtbSpecification.put(child.get("id").asInt(), child);
		}

		campaignRtbStd = ResultSetToJSON.factory.arrayNode();
		rs = CrosstalkConfig.getInstance().getStatement().executeQuery("select * from " + CAMP_RTB_STD);
		std = ResultSetToJSON.convert(rs);
		it = std.iterator();
		while (it.hasNext()) {
			JsonNode child = it.next();
			campaignRtbStd.add(child);
		}

		bannerRtbStd = ResultSetToJSON.factory.arrayNode();
		rs = CrosstalkConfig.getInstance().getStatement().executeQuery("select * from " + BANNER_RTB_STD);
		std = ResultSetToJSON.convert(rs);
		it = std.iterator();
		while (it.hasNext()) {
			JsonNode child = it.next();
			bannerRtbStd.add(child);
		}

		videoRtbStd = ResultSetToJSON.factory.arrayNode();
		rs = CrosstalkConfig.getInstance().getStatement().executeQuery("select * from " + VIDEO_RTB_STD);
		std = ResultSetToJSON.convert(rs);
		it = std.iterator();
		while (it.hasNext()) {
			JsonNode child = it.next();
			videoRtbStd.add(child);
		}

		exchangeAttributes = ResultSetToJSON.factory.arrayNode();
		rs = CrosstalkConfig.getInstance().getStatement().executeQuery("select * from exchange_attributes");
		std = ResultSetToJSON.convert(rs);
		it = std.iterator();
		while (it.hasNext()) {
			JsonNode child = it.next();
			exchangeAttributes.add(child);
		}
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
		Campaign c = getKnownCampaign(campaign);
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
	boolean parkCampaign(Campaign camp) throws Exception {
		if (camp == null || deletedCampaigns.get(camp.adId) != null)
			return false;

		CampaignCache.getClientInstance(RTBServer.getSharedInstance()).deleteCampaign(camp.adId);
		deletedCampaigns.put(camp.adId, camp); // add to the deleted campaigns map
		campaigns.remove(camp);							// remove from the campaigns set, used on refresh
		return true;
	}
	
	/**
	 * Return a list of all the deleted (parked) campaigns
	 * 
	 * @return List. The deleted campaign.
	 */
	public List<Campaign> getDeletedCampaigns() {
		List<Campaign> list = new ArrayList<Campaign>();
		for (Map.Entry<String, Campaign> entry : deletedCampaigns.entrySet()) {
			list.add(entry.getValue());
		}
		return list;
	}

	public Campaign getKnownCampaign(String id) {
		Campaign camp = deletedCampaigns.get(id);
		if (camp != null)
			return camp;

		camp = campaigns.get(id);
		return camp;
	}
	
	public String update(Campaign campaign, boolean add) throws Exception {
		String msg = null;
		
		Campaign c = getKnownCampaign(campaign.adId);

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
					logger.error("Failed to load campaign {} into bidders, reason: {}", c.adId,err.toString());
				}
			} else {
				logger.info("New campaign going inactive:{}, reason: {}", campaign, c.report());
				msg = "CAMPAIGN GOING INACTIVE: " + campaign + ", reason: " + c.report();
				deleteCampaign(campaign.adId);
			}
		}

		return msg;
	}
	
	public Campaign makeNewCampaign(Campaign node) throws Exception {
		String str = DbTools.mapper.writeValueAsString(node);
		Campaign ac = new Campaign(str);
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
		
		Campaign c = getKnownCampaign(x.adId);
		if (c == null) {
			throw new Exception("No such campaign: " + x.adId);
		}
		return update(x,true);
	}
	
}
