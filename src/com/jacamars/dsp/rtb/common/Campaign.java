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
import com.fasterxml.jackson.databind.node.TextNode;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;

import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

import com.jacamars.dsp.crosstalk.budget.AtomicBigDecimal;
import com.jacamars.dsp.crosstalk.budget.CampaignBuilderWorker;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.crosstalk.budget.CrosstalkConfig;
import com.jacamars.dsp.crosstalk.budget.DayPart;

import com.jacamars.dsp.crosstalk.budget.Targeting;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.blocks.ProportionalEntry;

import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.shared.AccountingCache;
import com.jacamars.dsp.rtb.shared.CampaignCache;
import com.jacamars.dsp.rtb.shared.FrequencyGoverner;
import com.jacamars.dsp.rtb.shared.PortableJsonFactory;
import com.jacamars.dsp.rtb.shared.TokenData;
import com.jacamars.dsp.rtb.tools.ChattyErrors;
import com.jacamars.dsp.rtb.tools.DbTools;
import com.jacamars.dsp.rtb.tools.JdbcTools;

/**
 * A class that implements a campaign. Provide the campaign with evaluation
 * Nodes (a stack) and a bid request, and this campaign will determine if the
 * bid request in question matches this campaign.
 * 
 * @author Ben M. Faul
 *
 */

public class Campaign implements Comparable, Portable {
	public static final int CLASS_ID = 3;

	/** SQL id */
	public int id;
	public String stringId;

	public String customer_id;

	/**
	 * Set to true if this is an Adx campaign. Can't mix Adx and regular campaigns
	 */
	public boolean isAdx;
	/** points back to the name of the owner of the campaign */
	public String name;
	/** The default ad domain */
	public String ad_domain = "default-domain";
	/** The list of constraint nodes for this campaign */
	public List<Node> attributes = new ArrayList<Node>();
	/** The list of creatives for this campaign */
	public List<Creative> creatives = new ArrayList<Creative>();;
	/** IAB Categories */
	public List<String> category;
	/** encoded IAB category */
	public StringBuilder encodedIab;
	/** Should you do forensiq fingerprinting for this campaign? */
	public Boolean forensiq = false;

	/** The spend rate of the campaign, default is $1/minute/second in micros. */
	public long spendrate = 16667;

	public FrequencyCap frequencyCap = null;

	/**
	 * The actual spend rate of the campaign, affected by the number of bidders in
	 * the system
	 */
	public long effectiveSpendRate;

	/** The selection algorithm name */
	public String algorithm;
	/** The keys in the br the algorithm uses */
	public List<String> algorithmKeys;

	public String weightAssignment;

	/** Set to runnable to make it actually loadable in the bidder. */
	public String status = "offline";

	public ProportionalEntry weights;

	public Long activate_time;
	public Long expire_time;
	public Budget budget;

	/** Database keys for the creatives */
	public List<Integer> banners;
	public List<Integer> videos;
	public List<Integer> audios;
	public List<Integer> natives;
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
	/**
	 * points to the target id for this guy (used to make rules from the campaign
	 * manager)
	 */
	public int target_id = 0;

	/** Identifies standard rules associated with this campaign */
	public List<Integer> rules = new ArrayList<>();

	// ///////////// Crude accounting /////////////////////
	public transient long bids = 0L;
	public transient long wins = 0L;
	public transient long pixels = 0L;
	public transient long clicks = 0L;
	public transient long adspend = 0L;
	//////////////////////////////////////////////////////

	public String day_parting_utc;

	/**
	 * Resources used to create campaign from JSON based SQL
	 */
	transient JsonNode myNode;
	transient Targeting targeting;
	/** The exchanges this campaign can be used with */
	public List<String> exchanges = new ArrayList<String>();
	public List<String> bcat = new ArrayList<String>();

	/** rtb attribute being capped */
	public String capSpec;
	/** Number of seconds before the frequency cap expires */
	public Integer capExpire;
	/** The count limit of the frequency cap */
	public Integer capCount;
	/** cap time unit **/
	public String capUnit;;
	//////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////
	//
	// Hour and day tracking

	public Integer currentHour;
	public Integer currentDay;

	public transient volatile boolean encoded = false;

	/**
	 * Return the in-memory accounting
	 * 
	 * @return
	 */
	public Map<String, Long> getCrudeAccounting() {
		Map<String, Long> map = new HashMap<>();
		map.put("bids", bids);
		map.put("wins", wins);
		map.put("pixels", pixels);
		map.put("clicks", clicks);
		map.put("adspend", adspend);
		return map;
	}

	/**
	 * Register the portable hazelcast serializeable object. Call this before
	 * hazelcast is initialized!
	 * 
	 * @param config ClientConfig. The configuration for the user.
	 */
	public static void registerWithHazelCast(ClientConfig config) {
		config.getSerializationConfig().addPortableFactory(PortableJsonFactory.FACTORY_ID, new PortableJsonFactory());
		ClassDefinitionBuilder portableCampaignClassBuilder = new ClassDefinitionBuilder(PortableJsonFactory.FACTORY_ID,
				Campaign.CLASS_ID);
		portableCampaignClassBuilder.addUTFField("json");

		ClassDefinition portablCampaignClassDefinition = portableCampaignClassBuilder.build();
		config.getSerializationConfig().addClassDefinition(portablCampaignClassDefinition);
	}

	/**
	 * Register the portable hazelcast serializeable object. Call this before
	 * hazelcast is initialized!
	 * 
	 * @param config ClientConfig. The configuration for the member.
	 */
	public static void registerWithHazelCast(Config config) {
		config.getSerializationConfig().addPortableFactory(PortableJsonFactory.FACTORY_ID, new PortableJsonFactory());
		ClassDefinitionBuilder portableCampaignClassBuilder = new ClassDefinitionBuilder(PortableJsonFactory.FACTORY_ID,
				Campaign.CLASS_ID);
		portableCampaignClassBuilder.addUTFField("json");

		ClassDefinition portableCampaignClassDefinition = portableCampaignClassBuilder.build();
		config.getSerializationConfig().addClassDefinition(portableCampaignClassDefinition);
	}

	public static Campaign getInstance(int id, TokenData td) throws Exception {
		String select = "select * from campaigns where id=" + id;
		var conn = CrosstalkConfig.getInstance().getConnection();
		var stmt = conn.createStatement();
		var prep = conn.prepareStatement(select);
		ResultSet rs = prep.executeQuery();

		ArrayNode inner = JdbcTools.convertToJson(rs);
		if (inner == null || inner.size() == 0)
			return null;

		ObjectNode y = (ObjectNode) inner.get(0);
		Campaign c = new Campaign(y);
		// c.processCreatives();

		if (td != null && td.isAuthorized(c.customer_id) == false)
			return null;

		c.id = id;
		return c;
	}

	public static Campaign getInstance(ResultSet rs) throws Exception {
		ArrayNode inner = JdbcTools.convertToJson(rs);
		if (inner == null || inner.size() == 0)
			return null;

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
	 * Constructor using a JSON Node. Crosstalk uses this to create this object from
	 * SQL
	 * 
	 * @param node JsonNode. The object as defined by SQL, and interpreted as JSON.
	 */
	public Campaign(JsonNode node) throws Exception {
		myNode = node;
		if (node.get("updated_at") != null)
			updated_at = node.get("updated_at").asLong();
		else
			updated_at = System.currentTimeMillis();
		setup(false);
		// processCreatives();
		process();
		doTargets();
	}

	/**
	 * Constructor using a string JSON. This is used by file readers.
	 * 
	 * @param data
	 * @throws Exception
	 */
	public Campaign(String data) throws Exception {

		Campaign camp = DbTools.mapper.readValue(data, Campaign.class);
		init(camp);
	}

	/**
	 * Crosstalk updates a campaign using this.
	 * 
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

		setup(false);
		processCreatives();
		process();
		doTargets();

	}

	/**
	 * An iniitializer from a copy.
	 * 
	 * @param camp
	 * @throws Exception
	 */
	private void init(Campaign camp) throws Exception {
		this.customer_id = customer_id;
		this.id = camp.id;
		this.stringId = "" + camp.id;
		this.isAdx = camp.isAdx;
		this.activate_time = camp.activate_time;
		this.expire_time = camp.expire_time;
		this.ad_domain = camp.ad_domain;
		this.attributes = camp.attributes;
		this.creatives = camp.creatives;
		this.name = camp.name;
		this.forensiq = camp.forensiq;
		this.status = camp.status;
		this.frequencyCap = camp.frequencyCap;
		this.capCount = camp.capCount;
		this.capExpire = camp.capExpire;
		this.capSpec = camp.capSpec;
		this.budget = camp.budget;
		this.updated_at = camp.updated_at;
		this.spendrate = camp.spendrate;
		this.banners = banners;
		this.videos = videos;
		this.natives = natives;
		this.audios = audios;
		if (camp.category != null)
			this.category = camp.category;

		encodeAttributes();
	}

	void overwrite(Campaign camp) throws Exception {
		customer_id = camp.customer_id;
		id = camp.id;
		stringId = "" + camp.id;
		isAdx = camp.isAdx;
		activate_time = camp.activate_time;
		expire_time = camp.expire_time;
		ad_domain = camp.ad_domain;
		attributes = camp.attributes;
		creatives = camp.creatives;
		name = camp.name;
		forensiq = camp.forensiq;
		status = camp.status;
		frequencyCap = camp.frequencyCap;
		capCount = camp.capCount;
		capExpire = camp.capExpire;
		capSpec = camp.capSpec;
		budget = camp.budget;
		updated_at = camp.updated_at;
		spendrate = camp.spendrate;
		banners = banners;
		videos = videos;
		natives = natives;
		audios = audios;
		category = camp.category;
		currentHour = camp.currentHour;
		currentDay = camp.currentDay;
		myNode = camp.myNode;

		encodeAttributes();
	}

	/**
	 * Sort the selection criteria in descending order of number of times false was
	 * selected. Then, after doing that, zero the counters.
	 */
	public void sortNodes() {
		Collections.sort(attributes, nodeSorter);

		for (int i = 0; i < attributes.size(); i++) {
			attributes.get(i).clearFalseCount();
		}

		for (int i = 0; i < creatives.size(); i++) {
			creatives.get(i).sortNodes();
		}
	}

	/**
	 * Find the node with the specified hierarchy string.
	 * 
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
	 * 
	 * @param br       BidRequest. The bid request to query.
	 * @param capSpecs Map. The current cap spec.
	 * @return boolean. Returns true if the IP address is capped, else false.
	 */
	public boolean isCapped(BidRequest br, Map<String, String> capSpecs) {
		if (frequencyCap == null)
			return false;
		return frequencyCap.isCapped(br, capSpecs, name);
	}

	/**
	 * Determine if this bid request + campaign is frequency Governed.
	 * 
	 * @param br BidReuestcount . The bid request to check for governance.
	 * @return boolean. Returns true if this campaign has bid on the same
	 *         user/synthkey in the last 1000 ms.
	 */
	public boolean isGoverned(BidRequest br) {
		if (RTBServer.frequencyGoverner == null || FrequencyGoverner.silent)
			return false;

		return RTBServer.frequencyGoverner.contains(name, br);
	}

	/**
	 * Return the Lucene query string for this campaign's attributes
	 * 
	 * @return String. The lucene query.
	 */

	@JsonIgnore
	public String getLucene() {
		return getLuceneFromAttrs(attributes);
	}

	String getLuceneFromAttrs(List<Node> attributes) {
		String str = "";

		List<String> strings = new ArrayList<String>();
		for (int i = 0; i < attributes.size(); i++) {
			Node x = attributes.get(i);
			String s = x.getLucene();
			if (s != null && s.length() > 0)
				strings.add(s);
		}

		for (int i = 0; i < strings.size(); i++) {
			String s = strings.get(i);
			str += s;
			if (i + 1 < strings.size())
				str += " AND ";
		}

		return str;
	}

	/**
	 * Get a creative of this campaign.
	 * 
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

	public Creative getCreativeById(String id) {
		for (Creative c : creatives) {
			if (("" + c.id).equals(id)) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Creates a copy of this campaign
	 * 
	 * @return Campaign. A campaign that is an exact clone of this one
	 * @throws Exception on JSON parse errors.
	 */
	public Campaign copy() throws Exception {

		String str = DbTools.mapper.writer().writeValueAsString(this);
		Campaign x = DbTools.mapper.readValue(str, Campaign.class);
		x.encodeAttributes();
		return x;
	}

	/**
	 * Constructor with pre-defined node.
	 * 
	 * @param id    String - the id of this campaign.
	 * @param nodes nodes. List - the list of nodes to add.
	 */
	public Campaign(String id, List<Node> nodes) {
		this.name = id;
		this.attributes.addAll(nodes);
	}

	public void encodeIfNeeded() {
		try {
			if (encoded)
				return;
			encoded = true;
			for (Creative c : creatives) {
				c.encodeUrl();
				c.encodeAttributes();
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	/**
	 * Encode the values of all the attributes, instantiating from JSON does not do
	 * this, it's an incomplete serialization Always call this if you add a campaign
	 * without using Configuration.getInstance().addCampaign();
	 * 
	 * @throws Exception if the attributes of the node could not be encoded.
	 */
	public void encodeAttributes() throws Exception {
		for (int i = 0; i < attributes.size(); i++) {
			Node n = attributes.get(i);
			n.setValues();
		}

		if (category == null) {
			category = new ArrayList<String>();
		}

		if (category.size() > 0) {
			String str = "\"cat\":" + DbTools.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(category);
			encodedIab = new StringBuilder(str);
		}

		setWeights(weightAssignment);
	}

	/**
	 * Set this campaign's assigned weights.
	 * 
	 * @param weight String. The weight of the creatives.
	 * @throws Exception on parsing errors
	 */
	public void setWeights(String weight) throws Exception {
		if (weight == null || weight.length() == 0) {
			weightAssignment = null;
			weights = null;
			return;
		}
		weightAssignment = weight;
		weights = new ProportionalEntry(weightAssignment);
	}

	/**
	 * Add an evaluation node to the campaign.
	 * 
	 * @param node Node - the evaluation node to be added to the set.
	 */
	public void add(Node node) {
		attributes.add(node);
	}

	/**
	 * The compareTo method to ensure that multiple campaigns don't exist with the
	 * same id.
	 * 
	 * @param o Object. The object to compare with.
	 * @return int. Returns 1 if the ids match, otherwise 0.
	 */
	@Override
	public int compareTo(Object o) {
		Campaign other = (Campaign) o;
		if (this.name.equals(other.name))
			return 1;

		return 0;
	}

	/**
	 * Returns this object as a JSON string
	 * 
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
	 * 
	 * @param exchange String. The name of the exchange
	 * @return boolean. Returns true if we can use the exchange, otherwise returns
	 *         false.
	 */
	public boolean canUseExchange(String exchange) {
		boolean canUse = false;
		for (Node node : attributes) {
			if (node.bidRequestValues.contains("exchange")) {
				canUse = false;
				Object obj = node.value;
				if (obj instanceof String) {
					String str = (String) obj;
					if (str.equals(exchange)) {
						canUse = true;
					} else {
						canUse = false;
					}
				} else if (obj instanceof List) {
					List<String> list = (List<String>) obj;
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
		Campaign camp = DbTools.mapper.readValue(json, Campaign.class);

		try {
			overwrite(camp);
			// System.out.println("\n\n" + toJson() + "\n\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Node version of init
	 * 
	 * @param node
	 * @throws Exception
	 */
	public void init(JsonNode node, boolean reader) throws Exception {
		myNode = node;
		updated_at = node.get("updated_at").asLong();
		setup(reader);
		// processCreatives();
		process();
		doTargets();
	}

	////////////////////////////

	public void runUsingElk() {

		try {

			Double x = AccountingCache.getInstance().getCampaignTotal("" + id);

			budget.totalCost.getAndAdd(x);
			budget.dailyCost.getAndAdd(x);
			budget.hourlyCost.getAndAdd(x);

			logger.debug("****** BUDGET TEST: Testing budgets at {} CAMPAIGN:{}, id: {}",
					Crosstalk.getInstance().getHour(), name, id);

			boolean z = Crosstalk.getInstance().hourChanged(currentHour);
			logger.debug("!!!!!!!!!!! HOURCHANGED: {}", z);
			logger.debug("Total cost: {}, daily cost: {}, hourly cost: {}", budget.totalCost.getDoubleValue(),
					budget.dailyCost.getDoubleValue(), budget.hourlyCost.getDoubleValue());

			if (x != 0.0 || Crosstalk.getInstance().timeChanged(currentDay, currentHour)) {
				for (Creative c : creatives) {
					c.runUsingElk(this);
				}

				AccountingCache.getInstance().reset("" + id);
				CampaignCache.getInstance().addCampaign(this);

				if (Crosstalk.getInstance().hourChanged(currentHour)) {
					logger.debug("Hour changed, campaign budget set to 0.0 @{} for {}",
							Crosstalk.getInstance().getHour(), stringId);
					budget.hourlyCost.set(0.0);
				}
				if (Crosstalk.getInstance().dayChanged(currentDay)) {
					logger.debug("Day changed, campaign budget set to 0.0 @{} for {}", Crosstalk.getInstance().getDay(),
							stringId);
					budget.dailyCost.set(0.0);
				}

				Crosstalk.getInstance().updateCampaignTotal(stringId, budget.totalCost.getDoubleValue());
				Crosstalk.getInstance().updateCampaignTotalDaily(stringId, budget.dailyCost.getDoubleValue());
				Crosstalk.getInstance().updateCampaignTotalHourly(stringId, budget.hourlyCost.getDoubleValue());

				currentHour = Crosstalk.getInstance().getHour();
				currentDay = Crosstalk.getInstance().getDay();
			}

		} catch (Exception error) {
			var msg = "BUDGETING is not accessible, no accounting data is possible for: " + name;
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
	 * 
	 * @return boolean. Returns true or false depending on if the campaign
	 *         expiration was reached. Always return false if no budget is set (Not
	 *         expired...)
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

		if (regions != null) {
			if (regions.toLowerCase().contains(CrosstalkConfig.getInstance().region.toLowerCase()) == false) {
				return false;
			}
		}

		if (budgetExceeded()) {
			logger.debug("BUDGET EXCEEDED: {}", name);
			return false;
		}

		if (budget == null)
			return true;

		if ((date.getTime() >= budget.activate_time) && (date.getTime() <= budget.expire_time)) {

			if (budget.daypart != null) {
				if (budget.daypart.isActive() != true) {
					logger.debug("Daypart is not active: {}", name);
					return false;
				}
			}

			logger.debug("IS ACTIVE: {}", name);
			return true;
		} else {
			logger.debug("ACTIVATION TIME NOT IN RANGE: {}", name);
			return false;
		}
	}

	public boolean budgetExceeded() throws Exception {
		if (budget == null || budget.totalBudget.doubleValue() == 0)
			return false;

		return checkCampaignBudgets();
	}

	public boolean compareTo(Campaign t) {
		return false;
	}

	public boolean checkCampaignBudgetsTotal() {
		try {

			if (budget != null && budget.totalBudget.doubleValue() != 0) {
				double bdget = budget.totalBudget.doubleValue();
				double spend = budget.totalCost.doubleValue();

				logger.debug("TOTAL {} budget: {} vs spend: {}", id, bdget, spend);
				if (spend >= bdget) {
					return true;
				}
			}
		} catch (Exception error) {
			error.printStackTrace();
			return true;
		}
		return false;
	}

	public boolean checkCampaignBudgetsDaily() {

		try {
			if (budget != null && budget.dailyBudget.doubleValue() != 0) {
				double spend;
				double bdget;

				bdget = budget.dailyBudget.getDoubleValue();
				spend = budget.dailyCost.getDoubleValue();
				logger.debug("DAILY {} budget: {} vs spend: {}", id, bdget, spend);
				if (spend >= bdget) {
					return true;
				}
			}
		} catch (Exception error) {
			error.printStackTrace();
			return true;
		}

		return false;
	}

	public boolean checkCampaignBudgetsHourly() {
		try {
			if (budget != null && budget.hourlyBudget.doubleValue() != 0) {
				double spend;
				double bdget;

				bdget = budget.hourlyBudget.getDoubleValue();
				spend = budget.hourlyCost.getDoubleValue();
				logger.debug("HOURLY {} budget: {} vs spend: {}", id, bdget, spend);
				if (spend >= bdget) {
					return true;
				}
			}
		} catch (Exception error) {
			error.printStackTrace();
			return true;
		}

		return false;
	}

	public boolean checkCampaignBudgets() {
		try {

			if (budget != null) {
				if (checkCampaignBudgetsTotal())
					return true;
				if (checkCampaignBudgetsDaily())
					return true;
				if (checkCampaignBudgetsHourly())
					return true;
			}

		} catch (Exception error) {
			error.printStackTrace();
			return true;
		}

		return false;
	}

	/**
	 * Check and see if this campaign is deletable fron the system
	 * 
	 * @return boolean. If campaign is expired or the total spend has been reached.
	 * @throws Exception on errors computing budgets.
	 */
	public boolean canBePurged() throws Exception {
		if (isExpired())
			return true;
		return checkCampaignBudgets();
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

	public void setup(JsonNode n, boolean reader) throws Exception {
		myNode = n;
		setup(reader);
	}

	void setup(boolean reader) throws Exception {
		// process
		customer_id = myNode.get("customer_id").asText();
		name = myNode.get("name").asText();
		id = myNode.get(CAMPAIGN_ID).asInt();
		stringId = "" + id;

		budget = new Budget();
		budget.totalCost = new AtomicBigDecimal(myNode.get("cost"));
		budget.dailyCost = new AtomicBigDecimal(myNode.get("daily_cost"));
		budget.hourlyCost = new AtomicBigDecimal(myNode.get("hourly_cost"));

		budget.totalBudget = new AtomicBigDecimal(myNode.get(TOTAL_BUDGET));

		if (myNode.get(EXPIRE_TIME) != null)
			expire_time = budget.expire_time = myNode.get(EXPIRE_TIME).asLong();
		if (myNode.get(ACTIVATE_TIME) != null)
			activate_time = budget.activate_time = myNode.get(ACTIVATE_TIME).asLong();

		if (myNode.get(DAYPART) != null && myNode.get(DAYPART) instanceof MissingNode == false) {
			day_parting_utc = myNode.get(DAYPART).asText();
			if (day_parting_utc.equals("null") || day_parting_utc.length() == 0)
				budget.daypart = null;
			else
				budget.daypart = new DayPart(day_parting_utc);
		} else
			budget.daypart = null;

		if (myNode.get("bcat") != null) {
			String str = myNode.get("bcat").asText();
			if (str.trim().length() != 0) {
				if (str.equals("null") == false)
					Targeting.getList(bcat, str);
			}
		}

		if (myNode.get("rules") != null) {
			ArrayNode n = (ArrayNode) myNode.get("rules");
			rules = new ArrayList<Integer>();
			for (int i = 0; i < n.size(); i++) {
				rules.add(n.get(i).asInt());
			}
		}

		if (myNode.get("exchanges") instanceof TextNode) {
			exchanges.clear();
			String[] x = myNode.get("exchanges").asText().split(",");
			for (String s : x) {
				exchanges.add(s.trim());
			}
		} else {
			if (myNode.get("exchanges") != null && myNode.get("exchanges").size() != 0) {
				exchanges.clear();
				ArrayNode an = (ArrayNode) myNode.get("exchanges");
				for (int i = 0; i < an.size(); i++) {
					exchanges.add(an.get(i).asText());
				}
			}
		}

		Object x = myNode.get(DAILY_BUDGET);
		if (x != null && !(x instanceof NullNode)) {
			if (budget.dailyBudget == null
					|| (budget.dailyBudget.doubleValue() != myNode.get(DAILY_BUDGET).asDouble())) {
				budget.dailyBudget = new AtomicBigDecimal(myNode.get(DAILY_BUDGET).asDouble());
			}
		} else
			budget.dailyBudget = null;

		x = myNode.get(HOURLY_BUDGET);
		if (x != null && !(x instanceof NullNode)) {
			if (budget.hourlyBudget == null
					|| (budget.hourlyBudget.doubleValue() != myNode.get(HOURLY_BUDGET).asDouble())) {
				budget.hourlyBudget = new AtomicBigDecimal(myNode.get(HOURLY_BUDGET).asDouble());
			}
		} else
			budget.hourlyBudget = null;

		status = myNode.get("status").asText();
		ad_domain = myNode.get("ad_domain").asText();
		forensiq = myNode.get("forensiq").asBoolean();
		spendrate = myNode.get("spendrate").asInt();
		regions = myNode.get("regions").asText();
		if (myNode.get("banners") != null)
			banners = getList(myNode.get("banners"));
		if (myNode.get("videos") != null)
			videos = getList(myNode.get("videos"));
		if (myNode.get("audios") != null)
			audios = getList(myNode.get("audios"));
		if (myNode.get("natives") != null)
			natives = getList(myNode.get("natives"));

		if (myNode.get("capspec") != null) {
			capSpec = myNode.get("capspec").asText();
		}
		if (myNode.get("capexpire") != null) {
			capExpire = myNode.get("capexpire").asInt();
		}
		if (myNode.get("capcount") != null) {
			capCount = myNode.get("capcount").asInt();
		}
		if (myNode.get("capunit") != null) {
			capUnit = myNode.get("capunit").asText();
		}

		processCreatives();
		/**
		 * Do this last
		 */

		x = myNode.get("target_id");
		if (x != null)
			target_id = ((JsonNode) x).asInt();
	}

	List<Integer> getList(JsonNode n) {
		List<Integer> list = new ArrayList<>();
		if (n instanceof ArrayNode == false)
			throw new RuntimeException("Node is not not an array");
		ArrayNode an = (ArrayNode) n;
		for (int i = 0; i < an.size(); i++) {
			list.add(an.get(i).asInt());
		}
		return list;
	}

	/**
	 * Load the creatives from the db and attach them to the the campaign.
	 * 
	 * @throws Exception if there is a database error.
	 */
	public void processCreatives() throws Exception {
		creatives = new ArrayList<>();
		try {
			banners.stream().forEach(id -> creatives.add(Creative.getInstance(id, "banner", customer_id)));
			videos.stream().forEach(id -> creatives.add(Creative.getInstance(id, "video", customer_id)));
			audios.stream().forEach(id -> creatives.add(Creative.getInstance(id, "audio", customer_id)));
			natives.stream().forEach(id -> creatives.add(Creative.getInstance(id, "native", customer_id)));
		} catch (Exception error) {
			logger.error("Database error loading creatives for campaign id: " + id + ", error: " + error.getMessage());
		}
	}

	public boolean process() throws Exception {
		boolean change = false;
		int n = creatives.size();
		List<Creative> list = new ArrayList<Creative>();

		for (Creative c : parkedCreatives) {
			if (!c.budgetExceeded(this)) {
				unpark(c);
				change = true;
			}
		}

		for (Creative creative : creatives) {
			if (creative.budgetExceeded(this) || creative.isExpired()) {
				list.add(creative);
				change = true;
			}
		}

		for (Creative c : list) {
			park(c);
		}

		if (myNode.get("updated_at") instanceof MissingNode != false)
			updated_at = myNode.get("updated_at").asLong();

		return change;
	}

	JsonNode getMyNode() {
		return myNode;
	}

	protected void doTargets() throws Exception {
		if (target_id != 0) {
			try {
			targeting = Targeting.getInstance(target_id, null);
			} catch (Exception err) {
				logger.warn("Campaign id: " + id + " references unknown target id: " + target_id);
				status = "offline";
			}
		}
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
			List<String> spec = new ArrayList<>();
			Targeting.getList(spec, capSpec);
			frequencyCap = new FrequencyCap(spec, capCount, capExpire, capUnit);
		}
		doStandardRtb();
	}

	/**
	 * Call after you compile!
	 * 
	 * @throws Exception on JSON errors.
	 */
	void doStandardRtb() throws Exception {
		rules.forEach(id -> {
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
	 * 
	 * @return String. The reasons why...
	 * @throws Exception on ES errors.
	 */
	public String report() throws Exception {
		String reason = "";
		if (budgetExceeded()) {
			if (reason.length() != 0)
				reason += " ";
			if (checkCampaignBudgetsTotal())
				reason += "Campaign total budget exceeded. ";
			if (checkCampaignBudgetsDaily())
				reason += "Campaign daily budget exceeded. ";
			if (checkCampaignBudgetsHourly())
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
				r.put("creative", p.impid);
				List<String> reasons = new ArrayList<String>();
				if (p.budgetExceeded(this)) {
					reasons.add("nobudget");
				}

				r.put("reasons", reasons);
			}
		}

		if (regions != null) {
			if (regions.toLowerCase().contains(CrosstalkConfig.getInstance().region.toLowerCase()) == false) {
				reason += "Campaign in wrong bidding region, campaign in: " + regions + ", this region: "
						+ CrosstalkConfig.getInstance().region + ". ";
			}
		}

		if (status.equals("runnable") == false) {
			reason += "Marked offline. ";
		}

		if (xreasons.size() != 0) {
			reason += DbTools.mapper.writeValueAsString(xreasons);
		}
		if (reason.length() > 0)
			logger.info("Campaign {} not loaded: {}", name, reason);

		if (reason.length() == 0)
			reason = "Runnable";
		return reason;
	}

	public boolean isRunnable() throws Exception {
		if (status == null)
			status = "runnable";
		if (!status.equals("runnable"))
			return false;
		return true;
	}

	public static void touchCampaignWithCreative(int id) throws Exception {

		PreparedStatement st = CrosstalkConfig.getInstance().getConnection()
				.prepareStatement("SELECT * FROM campaigns WHERE id=?");
		st.setInt(1, id);
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			int cid = rs.getInt("id");
			Campaign c = Campaign.getInstance(id, null);
			Campaign.toSql(c, CrosstalkConfig.getInstance().getConnection()).execute();
			c.encoded = false;
			CampaignBuilderWorker w = new CampaignBuilderWorker(c);
			w.run();
		}

		st.close();
	}

	/**
	 * Remove the target id from all campaigns pinned to it.
	 * 
	 * @param id int. The target id.
	 * @throws Exception on SQL errors;
	 */
	public static void removeTargetFromCampaigns(int id, TokenData td) throws Exception {
		PreparedStatement st = CrosstalkConfig.getInstance().getConnection()
				.prepareStatement("select * from campaigns where target_id=?");
		st.setInt(1, id);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			int cid = rs.getInt("id");
			Campaign c = Campaign.getInstance(id, td);
			if (c != null) {
				c.target_id = 0;
				Campaign.toSql(c, CrosstalkConfig.getInstance().getConnection()).execute();
				CampaignBuilderWorker w = new CampaignBuilderWorker(c);
				w.run();
			}
		}

		st.close();
	}

	public static void removeCreativeFromCampaigns(int id, String key, TokenData td) throws Exception {
		switch (key) {
		case "banner":
			removeSpecificTypeCreativeFromCampaigns("banners", id, td);
			break;
		case "video":
			removeSpecificTypeCreativeFromCampaigns("videos", id, td);
			break;
		case "audio":
			removeSpecificTypeCreativeFromCampaigns("audios", id, td);
			break;
		case "native":
			removeSpecificTypeCreativeFromCampaigns("natives", id, td);
			break;
		default:
			throw new Exception("Don't understand type: " + key);
		}
	}

	public static void removeSpecificTypeCreativeFromCampaigns(String table, int id, TokenData td) throws Exception {
		PreparedStatement st = CrosstalkConfig.getInstance().getConnection()
				.prepareStatement("SELECT * FROM campaigns WHERE ? IN (SELECT unnest(" + table + ") FROM campaigns)");
		st.setInt(1, id);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			int cid = rs.getInt("id");
			Campaign c = Campaign.getInstance(id, td);
			if (td.isAuthorized(c.customer_id)) {
				int index = -1;
				switch (table) {
				case "banners":
					index = c.banners.indexOf(id);
					if (c.banners != null)
						c.banners.remove(index);
					break;
				case "banner_videos":
					index = c.videos.indexOf(id);
					if (c.videos != null)
						c.videos.remove(index);
					break;
				case "banner_audios":
					index = c.audios.indexOf(id);
					if (c.audios != null)
						c.audios.remove(index);
					break;
				case "banner_natives":
					index = c.natives.indexOf(id);
					if (c.natives != null)
						c.natives.remove(index);
					break;
				default:
					throw new Exception("Cant update cratives type " + table + " for campaign " + cid);
				}
				c.creatives.clear();
				c.processCreatives();

				Campaign.toSql(c, CrosstalkConfig.getInstance().getConnection()).execute();
				CampaignBuilderWorker w = new CampaignBuilderWorker(c);
				w.run();
			}
		}

		st.close();
	}

	public static void removeRuleFromCampaigns(int id, TokenData td) throws Exception {
		PreparedStatement st = CrosstalkConfig.getInstance().getConnection()
				.prepareStatement("SELECT * FROM campaigns WHERE ? IN (SELECT unnest(rules) FROM campaigns)");
		st.setInt(1, id);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			int cid = rs.getInt("id");
			Campaign c = Campaign.getInstance(id, td);
			if (td.isAuthorized(c.customer_id)) {
				int index = c.banners.indexOf(id);
				c.rules.remove(index);

				c.process();

				Campaign.toSql(c, CrosstalkConfig.getInstance().getConnection()).execute();
				CampaignBuilderWorker w = new CampaignBuilderWorker(c);
				w.run();
			}
		}

		st.close();
	}

	public void saveToDatabase() throws Exception {
		PreparedStatement st = null;
		if (id == 0)
			st = doNew(this, CrosstalkConfig.getInstance().getConnection());
		else
			st = doUpdate(this, CrosstalkConfig.getInstance().getConnection());

		st.execute();
	}

	public static PreparedStatement toSql(Campaign c, Connection conn) throws Exception {
		if (c.id == 0)
			return doNew(c, conn);
		return doUpdate(c, conn);
	}

	static PreparedStatement doNew(Campaign c, Connection conn) throws Exception {
		PreparedStatement p = null;
		Array rulesArray = null;
		if (c.rules != null) {
			rulesArray = conn.createArrayOf("int", c.rules.toArray());
		}

		String sql = "INSERT INTO campaigns (" + "activate_time," + "expire_time," + "cost," + "ad_domain," + "name,"
				+ "status," + "budget_limit_daily," + "budget_limit_hourly," + "total_budget," + "forensiq,"
				+ "created_at," + "exchanges," + "regions," + "target_id," + "rules," + "banners," + "videos,"
				+ "audios," + "natives," + "day_parting_utc," + "capspec," + "capcount," + "capexpire," + "capunit,"
				+ "customer_id," + "spendrate) VALUES("
				+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

		p = conn.prepareStatement(sql);

		if (c.budget != null) {
			p.setTimestamp(1, new Timestamp(c.budget.activate_time));
			p.setTimestamp(2, new Timestamp(c.budget.expire_time));
		} else {
			p.setNull(1, Types.TIMESTAMP);
			p.setNull(2, Types.TIMESTAMP);
		}
		p.setDouble(3, c.costAsDouble());
		p.setString(4, c.ad_domain);
		p.setString(5, c.name);
		p.setString(6, c.status);

		if (c.budget == null || c.budget.dailyBudget == null) {
			p.setNull(7, Types.DECIMAL);
			p.setNull(8, Types.DECIMAL);
			p.setNull(9, Types.DECIMAL);
		} else {
			p.setDouble(7, c.budget.dailyBudget.doubleValue());
			p.setDouble(8, c.budget.hourlyBudget.doubleValue());
			p.setDouble(9, c.budget.totalBudget.doubleValue());
		}
		if (c.forensiq == null)
			p.setNull(10, Types.BOOLEAN);
		else
			p.setBoolean(10, c.forensiq);
		p.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
		if (c.exchanges == null || c.exchanges.size() == 0)
			p.setNull(12, Types.VARCHAR);
		else {
			var s = "";
			for (int i = 0; i < c.exchanges.size(); i++) {
				s += c.exchanges.get(i);
				if (i + 1 < c.exchanges.size())
					s += ",";
			}
			p.setString(12, s);
		}

		if (c.regions == null)
			p.setNull(13, Types.VARCHAR);
		else
			p.setString(13, c.regions);
		if (c.target_id == 0)
			p.setNull(14, Types.INTEGER);
		else
			p.setInt(14, c.target_id);
		if (rulesArray != null)
			p.setArray(15, rulesArray);
		else
			p.setNull(15, Types.ARRAY);

		if (c.banners != null) {
			System.out.println("BANNERS: " + c.banners);
			p.setArray(16, conn.createArrayOf("int", c.banners.toArray()));
		} else
			p.setNull(16, Types.ARRAY);
		if (c.videos != null)
			p.setArray(17, conn.createArrayOf("int", c.videos.toArray()));
		else
			p.setNull(17, Types.ARRAY);
		if (c.audios != null)
			p.setArray(18, conn.createArrayOf("int", c.audios.toArray()));
		else
			p.setNull(18, Types.ARRAY);
		if (c.natives != null)
			p.setArray(19, conn.createArrayOf("int", c.natives.toArray()));
		else
			p.setNull(19, Types.ARRAY);

		if (c.day_parting_utc != null)
			p.setString(20, c.day_parting_utc);
		else
			p.setNull(20, Types.VARCHAR);

		if (c.capSpec != null)
			p.setString(21, c.capSpec);
		else
			p.setNull(21, Types.VARCHAR);
		if (c.capCount == null)
			p.setNull(22, Types.INTEGER);
		else
			p.setInt(22, c.capCount);
		if (c.capExpire == null)
			p.setNull(23, Types.INTEGER);
		else
			p.setInt(23, c.capExpire);
		if (c.capUnit != null)
			p.setString(24, c.capUnit);
		else
			p.setNull(24, Types.VARCHAR);

		p.setString(25, c.customer_id);

		p.setInt(26, (int) c.spendrate);

		return p;
	}

	static PreparedStatement doUpdate(Campaign c, Connection conn) throws Exception {
		PreparedStatement p = null;
		Array rulesArray = null;
		c.updated_at = System.currentTimeMillis();
		if (c.rules != null) {
			rulesArray = conn.createArrayOf("int", c.rules.toArray());
		}

		String sql = "UPDATE campaigns SET " + "activate_time=?," + "expire_time=?," + "ad_domain=?," + "name=?,"
				+ "status=?," + "budget_limit_daily=?," + "budget_limit_hourly=?," + "total_budget=?," + "forensiq=?,"
				+ "updated_at=?," + "exchanges=?," + "regions=?," + "target_id=?," + "rules=?," + "banners=?,"
				+ "videos=?," + "audios=?," + "natives=?," + "day_parting_utc=?," + "capspec=?," + "capcount=?,"
				+ "capexpire=?," + "capunit=?," + "spendrate=? WHERE id=?";

		p = conn.prepareStatement(sql);

		if (c.budget != null) {
			p.setTimestamp(1, new Timestamp(c.budget.activate_time));
			p.setTimestamp(2, new Timestamp(c.budget.expire_time));
		} else {
			p.setNull(1, Types.TIMESTAMP);
			p.setNull(2, Types.TIMESTAMP);
		}

		p.setString(3, c.ad_domain);
		p.setString(4, c.name);
		p.setString(5, c.status);
		if (c.budget == null || c.budget.dailyBudget == null) {
			p.setNull(6, Types.DECIMAL);
			p.setNull(7, Types.DECIMAL);
			p.setNull(8, Types.DECIMAL);
		} else {
			p.setDouble(6, c.budget.dailyBudget.doubleValue());
			p.setDouble(7, c.budget.hourlyBudget.doubleValue());
			p.setDouble(8, c.budget.totalBudget.doubleValue());
		}
		if (c.forensiq == null)
			p.setNull(9, Types.VARCHAR);
		else
			p.setBoolean(9, c.forensiq);
		p.setTimestamp(10, new Timestamp(System.currentTimeMillis()));

		if (c.exchanges == null || c.exchanges.size() == 0)
			p.setNull(11, Types.VARCHAR);
		else {
			var s = "";
			for (int i = 0; i < c.exchanges.size(); i++) {
				s += c.exchanges.get(i);
				if (i + 1 < c.exchanges.size())
					s += ",";
			}
			// System.out.println("=======>" + c.exchanges);
			p.setString(11, s);
		}

		if (c.regions == null)
			p.setNull(12, Types.VARCHAR);
		else
			p.setString(12, c.regions);
		if (c.target_id == 0)
			p.setNull(13, Types.INTEGER);
		else
			p.setInt(13, c.target_id);

		if (rulesArray != null)
			p.setArray(14, rulesArray);
		else
			p.setNull(14, Types.ARRAY);

		if (c.banners != null) {
			// System.out.println("======> BANNERS: " + c.banners);
			p.setArray(15, conn.createArrayOf("int", c.banners.toArray()));
		} else
			p.setNull(15, Types.ARRAY);
		if (c.videos != null)
			p.setArray(16, conn.createArrayOf("int", c.videos.toArray()));
		else
			p.setNull(16, Types.ARRAY);
		if (c.audios != null)
			p.setArray(17, conn.createArrayOf("int", c.audios.toArray()));
		else
			p.setNull(17, Types.ARRAY);
		if (c.natives != null)
			p.setArray(18, conn.createArrayOf("int", c.natives.toArray()));
		else
			p.setNull(18, Types.ARRAY);

		if (c.day_parting_utc == null)
			p.setNull(19, Types.VARCHAR);
		else
			p.setString(19, c.day_parting_utc);

		if (c.capSpec != null)
			p.setString(20, c.capSpec);
		else
			p.setNull(20, Types.VARCHAR);
		if (c.capCount == null)
			p.setNull(21, Types.INTEGER);
		else
			p.setInt(21, c.capCount);
		if (c.capExpire == null)
			p.setNull(22, Types.INTEGER);
		else
			p.setInt(22, c.capExpire);
		if (c.capUnit != null)
			p.setString(23, c.capUnit);
		else
			p.setNull(23, Types.VARCHAR);

		p.setInt(24, (int) c.spendrate);

		p.setInt(25, c.id);

		return p;
	}

	public Creative getCreative(String id, String type) {
		int xid = Integer.valueOf(id);
		for (Creative c : creatives) {
			if (c.id == xid && c.type.equals(type))
				return c;
		}
		return null;
	}

	public void reloadBudgetFromDb() throws Exception {
		String select = "select cost, hourly_cost, daily_cost from campaigns where id=?";
		PreparedStatement st = CrosstalkConfig.getInstance().getConnection().prepareStatement(select);
		st.setInt(1, id);
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			budget.totalCost = new AtomicBigDecimal(rs.getDouble("cost"));
			budget.dailyCost = new AtomicBigDecimal(rs.getDouble("daily_cost"));
			budget.hourlyCost = new AtomicBigDecimal(rs.getDouble("hourly_cost"));
		}

		st.close();
	}
}
