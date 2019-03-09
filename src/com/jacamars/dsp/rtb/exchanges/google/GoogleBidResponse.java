package com.jacamars.dsp.rtb.exchanges.google;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.google.doubleclick.AdxExt;
import com.google.doubleclick.AdxExt.BidExt;
import com.google.openrtb.OpenRtb;
import com.google.openrtb.OpenRtb.BidResponse;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid;
import com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.InvalidProtocolBufferException;
import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.bidder.SelectedCreative;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.exchanges.adx.Base64;
import com.jacamars.dsp.rtb.pojo.Impression;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;



/**
 * Build a Protobuf openRTB bid response.
 * @author Ben M. Faul
 *
 */
public class GoogleBidResponse extends com.jacamars.dsp.rtb.pojo.BidResponse {
	transient BidResponse internal;
	transient BidResponse.Builder builder;
	
	/**
	 * Default constructor
	 */
	public GoogleBidResponse() {
		
	}
	
	/**
	 * Reconstitute a response using a proobud
	 * @param bytes byte[]. The protobuf bytes.
	 * @throws InvalidProtocolBufferException on bad expression of a a bid request.
	 */
	public GoogleBidResponse(byte [] bytes) throws InvalidProtocolBufferException {
		ExtensionRegistry reg = ExtensionRegistry.newInstance();
	    AdxExt.registerAllExtensions(reg);
		internal = com.google.openrtb.OpenRtb.BidResponse.parseFrom(bytes,reg);
	}
	
	/**
	 * Create a protobuf based bid response, multiple creative response.
	 * @param br GoogleBidRequest. The Bid request corresponding to this response.
	 * @param imp Impression. The impression belonging to this response.
	 * @param multi List. The list of selected creatives that match.
	 * @param xtime int. The time it took to make the request.
	 * @throws Exception on Protobuf serialization errors.
	 */
	public GoogleBidResponse(GoogleBidRequest br, Impression imp, List<SelectedCreative> multi, int xtime) throws Exception {
		this.br = br;
		this.exchange = br.getExchange();
		this.xtime = xtime;
		this.oidStr = br.id;
		this.impid = imp.getImpid();
				
		setResponseType();
		
		/** The configuration used for generating this response */
		Configuration config = Configuration.getInstance();
		StringBuilder snurl = new StringBuilder();
		
		/**
		 * Create the stub for the nurl, thus
		 */
		StringBuilder xnurl = new StringBuilder(config.winUrl);
		xnurl.append("/");
		xnurl.append(br.siteDomain);
		xnurl.append("/");
		if (br.isSite())
			xnurl.append("SITE/");
		else
			xnurl.append("APP/");
		xnurl.append(br.getExchange());
		xnurl.append("/");
		xnurl.append("${AUCTION_PRICE}"); // to get the win price back from the
											// Exchange....
		xnurl.append("/");
		xnurl.append(lat);
		xnurl.append("/");
		xnurl.append(lon);
		xnurl.append("/");
		
		SeatBid.Builder sbb = SeatBid.newBuilder();
		for (int i=0;i<multi.size(); i++) {
			SelectedCreative x = multi.get(i);
			this.camp = x.getCampaign();
			this.creat = x.getCreative();
			this.price = Double.toString(x.price /* * 1000000 */);
			this.dealId = x.dealId;
			this.adid = camp.adId;
			this.imageUrl = substitute(creat.imageurl);
			String billingId = getBillingId(camp,creat);
			
			snurl = new StringBuilder(xnurl);
			snurl.append(adid);
			snurl.append("/");
			snurl.append(creat.impid);
			snurl.append("/");
			snurl.append(oidStr.replaceAll("#", "%23"));
			snurl.append("/");
			snurl.append(br.siteId);
			
			
			Bid.Builder bb = Bid.newBuilder();
			bb.addAdomain(camp.adomain);
			bb.setW(this.width);
			bb.setH(this.height);
			bb.setCid(billingId);
			bb.setAdid(camp.adId);

			if (creat.w != null)
				bb.setW(creat.w);
			else {
				if (imp.format != null && imp.format.size()!=0) {
					bb.setW(imp.format.get(0).w);
				}
			}
			if (creat.h != null)
				bb.setH(creat.h);
			else {
				if (imp.format != null && imp.format.size()!=0) {
					bb.setH(imp.format.get(0).h);
				}
			}
			
			
			//bb.setNurl(snurl.toString());
			bb.setExtension(AdxExt.bid, BidExt.newBuilder()
                    .addAllImpressionTrackingUrl(asList(snurl.toString())).build());
			
			bb.setImpid(x.impid);
			bb.setId(br.id);
			bb.setPrice(x.price * 100000);
			if (dealId != null)
				bb.setDealid(x.dealId);
			bb.setIurl(substitute(imageUrl));
			
			String adm;
			if (br.usesEncodedAdm)
				adm = substitute(creat.encodedAdm);
			else
				adm = substitute(creat.unencodedAdm);

			StringBuilder subbed = new StringBuilder(adm);
			macroSubs(subbed);
			adm = subbed.toString();
			if (x.getCreative().isVideo()) {
				adm = switchVastToUrl(adm, br.id);
			}


			bb.setAdm(subbed.toString());
			sbb.addBid(bb.build());
		}
		
		sbb.setSeat(Configuration.getInstance().seats.get(exchange));
		SeatBid seatBid = sbb.build();
		builder.addSeatbid(seatBid);
		builder.setCur(creat.cur);
			
		internal = builder.build();
	}

	/**
	 * Convert the VAST tag to a URL that can be retrieved from aerospike
	 * @param adm String. The adm with the vast tag
	 * @return String. The URL that can be used to retrieve this vast tag.
	 */
	String switchVastToUrl(String adm, String bidid) throws Exception {
		String uuid = UUID.randomUUID().toString();
		String newAdm = Configuration.getInstance().vastUrl + "?key=" + uuid + "&bidid=" + bidid;
		Controller.getInstance().recordVastVideo(adm, uuid);
		return newAdm;
	}
	
	/**
	 * Single response constructor for Google protobuf.
	 * @param br GoogleBidRequest. The bid request that belongs to this response.
	 * @param imp Impression. Not used.
	 * @param camp Campaign. The campaign that is responding.
	 * @param creat Creative. The creative that is responding.
	 * @param oidStr The request object id.
	 * @param price double. The price we are bidding at.
	 * @param dealId String. The deal id.
	 * @param xtime int. The time it took to make the bid.
	 * @throws Exception on protobuf errors.
	 */
	public GoogleBidResponse(GoogleBidRequest br, Impression imp, Campaign camp, Creative creat,
			String oidStr, double price, String dealId, int xtime) throws Exception {
		this.br = br;
		this.imp = imp;
		this.camp = camp;
		this.oidStr = oidStr;
		this.creat = creat;
		this.xtime = xtime;
		this.price = Double.toString(price);
		this.dealId = dealId;
		this.exchange = br.getExchange();
		
		this.cost = price;

		setResponseType();
		
		String billingId = getBillingId(camp,creat);

		impid = imp.getImpid();
		adid = camp.adId;
		crid = creat.impid;
		this.domain = br.siteDomain;

		forwardUrl = substitute(creat.getForwardUrl()); // creat.getEncodedForwardUrl();
		imageUrl = substitute(creat.imageurl);
		exchange = br.getExchange();
		
		builder = BidResponse.newBuilder();
		
		builder.setBidid(br.id);
		
		StringBuilder snurl = new StringBuilder(Configuration.getInstance().winUrl);
		snurl.append("/");
		snurl.append(br.siteDomain);
		snurl.append("/");
		if (br.isSite())
			snurl.append("SITE/");
		else
			snurl.append("APP/");
		snurl.append(br.getExchange());
		snurl.append("/");
		snurl.append("${AUCTION_PRICE}"); // to get the win price back from the
											// Exchange....
		snurl.append("/");
		snurl.append(lat);
		snurl.append("/");
		snurl.append(lon);
		snurl.append("/");
		snurl.append(adid);
		snurl.append("/");
		snurl.append(creat.impid);
		snurl.append("/");
		snurl.append(oidStr.replaceAll("#", "%23"));

		String adm;
		
		//////////////////
		if (this.creat.isVideo()) {
			if (br.usesEncodedAdm) {
				adm = substitute(creat.encodedAdm);
			} else {
				adm = substitute(creat.unencodedAdm);
			}
			
		} else if (this.creat.isNative()) {
			if (br.usesEncodedAdm) {
				adm = substitute(this.creat.getEncodedNativeAdm(br));
			} else {
				adm = substitute(this.creat.getUnencodedNativeAdm(br));
			}
		} else {
			adm = substitute(getTemplate());
		}
		
		//////////////////
		
		adm = adm.replaceAll("\\\\", "");

		StringBuilder subbed = new StringBuilder(adm);
		macroSubs(subbed);
		adm = subbed.toString();

		if (creat.isVideo())
			adm = switchVastToUrl(adm, br.id);
		
		this.forwardUrl = adm;
		
		Bid.Builder bb = Bid.newBuilder();
		bb.addAdomain(camp.adomain);
		bb.setAdid(camp.adId);

		if (creat.w != null)
			bb.setW(creat.w);
		else {
			if (imp.format != null && imp.format.size()!=0) {
				bb.setW(imp.format.get(0).w);
			}
		}
		if (creat.h != null)
			bb.setH(creat.h);
		else {
			if (imp.format != null && imp.format.size()!=0) {
				bb.setH(imp.format.get(0).h);
			}
		}
		
		// bb.setNurl(snurl.toString());
		bb.setExtension(AdxExt.bid, BidExt.newBuilder()
                .addAllImpressionTrackingUrl(asList(snurl.toString())).build());
		
		if (imp.getImpid() == null) {
			imp.setImpid("1");
		}
		
		bb.setImpid(imp.getImpid());
		if (creat.w != null)
			bb.setW(creat.w);
		if (creat.h != null)
			bb.setH(creat.h);
		bb.setId(br.id);              // ?
		bb.setCid(billingId);
		bb.setCrid(creat.impid);
		bb.setPrice(price /* * 1000000 */);
		if (dealId != null)
			bb.setDealid(dealId);
		if (imageUrl != null)
			bb.setIurl(substitute(imageUrl));

		bb.setAdm(adm);
		
		SeatBid.Builder sbb = SeatBid.newBuilder();
		sbb.addBid(0,bb.build());
		sbb.setSeat(Configuration.getInstance().seats.get(exchange));
		
		SeatBid seatBid = sbb.build();
		builder.addSeatbid(seatBid);
		builder.setId(br.id);
		builder.setCur(creat.cur);
			
		internal = builder.build();
		
		//if (adid.equals("219"))
		//	System.out.println(internal);
		
		// add this to the log
		byte[] bytes = internal.toByteArray();
		protobuf = new String(Base64.encodeBase64(bytes));
	}
	
	void setResponseType() {
		/** Set the response type ****************/
		if (imp.nativead)
			this.adtype="native";
		else
		if (imp.video != null)
			this.adtype="video";
		else
			this.adtype="banner";
		/******************************************/
	}
	
	String getBillingId(Campaign x, Creative c) throws Exception {
		String billingId = creat.extensions.get("billing_id");
		if (billingId == null) {
			throw new Exception(
					x.adId + "/" + c.impid + " is missing required billing_id for Google SSP");
		}
		return billingId;
	}

	public GoogleBidResponse(JsonNode root) {
		if (root != null) {
			builder = OpenRtb.BidResponse.newBuilder();
			builder.setId(root.path("id").textValue());
			builder.setBidid(root.path("id").textValue());
			builder.setCur(root.path("cur").textValue());

			ArrayNode seatBids = (ArrayNode) root.path("seatbid");
			for (int i = 0; i < seatBids.size(); i++) {
				JsonNode seatBid = seatBids.get(i);
				ArrayNode bids = (ArrayNode) seatBid.path("bid");
				OpenRtb.BidResponse.SeatBid.Builder seatBidBuilder = OpenRtb.BidResponse.SeatBid.newBuilder();
				for (int j = 0; j < bids.size(); j++) {
					JsonNode bid = bids.get(j);
					seatBidBuilder.addBid(j, constructBid(bid));
				}
				seatBidBuilder.setSeat("google-id");
				builder.addSeatbid(i, seatBidBuilder.build());
			}
			internal = builder.build();
			byte[] bytes = internal.toByteArray();
			protobuf = new String(Base64.encodeBase64(bytes));
		}
	}
	
	@JsonIgnore
	public BidResponse getInternal() {
		return internal;
	}
	
	/**
	 * Write the response This used to transmit the OCTET string back.
	 */
	@Override
	public void writeTo(HttpServletResponse response) throws Exception {
		response.setContentType("application/octet-string");
		internal.writeTo(response.getOutputStream());
	}
	
	/**
	 * Write the response using your favorite type.
	 */
	@Override
	public void writeTo(HttpServletResponse response, String x) throws Exception {
		response.setContentType(x);
		internal.writeTo(response.getOutputStream());
	}
	
	/**
	 * Returns a string representation of the request in Protobuf form.
	 */
	@Override
	public String toString() {
		return internal.toString();
	}
	
	/**
	 * Return the response buffer for marshaling a log record.
	 */
	@Override
	public StringBuilder getResponseBuffer() {
		if (response == null)
			response = new StringBuilder(internal.toString());
		return response;
	}
	
	/**
	 * Set the response buffer for marshaling a log record. 
	 */
	public void setResponseBuffer(String s) {
		response = new StringBuilder(s);
	}

	private static com.google.openrtb.OpenRtb.BidResponse.SeatBid.Bid constructBid(JsonNode bid) {
		OpenRtb.BidResponse.SeatBid.Bid.Builder bidBuilder = OpenRtb.BidResponse.SeatBid.Bid.newBuilder();
		JsonNode field = bid.path("id");
		if (field != null) {
			bidBuilder.setId(field.textValue());
		}

		field = bid.path("impid");
		if (!(field instanceof MissingNode)) {
			bidBuilder.setImpid(field.textValue());
		}

		field = bid.path("adid");
		if (!(field instanceof MissingNode)) {
			bidBuilder.setAdid(field.textValue());
		}

		field = bid.path("nurl");
		if (!(field instanceof MissingNode)) {
			bidBuilder.setExtension(AdxExt.bid, AdxExt.BidExt.newBuilder().addAllImpressionTrackingUrl(asList(field.textValue())).build());
		}

		field = bid.path("adm");
		if (!(field instanceof MissingNode)) {
			bidBuilder.setAdm(field.textValue());
		}

		field = bid.path("price");
		if (!(field instanceof MissingNode)) {
			bidBuilder.setPrice(bid.path("price").doubleValue());
		}

		field = bid.path("iurl");
		if (!(field instanceof MissingNode)) {
			bidBuilder.setIurl(field.textValue());
		}

		field = bid.path("cid");
		if (!(field instanceof MissingNode)) {
			bidBuilder.setCid(field.textValue());
		}

		field = bid.path("h");
		if (!(field instanceof MissingNode)) {
			bidBuilder.setH(field.intValue());
		}

		field = bid.path("w");
		if (!(field instanceof MissingNode)) {
			bidBuilder.setW(field.intValue());
		}

		field = bid.path("crid");
		if (!(field instanceof MissingNode)) {
			bidBuilder.setCrid(field.textValue());
		}

		field = bid.path("dealid");
		if (!(field instanceof MissingNode)) {
			bidBuilder.setDealid(field.textValue());
		}

		field = bid.path("cat");
		if (!(field instanceof MissingNode)) {
			ArrayNode creativeCategories = (ArrayNode) field;
			if (creativeCategories != null) {
				for (int i = 0; i < creativeCategories.size(); i++) {
					bidBuilder.setCat(i, creativeCategories.get(i).textValue());
				}
			}
		}

		field = bid.path("attr");
		if (!(field instanceof MissingNode)) {
			ArrayNode creativeAttributes = (ArrayNode) field;
			if (creativeAttributes != null) {
				for (int i = 0; i < creativeAttributes.size(); i++) {
					bidBuilder.addAttr(OpenRtb.CreativeAttribute.valueOf(creativeAttributes.get(i).textValue()));
				}
			}
		}

		field = bid.path("adomain");
		if (!(field instanceof MissingNode)) {
			ArrayNode aDomains = (ArrayNode) field;
			if (aDomains != null) {
				for (int i = 0; i < aDomains.size(); i++) {
					bidBuilder.addAdomain(aDomains.get(i).textValue());
				}
			}
		}

		field = bid.path("ext");
		if (!(field instanceof MissingNode)) {
			field = field.path("tracker");
			if (!(field instanceof MissingNode)) {
				bidBuilder.setExtension(AdxExt.bid, AdxExt.BidExt.newBuilder().addAllImpressionTrackingUrl(asList(field.textValue())).build());
			} else {
				field = field.path("trackers"); //handle multiple url in tracker.
				if (!(field instanceof MissingNode)) {
					ArrayNode trackers = (ArrayNode) field;
					List<String> urls = new ArrayList<>(trackers.size());
					for (int i = 0; i < trackers.size(); i++) {
						urls.add(trackers.get(i).textValue());
					}
					bidBuilder.setExtension(AdxExt.bid, AdxExt.BidExt.newBuilder().addAllImpressionTrackingUrl(urls).build());
				}
			}
		}

		return bidBuilder.build();
	}

}
