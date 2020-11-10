package com.jacamars.dsp.crosstalk.budget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.hazelcast.map.IMap;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Preshuffle;
import com.jacamars.dsp.rtb.shared.CampaignCache;

public class Shadow {

	private volatile IMap<String, Campaign> campaigns;
	private volatile Map<String, Campaign> scampaigns = new ConcurrentHashMap<>();
	Object lock = new Object();

	public Shadow() {
		campaigns = RTBServer.getSharedInstance().getMap(Crosstalk.CAMPAIGNS_KEY);
	}

	public void runUsingElk() {
		scampaigns.entrySet().forEach(e -> {
			e.getValue().runUsingElk();
		});
	}

	public void delete(String key) {
		synchronized (lock) {
			campaigns.remove(key);
			scampaigns.remove(key);
		}
		Preshuffle.getInstance().compile();
	}

	public Campaign get(String key) {
		return scampaigns.get(key);
	}

	public void add(Campaign camp) {
		try {
			camp.encodeCreatives();       // this is a HACK
		} catch (Exception err) {
			err.printStackTrace();
		}
		
		synchronized (lock) {
			campaigns.put("" + camp.id, camp);
			scampaigns.put("" + camp.id, camp);
		}
	}

	public Set<Entry<String, Campaign>> entrySet() {
		return scampaigns.entrySet();
	}
	
	public Set<Entry<String, Campaign>> entrySet(String customer_id) {
		if (customer_id.equals("rtb4free"))
			return entrySet();
		
		Set<Entry<String,Campaign>> entries = new HashSet<>();

		scampaigns.entrySet().forEach(entry->{
			Campaign c = entry.getValue();
			System.out.println(c.toJson());
			if (entry.getValue() != null  && entry.getValue().customer_id.equals(customer_id))
				entries.add(entry);
		});
		return entries;
	}

	public int size() {
		return scampaigns.size();
	}

	public void remove(Campaign camp) {
		delete("" + camp.id);
	}
	
	public void clear() {
		scampaigns.clear();
		campaigns.clear();
		Preshuffle.getInstance().compile();
	}
	
	public List<Campaign> getCampaigns() {
		List<Campaign> list = new ArrayList<>();
		scampaigns.forEach((k,v)->{
			list.add(v);
		});
		return list;
	}

	public void refresh() {
		synchronized (lock) {
			scampaigns.clear();
			campaigns.entrySet().forEach(entry -> {
				scampaigns.put(entry.getKey(), entry.getValue());
			});
		}
	}
}
