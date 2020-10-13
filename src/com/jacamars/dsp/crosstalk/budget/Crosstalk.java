package com.jacamars.dsp.crosstalk.budget;

import java.io.IOException;




import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import com.hazelcast.map.IMap;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.jmq.EventIF;
import com.jacamars.dsp.rtb.jmq.Subscriber;
import com.jacamars.dsp.rtb.jmq.ZPublisher;
import com.jacamars.dsp.rtb.shared.BidCachePool;
import com.jacamars.dsp.rtb.shared.CampaignCache;
import com.jacamars.dsp.rtb.shared.TokenData;
import com.jacamars.dsp.rtb.tools.DbTools;
import com.jacamars.dsp.rtb.tools.JdbcTools;

import com.jacamars.dsp.rtb.shared.AccountingCache;

/**
 * Class that loads,updates,deletes campaigns based on SQL queries. Runs once a
 * minute to (a) handle the budgets and (b) update the campaigns pursuant to
 * SQL.
 * 
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

	/** The cache to contain the general context info on running campaigns */
	public static Shadow shadow;
//	public static volatile IMap<String, Campaign> campaigns;
//	public static volatile Map<String, Campaign> scampaigns = new HashMap<>();

	/** The cache to contain the general context info on runing campaigns */
	public static volatile IMap<String, Campaign> deletedCampaigns;

	/** The default backup count if you dont set it */
	static public int backupCount = 3;

	/** The default value to read backups, is true */
	static public boolean readBackup = true;

	public static Map<Integer, JsonNode> globalRtbSpecification;
	public static ArrayNode exchangeAttributes = JdbcTools.factory.arrayNode();
	
	

	/**
	 * The list of RTB rules not specified in campaigns, creatives and targets.
	 */
	public static final String RTB_STD = "rtb_standards";

	protected static ZPublisher signaler;
	protected static Subscriber signals;

	static ResultSet rs;

	public static String info = "";
	
	public static final String CAMPAIGNS_KEY = "CONTEXT"; // "accountingCampaigns";
	public static final String DELETED_CAMPAIGNS_KEY = "deletedCampaigns";
	/**
	 * The /log in memory queues
	 */
	public static final List<Deque<String>> deqeues = new ArrayList<Deque<String>>();
	
	
	volatile int day;
	volatile int hour;
	Integer nowDay;
	Integer nowHour;

	public static Crosstalk getInstance() throws Exception {
		if (shadow != null)
			return INSTANCE;
		try {
			initialize();
		} catch (Exception error) {
			tryCreate();
		}

		// Start the connection to elastic search
		//BudgetController.getInstance(CrosstalkConfig.elk);
		
		Config config = RTBServer.getSharedInstance().getConfig();
		deletedCampaigns = RTBServer.getSharedInstance().getMap(DELETED_CAMPAIGNS_KEY);
		config.getMapConfig(DELETED_CAMPAIGNS_KEY).setAsyncBackupCount(backupCount).setReadBackupData(readBackup);

		shadow = new Shadow();

		
		config.getMapConfig(CAMPAIGNS_KEY).setAsyncBackupCount(backupCount).setReadBackupData(readBackup);
		
		signaler = new ZPublisher(RTBServer.getSharedInstance(), "hazelcast://topic=rtbcommands");
		signals = new Subscriber(RTBServer.getSharedInstance(), new Controller(), "hazelcast://topic=rtbcommands");

		CampaignCache.getInstance();
		
		if (RTBServer.isLeader())
			INSTANCE.refresh(); // Load campaigns.
		else {
			shadow.refresh();
			shadow.getCampaigns().forEach(c->{
				logger.info("*** Loaded Shared Campaign: {} - {}",c.id,c.name);
			});
		}
		
		ScheduledExecutorService execService = Executors.newScheduledThreadPool(1);
		execService.scheduleAtFixedRate(() -> {
			try {
				if (RTBServer.isLeader()) {
					
					updateBudgets();
					INSTANCE.scan();
					
				}
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
			throw new Exception("Crosstalk database connection was not established");

		List<String> specs = Arrays.asList("sql/create/banner_videos.sql", "sql/create/banners_rtb_standards.sql",
				"sql/create/banner_videos_rtb_standards.sql", "sql/create/banners.sql",
				"sql/create/campaigns_rtb_standards.sql", "sql/create/campaigns.sql",
				"sql/create/rtb_standards.sql", "sql/create/exchange_attributes.sql", "sql/create/targets.sql",
				"sql/create/banner_audios.sql", "sql/create/banner_natives.sql","sql/create/users.sql",
				"sql/create/companies.sql","sql/create/cande.sql");

		final var stmt = conn.createStatement();
		specs.stream().forEach(e -> {
			try {
				stmt.execute(new String(Files.readAllBytes(Paths.get(e)), StandardCharsets.UTF_8));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

	}

	static void updateBudgets() {
		if (!RTBServer.isLeader())
			return;
		shadow.runUsingElk();
	}

	static void initialize() throws Exception {
		rs = CrosstalkConfig.getInstance().getStatement().executeQuery("select * from banners");
	}

	////////////////////////////////////

	/**
	 * Add a single campaign back into the system (usually from the API.
	 * 
	 * @param campaign String. The campaign id.
	 * @return String. The response to send back.
	 * @throws Exception on SQL errors.
	 */
	public String add(String json) throws Exception {
		Campaign c = new Campaign(json);
		return update(c, true);

	}

	public List<String> deleteCampaign(String campaign, TokenData td) throws Exception {
		Campaign c = getKnownCampaign(campaign);
		if (td != null && td.isAuthorized(c.customer_id)==false)
			return null;
		
		c.setStatus("offline");
		parkCampaign(c);
		return null;
	}

	/**
	 * Park a campaign. This causes it to get unloaded from the bidders
	 * 
	 * @param camp Campaign. The campaign to park.
	 * @return boolean. Returns true.
	 * @throws Exception if there was an error.
	 */
	boolean parkCampaign(Campaign camp) throws Exception {
		if (camp == null || deletedCampaigns.get("" + camp.id) != null)
			return false;

		shadow.delete("" + camp.id);
		deletedCampaigns.put("" + camp.id, camp); // add to the deleted campaigns map

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

		camp = shadow.get(id);
		return camp;
	}

	public void setKnownCampaign(Campaign c) {
		if (deletedCampaigns.get(c.id) != null)
			deletedCampaigns.set("" + c.id, c);
	}
	
	void addCampaign(Campaign c) {
		shadow.add(c);
	}

	public String update(Campaign campaign, boolean add) throws Exception {
		String msg = null;

		Campaign c = getKnownCampaign("" + campaign.id);

		// New campaign
		if (c == null) {
			c = makeNewCampaign(campaign);
			if (c.isActive()) {
				addCampaign(c);
				deletedCampaigns.remove("" + c.id);
				c.runUsingElk();
				logger.info("New campaign {} going active", campaign);
				msg = "NEW CAMPAIGN GOING ACTIVE: " + campaign;
				Crosstalk.signaler.addString("load " + c.id);
			} else {
				logger.info("New campaign is inactive {}, reason: {}", campaign, c.report());
				deleteCampaign("" + campaign.id, null);
			}
		} else {
			// A previously known campaign is updated.
			c.update(campaign);
			if (c.isActive()) {
				logger.info("Previously inactive campaign going active: {}", campaign);
				if (deletedCampaigns.get("" + campaign.id) != null) {
					deletedCampaigns.remove("" + campaign.id);
				}
				msg = "CAMPAIGN GOING ACTIVE: " + campaign;
				try {
					c.addToRTB(); // notifies the bidder
				} catch (Exception err) {
					logger.error("Failed to load campaign {} into bidders, reason: {}", c.name, err.toString());
				}
			} else {
				logger.info("New campaign going inactive:{}, reason: {}", campaign, c.report());
				msg = "CAMPAIGN GOING INACTIVE: " + campaign + ", reason: " + c.report();
				deleteCampaign("" + campaign.id, null);
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
	 * @param campaign String.
	 * @return String. The message on return.
	 * @throws Exception on SQL errors.
	 */
	public String update(String json) throws Exception {
		Campaign x = new Campaign(json);

		Campaign c = getKnownCampaign("" + x.id);
		if (c == null) {
			throw new Exception("No such campaign: " + x.name);
		}
		return update(x, true);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public ArrayNode createJson() throws Exception {
		Date now = new Date();
		Timestamp update = new Timestamp(now.getTime());

		Configuration config = Configuration.getInstance();
		// String select = "select * from campaigns where status = 'runnable' and
		// activate_time <= ? and expire_time > ?";
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
	 * Convert SQL tables for campaigns, creatives, target and rtb_standard into
	 * JSON object
	 * 
	 * @param nodes JSON array to hold the results.
	 * @throws Exception on JSON or SQL errors.
	 */
	public static void handleNodes(ArrayNode nodes) throws Exception {

		ResultSet rs;
		var conn = CrosstalkConfig.getInstance().getConnection();
		Configuration config = Configuration.getInstance();
		var stmt = conn.createStatement();
		List<Integer> list = new ArrayList<Integer>();
		globalRtbSpecification = new HashMap<Integer, JsonNode>();

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
		rs = stmt.executeQuery("select * from " + RTB_STD);
		ArrayNode std = JdbcTools.convertToJson(rs);
		Iterator<JsonNode> it = std.iterator();
		while (it.hasNext()) {
			JsonNode child = it.next();
			globalRtbSpecification.put(child.get("id").asInt(), child);
		}

		exchangeAttributes = JdbcTools.factory.arrayNode();
		rs = stmt.executeQuery("select * from exchange_attributes");
		std = JdbcTools.convertToJson(rs);
		it = std.iterator();
		while (it.hasNext()) {
			JsonNode child = it.next();
			exchangeAttributes.add(child);
		}
	}
	
	public void scan() {

		try {

			List<Campaign> deletions = new ArrayList<>();

			shadow.entrySet().forEach(e -> {
				Campaign camp = e.getValue();
//				camp.runUsingElk();
				try {
					if (!camp.isActive()) {
						logger.info("Campaign has become inactive: {}", camp.name);
						deletions.add(camp);
						camp.report();
						parkCampaign(camp);
					}
					if (!stillRunnable(camp)) {
						logger.info("Campaign is taken offline: {}", camp.name);
						deletions.add(camp);
						camp.report();
						parkCampaign(camp);
					}
				} catch (Exception error) {
					logger.error("Error scanning campaign: {}, error: {}", camp.name, error.getMessage());
				}
			});
			
			deletions.stream().forEach(c -> {
				CampaignCache.getInstance().deleteCampaign(c);
				Crosstalk.signaler.addString("unload " + c.id);
			});

			List<String> additions = new ArrayList<String>();
			List<String> clist = new ArrayList<String>();
			for (String key : deletedCampaigns.keySet()) {
				Campaign camp = deletedCampaigns.get(key);
				if (stillRunnable(camp)) {
					camp.runUsingElk();
					if (camp.isActive() && camp.isRunnable()) {
						logger.info("Currently inactive campaign going active: {}", camp.name);
						CampaignCache.getInstance().addCampaign(camp);
						Crosstalk.signaler.addString("load " + camp.id);
						additions.add(key);
						try {
							// camp.addToRTB(); // one at a time, not good. TBD
							clist.add(key);
						} catch (Exception error) {
							logger.error("Error: Failed to load campaign {} into bidders, reason: {}", camp.name,
									error.toString());
						}
					}
				}

			}

			for (String key : additions) {
				deletedCampaigns.remove(key);
			}

			var canrun = Configuration.getInstance().deadmanSwitch.canRun();
			info = String.format("[canbid=%b, runnable campaigns=%d, parked=%d, dailyspend=%f avg-spend-min=%f] ",
					canrun, shadow.size(), deletedCampaigns.size(),0.0,0.0);
	//				BudgetController.getInstance().getCampaignDailySpend(null),
	//				BudgetController.getInstance().getCampaignSpendAverage(null));
			//logger.info(info);
		} catch (Exception error) {
			error.printStackTrace();
			if (error.toString().toLowerCase().contains("sql")) {
				System.err.println("SQL Error, goodbye");
				System.exit(0);
			}
		}
		
		setTimes();
	}

	boolean stillRunnable(Campaign c) throws Exception {
		var rs = CrosstalkConfig.getInstance().getStatement()
				.executeQuery("select * from campaigns where status='runnable' and id=" + c.id);
		if (rs.next())
			return true;
		return false;
	}

	void purge() throws Exception {
		StringBuilder list = new StringBuilder("");
		List<Campaign> dc = new ArrayList<Campaign>();
		for (String key : deletedCampaigns.keySet()) {
			Campaign c = deletedCampaigns.get(key);
			if (c.canBePurged()) {
				removeFromRTB(c);
				list.append(c.id + " ");
				dc.add(c);
			}
		}

		for (Campaign c : dc) {
			deletedCampaigns.remove("" + c.id);
		}

		shadow.entrySet().forEach(e -> {
			Campaign c = e.getValue();
			try {
				if (c.canBePurged()) {
					removeFromRTB(c);
					list.append(c.id + " ");
				}
			} catch (Exception error) {
				logger.error("Error purging campaign: {}: error: {}" + c.name, error.getMessage());
			}
		});

		if (list.length() > 0) {
			logger.info("The following campaigns have been purged: {}", list.toString());
		}
	}

	/**
	 * Remove a campaign from the hazelcast IMap.
	 * 
	 * @param camp Campaign. The campaign to add.
	 */
	void removeFromRTB(Campaign camp) {
		shadow.remove(camp);
	}

	/**
	 * the system. Load all bidders with all runnable campaigns.
	 * 
	 * @return List. The bidder list.
	 * @throws Exception on SQL or 0MQ errors.
	 */
	public List<String> refresh() throws Exception {
		ArrayNode array = createJson(); // get a copy of the SQL database
		List<String> list = new ArrayList<>();
		List<CampaignBuilderWorker> workers = new ArrayList<>();

		long time = System.currentTimeMillis();

		if (array.size() == 0) {
			return list;
		}

		ExecutorService executor = Executors.newFixedThreadPool(array.size());

		logger.info("Sending {} periodic campaign updates, to {} members.", list.size(), workers.size());
		for (JsonNode s : array) {
			CampaignBuilderWorker w = new CampaignBuilderWorker(s);
			executor.execute(w);
			workers.add(w);
		}

		time = System.currentTimeMillis() - time;
		time /= 1000;
		logger.info("Periodic updates took {} seconds", time);
		executor.shutdown();
		while (!executor.isTerminated()) {

		}

		for (CampaignBuilderWorker w : workers) {
			list.add(w.toString());
		}

		shadow.refresh();
		
		return list;

	}
	
	/////////////////////////
	
	public void updateCampaignTotal(String cid, Double price) throws Exception {
		String sql = "update campaigns set cost=" + price.toString() + " where id="+cid;
		
		var stmt = CrosstalkConfig.getInstance().getConnection().createStatement();
		stmt.execute(sql);
	}
	
	public void updateCampaignTotalDaily(String cid, Double price) throws Exception {
		String sql = "update campaigns set daily_cost=" + price.toString() + " where id="+cid;
		
		var stmt = CrosstalkConfig.getInstance().getConnection().createStatement();
		stmt.execute(sql);
	}
	
	public void updateCampaignTotalHourly(String cid, Double price) throws Exception {
		String sql = "update campaigns set hourly_cost=" + price.toString() + " where id="+cid;
		
		var stmt = CrosstalkConfig.getInstance().getConnection().createStatement();
		stmt.execute(sql);
	}
	
	public boolean dayChanged(Integer cd) {
		if (nowDay == null || cd == null)
			return true;
		if (nowDay.equals(cd))
			return false;
		return true;
	}
	
	public boolean hourChanged(Integer ch) {
		if (nowHour == null || ch == null)
			return true;
		if (nowHour.equals(ch))
			return false;
		return true;
	}
	
	public int getHour() {
		Calendar calendar = Calendar.getInstance();
		//var minutesPastZero = calendar.get(Calendar.MINUTE);
		//return;
		return calendar.get(Calendar.HOUR_OF_DAY);
	}
	
	public int getDay() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DAY_OF_YEAR);
	}
	
	public boolean timeChanged(Integer cd, Integer ch) {
		return (hourChanged(ch) || dayChanged(cd));
	}
	
	void getTimes() {
		day = getDay();
		hour = getHour();
	}
	
	void setTimes() {
		nowDay = getDay();
		nowHour = getHour();
	}
	
	
	public static Map<String,String> typeMap = new HashMap<>();
	static {
		typeMap.put("banner", "banners");
		typeMap.put("video", "banner_videos");
		typeMap.put("audio","banner_audios");
		typeMap.put("natives", "banner_natives");
	}
	
	public void updateCreativeTotal(String cid, String type, Double price) throws Exception {
		String table = typeMap.get(type);
		String sql = "update " + table +  " set total_cost=" + price.toString() + " where id="+ cid;
		
		var stmt = CrosstalkConfig.getInstance().getConnection().createStatement();
		stmt.execute(sql);
	}
	
	public void updateCreativeHourly(String cid,String type, Double price) throws Exception {
		String table = typeMap.get(type);
		String sql = "update " + table +  " set hourly_cost=" + price + " where id="+ cid;
		
		var stmt = CrosstalkConfig.getInstance().getConnection().createStatement();
		stmt.execute(sql);
	}
	
	public void updateCreativeDaily(String cid,String type, Double price) throws Exception {
		String table = typeMap.get(type);
		String sql = "update " + table +  " set daily_cost=" + price + " where id="+ cid;
		
		var stmt = CrosstalkConfig.getInstance().getConnection().createStatement();
		stmt.execute(sql);
	}
	/////////////////////

}

class Controller implements EventIF {

	@Override
	public void handleMessage(String id, String msg) {
		try {
			// System.out.println("====> GOT A SIGNAL MESSAGE FROM: " + id + " Message is " + msg);
			String[] parts = msg.split(" ");
			switch (parts[0]) {
			case "load":
				String [] array = parts[1].split(",");
				Configuration.getInstance().addCampaignsList(array);
				break;
			case "unload":
				Configuration.getInstance().deleteCampaign(parts[1]);
				break;
			}
		} catch (Exception e) {
			throw (RuntimeException) e;
		}
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

}
