package com.jacamars.dsp.rtb.common;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.core.util.BufferRecyclers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jacamars.dsp.crosstalk.budget.AtomicBigDecimal;
import com.jacamars.dsp.crosstalk.budget.BudgetController;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.crosstalk.budget.CrosstalkConfig;
import com.jacamars.dsp.crosstalk.budget.RtbStandard;
import com.jacamars.dsp.crosstalk.budget.Targeting;
import com.jacamars.dsp.rtb.bidder.MimeTypes;
import com.jacamars.dsp.rtb.bidder.SelectedCreative;
import com.jacamars.dsp.rtb.exchanges.Nexage;
import com.jacamars.dsp.rtb.exchanges.adx.AdxCreativeExtensions;

import com.jacamars.dsp.rtb.nativeads.creative.NativeCreative;
import com.jacamars.dsp.rtb.pojo.*;
import com.jacamars.dsp.rtb.probe.Probe;
import com.jacamars.dsp.rtb.tools.Env;
import com.jacamars.dsp.rtb.tools.JdbcTools;
import com.jacamars.dsp.rtb.tools.MacroProcessing;

/**
 * An object that encapsulates the 'creative' (the ad served up and it's
 * attributes). The creative represents the physical object served up by the
 * bidder to the mobile device. The creative contains the image url, the pixel
 * url, and the referring url. The creative will them be used to create the
 * components of the RTB 2 bid
 * 
 * @author Ben M. Faul
 *
 */
public class Creative  {
	/** The forward URL used with this creative */
	public String forwardurl;
	/** The encoded version of the forward url used by this creative */
	private transient String encodedFurl;
	/** Database id */
	public int id;
	/* The image url used by this creative */
	public String imageurl;
	/** The encoded image URL used by this creative */
	private transient String encodedIurl;
	/** The impression id of this creative */
	public String impid;
	
	/** The width of this creative */
	public Integer w;
	/** The height of this creative */
	public Integer h;
	
	public Dimensions dimensions;
	
	/** The string specification of deals */
	public String dealSpec;
	/** sub-template for banner */
	public String subtemplate;
	/** Private/preferred deals */
	public Deals deals = new Deals();
	/** String representation of w */
	transient public String strW;
	/** String representation of h */
	transient public String strH;
	/** String representation of price */
	transient public String strPrice;
	/** Attributes applied to all impressions */
	public List<Node> attributes = new ArrayList<Node>();
	/** Input ADM field */
	public List<String> adm;
	/** The encoded version of the adm as a single string */
	public transient String encodedAdm;
	// unencoded adm of the
	public transient String unencodedAdm;
	/** Extensions needed by SSPs */
	public Map<String,String> extensions = null;
	// Currency
	public String cur = "USD";
	/** Width range specification */
	public String width_range;
	/** height range specification */
	public String  height_range;
	/** Bith w and h specification */
	public String width_height_list;
	
	/** if this is a video creative (NOT a native content video) its protocol */
	public Integer videoProtocol;
	/**
	 * if this is a video creative (NOT a native content video) , the duration
	 * in seconds
	 */
	public Integer videoDuration;
	/** If this is a video (Not a native content, the linearity */
	public Integer videoLinearity;
	/** The videoMimeType */
	public String videoMimeType;
	/**
	 * vast-url, a non standard field for passing an http reference to a file
	 * for the XML VAST
	 */
	public String vasturl;
	/** The price associated with this creative */
	public double price = .01;

	/** Selection weight. Weight is 1 to n. >> is heavier weight. */
	public int weight = 1;
	
	// If this creative is tagged with categories. Used by bidswitch for example
	public List<String> categories;

	/** SQL Name of this creative */
	public String name;
	
	// /////////////////////////////////////////////
	/** Native content assets */
	public NativeCreative nativead;

	/**
	 * Don't use the template, use exactly what is in the creative for the ADM
	 */
	public boolean adm_override = false;

	/** If this is an Adx type creative, here is the payload */
	public AdxCreativeExtensions adxCreativeExtensions;

	@JsonIgnore
	public transient StringBuilder smaatoTemplate = null;
	// //////////////////////////////////////////////////

	/** The macros this particular creative is using */
	@JsonIgnore
	public transient List<String> macros = new ArrayList<String>();
	
	// Alternate to use for the adid, instead of the one in the creative. This cab
	// happen if SSPs have to assign the id ahead of time.
	public transient String alternateAdId;

	/* creative's status */
	public String status;

	/* Only Active creative is allowed to bid */
	public static String ALLOWED_STATUS = "Active";

	public List<Integer> rules = new ArrayList<>();

	/** When this is not null, this means this creative is a proxy for a list of rotating creatives */
	public Map<String,Creative> subCreatives;

	/** These are common attributes across all impressions. */
	@JsonIgnore
	public List<Node> fixedNodes = new ArrayList<Node>();

	/** A sorter for the campaign/creative attributes, who is most likely to cause a false will bubble up */
	private SortNodesFalseCount nodeSorter = new SortNodesFalseCount();
	
	/** The budget for this creative */
	public Budget budget = new Budget();
	
	/** This class's logger */
	static final Logger logger = LoggerFactory.getLogger(Creative.class);
	
	/**
	 * Components used for creating creatives from JSON derived from SQL
	 */
	/** The id of the cre4ative, as a string */
	transient String bannerid; 
	/** Width of the creative */
	public int width = 0;	
	/** Height of the creative */
	public int height = 0;
	/** The type, as in 'banner' or 'video' */
	transient String type;
	/** The json node derived from SQL */
	transient JsonNode node;
	// //////////////// BANNER SPECIFIC TARGETING	
	/** When true, this is a banner, else it is a video. Will need updating for native and audio support */
	public boolean isBanner;
	public boolean isVideo;
	public boolean isAudio;
	public boolean isNative;
	/** The content type of the banner template */
	public String contenttype = "";
	/** The HTML snippet for the banner */
	public String htmltemplate = "";
	// ////////////// VIDEO SPECIFIC TARGETTING	
	/** The video duration */
	transient int video_duration = 0;	
	/** The video width */
	transient int video_width = 0;
	/** The video height */
	transient int video_height = 0;	
	/** The video type */
	transient String video_type = "";
	/** The video XML */
	transient String video_data = null;
	/** The video VAST protcol */
	transient int video_protocol = 2;	
	/** The video linearity */
	transient int video_linearity = 1;
	/** The bitrate of the video */
	transient Integer video_bitrate;
	/** The mime type of the video */
	transient String video_mimetype = null;
	/** The table name where this thing is stored in sql */
	transient String tableName = null;
	/** The position on the page for the creative */
	public String position;
	transient AtomicBigDecimal bid_ecpm = new AtomicBigDecimal(0);
	transient boolean interstitialOnly = false;
	private final String INTERSTITIAL = "interstitial";
	/** SQL name for the total cost attribute */
	private static final String TOTAL_COST = "total_cost";	
	/** SQL name for the hourly cost attribute */
	private static final String HOURLY_COST = "hourly_cost";	
	/** SQL name for the daily cost attribute */
	private static final String DAILY_COST = "daily_cost";	
	/** SQL name for the campaign that owns this record */
	private static final String CAMPAIGN_ID = "campaign_id";	
	/** SQL name for the updated attribute */
	private static final String UPDATED = "updated_at";	
	/** SQL name for the content type attribute */
	private static final String CONTENT_TYPE = "contenttype";	
	/** SQL name for the html snippet for this banner */
	private static final String HTML_TEMPLATE = "htmltemplate";	
	/** SQL name for the daily budget of this creative */
	private static final String DAILY_BUDGET = "daily_budget";	
	/** SQL name for the hourly budget */
	private static final String HOURLY_BUDGET = "hourly_budget";
	/** SQL name for the vast data attribute */
	private static final  String VAST_DATA = "vast_video_outgoing_file";

	public static Creative getInstance(int id, String key) {
		switch(key.toLowerCase()) {
		case "banner":
			return getBannerInstance(id);
		case "video":
			return getVideoInstance(id);
		case "audio":
			return getAudioInstance(id);
		case "native":
			return getNativeInstance(id);
		}
		throw new RuntimeException("Can't instantiate unknown type: " + key);
	}

	public static Creative getBannerInstance(int id) {
		try {
		String select = "select * from banners where id="+id;
		var conn = CrosstalkConfig.getInstance().getConnection();
		var stmt = conn.createStatement();
		var prep = conn.prepareStatement(select);
		ResultSet rs = prep.executeQuery();
		
		ArrayNode inner = JdbcTools.convertToJson(rs);
		ObjectNode y = (ObjectNode) inner.get(0);
		Creative c = new Creative(y,"banner");
		c.id = id;
		return c;
		} catch (Exception error) {
			throw (RuntimeException)error;
		}
	
	}
	
	public static Creative getVideoInstance(int id) {
		try {
			String select = "select * from banner_videos where id="+id;
			var conn = CrosstalkConfig.getInstance().getConnection();
			var stmt = conn.createStatement();
			var prep = conn.prepareStatement(select);
			ResultSet rs = prep.executeQuery();
			
			ArrayNode inner = JdbcTools.convertToJson(rs);
			ObjectNode y = (ObjectNode) inner.get(0);
			Creative c = new Creative(y,"video");
			c.id = id;
			return c;
			} catch (Exception error) {
				throw (RuntimeException)error;
			}
	}
	
	public static Creative getAudioInstance(int id) {
		try {
			String select = "select * from banner_audios where id="+id;
			var conn = CrosstalkConfig.getInstance().getConnection();
			var stmt = conn.createStatement();
			var prep = conn.prepareStatement(select);
			ResultSet rs = prep.executeQuery();
			
			ArrayNode inner = JdbcTools.convertToJson(rs);
			ObjectNode y = (ObjectNode) inner.get(0);
			Creative c = new Creative(y,"audio");
			c.id = id;
			return c;
			} catch (Exception error) {
				throw (RuntimeException)error;
			}
	}
	
	public static Creative getNativeInstance(int id) {
		try {
			String select = "select * from banner_natives where id="+id;
			var conn = CrosstalkConfig.getInstance().getConnection();
			var stmt = conn.createStatement();
			var prep = conn.prepareStatement(select);
			ResultSet rs = prep.executeQuery();
			
			ArrayNode inner = JdbcTools.convertToJson(rs);
			ObjectNode y = (ObjectNode) inner.get(0);
			Creative c = new Creative(y,"native");
			c.id = id;
			return c;
			} catch (Exception error) {
				throw (RuntimeException)error;
			}
	}
	
	public static PreparedStatement toSql(Creative c, Connection conn) throws Exception {
		String table = null;
		if (c.id == 0) 
			return doNew(c, conn);
		return doUpdate(c, conn);
	}
	
	static PreparedStatement doNew(Creative c, Connection conn) throws Exception {
		PreparedStatement p = null;
		String table = getTable(c);
		String rules = "";
		for (int i=0;i<c.rules.size();i++) {
			rules += c.rules.get(i);
			if (i+1 < c.rules.size()) rules += ",";
		}
		
		String sql = "INSERT INTO " + table + " ("
				+"interval_start,"
				+"interval_end,"
				+"total_budget,"
				+"daily_budget,"
				+"hourly_budget,"
				+"bid_ecpm,"
				+"total_cost,"
				+"daily_cost,"
				+"hourly_cost,"
				+"created_at,"
				+"updated_at,"
				+"rules,"
				+"deals,"
				+"interstitial,"
				+"width_range,"
				+"height_range,"
				+"width_height_list,"
				+"name,"
				+"cur,";
		
		if (c.isBanner) {
			sql += "imageurl,"
					+"width,"
					+"height,"
					+"contenttype,"
					+"htmltemplate,"
					+"position) VALUES ("
			+"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,   ?,?,?,?,?,?)";
			p = conn.prepareStatement(sql);	
			p.setString(20, c.imageurl);
			p.setInt(21, c.width);
			p.setInt(22, c.height);
			p.setString(23, c.contenttype);
			p.setString(24, c.htmltemplate);
			if (c.position != null)
				p.setString(25,c.position);
			else
				p.setNull(25, Types.VARCHAR);
		} else
		if (c.isVideo) {
			
		} else 
		if (c.isAudio) {
			
		} else
		if (c.isNative) {
			
		} else
			throw new Exception("Can't tell what kind of creative " + c.name + " is.");
		
		
		if (c.budget != null) {
			p.setTimestamp(1,new Timestamp(c.budget.activate_time));
			p.setTimestamp(2,new Timestamp(c.budget.expire_time));
		} else {
			p.setNull(1, Types.TIMESTAMP);
			p.setNull(2, Types.TIMESTAMP);
		}
		
		if (c.budget != null && c.budget.totalBudget != null)
			p.setDouble(3,c.budget.totalBudget.doubleValue());
		else
			p.setNull(3, Types.DOUBLE);
		
		if (c.budget != null && c.budget.dailyBudget != null)
			p.setDouble(4,c.budget.dailyBudget.doubleValue());
		else
			p.setNull(4, Types.DOUBLE);
		
		if (c.budget != null && c.budget.hourlyBudget != null)
			p.setDouble(5,c.budget.hourlyBudget.doubleValue());
		else
			p.setNull(5, Types.DOUBLE);
		
		p.setDouble(6, c.price);

		if (c.budget != null) {
			if (c.budget.totalCost != null)
				p.setDouble(7,c.budget.totalCost.doubleValue());
			else
				p.setNull(7, Types.DOUBLE);
			if (c.budget.dailyCost != null)
				p.setDouble(8,c.budget.dailyCost.doubleValue());
			else
				p.setNull(8, Types.DOUBLE);
			if (c.budget.hourlyCost != null)
				p.setDouble(9,c.budget.hourlyCost.doubleValue());
			else
				p.setNull(9, Types.DOUBLE);
		} else {
			p.setNull(7, Types.DOUBLE);
			p.setNull(8, Types.DOUBLE);
			p.setNull(9, Types.DOUBLE);
		}
		
		p.setTimestamp(10,new Timestamp(System.currentTimeMillis()));
		p.setTimestamp(11,new Timestamp(System.currentTimeMillis()));
		p.setString(12, rules);
		p.setString(13, c.dealSpec);
		if (c.interstitialOnly)
			p.setInt(14, 1);
		else
			p.setInt(14, 0);
		if (c.width_range != null)
			p.setString(15, c.width_range);
		else
			p.setNull(15,  Types.VARCHAR);
		if (c.height_range != null)
			p.setString(16, c.width_range);
		else
			p.setNull(16,  Types.VARCHAR);
		if (c.width_height_list != null)
			p.setString(17, c.width_range);
		else
			p.setNull(17,  Types.VARCHAR);
		p.setString(18, c.name);		
		p.setString(19, c.cur);
		
		return p;
	}
	
	static String getTable(Creative c) throws Exception {
		String table = null;
	if (c.isAudio)
		table = "banner_audios";
	else
	if (c.isNative)
		table = "banner_natives";
	else
	if (c.isVideo)
		table = "banner_videos";
	else
	if (c.isBanner)
		table = "banners";
	else
		throw new Exception("Can't tell what kind of creative id: " + c.id + " is.");
	return table;
	}
	
	static PreparedStatement doUpdate(Creative c, Connection conn) throws Exception {
		PreparedStatement p = null;
		String table = getTable(c);
		String rules = "";
		for (int i=0;i<c.rules.size();i++) {
			rules += c.rules.get(i);
			if (i+1 < c.rules.size()) rules += ",";
		}
		
		String sql = "UPDATE " + table + " SET "
				+"interval_start=?,"
				+"interval_end=?,"
				+"total_budget=?,"
				+"daily_budget=?,"
				+"hourly_budget=?,"
				+"bid_ecpm=?,"
				+"total_cost=?,"
				+"daily_cost=?,"
				+"hourly_cost=?,"
				+"updated_at=?,"
				+"rules=?,"
				+"deals=?,"
				+"interstitial=?,"
				+"width_range=?,"
				+"height_range=?,"
				+"width_height_list=?,"
				+"name=?,"
				+"cur=?,";
		
		if (c.isBanner) {
			sql += "imageurl=?,"
					+"width=?,"
					+"height=?,"
					+"contenttype=?,"
					+"htmltemplate=?,"
					+"position=? WHERE id=?";
			p = conn.prepareStatement(sql);	
			
			p.setString(19, c.imageurl);
			p.setInt(20, c.width);
			p.setInt(21, c.height);
			p.setString(22, c.contenttype);
			p.setString(23, c.htmltemplate);
			if (c.position != null)
				p.setString(24,c.position);
			else
				p.setNull(24, Types.VARCHAR);
			
			p.setInt(25, c.id);
		} else
		if (c.isVideo) {
			
		} else 
		if (c.isAudio) {
			
		} else
		if (c.isNative) {
			
		} else
			throw new Exception("Can't tell what kind of creative " + c.name + " is.");
		
		
		if (c.budget != null) {
			p.setTimestamp(1,new Timestamp(c.budget.activate_time));
			p.setTimestamp(2,new Timestamp(c.budget.expire_time));
		} else {
			p.setNull(1, Types.TIMESTAMP);
			p.setNull(2, Types.TIMESTAMP);
		}
		
		if (c.budget != null && c.budget.totalBudget != null)
			p.setDouble(3,c.budget.totalBudget.doubleValue());
		else
			p.setNull(3, Types.DOUBLE);
		
		if (c.budget != null && c.budget.dailyBudget != null)
			p.setDouble(4,c.budget.dailyBudget.doubleValue());
		else
			p.setNull(4, Types.DOUBLE);
		
		if (c.budget != null && c.budget.hourlyBudget != null)
			p.setDouble(5,c.budget.hourlyBudget.doubleValue());
		else
			p.setNull(5, Types.DOUBLE);
		
		p.setDouble(6, c.price);

		if (c.budget != null) {
			if (c.budget.totalCost != null)
				p.setDouble(7,c.budget.totalCost.doubleValue());
			else
				p.setNull(7, Types.DOUBLE);
			if (c.budget.dailyCost != null)
				p.setDouble(8,c.budget.dailyCost.doubleValue());
			else
				p.setNull(8, Types.DOUBLE);
			if (c.budget.hourlyCost != null)
				p.setDouble(9,c.budget.hourlyCost.doubleValue());
			else
				p.setNull(9, Types.DOUBLE);
		} else {
			p.setNull(7, Types.DOUBLE);
			p.setNull(8, Types.DOUBLE);
			p.setNull(9, Types.DOUBLE);
		}
		
		p.setTimestamp(10,new Timestamp(System.currentTimeMillis()));
		p.setString(11, rules);
		p.setString(12, c.dealSpec);
		if (c.interstitialOnly)
			p.setInt(13, 1);
		else
			p.setInt(13, 0);
		if (c.width_range != null)
			p.setString(14, c.width_range);
		else
			p.setNull(14,  Types.VARCHAR);
		if (c.height_range != null)
			p.setString(15, c.width_range);
		else
			p.setNull(15,  Types.VARCHAR);
		if (c.width_height_list != null)
			p.setString(16, c.width_height_list);
		else
			p.setNull(16,  Types.VARCHAR);
		p.setString(17, c.name);		
		p.setString(18, c.cur);
		
		return p;
	}

	/**
	 * Empty constructor for creation using json.
	 */
	public Creative() {

	}
	
	public Creative(JsonNode node, boolean isBanner) throws Exception {
		this.isBanner = isBanner;
		if (isBanner)
			tableName = "banners";
		else
			tableName = "banner_videos";
		update(node);
	}
	
	public Creative(JsonNode node, String type) throws Exception {
		switch(type.toLowerCase()) {
		case "banner":
			isBanner = true;
			tableName = "banners";
			break;
		case "video":
			isVideo = true;
			tableName = "banner_videos";
			break;
		case "audio":
			isAudio = true;
			tableName = "banner_audios";
			break;
		case "native":
			isNative = true;
			tableName = "banner_natives";
		}
		update(node);
	}
	

	/**
	 * A shallow copy. This is used to create a 'rotating creative'. The rotating creative will inherit all the
	 * attributes of the proxy, but, the adm will be changed. as needed.
	 * @return
	 */
	public Creative copy() {
		Creative c = new Creative();
		c.alternateAdId = alternateAdId;
		c.adm_override = adm_override;
		c.cur = cur;
		c.w = w;
		c.h = h;
		c.strW = strW;
		c.strH = strH;
		c.strPrice = strPrice;
		c.videoDuration = videoDuration;
		c.videoLinearity = videoLinearity;
		c.videoProtocol = videoProtocol;
		c.macros = macros;
		c.price = price;
		c.weight = weight;
		c.forwardurl = forwardurl;
		c.imageurl = imageurl;
		c.dimensions = dimensions;
		c.categories = categories;

		c.encodeUrl();
		return c;
	}
	
	public void saveToDatabase() throws Exception {	
		PreparedStatement st = toSql(this, CrosstalkConfig.getInstance().getConnection());
		st.executeUpdate();
		st.close();	
	}

	/**
	 * Find a deal by id, if exists, will bid using the deal
	 * 
	 * @param id
	 *            String. The of the deal in the bid request.
	 * @return Deal. The deal, or null, if no deal.
	 */
	public Deal findDeal(String id) {
		if (deals == null || deals.size() == 0)
			return null;
		for (int i = 0; i < deals.size(); i++) {
			Deal d = deals.get(i);
			if (d.id.equals(id)) {
				return d;
			}
		}
		return null;
	}

	/**
	 * Given a list of deals, find out if we have a deal that matches.
	 * 
	 * @param ids
	 *            List. A list of ids.
	 * @return Deal. A matching deal or null if no deal.
	 */
	public Deal findDeal(List<String> ids) {
		if (deals == null || deals.size() == 0)
			return null;
		
		for (int i = 0; i < ids.size(); i++) {
			Deal d = findDeal(ids.get(i));
			if (d != null)
				return d;
		}
		return null;
	}

	/**
	 * Find a deal by id, if exists, will bid using the deal
	 * 
	 * @param id
	 *            String. The of the deal in the bid request.
	 * @return Deal. The deal, or null, if no deal.
	 */
	public Deal findDeal(long id) {
		if (deals == null || deals.size() == 0)
			return null;
		for (int i = 0; i < deals.size(); i++) {
			Deal d = deals.get(i);
			if (Long.parseLong(d.id) == id) {
				return d;
			}
		}
		return null;
	}

	/**
	 * Does the HTTP encoding for the forward url and image url. The bid will
	 * use the encoded form.
	 */
	void encodeUrl() {
		MacroProcessing.findMacros(macros, forwardurl);
		MacroProcessing.findMacros(macros, imageurl);
		
		/**
		 * Docker environment variable substitutions too
		 */
		try {
			imageurl = Env.getInstance().substitute(imageurl);
			forwardurl = Env.getInstance().substitute(forwardurl);
		} catch (Exception error) {
			error.printStackTrace();
		}

		if (w != null) {
			if (dimensions == null)
				dimensions = new Dimensions();
			Dimension d = new Dimension(w,h);
			dimensions.add(d);
		}
		/*
		 * Encode JavaScript tags. Redis <script src=\"a = 100\"> will be
		 * interpeted as <script src="a=100"> In the ADM, this will cause
		 * parsing errors. It must be encoded to produce: <script src=\"a=100\">
		 */
		 if (forwardurl != null) {
			 JsonStringEncoder encoder = BufferRecyclers.getJsonStringEncoder();
			 char[] output =  encoder.quoteAsString(forwardurl);
	     	forwardurl = new String(output);
		 }


		encodedFurl = URIEncoder.myUri(forwardurl);
		encodedIurl = URIEncoder.myUri(imageurl);

		if (adm != null && adm.size() > 0) {
			String s = "";
			for (String ss : adm) {
				s += ss;
			}
			unencodedAdm = s.replaceAll("\r\n", "");
			//unencodedAdm = unencodedAdm.replaceAll("\"", "\\\\\"");
			JsonStringEncoder encoder = BufferRecyclers.getJsonStringEncoder();
			char[] output =  encoder.quoteAsString(unencodedAdm);
			unencodedAdm = new String(output);
			MacroProcessing.findMacros(macros, unencodedAdm);
			encodedAdm = URIEncoder.myUri(s);
		}

		strPrice = Double.toString(price);
		
		if (extensions != null) {
			String cat = extensions.get("categories");
			if (cat != null) {
				categories = new ArrayList();
				String[] cats = cat.split(",");
				for (String c : cats) {
					categories.add(c.trim());
				}
			}
		}
		
		/**
		 * Always contain these!
		 */
		if (!macros.contains("{pixel_url}"))
			macros.add("{pixel_url}");
		if (!macros.contains("{win_url}"))
			macros.add("{win_url}");
	}

	/**
	 * Getter for the forward URL, unencoded.
	 * 
	 * @return String. The unencoded url.
	 */
	@JsonIgnore
	public String getForwardUrl() {
		return forwardurl;
	}

	/**
	 * Return the encoded forward url
	 * 
	 * @return String. The encoded url
	 */
	@JsonIgnore
	public String getEncodedForwardUrl() {
		if (encodedFurl == null)
			encodeUrl();
		return encodedFurl;
	}

	/**
	 * Return the encoded image url
	 * 
	 * @return String. The returned encoded url
	 */
	@JsonIgnore
	public String getEncodedIUrl() {
		if (encodedIurl == null)
			encodeUrl();
		return encodedIurl;
	}

	/**
	 * Setter for the forward url, unencoded.
	 * 
	 * @param forwardUrl
	 *            String. The unencoded forwardurl.
	 */
	public void setForwardUrl(String forwardUrl) {
		this.forwardurl = forwardUrl;
	}

	/**
	 * Setter for the imageurl
	 * 
	 * @param imageUrl
	 *            String. The image url to set.
	 */
	public void setImageUrl(String imageUrl) {
		this.imageurl = imageUrl;
	}

	/**
	 * Returns the impression id for this creative (the database key used in
	 * wins and bids).
	 * 
	 * @return String. The impression id.
	 */
	public String getImpid() {
		return impid;
	}

	/**
	 * Set the impression id object.
	 * 
	 * @param impid
	 *            String. The impression id to use for this creative. This is
	 *            merely a databse key you can use to find bids and wins for
	 *            this id.
	 */
	public void setImpid(String impid) {
		this.impid = impid;
	}

	/**
	 * Set the price on this creative
	 * 
	 * @param price
	 *            double. The price to set.
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * Get the price of this campaign.
	 * 
	 * @return double. The price associated with this creative.
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Determine if this is a native ad
	 * 
	 * @return boolean. Returns true if this is a native content ad.
	 */
	@JsonIgnore
	public boolean isNative() {
		if (nativead != null)
			return true;
		return false;
	}

	/**
	 * Determine if this creative is video or not
	 * 
	 * @return boolean. Returns true if video.
	 */
	@JsonIgnore
	public boolean isVideo() {
		if (this.videoDuration != null)
			return true;
		return false;
	}

	/**
	 * Encodes the attributes of the node after the node is instantiated.
	 * 
	 * @throws Exception
	 *             on JSON errors.
	 */
	public void encodeAttributes() throws Exception {
		for (Node n : attributes) {
			n.setValues();
		}

		if (nativead != null) {
			nativead.encode();
		}

		// assign the fixed nodes
		fixedNodes.add(new FixedNodeStatus());
        fixedNodes.add(new FixedNodeNonStandard());

        // These are impression releated
		attributes.add(new FixedNodeRequiresDeal());
		attributes.add(new FixedNodeNoDealMatch());
		attributes.add(new FixedNodeIsVideo());
		attributes.add(new FixedNodeIsNative());
		attributes.add(new FixedNodeIsBanner());
		attributes.add(new FixedNodeDoNative());
        attributes.add(new FixedNodeDoSize());
		attributes.add(new FixedNodeDoVideo());
	}

    /**
     * Sort the selection criteria in descending order of number of times false was selected.
     * Then, after doing that, zero the counters.
     */
	public void sortNodes() {
        Collections.sort(fixedNodes, nodeSorter);
        Collections.sort(attributes, nodeSorter);

        for (int i = 0; i<fixedNodes.size();i++) {
            fixedNodes.get(i).clearFalseCount();
        }

        for (int i = 0; i<attributes.size();i++) {
            attributes.get(i).clearFalseCount();
        }
    }

	/**
	 * Returns the native ad encoded as a String.
	 * 
	 * @param br
	 *            BidRequest. The bid request.
	 * @return String. The encoded native ad.
	 */
	@JsonIgnore
	public String getEncodedNativeAdm(BidRequest br) {
		return nativead.getEncodedAdm(br);
	}
	
	/**
	 * Returns the native ad escaped.
	 * @param br BidRequest. The bid request.
	 * @return String. The returned escaped string.
	 */
	@JsonIgnore
	public String getUnencodedNativeAdm(BidRequest br) {
		return nativead.getEscapedAdm(br);
	}

	/**
	 * Process the bid request against this creative.
	 * 
	 * @param br
	 *            BidRequest. Returns true if the creative matches.
	 * @param errorString
	 *            StringBuilder. The string to hold any campaign failure
	 *            messages
	 * @return boolean. Returns true of this campaign matches the bid request,
	 *         ie eligible to bid
	 */
	public SelectedCreative process(BidRequest br, String adId, StringBuilder errorString , Probe probe) throws Exception {

        /**
         * Fixed nodes do not access deals or the br impressions
         */
        for (int i=0;i<fixedNodes.size();i++) {
            Deal d = null;
            if (!fixedNodes.get(i).test(br,this,adId,null,errorString,probe,null))
                return null;

        }

        /**
         * Ok, the standard set has been dealt with, let's work with impressions and deals rules.
         */
		int n = br.getImpressions();
		StringBuilder sb = new StringBuilder();
		Impression imp;
	
		for (int i=0; i<n;i++) {
			imp = br.getImpression(i);
			SelectedCreative cr = xproc(br,adId,imp,errorString, probe);
			if (cr != null) {
				cr.setImpression(imp);
				return cr;
			}
		}
		return null;
	}
	
	public SelectedCreative xproc(BidRequest br, String adId, Impression imp, StringBuilder errorString, Probe probe) throws Exception {
		//List<Deal> newDeals = null;
		String dealId = null;
		double xprice = price;
		String impid = this.impid;

		Node n = null;
		/**
		 * Attributes that are specific to the creative (additional to the
		 * campaign
		 */
		try {
            Deal deal = null;
            List<Deal> deals = new ArrayList<Deal>();
			for (int i = 0; i < attributes.size(); i++) {
				n = attributes.get(i);
				if (n.test(br,this,adId,imp,errorString,probe,deals) == false) {
				    if (n.hierarchy == null) {
                        if (errorString != null)
                            errorString.append("Creative mismatch: ");
                        if (errorString != null) {
                            if (n.operator == Node.OR)
                                errorString.append("OR failed on all branches\n");
                            else
                                errorString.append(n.hierarchy);
                        }
                        probe.process(br.getExchange(), adId, impid, Probe.CREATIVE_MISMATCH + n.hierarchy);
                    }
					return null;
				}

				if (deals.size()!=0) {
                    deal = deals.get(0);
                    dealId = deal.id;
                    xprice = deal.price;
                }
			}
		} catch (Exception error) {
		    error.printStackTrace();
			if (errorString != null) {
				errorString.append("Internal error processing bid request with: "+ n.name);
				errorString.append(error.toString());
				errorString.append("\n");
			}
			return null;
		}
		
		/**
		 * If there was no deal, then make sure the bid floor is not blown
		 */
		if (dealId == null) {
			if(imp.bidFloor != null && xprice < imp.bidFloor) {
				if (errorString != null) {
					errorString.append("Price of creative: ");
					errorString.append(xprice);
					errorString.append(" < bidfloor: ");
					errorString.append(imp.bidFloor);
				}
				probe.process(br.getExchange(), adId, impid, Probe.BID_FLOOR);
				return null;
			}
		}

		return new SelectedCreative(this, dealId, xprice, impid);
	}

	/**
	 * Creates a sample of the ADM field, useful for testing your ad markup to
	 * make sure it works.
	 * 
	 * @param camp
	 *            Campaign. The campaign to use with this creative.
	 * @return String. The ad markup HTML.
	 */
	public String createSample(Campaign camp) {
		BidRequest request = new Nexage();

		String page = null;
		String str = null;
		File temp = null;
		
		Impression imp = new Impression();

		imp.w = 666;
		imp.h = 666;

		BidResponse br = null;

		try {
			if (this.isVideo()) {
				br = new BidResponse(request, imp, camp, this, "123", 1.0, null, 0);
				imp.video = new Video();
				imp.video.linearity = this.videoLinearity;
				imp.video.protocol.add(this.videoProtocol);
				imp.video.maxduration = this.videoDuration + 1;
				imp.video.minduration = this.videoDuration - 1;

				str = br.getAdmAsString();
				/**
				 * Read in the stubbed video page and patch the VAST into it
				 */
				page = new String(Files.readAllBytes(Paths.get("web/videostub.html")), StandardCharsets.UTF_8);
				page = page.replaceAll("___VIDEO___", "http://localhost:8080/vast/onion270.xml");
			} else if (this.isNative()) {

				// br = new BidResponse(request, camp, this,"123",0);
				// request.nativead = true;
				// request.nativePart = new NativePart();
				// str = br.getAdmAsString();

				page = "<html><title>Test Creative</title><body><img src='images/under-construction.gif'></img></body></html>";
			} else {
				br = new BidResponse(request, imp, camp, this, "123", 1.0, null, 0);
				str = br.getAdmAsString();
				page = "<html><title>Test Creative</title><body><xmp>" + str + "</xmp>" + str + "</body></html>";
			}
			page = page.replaceAll("\\{AUCTION_PRICE\\}", "0.2");
			page = page.replaceAll("\\$", "");
			temp = File.createTempFile("test", ".html", new File("www/temp"));
			temp.deleteOnExit();

			Files.write(Paths.get(temp.getAbsolutePath()), page.getBytes());
		} catch (Exception error) {
			error.printStackTrace();
		}
		return "temp/" + temp.getName();
	}

	/**
	 * Find a node of the named hierarchy.
	 * 
	 * @param hierarchy
	 *            String. The hierarchy you are looking for. Note, nodes can have a null
     *            hierarchy if this is a fixed node.
	 * @return Node. The node with this hierarchy, or, null if not found.
	 */
	public Node findAttribute(String hierarchy) {
		for (int i = 0; i < attributes.size(); i++) {
		    Node n = attributes.get(i);
			if (n.hierarchy != null && n.hierarchy.equals(hierarchy))
				return n;
		}
		return null;
	}
	
	////////////////////////
	
	public boolean runUsingElk(String cid) {

		try {
			budget.totalCost.set(BudgetController.getInstance().getCreativeTotalSpend(cid, impid, getType()));
			budget.dailyCost.set(BudgetController.getInstance().getCreativeDailySpend(cid, impid, getType()));
			budget.hourlyCost.set(BudgetController.getInstance().getCreativeHourlySpend(cid, impid, getType()));
		
			logger.debug("*** ELK TEST: Updating budgets: {}/{}/{}",cid, impid, getType());
			logger.debug("Total cost: {} hourly cost: {}, daily_cost: {}",budget.totalCost.getDoubleValue(),
					budget.dailyCost.getDoubleValue(), budget.hourlyCost.getDoubleValue());
		} catch (Exception error) {
			error.printStackTrace();
		}
		return true;
	}
	
	String getType() {
		if (isNative())
			return "native";
		if (isVideo())
			return "video";
		return "banner";
	}
	
	public boolean isActive(String cid) throws Exception {

		if (budgetExceeded(cid))
			return false;
		return true;
	}

	public void stop() {

	}

	String clean(String data) {
		String[] lines = data.split("\n");
		String rc = "";
		for (String s : lines) {
			rc += s.trim();
		}
		return rc;
	}

	/**
	 * Determine if the budget was exceeded.
	 * @return boolean. Returns true if the budget was exceeded.
	 * @throws Exception on Elk errors.
	 */
	public boolean budgetExceeded(String cid) throws Exception {
			logger.debug("********* CHECKING BUDGET FOR CREATIVE {} of campaign {}",impid, cid);
			if (budget == null)
				return false;
			return BudgetController.getInstance().checkCreativeBudgets(cid, impid, getType(),
						budget.totalBudget, budget.dailyBudget, budget.hourlyBudget);
	}


	protected List<String> getList(String text) {
		List<String> temp = new ArrayList<String>();
		if (text != null && text.length() > 0) {
			String[] parts = text.split(",");
			for (String part : parts) {
				temp.add(part);
			}
		}
		return temp;

	}

	/**
	 * Set the new total budget. Used by the api.
	 * @param amount double. The value to set.
	 */
	public void setTotalBudget(double amount) {
		budget.totalBudget.set(amount);
	}

	/**
	 * Set the new total daily. Used by the api.
	 * @param amount double. The value to set.
	 */
	public void setDailyBudget(double amount) {
		budget.dailyBudget.set(amount);
	}

	/**
	 * Set the new hourly budget. Used by the api.
	 * @param amount double. The value to set.
	 */
	public void setHourlyBudget(double amount) {
		budget.hourlyBudget.set(amount);
	}
	
	/**
	 * Compile the class into the JSON that will be loaded into Aerospike.
	 * @return Creative. The actual RTB4FREE creative.
	 * @throws Exception on JSON errors.
	 */
	public void compile() throws Exception {
		attributes.clear();

		price = bid_ecpm.doubleValue();
		impid = "" + bannerid;

		if (isBanner) {
			if (contenttype != null && (contenttype.equalsIgnoreCase("OVERRIDE"))) {
				adm_override = true;
			} else {
				adm_override = false;
				contenttype = MimeTypes.determineType(htmltemplate);
				if (contenttype != null) {
					Node n = new Node("contenttype", "imp.0.banner.mimes", Node.MEMBER, contenttype);
					n.notPresentOk = true;
					attributes.add(n);
				}
			}

			///////// Handle the height and width ///////////////////////////
			if (width == 0 || height == 0) {
				addDimensions();
			} else {
				w = width;
				h = height;
			}

			forwardurl = htmltemplate;

			if (position != null && position.length() > 0) {
				String[] data = position.split(",");
				List<Integer> positions = new ArrayList<Integer>();
				for (String s : data) {
					s = s.trim();
					positions.add(Integer.parseInt(s));
				}
				if (positions.size() > 0) {
					if ((positions.size() == 1 && positions.get(0) != 0) || positions.size() > 0) {
						Node n = new Node("position", "imp.0.banner.pos", Node.INTERSECTS, positions);
						attributes.add(n);
						n.notPresentOk = true;
					}
				}

			}

		} else
			compileVideo();

		if (interstitialOnly) {
			Node n = new Node(INTERSTITIAL, "imp.0.instl", Node.EQUALS, 1);
			n.notPresentOk = false;
			attributes.add(n);
		}

		compileExchangeAttributes();
		handleDeals();
		doStandardRtb();
	}
	
	/**
	 * Compiles the exchange specific attributes, like for Stroer and Adx.
	 * 
	 * @param creative
	 *            Creative. The creative we are attaching the extensions for.
	 */
	public void compileExchangeAttributes() throws Exception {
		String theKey = "banner_id";

		extensions = new HashMap();
		AdxCreativeExtensions x = null;

		if (!isBanner)
			theKey = "banner_video_id";

		for (JsonNode node : Crosstalk.getInstance().exchangeAttributes) {
			String id;
			if ((id = node.get(theKey).asText("-1")).equals(bannerid)) {
				String key = node.get("name").asText(null);
				String value = node.get("value").asText(null);
				String exchange = node.get("exchange").asText("");

				if (exchange.equalsIgnoreCase("adx")) {
					if (x == null) {
						x = new AdxCreativeExtensions();
						adxCreativeExtensions = x;
					}
					switch (key) {
					case "click_thru_url":
						x.adxClickThroughUrl = value;
						break;
					case "tracking_url":
						x.adxTrackingUrl = value;
						break;
					case "categories":
						value = value.replaceAll("\"", "");
						value = value.replaceAll("\\[", "");
						value = value.replaceAll("\\]", "");
						x.adxCategory = new Integer(value);
						break;
					case "vendor_type":
						try {
							x.adxVendorType = new Integer(value);
						} catch (Exception error) {
							x.adxVendorType = 0;
							logger.error("{}/{}, creative  has bad vendor_type: ", bannerid, getType(),value);
						}
						break;
					case "attributes":
						value = value.substring(1, value.length() - 1);
						value = value.replaceAll("\"", "");
						List<String> list = getList(value);
						x.attributes = new ArrayList<Integer>();
						for (String s : list) {
							x.attributes.add(Integer.parseInt(s));
						}
						break;
					}
				} else {
					extensions.put(key, value);
				}
			}
		}
	}
	
	/**
	 * Compile the video specific components of a creative.
	 * 
	 * @param c
	 *            Campaign. The campaign to attach to.
	 * @throws Exception
	 *             on JSON parsing errors.
	 */
	protected void compileVideo() throws Exception {
		videoDuration = video_duration;
		videoMimeType = video_type;

		///////////// Handle width and height /////////////////////////////
		if (video_width == 0 || video_height == 0) {
			addDimensions();
		} else {
			// Old Style
			w = video_width;
			h = video_height;
		}
		//////////////////////////////////////////////////////////////////

		videoProtocol = video_protocol;
		attributes = new ArrayList<Node>();

		if (video_bitrate != null) {
			Node n = new Node("contenttype", "imp.0.video.bitrate", Node.GREATER_THAN_EQUALS, video_bitrate);
			n.notPresentOk = true;
			attributes.add(n);
		}

		String theVideo;

		videoLinearity = video_linearity;
		if (video_data.startsWith("http")) {
			HttpPostGet hp = new HttpPostGet();
			theVideo = hp.sendGet(video_data, 5000, 5000);
		} else if (video_data.startsWith("file")) {
			String fname = video_data.substring(7);
			theVideo = new String(Files.readAllBytes(Paths.get(fname)), StandardCharsets.UTF_8);
		} else {
			theVideo = video_data;
		}

		StringBuilder sb = new StringBuilder(theVideo);
		xmlEscapeEncoded(sb);
		theVideo = sb.toString();

		adm = new ArrayList<String>();
		adm.add(theVideo);
	}

	void addDimensions() {
		String[] parts = null;
		Dimension d = null;

		// Is this a width dimension?
		if (width_range != null) {
			if (width_range != null) {
				dimensions = new Dimensions();
				parts = width_range.split("-");
				int leftX = Integer.parseInt(parts[0].trim());
				int rightX = Integer.parseInt(parts[1].trim());
				d = new Dimension(leftX, rightX, -1, -1);
				dimensions.add(d);
				return;
			}
		}

		// Is this a height dimension
		if (height_range != null) {
			dimensions = new Dimensions();
			parts = height_range.split("-");
			int leftY = Integer.parseInt(parts[0].trim());
			int rightY = Integer.parseInt(parts[1].trim());
			d = new Dimension(-1, -1, leftY, rightY);
			dimensions.add(d);
			return;
		}

		// Is this WxH, ... list
		if (node.get("width_height_list") != null) {
			if (width_height_list != null && width_height_list.length() > 0) {
				dimensions = new Dimensions();
				String [] elements;
				if (width_height_list.contains(","))
					elements = width_height_list.split(",");
				else
					elements = width_height_list.split("\n");
				for (String s : elements) {
					parts = s.split("x");
					int w = Integer.parseInt(parts[0].trim());
					int h = Integer.parseInt(parts[1].trim());
					d = new Dimension(w, h);
					dimensions.add(d);
				}
			}
		}
	}

	/**
	 * XML Escape a stringbuilder budder.
	 * 
	 * @param sb
	 *            StringBuilder. The data escape.
	 */
	private void xmlEscapeEncoded(StringBuilder sb) {
		int i = 0;
		while (i < sb.length()) {
			i = sb.indexOf("%26", i);
			if (i == -1)
				return;
			if (!(sb.charAt(i + 3) == 'a' && sb.charAt(i + 4) == 'm' && sb.charAt(i + 5) == 'p'
					&& sb.charAt(i + 6) == ';')) {

				sb.insert(i + 3, "amp;");
			}
			i += 7;
		}
	}

	/**
	 * Attach any defined deals to the creative. deal,deal,deal. Where
	 * deal=id:price, thus id:price,id:price...
	 * 
	 * @param c
	 *            Campaign. The RTB campaign using the dealsl.
	 */
	void handleDeals() {
		deals.clear();
		if (node.get("deals") == null)
			return;

		dealSpec = node.get("deals").asText(null);
		if (dealSpec == null || dealSpec.trim().length() == 0)
			return;
		deals = new Deals();
		String[] parts = dealSpec.split(",");
		for (String part : parts) {
			Deal d = new Deal();
			String[] subpart = part.split(":");
			d.id = subpart[0].trim();
			d.price = Double.parseDouble(subpart[1].trim());
			deals.add(d);
		}
	}
	
	/**
	 * Call after you compile!
	 * @param creative Creative. The RTB4FREE campaign to send out to the bidders.
	 * @throws Exception
	 *             on JSON errors
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
	
	public void update(JsonNode myNode) throws Exception {
		node = myNode;
		
		if (node.get("width_range") != null)
			width_range = node.get("width_range").asText();
		if (node.get("height_range") != null)
			height_range = node.get("width_range").asText();
		if (node.get("width_height_list") != null)
			width_height_list = node.get("width_height_list").asText();
		
		if (width_range != null && (width_range.equals("") || width_range.equals("null")))
				width_range = null;
		if (height_range != null && (height_range.equals("") || height_range.equals("null")))
			height_range = null;
		if (width_height_list != null && (width_height_list.equals("") || width_height_list.equals("null")))
			width_height_list = null;
		
		id = node.get("id").asInt();
		if (node.get("bid_ecpm") != null)
			price = node.get("bid_ecpm").asDouble();
		else
			price = node.get("price").asDouble();
		bid_ecpm = new AtomicBigDecimal(price);
		cur = node.get("cur").asText();
		name = myNode.get("name").asText();
		
		Object x = myNode.get(TOTAL_COST);  // this will be null on network update, but not when instantiating from the db
		if (x != null) {
			double dt = myNode.get(TOTAL_COST).asDouble(0);
			budget.totalCost.set(dt);
			budget.hourlyCost = new AtomicBigDecimal(myNode.get(HOURLY_COST).asDouble());
			budget.dailyCost = new AtomicBigDecimal(myNode.get(DAILY_COST).asDouble());

		}
		
		x = myNode.get("total_budget");
		if (x != null) {
			budget.totalBudget =  new AtomicBigDecimal(myNode.get("total_budget").asDouble());
			budget.dailyBudget = new AtomicBigDecimal(myNode.get(DAILY_BUDGET).asDouble());
			budget.hourlyBudget = new AtomicBigDecimal(myNode.get(HOURLY_BUDGET).asDouble());
		}
		
		if (myNode.get("rules") != null) {
			String str = myNode.get("rules").asText();
			rules = new ArrayList<>();
			if (str.trim().length() != 0) {
				if (str.equals("null")==false)
					Targeting.getIntegerList(rules, str);
			}
		}

		process();
	}

	public void process() throws Exception {
		if (isBanner) {
			imageurl = node.get("imageurl").asText(null);
		}
	
		bid_ecpm.set(node.get("bid_ecpm").asDouble());

		if (isBanner) {
			if (node.get("width") != null)
				width = node.get("width").asInt();
			if (node.get("height") != null)
				height = node.get("height").asInt();

			contenttype = node.get(CONTENT_TYPE).asText();

			htmltemplate = node.get(HTML_TEMPLATE).asText();

			htmltemplate = htmltemplate.replaceAll("\n", "");
			htmltemplate = htmltemplate.replaceAll("\r", "");

			if (node.get("position") != null)
				position = node.get("position").asText(null);
		} else 
		if (isVideo) {
			video_duration = node.get("vast_video_duration").asInt();
			video_width = node.get("vast_video_width").asInt();
			video_height = node.get("vast_video_height").asInt();

			video_type = node.get("mime_type").asText();
			video_linearity = node.get("vast_video_linerarity").asInt();

			video_data = node.get(this.VAST_DATA).asText();
			if (node.get("vast_video_protocol") != null) {
				video_protocol = node.get("vast_video_protocol").asInt();
			}
			video_data = clean(video_data.trim());

			if (node.get("bitrate") != null) {
				video_bitrate = new Integer(node.get("bitrate").asInt());
			}
		}
		else
		if (isAudio) {
			
		} else 
		if (isNative) {
			
		} else 
			throw new Exception("Can't tell what kind of creative " + name + " is.");

		if (node.get(INTERSTITIAL) != null && node.get(INTERSTITIAL) instanceof MissingNode == false) {
			int x = node.get(INTERSTITIAL).asInt();
			if (x == 1)
				interstitialOnly = true;
			else
				interstitialOnly = false;
		} else
			interstitialOnly = false;

		if (node.get("status") != null && node.get("status") instanceof MissingNode == false) {
			status = node.get("status").asText();
		}
	}
}
