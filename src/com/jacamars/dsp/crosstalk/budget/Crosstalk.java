package com.jacamars.dsp.crosstalk.budget;

import java.nio.charset.StandardCharsets;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
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
import com.hazelcast.core.IMap;

import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.shared.CampaignCache;
import com.jacamars.dsp.rtb.tools.DbTools;
import com.jacamars.dsp.rtb.tools.JdbcTools;
import com.jacamars.dsp.rtb.tools.Performance;

/**
 * Class that loads,updates,deletes campaigns based on SQL queries. Runs once a minute to (a) handle the budgets and (b) update the
 * campaigns pursuant to SQL.
 * @author Ben M. Faul
 *
 */
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
				INSTANCE.scan();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}, 0L, 1L, TimeUnit.MINUTES);
		
		return INSTANCE;

	}
	
	static void tryCreate() throws Exception {
		var conn = CrosstalkConfig.getInstance().getConnection();
		if (conn == null)
			throw new Exception("Crosstalk connection was not established");
		
		var stmt = conn.createStatement();
		String content = new String(Files.readAllBytes(Paths.get("data/postgres/banner_videos.sql")), StandardCharsets.UTF_8);
		stmt.execute(content);
		content = new String(Files.readAllBytes(Paths.get("data/postgres/banners_rtb_standards.sql")), StandardCharsets.UTF_8);
		stmt.execute(content);
		content = new String(Files.readAllBytes(Paths.get("data/postgres/banner_videos_rtb_standards.sql")), StandardCharsets.UTF_8);
		stmt.execute(content);
		content = new String(Files.readAllBytes(Paths.get("data/postgres/banners.sql")), StandardCharsets.UTF_8);
		stmt.execute(content);
		content = new String(Files.readAllBytes(Paths.get("data/postgres/campaigns_rtb_standards.sql")), StandardCharsets.UTF_8);
		stmt.execute(content);
		content = new String(Files.readAllBytes(Paths.get("data/postgres/campaigns.sql")), StandardCharsets.UTF_8);
		stmt.execute(content);
		content = new String(Files.readAllBytes(Paths.get("data/postgres/rtb_standards.sql")), StandardCharsets.UTF_8);
		stmt.execute(content);
		content = new String(Files.readAllBytes(Paths.get("data/postgres/exchange_attributes.sql")), StandardCharsets.UTF_8);
		stmt.execute(content);
		content = new String(Files.readAllBytes(Paths.get("data/postgres/targets.sql")), StandardCharsets.UTF_8);
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
		ArrayNode std = JdbcTools.convertToJson(rs);
		Iterator<JsonNode> it = std.iterator();
		while (it.hasNext()) {
			JsonNode child = it.next();
			globalRtbSpecification.put(child.get("id").asInt(), child);
		}

		campaignRtbStd = JdbcTools.factory.arrayNode();
		rs = CrosstalkConfig.getInstance().getStatement().executeQuery("select * from " + CAMP_RTB_STD);
		std = JdbcTools.convertToJson(rs);
		it = std.iterator();
		while (it.hasNext()) {
			JsonNode child = it.next();
			campaignRtbStd.add(child);
		}

		bannerRtbStd = JdbcTools.factory.arrayNode();
		rs = CrosstalkConfig.getInstance().getStatement().executeQuery("select * from " + BANNER_RTB_STD);
		std = JdbcTools.convertToJson(rs);
		it = std.iterator();
		while (it.hasNext()) {
			JsonNode child = it.next();
			bannerRtbStd.add(child);
		}

		videoRtbStd = JdbcTools.factory.arrayNode();
		rs = CrosstalkConfig.getInstance().getStatement().executeQuery("select * from " + VIDEO_RTB_STD);
		std = JdbcTools.convertToJson(rs);
		it = std.iterator();
		while (it.hasNext()) {
			JsonNode child = it.next();
			videoRtbStd.add(child);
		}

		exchangeAttributes = JdbcTools.factory.arrayNode();
		rs = CrosstalkConfig.getInstance().getStatement().executeQuery("select * from exchange_attributes");
		std = JdbcTools.convertToJson(rs);
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

		campaigns.delete(camp.adId);
		deletedCampaigns.put(camp.adId, camp); // add to the deleted campaigns map

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
				addCampaignToRTB(c);
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
	
	public Campaign makeNewCampaign(ObjectNode node) throws Exception {
		return new Campaign(node);
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
	
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public ArrayNode createJson() throws Exception {
		Date now = new Date();
		Timestamp update = new Timestamp(now.getTime());

		Configuration config = Configuration.getInstance();
		//String select = "select * from campaigns where  status = 'runnable' and activate_time <= ? and expire_time > ?";
		String select = "select * from campaigns where status='runnable'";

		PreparedStatement prep;

		var conn = CrosstalkConfig.getInstance().getConnection();
		var stmt = conn.createStatement();
		prep = conn.prepareStatement(select);
	
		ResultSet rs = prep.executeQuery();
		ArrayNode nodes = JdbcTools.convertToJson(rs);
		handleNodes(nodes);

		return nodes;
	}

	public ArrayNode createJson(String id) throws Exception {
		Date now = new Date();
		Timestamp update = new Timestamp(now.getTime());

		Configuration config = Configuration.getInstance();

		String select = "select * from campaigns where id = " + id;
		var conn = CrosstalkConfig.getInstance().getConnection();
		var stmt = conn.createStatement();
		var prep = conn.prepareStatement(select);
		ResultSet rs = prep.executeQuery();

		ArrayNode nodes = JdbcTools.convertToJson(rs);
		handleNodes(nodes);

		return nodes;
	}

	/**
	 * Convert SQL tables for campaigns, creatives, target and rtb_standard into JSON object
	 * @param nodes JSON array to hold the results.
	 * @throws Exception on JSON or SQL errors.
	 */
	public static void handleNodes(ArrayNode nodes) throws Exception {

		ResultSet rs;
		var conn = CrosstalkConfig.getInstance().getConnection();
		Configuration config = Configuration.getInstance();
		var stmt = conn.createStatement();
		List<Integer> list = new ArrayList<Integer>();

		for (int i = 0; i < nodes.size(); i++) {
			ObjectNode x = (ObjectNode) nodes.get(i);
			int campaignid = x.get("id").asInt();
			String regions = x.get("regions").asText();
			regions = regions.toLowerCase();
			if (regions.contains(CrosstalkConfig.getInstance().region.toLowerCase())) {
				int targetid = x.get("target_id").asInt();
				rs = stmt.executeQuery("select * from targets where id = " + targetid);
				ArrayNode inner = JdbcTools.convertToJson(rs);
				ObjectNode y = (ObjectNode) inner.get(0);
				x.set("targetting", y);
			} else {
				list.add(i);
			}
		}
		
		//
		// Remove in reverse all that don't belong to my region/
		//
		while (list.size() > 0) {
			Integer x = list.get(list.size() - 1);
			nodes.remove(x);
			list.remove(x);
		}

		if (nodes.size() == 0)
			return;

		// /////////////////////////// GLOBAL rtb_spec
		globalRtbSpecification = new HashMap<Integer, JsonNode>();
		rs = stmt.executeQuery("select * from " + RTB_STD);
		ArrayNode std = JdbcTools.convertToJson(rs);
		Iterator<JsonNode> it = std.iterator();
		while (it.hasNext()) {
			JsonNode child = it.next();
			globalRtbSpecification.put(child.get("id").asInt(), child);
		}

		campaignRtbStd = JdbcTools.factory.arrayNode();
		rs = stmt.executeQuery("select * from " + CAMP_RTB_STD);
		std = JdbcTools.convertToJson(rs);
		it = std.iterator();
		while (it.hasNext()) {
			JsonNode child = it.next();
			campaignRtbStd.add(child);
		}

		bannerRtbStd = JdbcTools.factory.arrayNode();
		rs = stmt.executeQuery("select * from " + BANNER_RTB_STD);
		std = JdbcTools.convertToJson(rs);
		it = std.iterator();
		while (it.hasNext()) {
			JsonNode child = it.next();
			bannerRtbStd.add(child);
		}

		videoRtbStd = JdbcTools.factory.arrayNode();
		rs = stmt.executeQuery("select * from " + VIDEO_RTB_STD);
		std = JdbcTools.convertToJson(rs);
		it = std.iterator();
		while (it.hasNext()) {
			JsonNode child = it.next();
			videoRtbStd.add(child);
		}

		exchangeAttributes = JdbcTools.factory.arrayNode();
		rs = stmt.executeQuery("select * from exchange_attributes");
		std = JdbcTools.convertToJson(rs);
		it = std.iterator();
		while (it.hasNext()) {
			JsonNode child = it.next();
			exchangeAttributes.add(child);
		}
		// ////////////////////////////////////////////////////////////////////////////
		// Banner
		for (int i = 0; i < nodes.size(); i++) {
			ObjectNode x = (ObjectNode) nodes.get(i);
			int campaignid = x.get("id").asInt();
			rs = stmt.executeQuery("select * from banners where campaign_id = " + campaignid);
			ArrayNode inner = JdbcTools.convertToJson(rs);
			x.set("banner", inner);
		}

		// Video
		for (int i = 0; i < nodes.size(); i++) {
			ObjectNode x = (ObjectNode) nodes.get(i);
			int campaignid = x.get("id").asInt(); ///////// CHECK
			rs = stmt.executeQuery("select * from banner_videos where campaign_id = " + campaignid);
			ArrayNode inner = JdbcTools.convertToJson(rs);
			x.set("banner_video", inner);
		}
	}
	
	public void scan() {

			try {
				refresh();
				
				campaigns.entrySet().forEach(e->{
					Campaign camp = e.getValue();
					camp.runUsingElk();
					try {
					if (!camp.isActive()) {
						logger.info("Campaign has become inactive: {}", camp.adId);
						removeFromRTB(camp);
						camp.report();
						parkCampaign(camp);
					} 
					} catch (Exception error) {
						logger.error("Error scanning campaign: {}, error: {}", camp.adId,error.getMessage());
					}
				});
				
				
				List<String> additions = new ArrayList<String>();
				List<String> clist = new ArrayList<String>();
				for (String key : deletedCampaigns.keySet()) {
					Campaign camp = deletedCampaigns.get(key);
					if (camp.isRunnable()) {
						camp.runUsingElk();
						if (camp.isActive()) {
							logger.info("Currently inactive campaign going active: {}", camp.adId);
							addCampaignToRTB(camp);
							additions.add(key);
							try {
								//camp.addToRTB();                                  // one at a time, not good. TBD
								clist.add(key);
							} catch (Exception error) {
								logger.error("Error: Failed to load campaign {} into bidders, reason: {}", camp.adId,error.toString());
							}
						}
					}

				}
				
				for (String key : additions) {
					deletedCampaigns.remove(key);
				}

				logger.info("Heartbeat,runnable campaigns: {}, parked: {}, dailyspend: {} avg-spend-min: {}",
						campaigns.size(),deletedCampaigns.size(),
						BudgetController.getInstance().getCampaignDailySpend(null),
						BudgetController.getInstance().getCampaignSpendAverage(null));
			} catch (Exception error) {
				error.printStackTrace();
				if (error.toString().toLowerCase().contains("sql")) {
					System.err.println("SQL Error, goodbye");
					System.exit(0);
				}
			}
		}
	
	void purge() throws Exception {
		StringBuilder list = new StringBuilder("");  
		List<Campaign> dc = new ArrayList<Campaign>();
		for (String key : deletedCampaigns.keySet()) {
			Campaign c = deletedCampaigns.get(key);
			if (c.canBePurged()) {
				removeFromRTB(c);
				list.append(c.adId + " ");
				dc.add(c);
			}
		}

		for (Campaign c : dc) {
			deletedCampaigns.remove("" + c.adId);
		}

		
		campaigns.entrySet().forEach(e->{
			Campaign c = e.getValue();
			try {
			if (c.canBePurged()) {
				removeFromRTB(c);
				list.append(c.adId + " ");
			}
			} catch (Exception error) {
				logger.error("Error purging campaign: {}: error: {}" + c.adId,error.getMessage());
			}
		});

		if (list.length()>0) {
			logger.info("The following campaigns have been purged: {}",list.toString());
		}
	}

	/**
	 * Remove a campaign from the hazelcast IMap.
	 * @param camp Campaign. The campaign to add.
	 */
	void removeFromRTB(Campaign camp) {
		campaigns.remove(camp.adId);
	}
	
	/**
	 * Add a campaign from the hazelcast IMap.
	 * @param camp Campaign. The campaign to add.
	 */
	void addCampaignToRTB(Campaign camp) {
		campaigns.put(camp.adId,camp);
	}
	
	/**
	 * Refresh the system. Load all bidders with all runnable campaigns.
	 * 
	 * @return List. The bidder list.
	 * @throws Exception
	 *             on SQL or 0MQ errors.
	 */
	public List<String> refresh() throws Exception {
		ArrayNode array = createJson(); // get a copy of the SQL database
		List<String> list = new ArrayList();
		List<CampaignBuilderWorker> workers = new ArrayList();

		long time = System.currentTimeMillis();
	
		if (array.size()==0) {
			logger.info("No campaign updates scheduled to be being sent.");
			return list;
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(array.size());

		logger.info("Sending {} periodic campaign updates, to {} members.",list.size(),workers.size());
		for (JsonNode s : array) {
			CampaignBuilderWorker w = new CampaignBuilderWorker(s);
			executor.execute(w);
			workers.add(w);
		}

		time = System.currentTimeMillis() - time;
		time /= 1000;
		logger.info("Periodic updates took {} seconds",time);
		executor.shutdown();
		while (!executor.isTerminated()) {
			
		}
		
		for (CampaignBuilderWorker w : workers ) {
			list.add(w.toString());
		}
		
		return list;
		
	}

}
