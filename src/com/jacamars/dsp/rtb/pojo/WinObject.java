package com.jacamars.dsp.rtb.pojo;

import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.RecordedBid;
import com.jacamars.dsp.rtb.exchanges.adx.AdxBidRequest;
import com.jacamars.dsp.rtb.exchanges.adx.AdxWinObject;
import com.jacamars.dsp.rtb.exchanges.google.GoogleWinObject;
import com.jacamars.dsp.rtb.exchanges.google.OpenRTB;
import com.jacamars.dsp.rtb.exchanges.openx.OpenX;
import com.jacamars.dsp.rtb.tools.MemoryAccounting;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.util.Map;

/**
 * TODO: This needs work, this is a performance pig
 * 
 * @author Ben M. Faul
 *
 */
public class WinObject {

	static final Logger logger = LoggerFactory.getLogger(WinObject.class);
	/** URL decoder used with digesting encoded url fields */
	transient static ObjectMapper mapper = new ObjectMapper();
	static transient URLDecoder decoder = new URLDecoder();

	public String hash, cost, lat, lon, adId, pubId, image, forward, price, cridId, adm, adtype, domain, bidtype;
	
	/** The region field, may be added by crosstalk, but if not using crosstalk, will be null */
	public String region;
	/** The time the record was written */
	public long timestamp;
	/** The instance where this originated from */
	public String origin = Configuration.instanceName;
	// The type field, used in logging
	public String type = "wins";

	public WinObject() {

	}

	public WinObject(String hash, String cost, String lat, String lon, String adId, String crid, String pubId,
			String image, String forward, String price, String adm, String adtype, String domain, String bidType) {
		this.hash = hash;
		this.cost = cost;
		this.lat = lat;
		this.lon = lon;
		this.adId = adId;
		this.cridId = crid;
		this.pubId = pubId;
		this.image = image;
		this.forward = forward;
		this.price = price;
		this.adtype = adtype;
		this.domain = domain;
		this.bidtype = bidType;
		if (adm == null)
			this.adm = "";
		else
			this.adm = adm;
		this.adtype = adtype;
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * The worker method for converting a WIN http target into a win
	 * notification in the bidder.
	 * 
	 * @param target
	 *            String. The HTTP url that makes up the win notification from
	 *            the exchange.
	 *            Notice, there are 12 parts:
	 *            index		what
	 *            5			domain
	 *            6			bid-type
	 *            6			pubid
	 *            7 		price
	 *            8			lat
	 *            9			lon
	 *            10			adid
	 *            11		cridid
	 *            12		bid id
	 *
	 *            13...		The rest are concatenated to the bid id . So: ..../xxxx/yyy/xxx will result in a
	 *            			bidid of xxxx/yyy/xxx - This handles Google's idiotic implementation of RTB, which allows bidid's
	 *            			to contain "/" even though a bidid in RTB doesn't contain html reserverd chars.
	 * @return String. The ADM field to be used by exchange serving up the data.
	 * @throws Exception
	 *             on REDIS errors.
	 */
	@JsonIgnore
	public static String getJson(String target) throws Exception {
		String image = null;
		String adm = StringUtils.EMPTY;
		String cost = StringUtils.EMPTY;
		String[] parts = target.split("http");

		String forward = "http:" + parts[1];
		if (parts.length > 2)
			image = "http:" + parts[2];

		parts = parts[1].split("/");

		if (parts.length < 14) {
			logger.error("Error, badly formed win record: {}", target);
			return "";
		}

		String domain = parts[5];

		String bidType = parts[6];
		String pubId = parts[7];
		String price = parts[8];
		String lat = parts[9];
		String lon = parts[10];
		String adId = parts[11];
		String cridId = parts[12];
		String hash = parts[13];

		if (parts.length > 14) {
			for (int i=14;i<parts.length;i++) {
				hash += "/" + parts[i];
			}
		}
		// watch out for special characters encoded in the hash.
		hash = URLDecoder.decode(hash, "UTF-8");

		try {
			if (image != null)
				image = decoder.decode(image, "UTF-8");
			forward = decoder.decode(forward, "UTF-8");
		} catch (Exception e) {
			//Ignore this exception. Log level debug is enough
			logger.debug("Error encountered in decoding win url: '{}' was {}", target, e);
		}

        // Get adm and cost from bidCachePool as early as possible.
		try {
			RecordedBid bid = Controller.getInstance().getBidData(hash);
			if (bid != null) {
				// adm = (String) bid.get("ADM");
				cost = bid.getPrice();
			}
		} catch (Exception error) {
			logger.error("CANT RETRIEVE BID DATA, HAZELCAST ERROR: {}", error);
		}

		// This is synthetic, because in reality, adx has no win notification, this is a fake pixel fire that does the work.
		if (pubId.equals(AdxBidRequest.ADX)) {
			Double dv = new Double(0);
			try {
				Long value = AdxWinObject.decrypt(price, System.currentTimeMillis());
				dv = new Double(value);
				dv /= 1000;
			} catch (Exception error) {
				try {
					dv = Double.parseDouble(price);
				} catch (Exception err) {
					logger.warn("Error parsing price from {}", target);
				}
			}
			convertBidToWin(hash, cost, lat, lon, adId, cridId, pubId, image, forward, dv.toString(), pubId, domain, bidType);
			BidRequest.incrementWins(pubId);
			return adm;
		}
		
		if (pubId.equals(OpenRTB.GOOGLE)) {
			Double dv = new Double(0);
			try {
				dv = GoogleWinObject.decrypt(price, System.currentTimeMillis());
			} catch (Exception error) {
				logger.error("Bad price parse for google on: " + price);
				dv = GoogleWinObject.decrypt(hash, System.currentTimeMillis());
				logger.warn("Google win, price {} and hash {} are swapped", price, hash);
			}
			dv /= 1000;
			convertBidToWin(hash, cost, lat, lon, adId, cridId, pubId, image, forward, dv.toString(), pubId, domain, bidType);
			BidRequest.incrementWins(pubId);
			return adm;
		}

		if (pubId.equals(OpenX.OPENX)) {
			// DO NOT DECCRYPT OpenX here, we already did it when we created the synthetic from the pixel.
			// Already divided! dv /= 1000;
			// Refer PixelClickConvertLog.doClick()
			Double dv = Double.parseDouble(price);
			convertBidToWin(hash, cost, lat, lon, adId, cridId, pubId, image, forward, dv.toString(), pubId, domain, bidType);
			BidRequest.incrementWins(pubId);
			return adm;
		}

		// If the adm can't be retrieved, go ahead and convert it to win so that the accounting works. just return ""
		try {
			convertBidToWin(hash, cost, lat, lon, adId, cridId, pubId, image, forward, price, adm, domain, bidType);
		} catch (Exception error) {
			error.printStackTrace();
			logger.error("Error: {}, target: {}",error.toString(),target);
		}
		BidRequest.incrementWins(pubId);

		return adm;
	}

	/**
	 * Fast write this to a JSON String.
	 * 
	 * @return String. The json representation of this object.
	 */
	public String toString() {
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Pluck out the pieces from the win notification and create a win message.
	 * 
	 * @param hash
	 *            String. The object ID of the bid
	 * @param cost
	 *            String. The cost of the bid.
	 * @param lat
	 *            String. The latitude of the usre.
	 * @param lon
	 *            String. The longitude of the user.
	 * @param adId
	 *            String. The campaign ad id.
	 * @param pubId
	 *            String. The publisher id.
	 * @param image
	 *            String. The image served.
	 * @param forward
	 *            String. The forwarding URL.
	 * @param price
	 *            String. ??????????
	 * @param adm
	 *            String. The adm that was returned.
	 *
	 */
	public static double convertBidToWin(String hash, String cost, String lat, String lon, String adId, String cridId,
			String pubId, String image, String forward, String price, String adm, String domain, String bidType) {
		String adType = null;
		double value = 0.0;
		try {
			Controller c = Controller.getInstance();
			adType = c.deleteBidFromCache(hash);
			c.getInstance().sendWin(hash, cost, lat, lon, adId, cridId, pubId, image, forward, price, adm, adType, domain, bidType);
			value = Double.parseDouble(price);
			
			MemoryAccounting.getInstance().increment(adId+".adspend",new Double(value * 100).longValue());
			RTBServer.adspend += value;
		} catch (Exception error) {
			logger.error("Failed to delete bid from cache on exchange: {}, id: {}, error: {}", pubId, hash, error.toString());
		}
		return value;
	}
	
}
