package com.jacamars.dsp.rtb.common;

import java.io.IOException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;

import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

import com.jacamars.dsp.crosstalk.budget.AtomicBigDecimal;
import com.jacamars.dsp.crosstalk.budget.BudgetController;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.crosstalk.budget.CrosstalkConfig;
import com.jacamars.dsp.crosstalk.budget.DayPart;
import com.jacamars.dsp.crosstalk.budget.RtbStandard;
import com.jacamars.dsp.crosstalk.budget.Targeting;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.blocks.ProportionalEntry;

import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.rate.Limiter;
import com.jacamars.dsp.rtb.shared.FrequencyGoverner;
import com.jacamars.dsp.rtb.shared.PortableJsonFactory;
import com.jacamars.dsp.rtb.tools.ChattyErrors;
import com.jacamars.dsp.rtb.tools.DbTools;
import com.jacamars.dsp.rtb.tools.JdbcTools;

/**
 * A class that implements a campaign. Provide the campaign with evaluation
 * Nodes (a stack) and a bid request, and this campaign will determine if the
 * bid request in question matches this campaign.
 * @author Ben M. Faul
 *
 */

public class Campaign implements Comparable, Portable  {
	public static final int CLASS_ID = 3;
	
	/** SQL id */
	public int id; 
	/** Set to true if this is an Adx campaign. Can't mix Adx and regular campaigns */
	public boolean isAdx;
	/** points back to the name of the owner of the campaign */
	public String adId = "default-campaign";
	/** The campaign name */
	public String name;
	/** The default ad domain */
	public String adomain = "default-domain";
	/** The list of constraint nodes for this campaign */
	public List<Node> attributes = new ArrayList<Node>();
	/** The list of creatives for this campaign */
	public List<Creative> creatives = new ArrayList<Creative>();;
	/** IAB Categories */
	public List<String> category;
	/** encoded IAB category */
	public transient StringBuilder encodedIab;	
	/** Should you do forensiq fingerprinting for this campaign? */
	public Boolean forensiq = false;

	/** The spend rate of the campaign, default is $1/minute/second in micros. */
	public long assignedSpendRate = 16667;

	public FrequencyCap frequencyCap = null;

	/** The actual spend rate of the campaign, affected by the number of bidders in the system */
	public transient long effectiveSpendRate;

	/** The selection algorithm name */
	public String algorithm;
	/** The keys in the br the algorithm uses */
	public List<String> algorithmKeys;

	public String weightAssignment;
	
	/** Set to runnable to make it actually loadable in the bidder. */
	public String status = "offline";
	
	public transient volatile ProportionalEntry weights;
	
	public Budget budget;
	
	/** Database keys for the creatives */
	public String banners;
	public String videos;
	public String audios;
	public String natives;
	/////////////////////////////////////
	
	/** The SQL name for this campaign id */
	protected final String CAMPAIGN_ID = "id";
	
	/** Thew SQL name for the updated flag */
	protected final String UPDATED = "updated_at";
	
	/** The SQL name for the total budget */
	protected final String TOTAL_BUDGET = "total_budget";
	
	/** SQL name for the descriptive name */
	protected final String CAMPAIGN_NAME = "name";
	
	/** SQL name of Datetime of expiration */
	protected final String EXPIRE_TIME = "expire_time";
	
	/** SQL name for the date time to activate */
	protected final String ACTIVATE_TIME = "activate_time";
	
	/** The SQL name for the budget limit daily */
	protected final String DAILY_BUDGET = "budget_limit_daily";
	
	/** The SQL name for the hourly budget */
	protected final String HOURLY_BUDGET = "budget_limit_hourly";

	protected final String DAYPART = "day_parting_utc";
	
	transient Set<Creative> parkedCreatives = new HashSet<Creative>();
	
	/** This class's logger */
	static final Logger logger = LoggerFactory.getLogger(Campaign.class);

    private SortNodesFalseCount nodeSorter = new SortNodesFalseCount();
    
    // when it was last updated if by sql
    public long updated_at;
    /** points to the regions in the sql database */
    public String regions;
    /** points to the target id for this guy (used to make rules from the campaign manager) */
    public int target_id = 0;
    
    /** Identifies standard rules associated with this campaign */
    public List<Integer>rules = new ArrayList<>();
    
    // ///////////// Crude accounting /////////////////////
    public transient long bids = 0L;
    public transient long wins = 0L;
    public transient long pixels = 0L;
    public transient long clicks = 0L;
    public transient long adspend = 0L;
    //////////////////////////////////////////////////////
    
    
    /**
     * Resources used to create campaign from JSON based SQL
     */
    transient JsonNode myNode;
    transient Targeting targeting;
	/** The exchanges this campaign can be used with */
	public List<String> exchanges = new ArrayList<String>();
	transient List<String> bcat = new ArrayList<String>();
	transient String capSpec;	
	/** Number of seconds before the frequency cap expires */
	transient int capExpire;	
	/** The count limit of the frequency cap */
	transient int capCount;
	/** cap time unit **/
	transient String capTimeUnit;
	//////////////////////////////////////////////////////////////////////
    
	/**
	 * Return the in-memory accounting
	 * @return
	 */
	public Map<String,Long> getCrudeAccounting() {
		Map<String,Long> map = new HashMap<>();
		map.put("bids",bids);
		map.put("wins", wins);
		map.put("pixels", pixels);
		map.put("clicks", clicks);
		map.put("adspend", adspend);
		return map;
	}
    
    /**
	 * Register the portable hazelcast serializeable object. Call this before hazelcast is initialized!
	 * @param config ClientConfig. The configuration for the user.
	 */
	public static void registerWithHazelCast(ClientConfig config) {
        config.getSerializationConfig().addPortableFactory(PortableJsonFactory.FACTORY_ID, new PortableJsonFactory());
        ClassDefinitionBuilder portableCampaignClassBuilder = new ClassDefinitionBuilder(PortableJsonFactory.FACTORY_ID, Campaign.CLASS_ID);
		portableCampaignClassBuilder.addUTFField("json");

		ClassDefinition portablCampaignClassDefinition = portableCampaignClassBuilder.build();
	    config.getSerializationConfig().addClassDefinition(portablCampaignClassDefinition);
	}
	
	/**
	 * Register the portable hazelcast serializeable object. Call this before hazelcast is initialized!
	 * @param config ClientConfig. The configuration for the member.
	 */
	public static void registerWithHazelCast(Config config) {
		config.getSerializationConfig().addPortableFactory(PortableJsonFactory.FACTORY_ID, new PortableJsonFactory());
	    ClassDefinitionBuilder portableCampaignClassBuilder = new ClassDefinitionBuilder(PortableJsonFactory.FACTORY_ID, Campaign.CLASS_ID);
	    portableCampaignClassBuilder.addUTFField("json");

		ClassDefinition portableCampaignClassDefinition = portableCampaignClassBuilder.build();
		config.getSerializationConfig().addClassDefinition(portableCampaignClassDefinition);
	}
	
	public static Campaign getInstance(int id) throws Exception {
		String select = "select * from campaigns where id="+id;
		var conn = CrosstalkConfig.getInstance().getConnection();
		var stmt = conn.createStatement();
		var prep = conn.prepareStatement(select);
		ResultSet rs = prep.executeQuery();
		
		ArrayNode inner = JdbcTools.convertToJson(rs);
		ObjectNode y = (ObjectNode) inner.get(0);
		Campaign c = new Campaign(y);
		return c;
	}
	
	/**
	 * Empty constructor, simply takes all defaults, useful for testing.
	 */
	public Campaign() {

	}
	
	
	/**
	 * Constructor using a JSON Node. Crosstalk uses this to create this object from SQL
	 * @param node JsonNode. The object as defined by SQL, and interpreted as JSON.
	 */
	public Campaign(JsonNode node) throws Exception {
		myNode = node;
		updated_at = node.get("updated_at").asLong();
		setup();
		process();
		doTargets();
	}
	
	/**
	 * Constructor using a string JSON. This is used by file readers.
	 * @param data
	 * @throws Exception
	 */
	public Campaign(String data) throws Exception {
		
		Campaign camp = DbTools.mapper.readValue(data, Campaign.class);
		init(camp);
	}
	
	
	/**
	 * Crosstalk updates a campaign using this.
	 * @param camp Campaign.
	 */
	public void update(Campaign camp) throws Exception {
		init(camp);
	}
	
	public void update(ObjectNode myNode) throws Exception {
		this.myNode = myNode;
		this.creatives.clear();
		this.bcat.clear();
		this.exchanges.clear();

		setup();
		process();
		doTargets();
	}
	
	/** 
	 * An iniitializer from a copy.
	 * @param camp
	 * @throws Exception
	 */
	private void init(Campaign camp) throws Exception {
		this.isAdx = camp.isAdx;
		this.adomain = camp.adomain;
		this.attributes = camp.attributes;
		this.creatives = camp.creatives;
		this.adId = camp.adId;
		this.name = camp.name;
		this.forensiq = camp.forensiq;
		this.status = camp.status;
		this.name = camp.name;
		this.frequencyCap = camp.frequencyCap;
		this.budget = camp.budget;
		this.updated_at = camp.updated_at;
		this.assignedSpendRate = camp.assignedSpendRate;
		this.banners = banners;
		this.videos = videos;
		this.natives = natives;
		this.audios = audios;
		if (camp.category != null)
			this.category = camp.category;
		
		encodeCreatives();
		encodeAttributes();	
	}

    /**
     * Sort the selection criteria in descending order of number of times false was selected.
     * Then, after doing that, zero the counters.
     */
    public void sortNodes() {
        Collections.sort(attributes, nodeSorter);

        for (int i = 0; i<attributes.size();i++) {
            attributes.get(i).clearFalseCount();
        }

        for (int i=0;i<creatives.size();i++) {
            creatives.get(i).sortNodes();
        }
    }
	
	/**
	 * Find the node with the specified hierarchy string.
	 * @param str String. The hierarchy we are looking for.
	 * @return Node. The node with this hierarchy, might be null if not exists.
	 */
	public Node getAttribute(String str) {
		
		for (Node n : attributes) {
			if (n.equals(str))
				return n;
		}
		return null;
	}

	/**
	 * Is this campaign capped on the item in this bid request?
	 * @param br BidRequest. The bid request to query.
	 * @param capSpecs Map. The current cap spec.
	 * @return boolean. Returns true if the IP address is capped, else false.
	 */
	public boolean isCapped(BidRequest br, Map<String, String> capSpecs) {
		if (frequencyCap == null)
			return false;
		return frequencyCap.isCapped(br,capSpecs,adId);
	}

	/**
	 * Determine if this bid request + campaign is frequency Governed.
	 * @param br BidReuestcount . The bid request to check for governance.
	 * @return boolean. Returns true if this campaign has bid on the same user/synthkey in the last 1000 ms.
	 */
	public boolean isGoverned(BidRequest br) {
		if (RTBServer.frequencyGoverner == null || FrequencyGoverner.silent)
			return false;

		return RTBServer.frequencyGoverner.contains(adId,br);
	}
	/**
	 * Return the Lucene query string for this campaign's attributes
	 * @return String. The lucene query.
	 */
	
	@JsonIgnore
	public String getLucene() {
		return getLuceneFromAttrs(attributes);
	}
	
	String getLuceneFromAttrs(List<Node> attributes) {
		String str = "";
		
		List<String> strings = new ArrayList<String>();
		for (int i=0; i < attributes.size(); i++) {
			Node x = attributes.get(i);
			String s = x.getLucene();
			if (s != null && s.length() > 0)
				strings.add(s);
		}
		
		for (int i=0; i<strings.size();i++) {
			String s = strings.get(i);
			str += s;
			if (i + 1 < strings.size())
				str += " AND ";
		}
		
		return str;
	}
	
	/**
	 * Return the lucene query string for the named creative.
	 * @param crid String. The creative id.
	 * @return String. The lucene string for this query.
	 */
	@JsonIgnore
	public String getLucene(String crid) {
		Creative c = this.getCreative(crid);
		if (c == null)
			return null;

		String pre = "((-_exists_: imp.bidfloor) OR imp.bidfloor :<=" + c.price + ") AND ";
		if (c.isNative()) {
			
		} else
		if (c.isVideo()) {
			pre += "imp.video.w: " + c.w + " AND imp.video.h: " + c.h + " AND imp.video.maxduration:< " + c.videoDuration;
			pre += " AND imp.video.mimes: *" + c.mime_type + "* AND imp.video.protocols: *" + c.videoProtocol + "*";
		} else {
			pre += "imp.banner.w: " + c.w + " AND imp.banner.h: " + c.h;
		}
		
		String str = getLucene();
		String rest = getLuceneFromAttrs(c.attributes);
		
		if (str == null || str.length() == 0)  {
			return pre + " AND " + rest;
		}
		
		if (rest == null || rest.length() == 0)
			return pre + " AND " + str;
		
		return pre + " AND " + str + " AND " + rest;
	}
	
	/**
	 * Get a creative of this campaign.
	 * @param crid: String. The creative id.
	 * @return Creative. The creative or null;
	 */
	public Creative getCreative(String crid) {
		for (Creative c : creatives) {
			if (c.impid.equals(crid)) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * Creates a copy of this campaign
	 * @return Campaign. A campaign that is an exact clone of this one
	 * @throws Exception on JSON parse errors.
	 */
	public Campaign copy() throws Exception {

		String str =  DbTools.mapper.writer().writeValueAsString(this);
		Campaign x = DbTools.mapper.readValue(str, Campaign.class);
		x.encodeAttributes();
		return x;
	}
	
	/**
	 * Constructor with pre-defined node.
	 * @param id String - the id of this campaign.
	 * @param nodes nodes. List - the list of nodes to add.
	 */
	public Campaign(String id, List<Node> nodes) {
		this.adId = id;
		this.attributes.addAll(nodes);
	}
	
	/**
	 * Enclose the URL fields. GSON doesn't pick the 2 encoded fields up, so you have to make sure you encode them.
	 * This is an important step, the WIN processing will get mangled if this is not called before the campaign is used.
	 * Configuration.getInstance().addCampaign() will call this for you.
	 */
	public void encodeCreatives() throws Exception {
		
		for (Creative c : creatives) {
			c.encodeUrl();
			c.encodeAttributes();
		}
	}
	
	/**
	 * Encode the values of all the attributes, instantiating from JSON does not do this, it's an incomplete serialization
	 * Always call this if you add a campaign without using Configuration.getInstance().addCampaign();
	 * @throws Exception if the attributes of the node could not be encoded.
	 */
	public void encodeAttributes() throws Exception {
		for (int i=0;i<attributes.size();i++) {
			Node n = attributes.get(i);
			n.setValues();
		}
		
		if (category == null) {
			category = new ArrayList<String>();
		}
		
		if (category.size()>0) {
			String str = "\"cat\":" + DbTools.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(category);
			encodedIab = new StringBuilder(str);
		}

		Limiter.getInstance().addCampaign(this);
		establishSpendRate();

		setWeights(weightAssignment);
	}

	/**
	 * Set this campaign's assigned weights.
	 * @param weight String. The weight of the creatives.
	 * @throws Exception on parsing errors
	 */
	public void setWeights(String weight) throws Exception {
		if (weight == null || weight.length()==0) {
			weightAssignment = null;
			weights = null;
			return;
		}
		weightAssignment = weight;
		weights = new ProportionalEntry(weightAssignment);
	}

	/**
	 * Calculate the effective spend rate. It is equal to assigned spend rate by the number of members. Then
	 * call the Limiter to fix the rate limiter access for it.
	 *
	 * This is called when the campaign is instantiated (via the encode attributes method, or whenever the
	 * bidder determines there has been a change in the number of bidders in the bid farm
	 */
	public void establishSpendRate() {
		int k = RTBServer.biddersCount;
		if (k == 0)
			k = 1;
		effectiveSpendRate = assignedSpendRate / k;
		Limiter.getInstance().setSpendRate(adId, effectiveSpendRate);
	}

	/**
	 * Add an evaluation node to the campaign.
	 * @param node Node - the evaluation node to be added to the set.
	 */
	public void add(Node node) {
		attributes.add(node);
	}

	/**
	 * The compareTo method to ensure that multiple campaigns
	 * don't exist with the same id.
	 * @param o Object. The object to compare with.
	 * @return int. Returns 1 if the ids match, otherwise 0.
	 */
	@Override
	public int compareTo(Object o) {
		Campaign other = (Campaign)o;
		if (this.adId.equals(other.adId))
			return 1;
		
		return 0;
	}
	
	/**
	 * Returns this object as a JSON string
	 * @return String. The JSON representation of this object.
	 */
	public String toJson() {
		try {
			return DbTools.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Answers the question, can this campaign use the named exchange?
	 * @param exchange String. The name of the exchange
	 * @return boolean. Returns true if we can use the exchange, otherwise returns false.
	 */
	public boolean canUseExchange(String exchange) {
		boolean canUse = false;
		for (Node node : attributes) {
			if (node.bidRequestValues.contains("exchange")) {
				canUse = false;
				Object obj = node.value;
				if (obj instanceof String) {
					String str = (String)obj;
					if (str.equals(exchange)) {
						canUse = true;
					} else {
						canUse = false;
					}
				} else
				if (obj instanceof List) {
					List<String> list = (List<String>)obj;
					if (list.contains(exchange))
						canUse = true;
					else
						canUse = false;
				}
			}
		}
		return canUse;
	}

	@Override
	public int getFactoryId() {
		return PortableJsonFactory.FACTORY_ID;
	}

	@Override
	public int getClassId() {
		
		return CLASS_ID;
	}

	@Override
	public void writePortable(PortableWriter writer) throws IOException {
		String json = DbTools.mapper.writeValueAsString(this);
		writer.writeUTF("json", json);
		
	}

	@Override
	public void readPortable(PortableReader reader) throws IOException {
		String json = reader.readUTF("json");
		Campaign c = DbTools.mapper.readValue(json, Campaign.class);
		try {
			init(c);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	////////////////////////////
	
	public void runUsingElk() {
		try {

			budget.totalCost.set(BudgetController.getInstance().getCampaignTotalSpend(adId));
			budget.dailyCost.set(BudgetController.getInstance().getCampaignDailySpend(adId));
			budget.hourlyCost.set(BudgetController.getInstance().getCampaignHourlySpend(adId));

			logger.debug("*** ELK TEST: Updating budgets CAMPAIGN:{}", adId);
			logger.debug("Total cost: {}, daily cost: {}, hourly cost: {}", budget.totalCost.getDoubleValue(),
					budget.dailyCost.getDoubleValue(), budget.hourlyCost.getDoubleValue());

			for (Creative c : creatives) {
				c.runUsingElk(adId);
			}

		} catch (Exception error) {
			var msg = "ELK is not accessible, no accounting data is possible for: " + adId;
			ChattyErrors.printErrorEveryHour(logger, msg);
		}
	}

	public double costAsDouble() {
		return budget.totalCost.doubleValue();
	}
	

	protected void park(Creative c) {
		creatives.remove(c);
		parkedCreatives.add(c);
	}

	protected void unpark(Creative c) {
		parkedCreatives.remove(c);
		creatives.add(c);
	}

	/**
	 * Is the campaign expired? Returns true if expired. If no budget returns false.
	 * @return boolean. Returns true or false depending on if the campaign expiration was reached. Always return false if no budget is set (Not expired...)
	 */
	public boolean isExpired() {
		Date date = new Date();
		
		if (budget == null)
			return false;
		
		boolean expired = date.getTime() > budget.expire_time;
		if (expired)
			return expired;
		expired = date.getTime() < budget.activate_time;
		if (expired)
			return expired;
		return false;

	}

	public boolean isActive() throws Exception {
		Date date = new Date();

		if (creatives.size() == 0) {
			return false;
		}

		if (budgetExceeded()) {
			logger.debug("BUDGET EXCEEDED: {}", adId);
			return false;
		}
		
		if (budget == null)
			return true;

		if ((date.getTime() >= budget.activate_time) && (date.getTime() <= budget.expire_time)) {

			if (budget.daypart != null) {
				if (budget.daypart.isActive() != true) {
					logger.debug("Daypart is not active: {}", adId);
					return false;
				}
			}

			logger.debug("IS ACTIVE: {}", adId);
			return true;
		} else {
			logger.debug("ACTIVATION TIME NOT IN RANGE: {}", adId);
			return false;
		}
	}



	public boolean budgetExceeded() throws Exception {
		if (budget == null || budget.totalBudget.doubleValue()==0)
			return false;

		return BudgetController.getInstance().checkCampaignBudgets(adId,budget.totalBudget, 
				budget.dailyBudget, budget.hourlyBudget);
	}

	public boolean compareTo(Campaign t) {
		return false;
	}

	/**
	 * Check and see if this campaign is deletable fron the system
	 * @return boolean. If campaign is expired or the total spend has been reached.
	 * @throws Exception on errors computing budgets.
	 */
	public boolean canBePurged() throws Exception {
		if (isExpired())
			return true;
		return BudgetController.getInstance().checkCampaignTotalBudgetExceeded(adId, budget.totalBudget);
	}

	public boolean addToRTB() throws Exception {
		return true;
	}

	public boolean addToRTB(String bidder) throws Exception {
		return true;
	}


	public void setStatus(String status) {
		this.status = status;
	}
	
	void setup() throws Exception {
		// process
		name = myNode.get("name").asText();
		id = myNode.get(CAMPAIGN_ID).asInt();
		adId = myNode.get(CAMPAIGN_ID).asText();
		budget = new Budget();
		
		budget.totalCost = new AtomicBigDecimal(myNode.get("cost"));
		budget.dailyCost = new AtomicBigDecimal(myNode.get("daily_cost"));
		budget.hourlyCost = new AtomicBigDecimal(myNode.get("hourly_cost"));
		if (myNode.get(EXPIRE_TIME) != null)
				budget.expire_time = myNode.get(EXPIRE_TIME).asLong();
		if (myNode.get(ACTIVATE_TIME) != null)
			budget.activate_time = myNode.get(ACTIVATE_TIME).asLong();
		budget.totalBudget = new AtomicBigDecimal(myNode.get(TOTAL_BUDGET));

		if (myNode.get(DAYPART) != null && myNode.get(DAYPART) instanceof MissingNode == false) {
				String parts = myNode.get(DAYPART).asText();
				if (parts.equals("null") || parts.length()==0)
					budget.daypart = null;
			else
				budget.daypart = new DayPart(parts);
		} else
			budget.daypart = null;

		if (myNode.get("bcat") != null) {
			String str = myNode.get("bcat").asText();
			if (str.trim().length() != 0) {
				if (str.equals("null")==false)
					Targeting.getList(bcat, str);
			}
		}
		
		if (myNode.get("rules") != null) {
			ArrayNode n = (ArrayNode)myNode.get("rules");
			rules = new ArrayList<Integer>();
			for (int i=0;i<n.size();i++) {
				rules.add(n.get(i).asInt());
			}
		}
		
		if (myNode.get("exchanges") != null && myNode.get("exchanges").asText().length() != 0) {
			exchanges.clear();
			
			String str = getMyNode().get("exchanges").asText(null);
			if (str != null) {
				Targeting.getList(exchanges, str);
			}
		}

		Object x = myNode.get(DAILY_BUDGET);
		if (x != null && !(x instanceof NullNode)) {
			if (budget.dailyBudget == null || (budget.dailyBudget.doubleValue() != myNode.get(DAILY_BUDGET).asDouble())) {
				budget.dailyBudget = new AtomicBigDecimal(myNode.get(DAILY_BUDGET).asDouble());
			}
		} else
			budget.dailyBudget = null;

		x = myNode.get(HOURLY_BUDGET);
		if (x != null && !(x instanceof NullNode)) {
			if (budget.hourlyBudget == null || (budget.hourlyBudget.doubleValue() != myNode.get(HOURLY_BUDGET).asDouble())) {
				budget.hourlyBudget = new AtomicBigDecimal(myNode.get(HOURLY_BUDGET).asDouble());
			}
		} else
			budget.hourlyBudget = null;
		
		status = myNode.get("status").asText();
		adomain = myNode.get("ad_domain").asText(); 
		forensiq = myNode.get("forensiq").asBoolean();
		assignedSpendRate = myNode.get("spendrate").asInt();
		regions = myNode.get("regions").asText();
		/**
		 * Do this last
		 */

		x = myNode.get("target_id");
		if (x == null || x instanceof NullNode || ((JsonNode)x).asInt() == 0) {
			if (!isActive())
				return;
			throw new Exception("Can't have null targetting for campaign " + adId);
		}
		if (x != null)
			target_id = ((JsonNode)x).asInt();
		
		if (myNode.get("banners") != null) 
			banners = myNode.get("banners").asText();
		if (myNode.get("videos") != null)
			videos = myNode.get("videos").asText();
		if (myNode.get("audios") != null)
			audios = myNode.get("audios").asText();
		if (myNode.get("natives") != null)
			natives = myNode.get("natives").asText();
		
		processCreatives();
	}
	
	/**
	 * Load the creatives from the db and attach them to the the campaign.
	 * @throws Exception if there is a database error.
	 */
	void processCreatives() throws Exception {
		if (creatives == null) 
			creatives = new ArrayList<>();
		List<Integer> ids = new ArrayList<>();
		Targeting.getIntegerList(ids, banners);
		ids.stream().forEach(id->creatives.add(Creative.getBannerInstance(id)));
		
		ids.clear();
		Targeting.getIntegerList(ids, videos);
		ids.stream().forEach(id->creatives.add(Creative.getVideoInstance(id)));
		
		ids.clear();
		Targeting.getIntegerList(ids, audios);
		ids.stream().forEach(id->creatives.add(Creative.getAudioInstance(id)));
		
		ids.clear();
		Targeting.getIntegerList(ids, natives);
		ids.stream().forEach(id->creatives.add(Creative.getNativeInstance(id)));
	}
	
	public boolean process() throws Exception {
		boolean change = false;
		int n = creatives.size();
		List<Creative> list = new ArrayList<Creative>();

		for (Creative c : parkedCreatives) {
			if (!c.budgetExceeded(adId)) {
				unpark(c);
				change = true;
			}
		}

		for (Creative creative : creatives) {
			if (creative.budgetExceeded(adId)) {
				list.add(creative);
				change = true;
			}
		}

		for (Creative c : list) {
			park(c);
		}
		
		updated_at = myNode.get("updated_at").asLong();

		return change;
	}
	
	JsonNode getMyNode() {
		return myNode;
	}
	
	protected void doTargets() throws Exception {
		if (target_id == 0) {
			if (!isActive())
				return;
			throw new Exception("No targeting record was found. for " + adId);
		}
 
		targeting = Targeting.getInstance(target_id); 

		instantiate("banner", true);
		instantiate("banner_video", false);
		compile();
	}
	
	public void compile() throws Exception {
		
		for (Creative c : creatives) {
			c.compile();
		}
		
		if (forensiq != null) {
			if (forensiq)
				forensiq = true;
			else
				forensiq = false;
		}

		List<Node> nodes;
		attributes.clear();
		if (targeting != null) {
			nodes = targeting.compile();
			if (nodes != null) {
				for (Node n : nodes)
					attributes.add(n);
			}
		}

		if (exchanges.size() != 0) {
			Node n = new Node("exchanges", "exchange", Node.MEMBER, exchanges);
			n.notPresentOk = false;
			attributes.add(n);
		}

		if (bcat.size() != 0) {
			Node n = new Node("bcat", "bcat", Node.NOT_INTERSECTS, bcat);
			n.notPresentOk = true;
			attributes.add(n);
		}

		int k = 0;
		for (Creative c : creatives) {
			if (c.adxCreativeExtensions != null)
				k++;
		}
		if (k > 0)
			isAdx = true;
		else
			isAdx = false;

		if (capSpec != null && capSpec.length() > 0 && capCount > 0 && capExpire > 0) {
			frequencyCap = new FrequencyCap();
			frequencyCap.capSpecification = new ArrayList<String>();
			Targeting.getList(frequencyCap.capSpecification, capSpec);
			frequencyCap.capTimeout = capExpire; // in seconds
			frequencyCap.capFrequency = capCount;
			frequencyCap.capTimeUnit = capTimeUnit;
		}
		doStandardRtb();
	}
	
	/**
	 * Call after you compile!
	 * 
	 * @throws Exception on JSON errors.
	 */
	void doStandardRtb() throws Exception {
		rules.forEach(id->{
			try {
				attributes.add(Node.getInstance(id));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	
	}
	/**
	 * Report why the campaign is not runnable.
	 * @return String. The reasons why...
	 * @throws Exception on ES errors.
	 */
	public String report() throws Exception {
		String reason = "";
		if (budgetExceeded()) {
			if (reason.length() != 0)
				reason += " ";
			if (BudgetController.getInstance().checkCampaignBudgetsTotal(adId, budget.totalBudget))
				reason += "Campaign total budget exceeded. ";
			if (BudgetController.getInstance().checkCampaignBudgetsDaily(adId, budget.dailyBudget))
				reason += "Campaign daily budget exceeded. ";
			if (BudgetController.getInstance().checkCampaignBudgetsHourly(adId, budget.hourlyBudget))
				reason += "Campaign hourly budget exceeded. ";
		}
		
		if (creatives.size() == 0) {
			if (reason.length() > 0)
				reason += " ";
			reason += "No attached creatives.";
		}

		if (isExpired()) {
			if (reason.length() > 0)
				reason += " ";
			reason += "Bid window closed, expiry. ";
		} else if (!budgetExceeded()) {
			if (reason.length() > 0)
				reason += " ";

			if (budget.daypart != null) {
				if (budget.daypart.isActive() != true) {
					reason += "Daypart is not active ";
				}
			}
		}

		List<Map> xreasons = new ArrayList<Map>();
		if (creatives.size() != 0) {
			for (Creative p : parkedCreatives) {
				Map<String, Object> r = new HashMap<String, Object>();
				r.put("creative",p.impid);
				List<String> reasons = new ArrayList<String>();
				if (p.budgetExceeded(adId)) {
					reasons.add("nobudget");
				}

				r.put("reasons",reasons);
			}
		}

		if (xreasons.size() != 0) {
			reason += DbTools.mapper.writeValueAsString(xreasons);
		}
		if (reason.length() > 0)
			logger.info("Campaign {} not loaded: {}",adId, reason);

		if (reason.length() == 0)
			reason = "Runnable";
		return reason;
	}
	
	/**
	 * Instantiate a creative.
	 * 
	 * @param type
	 *            String. The type of creative.
	 * @param isBanner
	 *            boolean. Is this a banner
	 * @throws Exception on SQL or JSON errors.
	 * 
	 */
	protected void instantiate(String type, boolean isBanner) throws Exception {

		ArrayNode array = (ArrayNode) getMyNode().get(type);
		if (array == null)
			return;

		for (int i = 0; i < array.size(); i++) {
			ObjectNode node = (ObjectNode) array.get(i);
			Creative creative = new Creative(node, isBanner); 
			if (!(creative.budgetExceeded(adId))) {
				unpark(creative);
			} else {
				park(creative);
			}
		}
	}

	
	public boolean isRunnable() throws Exception {
		if (status == null)
			status = "runnable";
		if (!status.equals("runnable"))
			return false;
		return true;
	}

	public static PreparedStatement toSql(Campaign c, Connection conn) throws Exception {
		if (c.id == 0) 
			return doNew(c, conn);
		return doUpdate(c, conn);
	}
	
	static PreparedStatement doNew(Campaign c, Connection conn) throws Exception {
		PreparedStatement p = null;
		Array rulesArray  = null;
		if (c.rules != null) {
			rulesArray = conn.createArrayOf("int",c.rules.toArray());
		}
		
		String sql = "INSERT INTO campaigns (" 
		 +"activate_time,"
		 +"expire_time,"
		 +"cost,"
		 +"ad_domain,"
		 +"name,"
		 +"status,"
		 +"budget_limit_daily,"
		 +"budget_limit_hourly,"
		 +"total_budget,"
		 +"forensiq,"
		 +"created_at,"
		 +"exchanges,"
		 +"regions,"
		 +"target_id,"
		 +"rules,"
		 +"spendrate) VALUES("
		 +"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
		
		p = conn.prepareStatement(sql);
		
		if (c.budget != null) {
			p.setTimestamp(1,new Timestamp(c.budget.activate_time));
			p.setTimestamp(2,new Timestamp(c.budget.expire_time));
		} else {
			p.setNull(1, Types.TIMESTAMP);
			p.setNull(2, Types.TIMESTAMP);
		}
		p.setDouble(3, c.costAsDouble());
		p.setString(4, c.adomain);
		p.setString(5, c.name);
		p.setString(6, c.status);
		
		if (c.budget==null || c.budget.dailyBudget == null) {
			p.setNull(7, Types.DECIMAL);
			p.setNull(8, Types.DECIMAL);
			p.setNull(9, Types.DECIMAL);
		}  else {
			p.setDouble(7,  c.budget.dailyBudget.doubleValue());
			p.setDouble(8,  c.budget.hourlyBudget.doubleValue());
			p.setDouble(9,  c.budget.totalBudget.doubleValue());
		}
		if (c.forensiq == null)
			p.setNull(10,  Types.BOOLEAN);
		else
			p.setBoolean(10,c.forensiq);
		p.setTimestamp(11,new Timestamp(System.currentTimeMillis()));
		if (c.exchanges == null || c.exchanges.size() == 0)
			p.setNull(12, Types.VARCHAR);
		else {
			var s = "";
			for (int i=0;i<c.exchanges.size();i++) {
				s += c.exchanges.get(i);
				if (i+1 < c.exchanges.size())
					s += ",";
			}
			p.setString(12, s);
		}

		if (c.regions == null)
			p.setNull(13, Types.VARCHAR);
		else
			p.setString(13,  c.regions);
		if (c.target_id == 0)
			p.setNull(14, Types.INTEGER);
		else
			p.setInt(14,  c.target_id);
		if (rulesArray != null)
			p.setArray(15, rulesArray);
		else
			p.setNull(15, Types.ARRAY);
		p.setInt(16,(int)c.assignedSpendRate);
		
		return p;
	}
	
	static PreparedStatement doUpdate(Campaign c,  Connection conn) throws Exception {
		PreparedStatement p = null;
		Array rulesArray  = null;
		if (c.rules != null) {
			rulesArray = conn.createArrayOf("int",c.rules.toArray());
		}
		
		String sql = "UPDATE campaigns SET "
		 +"activate_time=?,"
		 +"expire_time=?,"
		 +"cost=?,"
		 +"ad_domain=?,"
		 +"name=?,"
		 +"status=?,"
		 +"budget_limit_daily=?,"
		 +"budget_limit_hourly=?,"
		 +"total_budget=?,"
		 +"forensiq=?,"
		 +"updated_at=?,"
		 +"exchanges=?,"
		 +"regions=?,"
		 +"target_id=?,"
		 +"rules=?,"
		 +"spendrate=? WHERE id=?";

		
		p = conn.prepareStatement(sql);
		
		if (c.budget != null) {
			p.setTimestamp(1,new Timestamp(c.budget.activate_time));
			p.setTimestamp(2,new Timestamp(c.budget.expire_time));
		} else {
			p.setNull(1, Types.TIMESTAMP);
			p.setNull(2, Types.TIMESTAMP);
		}
		
		p.setDouble(3, c.costAsDouble());
		p.setString(4, c.adomain);
		p.setString(5, c.name);
		p.setString(6, c.status);
		if (c.budget==null || c.budget.dailyBudget == null) {
			p.setNull(7, Types.DECIMAL);
			p.setNull(8, Types.DECIMAL);
			p.setNull(9, Types.DECIMAL);
		}  else {
			p.setDouble(7,  c.budget.dailyBudget.doubleValue());
			p.setDouble(8,  c.budget.hourlyBudget.doubleValue());
			p.setDouble(9,  c.budget.totalBudget.doubleValue());
		}
		if (c.forensiq == null)
			p.setNull(10,  Types.VARCHAR);
		else
			p.setBoolean(10, c.forensiq);
		p.setTimestamp(11,new Timestamp(System.currentTimeMillis()));
		
		if (c.exchanges == null || c.exchanges.size() == 0)
			p.setNull(12, Types.VARCHAR);
		else {
			var s = "";
			for (int i=0;i<c.exchanges.size();i++) {
				s += c.exchanges.get(i);
				if (i+1 < c.exchanges.size())
					s += ",";
			}
			System.out.println("=======>" + c.exchanges);
			p.setString(12, s);
		}
		
		if (c.regions == null)
			p.setNull(13, Types.VARCHAR);
		else
			p.setString(13,  c.regions);
		if (c.target_id == 0)
			p.setNull(14, Types.INTEGER);
		else
			p.setInt(14,  c.target_id);
		
		if (rulesArray != null)
			p.setArray(15, rulesArray);
		else
			p.setNull(15, Types.ARRAY);
		p.setInt(16, (int)c.assignedSpendRate);
		
		p.setInt(17, c.id);

		
		return p;
	}
}
