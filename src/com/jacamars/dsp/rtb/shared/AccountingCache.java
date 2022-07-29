package com.jacamars.dsp.rtb.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.CPSubsystem;
import com.hazelcast.cp.IAtomicReference;
import com.jacamars.dsp.crosstalk.budget.AtomicBigDecimal;
import com.jacamars.dsp.rtb.commands.Echo;
import com.jacamars.dsp.rtb.common.Campaign;


public enum AccountingCache {

	INSTANCE;
	HazelcastInstance hz = null;
	Map<String,AccountRecord> map = new ConcurrentHashMap();
	
	public static AccountingCache getInstance() {
		return INSTANCE;
	}
	
	public static AccountingCache getInstance(HazelcastInstance hz) {
		INSTANCE.hz = hz;
		return INSTANCE;
	}
	
	public Map<String,Double> get(String campaign) {
		Map<String,Double> v = new HashMap<>();
		var ac = map.get(campaign);
		if (ac == null)
			return v;
		return ac.asMap();
	}

	/**
	 * Increment values, note value needs to be divided by 1000 at this level. Type is encoded 
	 * in the creative, like banner:1. which identifies banner table, index 1.
	 * @param campaign String. The string represention of the campaign id.
	 * @param creative. String. The encoded representation of type:id of the cretiv.
	 * @param value String. The string representation of the price in 1000s.
	 */
	public void increment(String campaign, String creative, String value) {
		if (hz == null)
			return;
		
		AccountRecord ac = map.get(campaign);
		if (ac == null) {
			ac = new AccountRecord(hz.getCPSubsystem(),campaign);
			map.put(campaign, ac);
		}
		ac.add(creative, Double.valueOf(value));
	}
	
	public void increment(String campaign, String creative, String type,  Double value) {
		if (hz == null)
			return;
		AccountRecord ac = map.get(campaign);
		if (ac == null) {
			ac = new AccountRecord(hz.getCPSubsystem(),campaign);
			map.put(campaign, ac);
		}
		ac.add(creative, value);
	}
	
	public void resetBudget(String campaign) {
		if (hz == null)
			return;
		AccountRecord ac = map.get(campaign);
		if (ac == null)
			return;
		
		ac.hardReset();
		map.put(campaign, ac);
	}
	
	/**
	 * For incrementing .bids, .wins, .pixels, .clicks, .postback
	 * @param campaign
	 * @param key
	 */
	public void incrementEvent(String campaign, String key) {
		if (hz == null)
			return;
		if (campaign == null || key == null)
			return;
		
		AccountRecord ac = map.get(campaign);
		if (ac == null) {
			ac = new AccountRecord(hz.getCPSubsystem(),campaign);
			map.put(campaign, ac);
		}
		ac.add(campaign+key, 1.0);
	}
	
	public void reset(String campaign, String creative, String type) {
		AccountRecord ac = map.get(campaign);
		if (ac == null)
			return;
		ac.reset(creative);
	}
	
	public void reset(String campaign) {
		AccountRecord ac = map.get(campaign);
		if (ac == null)
			return;
		ac.reset();
	}
	
	public void reset() {
		map.forEach((k,r)->{
			r.reset();
		});
	}
	
	public Double get(String campaign, String creative) {
		AccountRecord ac = map.get(campaign);
		if (ac == null)
			return 0.0;
		return ac.get(creative)/1000;
	}
	
	public void delete(String campaign, String creative) {
		AccountRecord ar = map.get(campaign);
		if (ar == null)
			return;
		ar.delete(creative);
	}
	
	public void delete(String campaign) {
		AccountRecord ar = map.get(campaign);
		if (ar == null)
			return;
		map.remove(campaign);
	}
	
	public double getCampaignTotal(String campaign) {
		AccountRecord record = map.get(campaign);
		if (record == null)
			return 0.0;
		
		double d = record.get();
		return d/1000;
	}
	
	public Map<String,Map<String,Double>> asMapAndReset() {
		Map<String,Map<String,Double>> m = new HashMap<>();
		map.forEach((k,v)->{
			m.put(k, v.asMapAndReset());
		});
		return m;
	}
	
	public Map<String,Map<String,Double>> asMap() {
		Map<String,Map<String,Double>> m = new HashMap<>();
		map.forEach((k,v)->{
			m.put(k, v.asMap());
		});
		return m;
	}


	
	public static void main(String [] args) throws Exception {
		Config config = new Config();
		HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);
		AccountingCache ac = AccountingCache.getInstance(hz);
		
		ac.increment("aaa","1","banner",Double.valueOf(1));
		ac.increment("aaa","2","banner",Double.valueOf(1));
		ac.increment("aaa","1","banner",Double.valueOf(1));
		
		System.out.println("A = " + ac.asMapAndReset());
		System.out.println("B = " + ac.asMapAndReset());
	}
	
}

class AccountRecord {
	CPSubsystem cp;
	String campaign;
	IAtomicReference<Double> counter;      // total cost
	Map<String,IAtomicReference<Double>> counters = new ConcurrentHashMap();
	
	public AccountRecord(CPSubsystem cp, String campaign) {
		this.cp = cp;
		this.campaign = campaign;
		counter = cp.getAtomicReference(campaign);
	}
	
	public Map<String, Double> asMap() {
		Map<String,Double> m = new HashMap<>();
		m.put(".total", counter.get());
		counters.forEach((k,v)->{
			m.put(k,v.get());
		});
		return m;
	}

	public String getCampaign() {
		return campaign;
	}
	
	public  void add(String creative, Double value) {
		IAtomicReference<Double> ref = counters.get(creative);
		if (ref == null) {
			ref = cp.getAtomicReference(campaign+creative);
			counters.put(creative, ref);
		}
		var v = new IncFunction(value);
		ref.alter(v);
		counter.alter(v);
	}
	
	/**
	 * Used to implement ".bids", ".wins", ".pixels", "clicks"
	 * @param key
	 */
	public void increment(String key) {
		IAtomicReference<Double> ref = counters.get(campaign+key);
		if (ref == null) {
			ref = cp.getAtomicReference(campaign+key);
			counters.put(campaign+key, ref);
		}
		var v = new IncFunction(1.0);
		ref.alter(v);
	}
	
	public Double get() {
		return counter.get();
	}
	
	public Double get(String creative) {
		IAtomicReference<Double> ar = counters.get(creative);
		if (ar == null)
			return Double.valueOf(0);
		return ar.get();
	}
	
	public void delete(String creative) {
		IAtomicReference<Double> ar = counters.get(creative);
		if (ar == null)
			return;
		counters.remove(creative);
	}
	
	public void reset() {
		Double value = Double.valueOf(0);
		counters.forEach((k,v)->{
			if (!k.contains("."))
				v.set(Double.valueOf(0));
		});
		counter.set(Double.valueOf(0));
	}
	
	public void hardReset() {
		Double value = Double.valueOf(0);
		counters.forEach((k,v)->{
			v.set(Double.valueOf(0));
		});
		counter.set(Double.valueOf(0));
	}
	
	public void reset(String creative) {
		IAtomicReference ar = counters.get(creative);
		if (ar == null)
			return;
		ar.set(Double.valueOf(0));
	}
	
	public Map<String,Double> asMapAndReset() {
		Map<String,Double> m = new HashMap<>();
		m.put(".total", counter.get());
		counter.set(Double.valueOf(0));
		counters.forEach((k,v)->{
			m.put(k,v.get());
			if (!k.contains("."))
				v.set(Double.valueOf(0));
		});
		return m;
	}
	
}
