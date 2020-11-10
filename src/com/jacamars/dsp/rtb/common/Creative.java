package com.jacamars.dsp.rtb.common;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jacamars.dsp.crosstalk.budget.AtomicBigDecimal;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.crosstalk.budget.CrosstalkConfig;
import com.jacamars.dsp.rtb.bidder.MimeTypes;
import com.jacamars.dsp.rtb.bidder.SelectedCreative;

import com.jacamars.dsp.rtb.exchanges.adx.AdxCreativeExtensions;

import com.jacamars.dsp.rtb.nativeads.creative.NativeCreative;
import com.jacamars.dsp.rtb.pojo.*;
import com.jacamars.dsp.rtb.probe.Probe;
import com.jacamars.dsp.rtb.shared.AccountingCache;
import com.jacamars.dsp.rtb.shared.TokenData;
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
public class Creative implements Serializable {

	public static final String SQL_BANNERS = "banners";
	public static final String SQL_VIDEOS = "banner_videos";
	public static final String SQL_AUDIOS = "banner_audios";
	public static final String SQL_NATIVES = "banner_natives";

	public String customer_id;

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
	/** The attributes of this creative */
	public List<Integer> attr;
	/** Input ADM field */
	public List<String> adm;
	/** The encoded version of the adm as a single string */
	public transient String encodedAdm;
	// unencoded adm of the
	public transient String unencodedAdm;
	/** Extensions needed by certain SSPs */
	public transient Map<String, String> extensions;
	/** Extension spec for certain ssps in the database */
	public List<String> ext_spec;
	// Currency
	public String cur = "USD";
	/** Width range specification */
	public String width_range;
	/** height range specification */
	public String height_range;
	/** Bith w and h specification */
	public String width_height_list;

	/** if this is a video creative (NOT a native content video) its protocol */
	public Integer videoProtocol;
	/**
	 * if this is a video creative (NOT a native content video) , the duration in
	 * seconds
	 */
	public Integer videoDuration;
	/** If this is a video (Not a native content, the linearity */
	public Integer videoLinearity;
	/** The video audio MimeType */
	public String mime_type;
	/**
	 * vast-url, a non standard field for passing an http reference to a file for
	 * the XML VAST
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

	/** Audio support */
	public Integer audio_duration;
	public Integer audio_bitrate;
	public Integer audio_start_delay;
	public Integer audio_protocol;
	public Integer audio_api;

	// /////////////////////////////////////////////
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
	public String status = ALLOWED_STATUS;

	/* Only runnable creative is allowed to bid */
	public static String ALLOWED_STATUS = "runnable";

	public List<Integer> rules = new ArrayList<>();

	/**
	 * When this is not null, this means this creative is a proxy for a list of
	 * rotating creatives
	 */
	public Map<String, Creative> subCreatives;

	/** These are common attributes across all impressions. */
	@JsonIgnore
	public List<Node> fixedNodes = new ArrayList<Node>();

	/**
	 * A sorter for the campaign/creative attributes, who is most likely to cause a
	 * false will bubble up
	 */
	private SortNodesFalseCount nodeSorter = new SortNodesFalseCount();

	/** The budget for this creative */
	public Budget budget = new Budget();
	public Long interval_start;
	public long interval_end;

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
	/** The type, as in 'banner' or 'video', or 'audio' or 'native' */
	public String type;
	/** The json node derived from SQL */
	transient JsonNode node;
	// //////////////// BANNER SPECIFIC TARGETING
	/**
	 * When true, this is a banner, else it is a video. Will need updating for
	 * native and audio support
	 */
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
	public Integer vast_video_duration;;
	/** The video width */
	public Integer vast_video_width;
	/** The video height */
	public Integer vast_video_height;
	public Integer vast_video_protocol = 2;
	/** The video linearity */
	public Integer vast_video_linearity = 1;
	/** The bitrate of the video */
	public Integer vast_video_bitrate;
	/** The mime type of the video */
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

	transient Integer currentDay;
	transient Integer currentHour;

	/** SQL name for the vast data attribute */

	public static Creative getInstance(int id, String key, String customer_id) {
		String select = "";
		try {
			switch (key.toLowerCase()) {
			case "banner":
				select = "select * from banners where id=" + id + " AND customer_id='" + customer_id + "'";
				break;
			case "video":
				select = "select * from banner_videos where id=" + id + " AND customer_id='" + customer_id + "'";
				break;
			case "audio":
				select = "select * from banner_audios where id=" + id + " AND customer_id='" + customer_id + "'";
				break;
			case "native":
				select = "select * from banner_natives where id=" + id + " AND customer_id='" + customer_id + "'";
				break;
			default:
				throw new RuntimeException("Can't instantiate unknown type: " + key);
			}

			var conn = CrosstalkConfig.getInstance().getConnection();
			var prep = conn.prepareStatement(select);
			ResultSet rs = prep.executeQuery();

			ArrayNode inner = JdbcTools.convertToJson(rs);
			ObjectNode y = (ObjectNode) inner.get(0);
			Creative c = new Creative(y);

			c.id = id;
			c.impid = "" + id;
			c.process();
			c.compile();
			return c;

		} catch (Exception error) {
			error.printStackTrace();
			throw new RuntimeException("Error retreiving banner instance: " + id + ", error: " + error.getMessage());
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
		Array rulesArray = null;
		Array extArray = null;
		Array attrArray = null;
		if (c.rules != null)
			rulesArray = conn.createArrayOf("int", c.rules.toArray());
		if (c.ext_spec != null)
			extArray = conn.createArrayOf("varchar", c.ext_spec.toArray());
		if (c.attr != null)
			attrArray = conn.createArrayOf("int", c.attr.toArray());

		var i = 24;

		String sql = "INSERT INTO " + table + " (" + "interval_start," + "interval_end," + "total_budget,"
				+ "daily_budget," + "hourly_budget," + "bid_ecpm," + "total_cost," + "daily_cost," + "hourly_cost,"
				+ "created_at," + "updated_at," + "rules," + "deals," + "interstitial," + "width_range,"
				+ "height_range," + "width_height_list," + "name," + "cur," + "type," + "ext_spec," + "attr,"
				+ "customer_id,";

		if (c.isBanner) {
			sql += "imageurl," + "width," + "height," + "contenttype," + "htmltemplate," + "position) VALUES ("
					+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,   ?,?,?,?,?,?)";
			p = conn.prepareStatement(sql);

			p.setString(i++, c.imageurl);
			p.setInt(i++, c.width);
			p.setInt(i++, c.height);
			p.setString(i++, c.contenttype);
			p.setString(i++, c.htmltemplate);
			if (c.position != null)
				p.setString(i++, c.position);
			else
				p.setNull(i++, Types.VARCHAR);
		} else if (c.isVideo) {
			sql += "mime_type," + "vast_video_bitrate," + "vast_video_duration," + "vast_video_height,"
					+ "vast_video_width," + "vast_video_protocol," + "vast_video_linearity," + "htmltemplate) VALUES ("

					+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,   ?,?,?,?,?,?,?,?)";
			p = conn.prepareStatement(sql);

			if (c.mime_type != null)
				p.setString(i++, c.mime_type);
			else
				p.setNull(i++, Types.VARCHAR);
			if (c.vast_video_bitrate != null)
				p.setInt(i++, c.vast_video_bitrate);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.vast_video_duration != null)
				p.setInt(i++, c.vast_video_duration);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.vast_video_height != null)
				p.setInt(i++, c.vast_video_height);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.vast_video_width != null)
				p.setInt(i++, c.vast_video_width);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.vast_video_protocol != null)
				p.setInt(i++, c.vast_video_protocol);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.vast_video_linearity != null)
				p.setInt(i++, c.vast_video_linearity);
			else
				p.setNull(i++, Types.INTEGER);
			p.setString(i++, c.htmltemplate);

		} else if (c.isAudio) {
			sql += "audio_bitrate, audio_duration, audio_start_delay, htmltemplate, audio_protocol, audio_api) VALUES (";
			sql += "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,  ?,?,?,?,?,?,?)";
			p = conn.prepareStatement(sql);

			if (c.audio_bitrate != null)
				p.setInt(i++, c.audio_bitrate);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.audio_duration != null)
				p.setInt(i++, c.audio_duration);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.audio_start_delay != null)
				p.setInt(i++, c.audio_start_delay);
			else
				p.setNull(i++, Types.INTEGER);
			p.setString(i++, c.htmltemplate);
			if (c.audio_protocol != null)
				p.setInt(i++, c.audio_protocol);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.audio_api != null)
				p.setInt(i++, c.audio_api);
			else
				p.setNull(i++, Types.INTEGER);
			p.setString(i++, c.contenttype);

		} else if (c.isNative) {
			sql += "native_assets,native_link,native_js_tracker,native_trk_urls,native_context,native_contextsubtype,native_plcmttype,native_plcmtct"

					+ ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,   ?,?,?,?,?,?,?,?)";

			System.out.println(sql);
			p = conn.prepareStatement(sql);

			if (c.nativead.native_assets != null) {
				Array arr = conn.createArrayOf("varchar", c.nativead.native_assets.toArray());
				p.setArray(i++, arr);
			} else {
				p.setNull(i++, Types.ARRAY);
			}
			if (c.nativead.native_link != null)
				p.setString(i++, c.nativead.native_link);
			else
				p.setNull(i++, Types.VARCHAR);
			if (c.nativead.native_js_tracker != null)
				p.setString(i++, c.nativead.native_js_tracker);
			else
				p.setNull(i++, Types.VARCHAR);
			if (c.nativead.native_trk_urls != null) {
				Array arr = conn.createArrayOf("varchar", c.nativead.native_trk_urls.toArray());
				p.setArray(i++, arr);
			} else
				p.setNull(i++, Types.ARRAY);
			if (c.nativead.native_context != null) {
				p.setInt(i++, c.nativead.native_context);
			} else
				p.setNull(i++, Types.INTEGER);
			if (c.nativead.native_contextsubtype != null) {
				p.setInt(i++, c.nativead.native_contextsubtype);
			} else
				p.setNull(i++, Types.INTEGER);
			if (c.nativead.native_plcmttype != null)
				p.setInt(i++, c.nativead.native_plcmttype);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.nativead.native_plcmttype != null)
				p.setInt(i++, c.nativead.native_plcmtct);
			else
				p.setNull(i++, Types.INTEGER);

		} else
			throw new Exception("Can't tell what kind of creative " + c.name + " is.");

		i = 1;

		if (c.budget != null) {
			p.setTimestamp(i++, new Timestamp(c.budget.activate_time));
			p.setTimestamp(i++, new Timestamp(c.budget.expire_time));
		} else {
			p.setNull(i++, Types.TIMESTAMP);
			p.setNull(i++, Types.TIMESTAMP);
		}

		if (c.budget != null && c.budget.totalBudget != null)
			p.setDouble(i++, c.budget.totalBudget.doubleValue());
		else
			p.setNull(i++, Types.DOUBLE);

		if (c.budget != null && c.budget.dailyBudget != null)
			p.setDouble(i++, c.budget.dailyBudget.doubleValue());
		else
			p.setNull(i++, Types.DOUBLE);

		if (c.budget != null && c.budget.hourlyBudget != null)
			p.setDouble(i++, c.budget.hourlyBudget.doubleValue());
		else
			p.setNull(i++, Types.DOUBLE);

		p.setDouble(i++, c.price);

		if (c.budget != null) {
			if (c.budget.totalCost != null)
				p.setDouble(i++, c.budget.totalCost.doubleValue());
			else
				p.setNull(i++, Types.DOUBLE);
			if (c.budget.dailyCost != null)
				p.setDouble(i++, c.budget.dailyCost.doubleValue());
			else
				p.setNull(i++, Types.DOUBLE);
			if (c.budget.hourlyCost != null)
				p.setDouble(i++, c.budget.hourlyCost.doubleValue());
			else
				p.setNull(i++, Types.DOUBLE);
		} else {
			p.setNull(i++, Types.DOUBLE);
			p.setNull(i++, Types.DOUBLE);
			p.setNull(i++, Types.DOUBLE);
		}

		p.setTimestamp(i++, new Timestamp(System.currentTimeMillis()));
		p.setTimestamp(i++, new Timestamp(System.currentTimeMillis()));
		if (rulesArray != null)
			p.setArray(i++, rulesArray);
		else
			p.setNull(i++, Types.ARRAY);
		p.setString(i++, c.dealSpec);
		if (c.interstitialOnly)
			p.setInt(i++, 1);
		else
			p.setInt(i++, 0);
		if (c.width_range != null)
			p.setString(i++, c.width_range);
		else
			p.setNull(i++, Types.VARCHAR);
		if (c.height_range != null)
			p.setString(i++, c.width_range);
		else
			p.setNull(i++, Types.VARCHAR);
		if (c.width_height_list != null)
			p.setString(i++, c.width_range);
		else
			p.setNull(i++, Types.VARCHAR);
		p.setString(i++, c.name);
		p.setString(i++, c.cur);
		p.setString(i++, c.type);

		if (extArray == null)
			p.setNull(i++, Types.ARRAY);
		else
			p.setArray(i++, extArray);

		if (attrArray == null)
			p.setNull(i++, Types.ARRAY);
		else
			p.setArray(i++, attrArray);

		p.setString(i++, c.customer_id);

		return p;
	}

	static String getTable(Creative c) throws Exception {
		String table = null;
		if (c.isAudio)
			table = SQL_AUDIOS;
		else if (c.isNative)
			table = SQL_NATIVES;
		else if (c.isVideo)
			table = SQL_VIDEOS;
		else if (c.isBanner)
			table = SQL_BANNERS;
		else
			throw new Exception("Can't tell what kind of creative id: " + c.id + " is.");
		return table;
	}

	/**
	 * Returns the array attribute name in the campaigns table this creative will
	 * belong to.
	 * 
	 * @return String. The array name in the campaingns table this is an entry in.
	 * @throws Exception if we can't figure out the type.
	 */
	public String getAttributeType() throws Exception {
		String attr = null;
		if (isAudio)
			attr = "audios";
		else if (isNative)
			attr = "natives";
		else if (isVideo)
			attr = "videos";
		else if (isBanner)
			attr = "banners";
		else
			throw new Exception("Can't tell what kind of creative id: " + id + " is.");
		return attr;
	}

	static PreparedStatement doUpdate(Creative c, Connection conn) throws Exception {
		PreparedStatement p = null;
		String table = getTable(c);
		Array rulesArray = null;
		Array extArray = null;
		Array attrArray = null;
		var i = 0;
		if (c.rules != null) {
			rulesArray = conn.createArrayOf("int", c.rules.toArray());
		}
		if (c.ext_spec != null)
			extArray = conn.createArrayOf("varchar", c.ext_spec.toArray());
		if (c.attr != null)
			attrArray = conn.createArrayOf("int", c.attr.toArray());

		String sql = "UPDATE " + table + " SET " + "interval_start=?," + "interval_end=?," + "total_budget=?,"
				+ "daily_budget=?," + "hourly_budget=?," + "bid_ecpm=?," + "updated_at=?," + "rules=?," + "deals=?,"
				+ "interstitial=?," + "width_range=?," + "height_range=?," + "width_height_list=?," + "name=?,"
				+ "cur=?," + "type=?," + "ext_spec=?," + "attr=?,";

		i = 19;

		if (c.isBanner) {
			sql += "imageurl=?," + "width=?," + "height=?," + "contenttype=?," + "htmltemplate=?,"
					+ "position=? WHERE id=?";
			p = conn.prepareStatement(sql);

			p.setString(i++, c.imageurl);
			p.setInt(i++, c.width);
			p.setInt(i++, c.height);
			p.setString(i++, c.contenttype);
			p.setString(i++, c.htmltemplate);
			if (c.position != null)
				p.setString(i++, c.position);
			else
				p.setNull(i++, Types.VARCHAR);

			p.setInt(i++, c.id);
		} else if (c.isVideo) {
			sql += "mime_type=?," + "vast_video_bitrate=?," + "vast_video_duration=?," + "vast_video_height=?,"
					+ "vast_video_width=?," + "vast_video_protocol=?," + "vast_video_linearity=?,"
					+ "htmltemplate=? WHERE id = ?";
			p = conn.prepareStatement(sql);

			if (c.mime_type != null)
				p.setString(i++, c.mime_type);
			else
				p.setNull(i++, Types.VARCHAR);
			if (c.vast_video_bitrate != null)
				p.setInt(i++, c.vast_video_bitrate);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.vast_video_duration != null)
				p.setInt(i++, c.vast_video_duration);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.vast_video_height != null)
				p.setInt(i++, c.vast_video_height);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.vast_video_width != null)
				p.setInt(i++, c.vast_video_width);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.vast_video_protocol != null)
				p.setInt(i++, c.vast_video_protocol);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.vast_video_linearity != null)
				p.setInt(i++, c.vast_video_linearity);
			else
				p.setNull(i++, Types.INTEGER);
			p.setString(i++, c.htmltemplate);

			p.setInt(i++, c.id);
		} else if (c.isAudio) {
			sql += "htmltemplate=?, audio_bitrate=?," + "audio_duration=?," + "audio_start_delay=?," + "audio_api=?,"
					+ "contenttype=?," + "audio_protocol=? WHERE id=?";
			p = conn.prepareStatement(sql);

			p.setString(i++, c.htmltemplate);
			if (c.audio_bitrate != null)
				p.setInt(i++, c.audio_bitrate);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.audio_duration != null)
				p.setInt(i++, c.audio_duration);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.audio_start_delay != null)
				p.setInt(i++, c.audio_start_delay);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.audio_api != null)
				p.setInt(i++, c.audio_api);
			else
				p.setNull(i++, Types.INTEGER);
			p.setString(i++, c.contenttype);
			if (c.audio_protocol != null)
				p.setInt(i++, c.audio_protocol);
			else
				p.setNull(i++, Types.INTEGER);
			p.setInt(i++, c.id);
		} else if (c.isNative) {
			sql += "native_assets=?," + "native_link=?," + "native_js_tracker=?," + "native_trk_urls=?,"
					+ "native_context=?," + "native_contextsubtype=?," + "native_plcmttype=?,"
					+ "native_plcmtct=? WHERE id=?";

			p = conn.prepareStatement(sql);

			if (c.nativead.native_assets != null) {
				Array arr = conn.createArrayOf("varchar", c.nativead.native_assets.toArray());
				p.setArray(i++, arr);
			} else {
				p.setNull(i++, Types.ARRAY);
			}
			if (c.nativead.native_link != null)
				p.setString(i++, c.nativead.native_link);
			else
				p.setNull(i++, Types.VARCHAR);
			if (c.nativead.native_js_tracker != null)
				p.setString(i++, c.nativead.native_js_tracker);
			else
				p.setNull(i++, Types.VARCHAR);
			if (c.nativead.native_trk_urls != null) {
				Array arr = conn.createArrayOf("varchar", c.nativead.native_trk_urls.toArray());
				p.setArray(i++, arr);
			} else
				p.setNull(i++, Types.ARRAY);
			if (c.nativead.native_context != null) {
				p.setInt(i++, c.nativead.native_context);
			} else
				p.setNull(i++, Types.INTEGER);
			if (c.nativead.native_contextsubtype != null) {
				p.setInt(i++, c.nativead.native_contextsubtype);
			} else
				p.setNull(i++, Types.INTEGER);
			if (c.nativead.native_plcmttype != null)
				p.setInt(i++, c.nativead.native_plcmttype);
			else
				p.setNull(i++, Types.INTEGER);
			if (c.nativead.native_plcmtct != null)
				p.setInt(i++, c.nativead.native_plcmtct);
			else
				p.setNull(i++, Types.INTEGER);
			p.setInt(i++, c.id);
		} else
			throw new Exception("Can't tell what kind of creative " + c.name + " is.");

		i = 1;

		if (c.budget != null) {
			p.setTimestamp(i++, new Timestamp(c.budget.activate_time));
			p.setTimestamp(i++, new Timestamp(c.budget.expire_time));
		} else {
			p.setNull(i++, Types.TIMESTAMP);
			p.setNull(i++, Types.TIMESTAMP);
		}

		if (c.budget != null && c.budget.totalBudget != null)
			p.setDouble(i++, c.budget.totalBudget.doubleValue());
		else
			p.setNull(i++, Types.DOUBLE);

		if (c.budget != null && c.budget.dailyBudget != null)
			p.setDouble(i++, c.budget.dailyBudget.doubleValue());
		else
			p.setNull(i++, Types.DOUBLE);

		if (c.budget != null && c.budget.hourlyBudget != null)
			p.setDouble(i++, c.budget.hourlyBudget.doubleValue());
		else
			p.setNull(i++, Types.DOUBLE);

		p.setDouble(i++, c.price);

		p.setTimestamp(i++, new Timestamp(System.currentTimeMillis()));
		if (rulesArray != null)
			p.setArray(i++, rulesArray);
		else
			p.setNull(i++, Types.ARRAY);
		p.setString(i++, c.dealSpec);
		if (c.interstitialOnly)
			p.setInt(i++, 1);
		else
			p.setInt(i++, 0);
		if (c.width_range != null)
			p.setString(i++, c.width_range);
		else
			p.setNull(i++, Types.VARCHAR);
		if (c.height_range != null)
			p.setString(i++, c.width_range);
		else
			p.setNull(i++, Types.VARCHAR);
		if (c.width_height_list != null)
			p.setString(i++, c.width_height_list);
		else
			p.setNull(i++, Types.VARCHAR);
		p.setString(i++, c.name);
		p.setString(i++, c.cur);
		p.setString(i++, c.type);

		if (c.ext_spec == null)
			p.setNull(i++, Types.ARRAY);
		else
			p.setArray(i++, extArray);

		if (attrArray == null)
			p.setNull(i++, Types.ARRAY);
		else
			p.setArray(i++, attrArray);
		return p;
	}

	public static void removeRuleFromCreatives(int id, TokenData td) throws Exception {
		updateRules(Creative.SQL_BANNERS, id, td);
		updateRules(Creative.SQL_VIDEOS, id, td);
		updateRules(Creative.SQL_AUDIOS, id, td);
		updateRules(Creative.SQL_NATIVES, id, td);
	}

	static void updateRules(String table, int id, TokenData td) throws Exception {
		PreparedStatement st = CrosstalkConfig.getInstance().getConnection()
				.prepareStatement("SELECT * FROM " + table + " WHERE ? IN (SELECT unnest(rules) FROM " + table + ")");
		st.setInt(1, id);
		ResultSet rs = st.executeQuery();
		List<Integer> creative_ids = new ArrayList<>();
		while (rs.next()) {
			int cid = rs.getInt("id");
			Creative c = Creative.getInstance(id, table, td.customer);
			int index = c.rules.indexOf(id);
			c.rules.remove(index);
			c.process();
			Creative.toSql(c, CrosstalkConfig.getInstance().getConnection());
			Campaign.touchCampaignsWithCreative(table, c.id);
		}

		st.close();
	}

	/**
	 * Empty constructor for creation using json.
	 */
	public Creative() {

	}

	public Creative(JsonNode node) throws Exception {
		type = node.get("type").asText();
		switch (type.toLowerCase()) {
		case "banner":
			isBanner = true;
			tableName = SQL_BANNERS;
			break;
		case "video":
			isVideo = true;
			tableName = SQL_VIDEOS;
			break;
		case "audio":
			isAudio = true;
			tableName = SQL_AUDIOS;
			break;
		case "native":
			isNative = true;
			tableName = SQL_NATIVES;
			break;
		default:
			throw new Exception("Don't know type: " + type);
		}
		update(node);
	}

	/**
	 * Return the sql table this creative belongs to.
	 * 
	 * @return
	 */
	public String getTable() {
		return tableName;
	}

	/**
	 * A shallow copy. This is used to create a 'rotating creative'. The rotating
	 * creative will inherit all the attributes of the proxy, but, the adm will be
	 * changed. as needed.
	 * 
	 * @return
	 */
	public Creative copy() {
		Creative c = new Creative();
		c.customer_id = customer_id;
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
		c.type = type;
		c.tableName = tableName;
		c.rules = rules;
		c.deals = deals;
		c.ext_spec = ext_spec;
		c.attributes = attributes;
		c.fixedNodes = fixedNodes;

		c.encodeUrl();
		try {
		c.encodeAttributes();
		} catch (Exception error) {
			error.printStackTrace();
		}
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
	 * @param id String. The of the deal in the bid request.
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
	 * @param ids List. A list of ids.
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
	 * @param id String. The of the deal in the bid request.
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
	 * Does the HTTP encoding for the forward url and image url. The bid will use
	 * the encoded form.
	 */
	void encodeUrl() {
		/**
		 * Do system macros first, because they will be comprised of other macros.
		 */
		forwardurl = Configuration.getInstance().replaceAllSystemMacros(forwardurl);
		if (imageurl != null) // only used on banners
			imageurl = Configuration.getInstance().replaceAllSystemMacros(imageurl);

		if (extensions != null && extensions.get("clickthrough_url") != null) {
			forwardurl = forwardurl.replace("_REDIRECT_URL_", extensions.get("clickthrough_url"));
			forwardurl = forwardurl.replace("{clickthrough_url}", extensions.get("clickthrough_url"));
		}

		if (imageurl != null)
			forwardurl = forwardurl.replace("{image_url}", imageurl);

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
			Dimension d = new Dimension(w, h);
			dimensions.add(d);
		}
		/*
		 * Encode JavaScript tags. Redis <script src=\"a = 100\"> will be interpeted as
		 * <script src="a=100"> In the ADM, this will cause parsing errors. It must be
		 * encoded to produce: <script src=\"a=100\">
		 */
		if (forwardurl != null) {
			JsonStringEncoder encoder = new JsonStringEncoder();
			char[] output = encoder.quoteAsString(forwardurl);
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
			// unencodedAdm = unencodedAdm.replaceAll("\"", "\\\\\"");
			JsonStringEncoder encoder = new JsonStringEncoder(); // BufferRecyclers.getJsonStringEncoder();
			char[] output = encoder.quoteAsString(unencodedAdm);
			unencodedAdm = new String(output);
			MacroProcessing.findMacros(macros, unencodedAdm);
			encodedAdm = URIEncoder.myUri(s);
		}

		strPrice = Double.toString(price);

		/**
		 * Create extensions, if required
		 */
		if (ext_spec != null) {
			extensions = new HashMap<>();
			ext_spec.forEach((val) -> {
				String[] parts = val.split(":#:");
				if (parts.length == 1)
					parts = val.split(":");
				extensions.put(parts[0], parts[1]);
			});
		}

		// Handle the assorted extensions
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
	 * @param forwardUrl String. The unencoded forwardurl.
	 */
	public void setForwardUrl(String forwardUrl) {
		this.forwardurl = forwardUrl;
	}

	/**
	 * Setter for the imageurl
	 * 
	 * @param imageUrl String. The image url to set.
	 */
	public void setImageUrl(String imageUrl) {
		this.imageurl = imageUrl;
	}

	/**
	 * Returns the impression id for this creative (the database key used in wins
	 * and bids).
	 * 
	 * @return String. The impression id.
	 */
	public String getImpid() {
		return impid;
	}

	/**
	 * Set the impression id object.
	 * 
	 * @param impid String. The impression id to use for this creative. This is
	 *              merely a databse key you can use to find bids and wins for this
	 *              id.
	 */
	public void setImpid(String impid) {
		this.impid = impid;
	}

	/**
	 * Set the price on this creative
	 * 
	 * @param price double. The price to set.
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
		return isNative;
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

	@JsonIgnore
	public boolean isAudio() {
		return isAudio;
	}

	/**
	 * Encodes the attributes of the node after the node is instantiated.
	 * 
	 * @throws Exception on JSON errors.
	 */
	public void encodeAttributes() throws Exception {
		for (Node n : attributes) {
			n.setValues();
		}

		if (nativead != null) {
			nativead.encode();
		}

		// assign the fixed nodes
		fixedNodes.clear();
		fixedNodes.add(new FixedNodeStatus());
		fixedNodes.add(new FixedNodeNonStandard());
		fixedNodes.add(new FixedNodeDoSize());

		if (extensions != null && extensions.get("site_or_app") != null
				&& !extensions.get("site_or_app").equals("undefined"))
			fixedNodes.add(new FixedNodeAppOrSite(extensions.get("site_or_app")));

		// These are impression related
		fixedNodes.add(new FixedNodeRequiresDeal());
		fixedNodes.add(new FixedNodeNoDealMatch());
		if (isVideo)
			fixedNodes.add(new FixedNodeIsVideo());
		if (isNative)
			fixedNodes.add(new FixedNodeIsNative());
		if (isBanner)
			fixedNodes.add(new FixedNodeIsBanner());
		if (isAudio)
			fixedNodes.add(new FixedNodeDoAudio());
	}

	/**
	 * Sort the selection criteria in descending order of number of times false was
	 * selected. Then, after doing that, zero the counters.
	 */
	public void sortNodes() {
		Collections.sort(fixedNodes, nodeSorter);
		Collections.sort(attributes, nodeSorter);

		fixedNodes.forEach(node -> node.clearFalseCount());
		attributes.forEach(node -> node.clearFalseCount());
	}

	/**
	 * Returns the native ad encoded as a String.
	 * 
	 * @param br BidRequest. The bid request.
	 * @return String. The encoded native ad.
	 */
	@JsonIgnore
	public String getEncodedNativeAdm(BidRequest br) {
		return nativead.getEncodedAdm(br);
	}

	/**
	 * Returns the native ad escaped.
	 * 
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
	 * @param br          BidRequest. Returns true if the creative matches.
	 * @param errorString StringBuilder. The string to hold any campaign failure
	 *                    messages
	 * @return boolean. Returns true of this campaign matches the bid request, ie
	 *         eligible to bid
	 */
	public SelectedCreative process(BidRequest br, String adId, StringBuilder errorString, Probe probe)
			throws Exception {

		/**
		 * Fixed nodes do not access deals or the br impressions
		 */
		int n = br.getImpressions();
		for (int i = 0; i < n; i++) {
			var imp = br.getImpression(i);
			for (int j = 0; j < fixedNodes.size(); j++) {
				var node = fixedNodes.get(j);
				if (!node.test(br, this, adId, imp, errorString, probe, null))
					return null;
			}

		}

		/**
		 * Ok, the standard set has been dealt with, let's work with impressions and
		 * deals rules.
		 */
		StringBuilder sb = new StringBuilder();
		Impression imp;

		for (int i = 0; i < n; i++) {
			imp = br.getImpression(i);
			SelectedCreative cr = xproc(br, adId, imp, errorString, probe);
			if (cr != null) {
				cr.setImpression(imp);
				return cr;
			}
		}
		return null;
	}

	public SelectedCreative xproc(BidRequest br, String adId, Impression imp, StringBuilder errorString, Probe probe)
			throws Exception {
		// List<Deal> newDeals = null;
		String dealId = null;
		double xprice = price;
		String impid = this.impid;

		//System.out.println("HERE: " + adId + "/" + impid + ": " + fixedNodes.size());
		Node n = null;
		/**
		 * Attributes that are specific to the creative (additional to the campaign
		 */
		try {
			Deal deal = null;
			List<Deal> deals = new ArrayList<Deal>();
			for (int i = 0; i < attributes.size(); i++) {
				n = attributes.get(i);
				if (n.test(br, this, adId, imp, errorString, probe, deals) == false) {
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

				if (deals.size() != 0) {
					deal = deals.get(0);
					dealId = deal.id;
					xprice = deal.price;
				}
			}
		} catch (Exception error) {
			error.printStackTrace();
			if (errorString != null) {
				errorString.append("Internal error processing bid request with: " + n.name);
				errorString.append(error.toString());
				errorString.append("\n");
			}
			return null;
		}

		/**
		 * If there was no deal, then make sure the bid floor is not blown
		 */
		if (dealId == null) {
			if (imp.bidFloor != null && xprice < imp.bidFloor) {
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
	 * Find a node of the named hierarchy.
	 * 
	 * @param hierarchy String. The hierarchy you are looking for. Note, nodes can
	 *                  have a null hierarchy if this is a fixed node.
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

	public boolean runUsingElk(Campaign cid) {

		try {

			Double x = AccountingCache.getInstance().get("" + cid.id, type + ":" + id);
			// Double x = BudgetController.getInstance().getCampaignTotalSpend(this);

			logger.debug("*** BUDGET TEST: Checking creative budgets: {}/{}/{}", cid, impid, getType());
			if (x > 0 || Crosstalk.getInstance().timeChanged(currentDay, currentHour)) {

				budget.totalCost.getAndAdd(x);
				budget.dailyCost.getAndAdd(x);
				budget.hourlyCost.getAndAdd(x);

				if (Crosstalk.getInstance().hourChanged(currentHour)) {
					logger.debug("Hour changed, creative budget set to 0.0 @{} for {}/{}/{}",
							Crosstalk.getInstance().getHour(), cid, impid, getType());
					budget.hourlyCost.set(0.0);
				}
				if (Crosstalk.getInstance().dayChanged(currentDay)) {
					logger.debug("Day changed, creative budget set to 0.0 @{} for {}/{}/{}",
							Crosstalk.getInstance().getDay(), cid, impid, getType());
					budget.dailyCost.set(0.0);
				}

				Crosstalk.getInstance().updateCreativeTotal("" + id, type, budget.totalCost.getDoubleValue());
				Crosstalk.getInstance().updateCreativeDaily("" + id, type, budget.dailyCost.getDoubleValue());
				Crosstalk.getInstance().updateCreativeHourly("" + id, type, budget.hourlyCost.getDoubleValue());

				currentHour = Crosstalk.getInstance().getHour();
				currentDay = Crosstalk.getInstance().getDay();
			}

			logger.debug("Total cost: {} hourly cost: {}, daily_cost: {}", budget.totalCost.getDoubleValue(),
					budget.dailyCost.getDoubleValue(), budget.hourlyCost.getDoubleValue());

		} catch (Exception error) {
			error.printStackTrace();
		}
		return true;
	}

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

	public String getType() {
		if (isNative())
			return "native";
		if (isVideo())
			return "video";
		if (isAudio())
			return "audio";
		return "banner";

	}

	public boolean isActive(Campaign c) throws Exception {

		if (budgetExceeded(c))
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
	 * 
	 * @return boolean. Returns true if the budget was exceeded.
	 * @throws Exception on Elk errors.
	 */
	public boolean budgetExceeded(Campaign c) throws Exception {
		logger.debug("********* CHECKING BUDGET FOR CREATIVE {} of campaign {}", impid, c.id);
		if (budget == null)
			return false;
		return checkCreativeBudgets(c);
	}

	public boolean checkCreativeBudgets(Campaign c) {
		double spend;
		double bdget;
		try {

			if (budget != null) {
				bdget = budget.totalBudget.getDoubleValue();
				spend = budget.totalCost.getDoubleValue();

				if (bdget == 0.0)
					return false;

				logger.debug("Creative check -------> ID: {}. TOTAL, {}/{} budget: {} vs spend: {}", c.id, id, type,
						budget, spend);
				if (spend != 0 && spend >= bdget)
					return true;

				bdget = budget.dailyBudget.getDoubleValue();
				spend = budget.dailyCost.getDoubleValue();
				logger.debug("Creative check -------> ID: {}. DAILY {}/{} budget: {} vs spend: {}", c.id, id, type,
						budget, spend);
				if (spend >= bdget)
					return true;

				bdget = budget.hourlyBudget.getDoubleValue();
				spend = budget.hourlyCost.getDoubleValue();

				logger.debug("Creative check ------->ID: {}. HOURLY {}/{}, budet: {} vs spend: {}", c.id, id, type,
						budget, spend);
				if (spend >= bdget)
					return true;
				
				if (budget.daypart != null) {
					String reason = null;
					if (budget.daypart.isActive() != true) {
						logger.debug("Creative Daypart not active -->ID: {}. HOURLY {}/{}, budet: {} vs spend: {}", c.id, id, type,
								budget, spend);
						return true;
					}
					
				}
			}

		} catch (Exception error) {
			error.printStackTrace();
			return true;
		}

		return false;
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
	 * 
	 * @param amount double. The value to set.
	 */
	public void setTotalBudget(double amount) {
		budget.totalBudget.set(amount);
	}

	/**
	 * Set the new total daily. Used by the api.
	 * 
	 * @param amount double. The value to set.
	 */
	public void setDailyBudget(double amount) {
		budget.dailyBudget.set(amount);
	}

	/**
	 * Set the new hourly budget. Used by the api.
	 * 
	 * @param amount double. The value to set.
	 */
	public void setHourlyBudget(double amount) {
		budget.hourlyBudget.set(amount);
	}

	/**
	 * Compile the class into the JSON that will be loaded into Aerospike.
	 * 
	 * @return Creative. The actual RTB4FREE creative.
	 * @throws Exception on JSON errors.
	 */
	public void compile() throws Exception {
		attributes.clear();

		price = bid_ecpm.doubleValue();
		impid = type + ":" + id;

		forwardurl = htmltemplate;

		if (isBanner) {
			if (contenttype != null && (contenttype.equalsIgnoreCase("OVERRIDE"))) {
				adm_override = true;
			} else {
				adm_override = false;
				var str = htmltemplate;
				if (imageurl != null)
					str = htmltemplate.replace("{image_url}", imageurl);

				contenttype = MimeTypes.determineType(str);
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

		} else if (isVideo)
			compileVideo();
		else if (isAudio) {
			compileAudio();
		} else
			compileNative();

		if (interstitialOnly) {
			Node n = new Node(INTERSTITIAL, "imp.0.instl", Node.EQUALS, 1);
			n.notPresentOk = false;
			attributes.add(n);
		}

		// compileExchangeAttributes();
		handleDeals();
		doRules();
		doBATTR();
	}

	/**
	 * If attr (creative attribute types) is defined, make sure the impression isn't
	 * blocking this
	 * 
	 * @throws Exception if the creation of the parse node fails.
	 */
	void doBATTR() throws Exception {
		var name = "imp.0.banner.battr";
		if (isVideo)
			name = "imp.0.video.battr";
		if (isAudio)
			name = "imp.0.audio.battr";
		if (isNative)
			name = "imp.0.banner.battr";

		if (attr != null) {
			Node n = new Node("battr", name, Node.NOT_INTERSECTS, attr);
			n.notPresentOk = true;
			attributes.add(n);
		}
	}

	/**
	 * Compile the video specific components of a creative.
	 * 
	 * @param c Campaign. The campaign to attach to.
	 * @throws Exception on JSON parsing errors.
	 */
	protected void compileVideo() throws Exception {
		videoDuration = vast_video_duration;

		///////////// Handle width and height /////////////////////////////
		if (vast_video_width != null || vast_video_height == 0) {
			addDimensions();
		} else {
			// Old Style
			w = vast_video_width;
			h = vast_video_height;
		}
		//////////////////////////////////////////////////////////////////

		videoProtocol = vast_video_protocol;
		attributes = new ArrayList<Node>();

		if (vast_video_bitrate != null) {
			Node n = new Node("contenttype", "imp.0.video.bitrate", Node.GREATER_THAN_EQUALS, vast_video_bitrate);
			n.notPresentOk = true;
			attributes.add(n);
		}

		String theVideo;

		videoLinearity = vast_video_linearity;
		if (htmltemplate.startsWith("http")) {
			HttpPostGet hp = new HttpPostGet();
			theVideo = hp.sendGet(htmltemplate, 5000, 5000);
		} else if (htmltemplate.startsWith("file")) {
			String fname = htmltemplate.substring(7);
			theVideo = new String(Files.readAllBytes(Paths.get(fname)), StandardCharsets.UTF_8);
		} else {
			theVideo = htmltemplate;
		}

		StringBuilder sb = new StringBuilder(theVideo);
		xmlEscapeEncoded(sb);
		theVideo = sb.toString();

		adm = new ArrayList<String>();
		adm.add(theVideo);
	}

	void compileAudio() throws Exception {
		if (node.get(CONTENT_TYPE) != null) {
			contenttype = mime_type = node.get(CONTENT_TYPE).asText();

			if (contenttype != null) {
				Node n = new Node("contenttype", "imp.0.audio.mimes", Node.MEMBER, contenttype);
				n.notPresentOk = true;
				attributes.add(n);
			}
		}
	}

	void compileNative() throws Exception {

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
				String[] elements;
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
	 * @param sb StringBuilder. The data escape.
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
	 * @param c Campaign. The RTB campaign using the deals.
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
	 * 
	 * @param creative Creative. The RTB4FREE campaign to send out to the bidders.
	 * @throws Exception on JSON errors
	 */
	void doRules() throws Exception {
		rules.forEach(id -> {
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

		customer_id = node.get("customer_id").asText();
		type = node.get("type").asText();
		if (node.get("attr") != null) {
			ArrayNode n = (ArrayNode) myNode.get("attr");
			attr = new ArrayList<Integer>();
			for (int i = 0; i < n.size(); i++) {
				attr.add(n.get(i).asInt());
			}
		}

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
		impid = "" + id;
		if (node.get("bid_ecpm") != null)
			price = node.get("bid_ecpm").asDouble();
		else
			price = node.get("price").asDouble();
		bid_ecpm = new AtomicBigDecimal(price);
		cur = node.get("cur").asText();
		name = myNode.get("name").asText();

		Object x = myNode.get(TOTAL_COST); // this will be null on network update, but not when instantiating from the
											// db
		interval_start = budget.activate_time = myNode.get("interval_start").asLong();
		interval_end = budget.expire_time = myNode.get("interval_end").asLong();
		if (x != null) {
			double dt = myNode.get(TOTAL_COST).asDouble(0);
			budget.totalCost.set(dt);
			budget.hourlyCost = new AtomicBigDecimal(myNode.get(HOURLY_COST).asDouble());
			budget.dailyCost = new AtomicBigDecimal(myNode.get(DAILY_COST).asDouble());

		}

		x = myNode.get("total_budget");
		if (x != null) {
			budget.totalBudget = new AtomicBigDecimal(myNode.get("total_budget").asDouble());
			budget.dailyBudget = new AtomicBigDecimal(myNode.get(DAILY_BUDGET).asDouble());
			budget.hourlyBudget = new AtomicBigDecimal(myNode.get(HOURLY_BUDGET).asDouble());
		}

		if (myNode.get("rules") != null) {
			ArrayNode n = (ArrayNode) myNode.get("rules");
			rules = new ArrayList<Integer>();
			for (int i = 0; i < n.size(); i++) {
				rules.add(n.get(i).asInt());
			}
		}

		if (myNode.get("ext_spec") != null) {
			ArrayNode n = (ArrayNode) myNode.get("ext_spec");
			ext_spec = new ArrayList<String>();
			for (int i = 0; i < n.size(); i++) {
				ext_spec.add(n.get(i).asText());
			}
		}

		process();
		compile();
		encodeUrl();
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
		} else if (isVideo) {
			vast_video_duration = node.get("vast_video_duration").asInt();
			vast_video_width = node.get("vast_video_width").asInt();
			vast_video_height = node.get("vast_video_height").asInt();

			mime_type = node.get("mime_type").asText();
			vast_video_linearity = node.get("vast_video_linearity").asInt();

			htmltemplate = node.get(HTML_TEMPLATE).asText();
			if (node.get("vast_video_protocol") != null)
				vast_video_protocol = node.get("vast_video_protocol").asInt();

			htmltemplate = clean(htmltemplate.trim());

			if (node.get("vast_video_bitrate") != null)
				vast_video_bitrate = node.get("vast_video_bitrate").asInt();

		} else if (isAudio) {
			if (node.get("audio_duration") != null)
				audio_duration = node.get("audio_duration").asInt();
			if (node.get("audio_bitrate") != null)
				audio_bitrate = node.get("audio_bitrate").asInt();
			if (node.get("audio_start_delay") != null)
				audio_start_delay = node.get("audio_start_delay").asInt();
			if (node.get("audio_protocol") != null)
				audio_protocol = node.get("audio_protocol").asInt();
			if (node.get("audio_api") != null)
				audio_api = node.get("audio_api").asInt();
			htmltemplate = node.get(HTML_TEMPLATE).asText();
		} else if (isNative) {
			nativead = new NativeCreative(node);
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
