package com.jacamars.dsp.crosstalk.budget;

import java.io.InputStream;


import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.entity.NStringEntity;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.shared.CampaignCache;

import com.jacamars.dsp.rtb.shared.AccountingCache;
import com.jacamars.dsp.rtb.shared.BidCachePool;

/**
 * A class that aggregates total, daily and hourly costs for campaigns and their
 * creatives.
 * 
 * @author Ben M. Faul
 *
 */
public class Aggregator {

	/** Global accumulator */
	static AccountingCampaign global = new AccountingCampaign("global");

	/** Indicates hourly aggregation */
	public static final int HOURLY = 0;

	/** Indicates daily aggregation */
	public static final int DAILY = 1;

	/** Indicates total cost aggregation */
	public static final int TOTAL = 2;

	/** How many hours past 0 hour */
	public static volatile double hoursPastZero = 0;

	/** How many minutes since last hour */
	public static volatile double minutesPastZero = 0;

	/** Which mode is this aggregegator */
	final int mode;

	/** Keeps campaigns in a handy map */
	static volatile InternalHashMap<String, AccountingCampaign> campaigns = new InternalHashMap();

	/** A map that holds the returned value from ELK */
	Map map;

	AccountingCache ac;

	long revolution = 0;

	volatile boolean delta = true;

	static final Logger logger = LoggerFactory.getLogger(Aggregator.class);

	/** A handy JSON object for pretty printing */
	public static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public Aggregator(int mode, AccountingCache ac) {
		this.mode = mode;
		this.ac = ac;
	}

	/**
	 * Returns the map of the aggregations.
	 * 
	 * @return Map. The hash map representing the aggregations.
	 */
	public Map getMap() {
		return map;
	}

	/**
	 * Execute the ELK query.
	 * 
	 * @throws Exception on network errors.
	 */
	public void query() throws Exception {
		doTotal();
		revolution++;
	}

	private void doTotal() throws Exception {
		
		return;
		
		
	/*	double total = 0;
		Map<String, Map<String, Double>> buckets = ac.asMapAndReset();
		Map x = null;

		buckets.forEach((k, v) -> {
			////////////////// Creatives ////////////////////
			var c = get(k);
			v.forEach((creative, cv) -> {
				var price = cv;
				price /= 1000;
				if (creative.equals("*total*")) {
					if (c == null) {
						var cc = new AccountingCampaign(k);
						add(cc);
					}
					setPrice(c, price);
				} else {
					AccountingCreative creat = c.getCreative(creative);
					if (creat == null) {
						creat = new AccountingCreative(creative);
						c.addCreative(creat);
					}
					setPrice(creat, price);
				}

				// don't update the db if its not necessary
				delta = true;
				if (delta) {
					try {
						switch (mode) {
						case TOTAL:
							updateTotalInDb(k, creative, price);
							break;
						case DAILY:
							updateDailyInDb(k, creative, price);
							break;
						case HOURLY:
							updateHourlyInDb(k, creative, price);
							break;
						}
					} catch (Exception error) {
						error.printStackTrace();
					}
				}
			});
		});
*/
	}

	void updateTotalInDb(String cid, String composite, Double price) throws Exception {
		if (composite.equals("*total*"))
			Crosstalk.getInstance().updateCampaignTotal(cid, price);
		else
			Crosstalk.getInstance().updateCreativeTotal(cid, composite, price);
	}

	void updateHourlyInDb(String cid, String composite, Double price) throws Exception {
		if (composite.equals("*total*"))
			Crosstalk.getInstance().updateCampaignTotalHourly(cid, price);
		else
			Crosstalk.getInstance().updateCreativeHourly(cid, composite, price);
	}

	void updateDailyInDb(String cid, String composite, Double price) throws Exception {
		if (composite.equals("*total*"))
			Crosstalk.getInstance().updateCampaignTotalDaily(cid, price);
		else
			Crosstalk.getInstance().updateCreativeDaily(cid, composite, price);
	}

	/**
	 * Set the cost in a campaign, based on the mode of this Aggregation.
	 * 
	 * @param c Campaign. The object holding the campaign.
	 * @param p double. The cost.
	 */
	private void setPrice(AccountingCampaign c, double p) {
		delta = false;
		switch (mode) {
		case DAILY:
			if (c.getDaily_price() == p)
				return;
			c.setDaily_price(c.getDaily_price()+p);
			break;
		case HOURLY:
			c.delta.add(p);
			if (c.getHourly_price() == p)
				return;
			c.setHourly_price(c.getHourly_price()+p);
			break;
		default:
			if (c.getTotal_price() == p)
				return;
			c.setTotal_price(c.getTotal_price()+p);
			break;
		}
		delta = true;
	}

	/**
	 * Set the cost of the Creative, based on the mode.
	 * 
	 * @param c Creative. The object holding the creative.
	 * @param p double. The cost.
	 */
	private void setPrice(AccountingCreative c, double p) {
		delta = false;
		switch (mode) {
		case DAILY:
			if (c.daily_price == p)
				return;
			c.daily_price += p;
			break;
		case HOURLY:
			c.delta.add(p);
			if (c.hourly_price == p)
				return;
			c.hourly_price += p;
			break;
		default:
			if (c.total_price == p)
				return;
			c.total_price += p;
			break;
		}
		delta = true;
	}

	/**
	 * Parses the results of aggregation and puts it into the campaign.
	 * 
	 * @param c  Campaign. The object representing the campaign.
	 * @param cr Map. The aggregation results.
	 */
	private void handleTotalCreative(AccountingCampaign c, Map cr) {

	}

	/**
	 * Handle the creatives in a campaign, from the results of the aggregation.
	 * 
	 * @param c  Campaign. The object representing the campaign.
	 * @param cr Map. The aggregations of all the creatives in the campaign.
	 */
	private void handleCreative(AccountingCampaign c, Map cr) {

	}

	////////////////////////////

	/**
	 * Add a campaign to the static map.
	 * 
	 * @param c Campaign. The campaign to put into the map.
	 */
	public static void add(AccountingCampaign c) {
		campaigns.put(c.id, c);
	}

	/**
	 * Clear the campaigns map.
	 */
	public static void clear() {
		campaigns.clear();
	}

	/**
	 * Return a list of campaign ids.
	 * 
	 * @return List. The list of campaign id strings.
	 */
	public static List<String> getCampaigns() {
		List<String> ids = new ArrayList<String>();
		int i = 0;
		String x = null;
		while ((x = campaigns.getKey(i++)) != null) {
			ids.add(x);
		}
		return ids;
	}

	/**
	 * Return a campaign given it's key.
	 * 
	 * @param id String. The id of the campaign.
	 * @return Campaign. The campaign keyed by id.
	 */
	public static AccountingCampaign get(String id) {
		return (AccountingCampaign) campaigns.get(id);
	}

	/**
	 * Return a list of creative ids with the campaign.
	 * 
	 * @param id String. The campaign id key.
	 * @return List. A list of strings of the creatives for this campoaign.
	 */
	public static List<AccountingCreative> getCreatives(String camp) {
		AccountingCampaign c = get(camp);
		return c.creatives;
	}

	/**
	 * Return whether the specified campaign/creative/type exists.
	 * 
	 * @param campaign String. The campaign id to search for.
	 * @param creative String. The creative id to search for.
	 * @param type     String. The type of the creative.
	 * @return boolean. Returns true if found. else returns false.
	 */
	public static boolean creativeExists(String id, String creative) {
		AccountingCampaign c = get(id);
		if (c == null)
			return false;

		AccountingCreative cr = c.getCreative(creative);
		if (cr == null)
			return false;
		return true;
	}
	
	public static boolean creativeExists(String id, String creative, String type) {
		String key = type + ":" + creative;
		AccountingCampaign c = get(id);
		if (c == null)
			return false;

		AccountingCreative cr = c.getCreative(key);
		if (cr == null)
			return false;
		return true;
	}

	/**
	 * Update the global counter. Make sure you call this after pa
	 */
	public static void updateGlobal() {
		global.setTotal_price(0);
		global.setDaily_price(0);
		global.setHourly_price(0);

		int i = 0;
		Object x = null;
		while ((x = campaigns.getNext(i++)) != null) {
			AccountingCampaign entry = (AccountingCampaign) x;
			double d = global.getTotal_price();
			global.setTotal_price(d + entry.getTotal_price());

			d = global.getDaily_price();
			global.setDaily_price(d + entry.getDaily_price());

			d = global.getHourly_price();
			global.setHourly_price(d + entry.getHourly_price());
		}

		global.delta.add(global.getHourly_price());
	}

	/**
	 * Pretty print dump all the campaigns and creatives.
	 */
	public static void dump() {
		logger.info("********************************************");

		for (int i = 0; i < campaigns.size(); i++) {
			AccountingCampaign c = campaigns.getNext(i++);

			// if (c.delta != 0) {
			logger.info("{}, total: {}, daily: {}. hourly: {}, delta: {}", c.id, c.getTotal_price(), c.getDaily_price(),
					c.getHourly_price(), c.delta.getAverage());

			double hourly = c.delta.getAverage() * 60;
			double daily = c.delta.getAverage() * 3600;
			logger.info("Predict: Daily: {}, Hourly: {}", daily, hourly);

			for (AccountingCreative cc : c.creatives) {
				// if (cc.delta != 0) {
				logger.info("{}/{}/{}, Total: {}, Daily: {}, Hourly: {}, Delta: {}", c.id, cc.id, cc.adtype,
						cc.total_price, cc.daily_price, cc.hourly_price, cc.delta.getAverage());
				// }
			}

			// }
		}
		logger.info("********************************************");
	}

	/**
	 * Pretty print dump all the campaigns and creatives.
	 */
	static void dumpAllActive() {
		logger.info("********************************************");
		for (int i = 0; i < campaigns.size(); i++) {
			AccountingCampaign c = campaigns.getNext(i++);
			if (c.getHourly_price() != 0) {
				logger.info("{}, total: {}, daily: {}. hourly: {}, delta: {}", c.id, c.getTotal_price(),
						c.getDaily_price(), c.getHourly_price(), c.delta.getAverage());

				double hourly = c.delta.getAverage() * 60;
				double daily = c.delta.getAverage() * 3600;
				logger.info("Predict: Daily: {}, Hourly: {}", daily, hourly);

				for (AccountingCreative cc : c.creatives) {
					if (cc.hourly_price != 0) {
						logger.info("{}/{}/{}, Total: {}, Daily: {}, Hourly: {}, Delta: {}", c.id, cc.id, cc.adtype,
								cc.total_price, cc.daily_price, cc.hourly_price, cc.delta.getAverage());
					}
				}

			}
		}
		logger.info("********************************************");
	}

	/**
	 * Dump the indicated campaign to stdout.
	 * 
	 * @param who String. The campaign to dump.
	 */
	static void dump(String who) {
		logger.info("********************************************");
		for (int i = 0; i < campaigns.size(); i++) {
			AccountingCampaign c = campaigns.getNext(i++);
			if (c.id.equals(who)) {
				logger.info("{}, Total: {}, Hourly: {}, Daily: {}, delta: {}", c.id, c.getTotal_price(),
						c.getDaily_price(), c.getHourly_price(), c.delta.getAverage());

				double hourly = c.delta.getAverage() * 60;
				double daily = c.delta.getAverage() * 3600;
				logger.info("Predict, Daily: {}, Hourly: {}", daily, hourly);

				for (AccountingCreative cc : c.creatives) {
					// if (cc.id.equals("420")) {
					logger.info("{}/{}/{}, Total: {}, Daily: {}, Hourly: {}, Delta: {}", who, cc.id, cc.adtype,
							cc.total_price, cc.daily_price, cc.hourly_price, c.delta.getAverage());
					// }
				}

			}
		}
		logger.info("********************************************");
	}

	static void touchAll() {
		for (int i = 0; i < campaigns.size(); i++) {
			AccountingCampaign c = campaigns.getNext(i);
			c.touch();
		}
	}
}

/**
 * A builtin class for maintaining aggregations for campaigns and creatives.
 * Used only within the budgeting part of the system. No fancy synchronization
 * needed, only 1 thread writes, all others read.
 * 
 * @author Ben M. Faul
 *
 */
class AccountingCampaign {

	/** The creatives of this campaign */
	public List<AccountingCreative> creatives = new ArrayList<AccountingCreative>();

	/** Campaign id */
	public String id;

	/** The hourly cost incurred since the last hour mark */
	private volatile double hourly_price;
	private boolean setHourly = false;

	/** The daily cost incurred since the last day mark */
	private volatile double daily_price;
	private boolean setDaily = false;

	/** The total price since campaign creation */
	private volatile double total_price;
	private boolean setTotal = false;

	/** price incurred per minute */
	MovingAverage delta = new MovingAverage(10);

	/**
	 * Constructor for the campaign aggregation tracker.
	 * 
	 * @param id String. The campaign id.
	 */
	public AccountingCampaign(Campaign camp) {
		this.id = "" + camp.id;
		try {
			total_price =  camp.budget.totalCost.doubleValue();
			daily_price = camp.budget.dailyCost.doubleValue();
			hourly_price = camp.budget.hourlyCost.doubleValue();
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
	
	public AccountingCampaign(String id) {
		this.id = id;
	}

	/**
	 * Constructor for JSON
	 */
	public AccountingCampaign() {

	}

	/**
	 * Given the creative id and its type, return creative object.
	 * 
	 * @param id   String. The id of the creative.
	 * @param type String. The type eg 'banner'
	 * @return Creative. The object if found, or, null.
	 */
	public AccountingCreative getCreative(Campaign camp, String type) {
		for (AccountingCreative c : creatives) {
			if (c.adtype.equals(type) && c.id.equals(""+camp.id))
				return c;
		}
		return null;
	}

	public AccountingCreative getCreative(String composite) {
		String[] parts = composite.split(":");
		for (AccountingCreative c : creatives) {
			if (c.adtype.equals(parts[0]) && c.id.equals(parts[1]))
				return c;
		}
		return null;
	}

	/**
	 * Touch the campaign hourly and daily totals. If the budget is exceeded for one
	 * of these, then the aggregator will not report any wins (cuz the budget is
	 * exceeded), so the data will not appear and these values will be left over.
	 * They will never roll over to 0 and then will not bid when the bidget is now
	 * available. Note no need to do the total. because there will always be a total
	 * aggregation of the campaign ever won anything at all.
	 *
	 * Call this before you process the aggrheations.
	 */
	public void touch() {
		setHourly = false;
		setDaily = false;
		setTotal = false;
	}

	/**
	 * Return the hourly price.
	 * 
	 * @return double. The current running total.
	 */
	public double getHourly_price() {
		return hourly_price;
	}

	/**
	 * Return the daily price.
	 * 
	 * @return double. The current running total.
	 */
	public double getDaily_price() {
		return daily_price;
	}

	/**
	 * Return the total price.
	 * 
	 * @return double. The current running total.
	 */
	public double getTotal_price() {
		return total_price;
	}

	/**
	 * Set the hourly price.
	 * 
	 * @param d double. The value to set hourly to.
	 */
	public void setHourly_price(double d) {
		hourly_price = d;
		setHourly = true;
	}

	/**
	 * Set the daily price.
	 * 
	 * @param d double. The value to set daily to.
	 */
	public void setDaily_price(double d) {
		daily_price = d;
		setDaily = true;
	}

	/**
	 * Set the total price.
	 * 
	 * @param d double. The price to set totsal to.
	 */
	public void setTotal_price(double d) {
		total_price = d;
		setTotal = true;
	}

	/**
	 * Add a creative to the campaign.
	 * 
	 * @param c Creative. The creative to add.
	 */
	public void addCreative(AccountingCreative c) {
		creatives.add(c);
	}

}

/**
 * The internal class to track aggregations at the creative level. No need for
 * fancy syncrhonization, only 1 thread updates, all other threads simply read
 * it.
 * 
 * @author Ben M. Faul
 *
 */
class AccountingCreative {
	/** Ad type as in 'banner' or 'video' */
	volatile public String adtype;

	/** The creative id */
	public String id;

	/** The hourly cost since the last hour */
	volatile public double hourly_price;

	/** The daily cost, since midnight */
	volatile public double daily_price;

	/** The total price, if not patched, is total cost as of 5 mins ago */
	volatile public double total_price;

	/** Rate of spend over last minute */
	public MovingAverage delta = new MovingAverage(60);

	public AccountingCreative(String composite)  {
		String[] parts = composite.split(":");
		this.adtype = parts[0];
		this.id = parts[1];
	}
	
	public void setBudget(Campaign camp) {
		try {
			Creative cr = camp.getCreative(id,adtype);
			hourly_price = cr.budget.hourlyCost.doubleValue();
			daily_price = cr.budget.dailyCost.doubleValue();
			total_price = cr.budget.totalCost.doubleValue();
			} catch (Exception error) {
				error.printStackTrace();
			}
	}

	/**
	 * Constructor for JSON to use.
	 */
	public AccountingCreative() {

	}

	/**
	 * Patch the total by adding in the daily_price. Use this when aggregations of
	 * the total are at zero hour instead of the default (which is 5 minutes old
	 */
	public void patchTotals() {
		total_price += daily_price;
	}
}
