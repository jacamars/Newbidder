package com.jacamars.dsp.crosstalk.budget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.shared.AccountingCache;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Singleton class for controlling budgets using ELK.
 * 
 * @author Ben M. Faul
 *
 */
public enum BudgetController {

	// Instance variable
	INSTANCE;

	/** Banner type */
	public static final String BANNER = "banner";

	/** Video type */
	public static final String VIDEO = "video";

	/** Audio type */
	public static final String AUDIO = "audio";

	/** Native type */
	public static final String NATIVE = "native";

	/** The loop counter */
	public static volatile long revolution;

	/** The hourly aggregator */
	static Aggregator hourly;

	/** The daily aggregator */
	static Aggregator daily;

	/** The total aggregator */
	static Aggregator total;

	/** The last log object */
	static LastLogTracker lastLog;

	/** The prediction interface */
	static Predictions predictor = new Predictions();

	/** This class's logger */
	static final Logger logger = LoggerFactory.getLogger(BudgetController.class);
	
	/** If set, is the debug file that is read to simulate ELK */
	static String debugFile;

	
	public static BudgetController getInstance(AccountingCache ac) throws Exception {
		BudgetController.hourly = new Aggregator(Aggregator.HOURLY, ac); 
		BudgetController.daily = new Aggregator(Aggregator.DAILY, ac);
		BudgetController.total = new Aggregator(Aggregator.TOTAL, ac);

		ScheduledExecutorService execService = Executors.newScheduledThreadPool(1);
		
		return INSTANCE;
	}

	/**
	 * Get the instance.
	 * 
	 * @return BidgetController. The instance.
	 */
	public static BudgetController getInstance() {
		return INSTANCE;
	}
	
	public static void dumpCampaigns(String fileName) throws Exception {
		List<AccountingCampaign> campaigns = new ArrayList<AccountingCampaign>();
		for (int i=0;i<Aggregator.campaigns.size();i++) {
			AccountingCampaign c = Aggregator.campaigns.getNext(i);
			campaigns.add(c);	
		}
		String content = Aggregator.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(campaigns);
		Files.write(Paths.get(fileName), content.getBytes());
	}
	
	public static void debugProcess() throws Exception {
		String content = new String(Files.readAllBytes(Paths.get(debugFile)), StandardCharsets.UTF_8);
		List<AccountingCampaign> campaigns = Aggregator.mapper.readValue(content,
				Aggregator.mapper.getTypeFactory().constructCollectionType(List.class, AccountingCampaign.class));
		Aggregator.campaigns = new InternalHashMap();
		for (AccountingCampaign c : campaigns) {
			Aggregator.campaigns.put(c.id, c);
		}
		revolution++;
		logger.info("Budget Simulation Revolution: {}", revolution);
	}

	/**
	 * Process all the aggregations and log host
	 * 
	 * @throws Exception
	 *             on network errors.
	 */
	static void process() throws Exception {
		
		if (!RTBServer.isLeader())
			return;
		
		logger.debug("*** Budget Process: Getting Aggregate Values {}", revolution);

		Aggregator.touchAll();

		try {
			hourly.query();
			daily.query();
			total.query();
			if (lastLog != null) // can be null in debug
				lastLog.query();

			Aggregator.updateGlobal();

			predictor.setScale();
		} catch (Exception error) {
			StringWriter sw = new StringWriter();
			error.printStackTrace(new PrintWriter(sw));
			logger.error("Account watch is postponed because: {}", sw.toString());
		}

		revolution++;

		// dump("285");
		// dumpAllActive();
		logger.info("Latency: {}, revolution: {}", deltaTime(), revolution);
	}

	/**
	 * Print the results
	 */
	public void dump() {
		Aggregator.dump();
	}

	/**
	 * Print the results of a campaign.
	 * 
	 * @param who
	 *            String. The campaign to print.
	 */
	public void dump(String who) {
		Aggregator.dump(who);
	}

	/**
	 * Dump all campaigns whose hourly spend > 0
	 */
	public void dumpAllActive() {
		Aggregator.dumpAllActive();
	}

	/**
	 * Return the revolution number, runs once per minute.
	 * 
	 * @return long. The revolution number - how many times we have processed
	 *         since starting.
	 */
	public long getRevolution() {
		return revolution;
	}

	/**
	 * Return campaign total spend.
	 * 
	 * @param id
	 *            String. The campaign id.
	 * @return double. The campaign's total spend.
	 * @throws Exception
	 *             on Elastic Search errors.
	 */
	public double getCampaignTotalSpend(Campaign c) throws Exception {
		return getCampaignSpend(c, Aggregator.TOTAL);
	}

	/**
	 * Get the campaign spend, based on the mode, TOTAL, DAILY or HOURLY
	 * 
	 * @param id
	 *            String. The campaign id.
	 * @param mode
	 *            int mode. Which item to get, TOTAL, DAILY or HORLY
	 * @return double the campaign spend.
	 * @throws Exception
	 *             on access errors.
	 */
	double getCampaignSpend(Campaign cc, int mode) throws Exception {
		AccountingCampaign c;
		if (cc == null)
			c = Aggregator.global;
		else
			c = Aggregator.get(""+cc.id);
		if (c == null) {
			c = new AccountingCampaign(cc);
			Aggregator.add(c);
			return 0;
		}

		double d = get(c, mode);
		return d;
		// return predictor.getEffectiveSpend(d, deltaTime(),
		// c.delta.getAverage());
	}

	/**
	 * Get the daily spend for a campaign.
	 * 
	 * @param id
	 *            String. The campaign if.
	 * @return double. The current daily spend for the campaign./
	 * @throws Exception
	 *             on access errors.
	 */
	public double getCampaignDailySpend(Campaign c ) throws Exception {
		return getCampaignSpend(c, Aggregator.DAILY);
	}

	/**
	 * Get the campaign hourly spend..
	 * 
	 * @param id
	 *            String. The id of the campaign.
	 * @return double. The value of the spend.
	 * @throws Exception
	 *             on access errors.
	 */
	public double getCampaignHourlySpend(Campaign c) throws Exception {
		return getCampaignSpend(c, Aggregator.HOURLY);
	}

	/**
	 * Get the creative total spend.
	 * 
	 * @param camp
	 *            String. The campaign id.
	 * @param id
	 *            String. The creative id.
	 * @param type
	 *            String. The type, e.g. 'banner'.
	 * @return double. The total spend.
	 */
	public double getCreativeTotalSpend(Campaign camp, String id, String type) throws Exception {
		return getCreativeSpend(camp, id, type, Aggregator.TOTAL);
	}

	/**
	 * Get the spend, hourly or daily based on type.
	 * 
	 * @param camp
	 *            String. The id of the campaign.
	 * @param id
	 *            String. The id of the creative.
	 * @param type
	 *            String. The type of the creative, e.g. 'banner'
	 * @param mode
	 *            int. The mode, DAILY or HOURLY.
	 * @return double. The spend, hourly or daily depending on type.
	 * @throws Exception
	 *             on access errors.
	 */
	double getCreativeSpend(Campaign camp, String id, String type, int mode) throws Exception {
		AccountingCampaign c = Aggregator.get(""+camp.id);
		if (c == null)
			return 0;
		AccountingCreative cr = c.getCreative(id);
		if (cr == null) {
			cr = new AccountingCreative(id);
			cr.setBudget(camp);
			c.addCreative(cr);
			return 0;
		}

		return get(cr, mode);
		// return predictor.getEffectiveSpend(get(cr, mode), deltaTime(),
		// cr.delta.getAverage());
	}

	/**
	 * Get the standard deviation of the minute spend average on a campaign.
	 * 
	 * @param camp
	 *            String. The campaig id.
	 * @return double. The standard deviation.
	 */
	public double getStdDeviationMinuteSpend(String camp) {
		AccountingCampaign c;
		if (camp == null)
			c = Aggregator.global;
		else
			c = Aggregator.get(camp);
		if (c == null)
			return Double.NaN;

		return c.delta.getStd();
	}

	/**
	 * Get the standard deviation of the campaign/creative/type average spend.
	 * 
	 * @param camp
	 *            String. The campaign id.
	 * @param creat
	 *            String. The creative id.
	 * @param type
	 *            String the creative type, e.g. 'banner'
	 * @return double. The standard deviation.
	 */
	public double getStdDeviationMinuteSpend(String camp, String creat, String type) {
		AccountingCampaign c = Aggregator.get(camp);
		if (c == null)
			return Double.NaN;

		AccountingCreative cr = c.getCreative(creat);
		if (cr == null)
			return Double.NaN;

		return cr.delta.getStd();
	}

	/**
	 * Get the creative daily spend based on campaign, creative and creative type.
	 * @param camp String. The campaign id.
	 * @param id String. The creative id.
	 * @param type String. The type of the creative, e.g. 'banner'
	 * @return double. The creative's daily spend.
	 * @throws Exception on access errors.
	 */
	public double getCreativeDailySpend(Campaign camp, String id, String type) throws Exception {
		return getCreativeSpend(camp, id, type, Aggregator.DAILY);
	}

	/**
	 * Get the creative hourly spend based on campaign, creative and type.
	 * @param camp String. The campaign id.
	 * @param id String. The creative id.
	 * @param type String. The creative type, e.g., 'banner'
	 * @return double. The creative's hourly spend.
	 * @throws Exception on access errors.
	 */
	public double getCreativeHourlySpend(Campaign camp, String id, String type) throws Exception {
		return getCreativeSpend(camp, id, type, Aggregator.HOURLY);

	}

	/**
	 * Low level get for campaign.
	 * @param c Campaign. The campaign object.
	 * @param mode int. The mode, what to get: TOTAL, DAILY, HOURLY.
	 * @return double. The value in the campaign object based on type.
	 * @throws Exception on access errors.
	 */
	double get(AccountingCampaign c, int mode) throws Exception {
		switch (mode) {
		case Aggregator.TOTAL:
			return c.getTotal_price();
		case Aggregator.DAILY:
			return c.getDaily_price();
		case Aggregator.HOURLY:
			return c.getHourly_price();
		}
		throw new Exception("Unknown mode " + mode);
	}

	/**
	 * Low level get for a creative.
	 * @param c Creative. The creative object.
	 * @param mode int. The mode of what to get: TOTAL, DAILY, or HOURLY.
	 * @return double. The returned value based on mode.
	 * @throws Exception on access errors.
	 */
	double get(AccountingCreative c, int mode) throws Exception {
		switch (mode) {
		case Aggregator.TOTAL:
			return c.total_price;
		case Aggregator.DAILY:
			return c.daily_price;
		case Aggregator.HOURLY:
			return c.hourly_price;
		}
		throw new Exception("Unknown mode " + mode);
	}

	/**
	 * Get a campaign's minute spend on average.
	 * @param id String. The campaign id.
	 * @return double. The average minute spend on this campaign.
	 * @throws Exception on JSON parsing errors.
	 */
	public double getCampaignSpendAverage(String id) throws Exception {
		AccountingCampaign c;
		if (id == null) 
			c = Aggregator.global;
		else
			c = Aggregator.get(id);
		if (c == null)
			throw new Exception("No such campaign " + id);
		return c.delta.getAverage();
	}

	/**
	 * Return the lag time in MINUTES.
	 * 
	 * @return long. The time in minutes the log is behind.
	 */
	public static long deltaTime() {
		if (lastLog == null)    // this is null in debug mode
			return 0;
		
		return lastLog.deltaTime() / 1000;
	}

	public double getCreativeSpendAverage(String camp, String id, String type) throws Exception {
		AccountingCampaign c = Aggregator.get(camp);
		if (c == null)
			throw new Exception("No such campaign " + camp);
		AccountingCreative cr = c.getCreative(id);
		if (cr == null)
			throw new Exception("No such creative " + id);
		return cr.delta.getAverage();
	}

	/**
	 * Determine if the total budget was exceeded. This is different than hourly and daily budget checks, If the total budget
	 * is exceeded the campaign can never restart, and should be removed from the system to save resources.
	 * @param id String. The campaign id to check.
	 * @param total_budget AtomicBigDecimal. This is the total budget, retrieved from Postgres.
	 * @return boolean. Returns true if the total spend is >= total budget.
	 * @throws Exception on errors accessing ELK.
	 */
	public boolean checkCampaignTotalBudgetExceeded(Campaign c, AtomicBigDecimal total_budget) throws Exception {
		if (total != null) {
			double budget = total_budget.getDoubleValue();
			double spend = getCampaignTotalSpend(c);

			logger.debug("TOTAL {} budget: {} vs spend: {}", c.id, budget, spend);
			if (spend >= budget) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Check a campaign to see if it overspent.
	 * 
	 * @param id
	 *            String. The campaign id.
	 * @param total
	 *            AtomicBigDecimal. The total budget.
	 * @return Boolean. Returns true if overspent or ELK error. Otherwise false.
	 */

	public boolean checkCampaignBudgetsTotal(Campaign c, AtomicBigDecimal total) {
		double spend;
		double budget;
		try {
			if (total != null) {
				budget = total.getDoubleValue();
				spend = getCampaignTotalSpend(c);

				logger.debug("TOTAL {} budget: {} vs spend: {}", c.id, budget, spend);
				if (spend >= budget) {
					return true;
				}
			}
		} catch (Exception error) {
			error.printStackTrace();
			return true;
		}
		return false;
	}

	public boolean checkCampaignBudgetsDaily(Campaign c, AtomicBigDecimal daily) {
		double spend;
		double budget;

		try {
			if (daily != null) {
				budget = daily.getDoubleValue();
				spend = getCampaignDailySpend(c);
				logger.debug("DAILY {} budget: {} vs spend: {}", c.id, budget, spend);
				if (spend >= budget) {
					return true;
				}
			}
		} catch (Exception error) {
			error.printStackTrace();
			return true;
		}

		return false;
	}

	public boolean checkCampaignBudgetsHourly(Campaign c, AtomicBigDecimal hourly) {
		double spend;
		double budget;

		try {
			if (hourly != null) {
				budget = hourly.getDoubleValue();
				spend = getCampaignHourlySpend(c);
				logger.debug("HOURLY {} budget: {} vs spend: {}", c.id, budget, spend);
				if (spend >= budget) {
					return true;
				}
			}
		} catch (Exception error) {
			error.printStackTrace();
			return true;
		}

		return false;
	}

	public boolean checkCampaignBudgets(Campaign c, AtomicBigDecimal total, AtomicBigDecimal daily,
			AtomicBigDecimal hourly) {
		double spend;
		double budget;
		try {
			
			if (total != null) {
				budget = total.getDoubleValue();
				spend = getCampaignTotalSpend(c);

				logger.debug("TOTAL {} budget: {} vs spend: {}", c.id, budget, spend);
				if (spend >= budget) {
					return true;
				}
			}

			if (daily != null) {
				budget = daily.getDoubleValue();
				spend = getCampaignDailySpend(c);			
				logger.debug("DAILY {} budget: {} vs spend: {}", c.id, budget, spend);
				if (spend >= budget) {
					return true;
				}
			}

			if (hourly != null) {
				budget = hourly.getDoubleValue();
				spend = getCampaignHourlySpend(c);
				logger.debug("HOURLY {} budget: {} vs {} spend: {}", c.id, budget, spend);
				if (spend >= budget) {
					return true;
				}
			}

		} catch (Exception error) {
			error.printStackTrace();
			return true;
		}

		return false;
	}

	/**
	 * Check a campaign/creative to see if it overspent.
	 * 
	 * @param id
	 *            String. The campaign id.
	 * @param crid
	 *            String. The creative id.
	 * @param type
	 *            String. The type of the creative, e.g. 'banner'
	 * @param total
	 *            AtomicBigDecimal. The total budget.
	 * @param daily
	 *            AtomicBigDecimal The daily budget.
	 * @param hourly
	 *            AtonicBigDecimal. The hourly budget.
	 * @return Boolean. Returns true if overspent or ELK error. Otherwise false.
	 */

	public boolean checkCreativeBudgets(Campaign c, String crid, String type, AtomicBigDecimal total,
			AtomicBigDecimal daily, AtomicBigDecimal hourly) {
		double spend;
		double budget;
		try {

			if (total != null && total.doubleValue() != 0) {
				budget = total.getDoubleValue();
				spend = getCreativeTotalSpend(c, crid, type);
				logger.debug("Creative check -------> ID: {}. TOTAL, {}/{} budget: {} vs spend: {}", c.id, crid, type, budget, spend);
				if (spend != 0 && spend >= budget)
					return true;
			}

			if (daily != null && daily.doubleValue() != 0) {
				budget = daily.getDoubleValue();
				spend = getCreativeDailySpend(c, crid, type);
				logger.debug("Creative check -------> ID: {}. DAILY {}/{} budget: {} vs spend: {}", c.id, crid, type, budget,
						spend);
				if (spend >= budget)
					return true;
			}

			if (hourly != null && hourly.doubleValue() != 0) {
				budget = hourly.getDoubleValue();
				spend = getCreativeHourlySpend(c, crid, type);
				logger.debug("Creative check ------->ID: {}. HOURLY {}/{}, budet: {} vs spend: {}", c.id, crid, type, budget,
						spend);
				if (spend >= budget)
					return true;
			}

		} catch (Exception error) {
			error.printStackTrace();
			return true;
		}

		return false;
	}
}
