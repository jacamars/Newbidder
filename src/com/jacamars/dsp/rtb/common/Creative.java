package com.jacamars.dsp.rtb.common;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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

import com.jacamars.dsp.crosstalk.budget.AtomicBigDecimal;
import com.jacamars.dsp.crosstalk.budget.BudgetController;
import com.jacamars.dsp.crosstalk.budget.Crosstalk;
import com.jacamars.dsp.crosstalk.budget.RtbStandard;
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
	/** currency of this creative */
	public String currency = null;
	/** Extensions needed by SSPs */
	public Map<String,String> extensions = null;
	// Currency
	public String cur = "USD";
	
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

	/* ad-exchange name */
	public String exchange;

	/** When this is not null, this means this creative is a proxy for a list of rotating creatives */
	public Map<String,Creative> subCreatives;

	/** These are common attributes across all impressions. */
	@JsonIgnore
	public List<Node> fixedNodes = new ArrayList<Node>();

	/** A sorter for the campaign/creative attributes, who is most likely to cause a false will bubble up */
	private SortNodesFalseCount nodeSorter = new SortNodesFalseCount();
	
	/** The budget for this creative */
	public Budget budget;
	
	/** This class's logger */
	static final Logger logger = LoggerFactory.getLogger(Creative.class);
	
	/**
	 * Components used for creating creatives from JSON derived from SQL
	 */
	/** The campaign this creative belongs to */
	transient int campaignid;	
	/** The id of the cre4ative, as a string */
	transient String bannerid; 
	/** Width of the creative */
	transient int width = 0;	
	/** Height of the creative */
	transient int height = 0;
	/** The type, as in 'banner' or 'video' */
	transient String type;
	/** The json node derived from SQL */
	transient JsonNode node;
	// //////////////// BANNER SPECIFIC TARGETING	
	/** When true, this is a banner, else it is a video. Will need updating for native and audio support */
	transient boolean isBanner;
	/** The content type of the banner template */
	transient String contenttype = "";
	/** The HTML snippet for the banner */
	transient String htmltemplate = "";
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
	transient String position;
	transient AtomicBigDecimal bid_ecpm = new AtomicBigDecimal(0);
	transient boolean interstitialOnly = false;
	private final String INTERSTITIAL = "interstitial";
	/** SQL name for the total cost attribute */
	private static final String TOTAL_COST = "total_cost";	
	/** SQL name for the hourly cost attribute */
	private static final String HOURLY_COST = "hourly_cost";	
	/** SQL name for the daily cost attribute */
	private static final String DAILY_COST = "daily_cost";	
	/** SQL name for the id of this creative */
	private static final String BANNER_ID = "id";	
	/** SQL name for the campaign that owns this record */
	private static final String CAMPAIGN_ID = "campaign_id";	
	/** SQL name for the image URL attribute */
	private static final String IMAGE_URL = "iurl";	
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
		c.currency = currency;
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
        fixedNodes.add(new FixedNodeExchange());

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
		String key = null;

		// Is this a width dimension?
		if (node.get("width_range") != null) {
			key = node.get("width_range").asText(null);
			if (key != null) {
				dimensions = new Dimensions();
				parts = key.split("-");
				int leftX = Integer.parseInt(parts[0].trim());
				int rightX = Integer.parseInt(parts[1].trim());
				d = new Dimension(leftX, rightX, -1, -1);
				dimensions.add(d);
				return;
			}
		}

		// Is this a height dimension
		if (node.get("height_range") != null) {
			key = node.get("height_range").asText(null);
			if (key != null) {
				dimensions = new Dimensions();
				parts = key.split("-");
				int leftY = Integer.parseInt(parts[0].trim());
				int rightY = Integer.parseInt(parts[1].trim());
				d = new Dimension(-1, -1, leftY, rightY);
				dimensions.add(d);
				return;
			}
		}

		// Is this WxH, ... list
		if (node.get("width_height_list") != null) {
			key = node.get("width_height_list").asText(null);
			if (key != null && key.length() > 0) {
				dimensions = new Dimensions();
				String[] elements = key.split(",");
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

		String spec = node.get("deals").asText(null);
		if (spec == null || spec.trim().length() == 0)
			return;
		deals = new Deals();
		String[] parts = spec.split(",");
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
		ArrayNode array = JdbcTools.factory.arrayNode();
		ArrayNode list;
		String rkey;
		int theId = Integer.parseInt(bannerid);
		if (isBanner) {
			list = Crosstalk.getInstance().bannerRtbStd;
			rkey = "banner_id";
		} else {
			list = Crosstalk.getInstance().videoRtbStd;
			rkey = "banner_video_id";

		}
		for (int i = 0; i < list.size(); i++) {
			JsonNode node = list.get(i);
			if (theId == node.get(rkey).asInt()) {
				Integer key = node.get("rtb_standard_id").asInt();
				JsonNode x = Crosstalk.getInstance().globalRtbSpecification.get(key);
				array.add(x);
			}
		}
		RtbStandard.processStandard(array, attributes);
	}
	
	public void update(JsonNode myNode) throws Exception {
		node = myNode;
		budget = new Budget();
		double dt = myNode.get(TOTAL_COST).asDouble(0);
		budget.totalCost.set(dt);
		budget.hourlyCost = new AtomicBigDecimal(myNode.get(HOURLY_COST).asDouble(0.0));
		budget.dailyCost = new AtomicBigDecimal(myNode.get(DAILY_COST).asDouble(0.0));

		Object x = myNode.get(DAILY_BUDGET);
		if (x != null && !(x instanceof NullNode)) {
			budget.dailyBudget = new AtomicBigDecimal(myNode.get(DAILY_BUDGET).asDouble());
		}

		x = myNode.get(HOURLY_BUDGET);
		if (x != null && !(x instanceof NullNode)) {
			budget.hourlyBudget = new AtomicBigDecimal(myNode.get(HOURLY_BUDGET).asDouble());
		}

		process();
	}

	public void process() throws Exception {
		bannerid = node.get(BANNER_ID).asText();
		campaignid = node.get(CAMPAIGN_ID).asInt();
		if (isBanner) {
			imageurl = node.get(IMAGE_URL).asText(null);
		}
		budget = new Budget();
		budget.totalBudget.set(node.get("total_basket_value"));
		budget.activate_time = node.get("interval_start").asLong();
		budget.expire_time = node.get("interval_end").asLong();

		bid_ecpm.set(node.get("bid_ecpm").asDouble());

		if (isBanner) {
			width = node.get("width").asInt();
			height = node.get("height").asInt();

			contenttype = node.get(CONTENT_TYPE).asText();

			htmltemplate = node.get(HTML_TEMPLATE).asText();

			htmltemplate = htmltemplate.replaceAll("\n", "");
			htmltemplate = htmltemplate.replaceAll("\r", "");

			if (node.get("position") != null)
				position = node.get("position").asText(null);
		} else {
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
