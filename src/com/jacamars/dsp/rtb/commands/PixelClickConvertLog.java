package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.exchanges.adx.AdxBidRequest;
import com.jacamars.dsp.rtb.exchanges.adx.AdxWinObject;
import com.jacamars.dsp.rtb.exchanges.google.GoogleWinObject;
import com.jacamars.dsp.rtb.exchanges.google.OpenRTB;
import com.jacamars.dsp.rtb.exchanges.openx.OpenX;
import com.jacamars.dsp.rtb.exchanges.openx.OpenXWinObject;
import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.WinObject;
import com.jacamars.dsp.rtb.tools.DbTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;

/**
 * Base class for logging pixel loads, clicks and conversions.
 *
 * @author Ben M. Faul
 */
public class PixelClickConvertLog {
    static final Logger logger = LoggerFactory.getLogger(PixelClickConvertLog.class);
    public String instance;
    public String payload;
    public double lat;
    public double lon;
    public double price;
    public long timestamp;
    public int type;
    public String ad_id;
    public String creative_id;
    public String bid_id;
    public boolean debug = false;
    public int x;
    public int y;
    public String exchange;
    public String domain = "undefined";
    public String bidtype = "undefined";
    public String userId = "undefined";
    public String deviceId = "undefined";
    public String userProfile = "undefined";
    /** The cookie id, if applicable */
    public String uid;
    /** The transaction id, if applicable */
    public String tid;
    /** The deviceid, if present */
    public String deviceid;

    public static final int PIXEL = 0;
    public static final int CLICK = 1;
    public static final int CONVERT = 2;


    public static void main(String[] args) {
        PixelClickConvertLog x = new PixelClickConvertLog();
        x.create("//pixel/citenko/4/3/25c40279-dd90-4caa-afc9-d0474705e0d1/0.0425/32.83/-83.65");

    }

    /**
     * Default constructor
     */
    public PixelClickConvertLog() {

    }

    /**
     * Create the log from a chunk of text
     *
     * @param data String. The data to use.
     */
    public void create(String data) {
        payload = data;
        timestamp = System.currentTimeMillis();
        if (data.contains("redirect")) {
            doClick();
            return;
        }

        doClick();
        type = PIXEL;
    }

    /**
     * Process a click
     */
    void doClick() {
        parseElements();
        type = CLICK;
        timestamp = System.currentTimeMillis();
    }

    void parseElements() {
        String str = payload.replaceAll("&","/");
        int i = str.indexOf("url=");
        if (i != -1) {
            str = str.substring(0,i);
        }
        String[] parts = str.split("/");

        for (i = 0; i < parts.length; i++) {
            if (parts[i].indexOf("=") > -1) {
                String[] items = parts[i].split("=");
                switch (items[0]) {
                    case "bid_type":
                        bidtype = items[1];
                        break;
                    case "domain":
                    case "site_domain":
                        if (items.length>1)
                            domain = items[1];
                        break;
                    case "lat":
                        try {
                            lat = Double.parseDouble(items[1]);
                        } catch (Exception error) {
                            lat = 0;
                        }
                        break;
                    case "lon":
                        try {
                            lon = Double.parseDouble(items[1]);
                        } catch (Exception error) {
                            lon = 0;
                        }
                        break;
                    case "x":
                        try {
                            x = Integer.parseInt(items[1]);
                        } catch (Exception error) {
                            x = 0;
                        }
                        break;
                    case "y":
                        try {
                            y = Integer.parseInt(items[1]);
                        } catch (Exception error) {
                            y = 0;
                        }
                        break;
                    case "price":
                        if (exchange.equals(OpenRTB.GOOGLE)) {
                            try {
                                price = GoogleWinObject.decrypt(items[1], System.currentTimeMillis());
                                price /= 1000;
                            } catch (Exception e) {
                                //e.printStackTrace();
                                price = 0;
                            }
                        } else if (exchange.equals(OpenX.OPENX)) {
                            try {
                                price = OpenXWinObject.decrypt(items[1]);
                                price /= 1000;
                            } catch (Exception e) {
                                //e.printStackTrace();
                                price = 0;
                            }
                        } else if (exchange.equals(AdxBidRequest.ADX)) {
                        	 try {                    
								price = AdxWinObject.decrypt(items[1], System.currentTimeMillis());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								System.out.println("Error decrypt: =====>'" + items[1] + "'");
								e.printStackTrace();
							}
                             price /= 1000;
                        } else {
                            try {
                                price = Double.parseDouble(items[1]);
                            } catch (Exception error) {
                                //System.err.println("Error in price for: " + payload);
                                price = 0;
                            }
                        }
                        break;
                    case "bid_id":
                        bid_id = items[1];
                        try {
                            bid_id = URLDecoder.decode(bid_id, "UTF-8");
                        } catch (Exception error) {
                            logger.warn("Error reading bid_id: {}, error: }", bid_id, error.toString());
                        }
                        break;
                    case "ad_id":
                        ad_id = items[1];
                        break;
                    case "creative_id":
                        creative_id = items[1];
                        break;
                    case "exchange":
                        exchange = items[1];
                        break;
                    case "user_id":
                        userId = items[1];
                        break;
                    case "user_profile":
                        userProfile = items[1];
                        break;
                    case "device_id":
                        deviceId = items[1];
                        break;
                    case "uid":
                        uid = items[1];
                        break;
                    case "tid":
                        tid = items[1];
                        break;
                    case "debug":
                    case "DEBUG":
                        debug = Boolean.parseBoolean(items[1]);
                        break;
                    case "deviceid":
                        deviceid = items[1];
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Will generate a fake win based on a pixel
     */
    public void handleFakeWin() {

        if (exchange == null)
            return;

        parseElements();

        /**
         * Huge hack. C1X SSP does not do win url's you have to piggy back the the win from the pixel
         */
        if (BidRequest.usesPiggyBackedWins(exchange)) {
            try {

                domain = domain.replaceAll("/","");
                domain = domain.replaceAll("https:","");
                domain = domain.replaceAll("http:", "");
                StringBuilder sb = new StringBuilder();
                sb.append("https://fake:notreal/rtb/win/");
                sb.append(domain);
                sb.append("/");
                sb.append(bidtype);
                sb.append("/");
                sb.append(exchange);
                sb.append("/");
                sb.append(price);
                sb.append("/");
                sb.append(lat);
                sb.append("/");
                sb.append(lon);
                sb.append("/");
                sb.append(ad_id);
                sb.append("/");
                sb.append(creative_id);
                sb.append("/");
                sb.append(bid_id);
                WinObject.getJson(sb.toString());
                RTBServer.win++;
            } catch (Exception error) {
                error.printStackTrace();
            }
        } 
    }

    /**
     * Returns a string representation in JSON
     * @return a JSOn formatted string of this item.
     */
    @Override
    public String toString() {
        try {
            return DbTools.mapper.writer().writeValueAsString(this);
        } catch (Exception error) {

        }
        return null;
    }
}
