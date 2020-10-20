package com.jacamars.dsp.rtb.tools;

import java.util.HashMap;

import java.util.Map;

import com.hazelcast.map.IMap;
import com.jacamars.dsp.rtb.bidder.RTBServer;

public enum MemoryAccounting {
	
	INSTANCE;

	public static final String MAPNAME = "CRUDEACCOUNTING";
	static final Map<String,IncrementCounterDistributedTask<String>> map = new HashMap<>();
	
	public static MemoryAccounting getInstance() {
		return INSTANCE;
	}
	
	IncrementCounterDistributedTask<String> resolve(String name) {
		IncrementCounterDistributedTask<String> tasker = map.get(name);
		if (tasker == null) {
			tasker = new IncrementCounterDistributedTask<>(name,MAPNAME);
			map.put(name, tasker);
		}
		return tasker;
	}
	
	public boolean exists(String key) {
		IncrementCounterDistributedTask<String> tasker = map.get(key);
		if (tasker == null) {
			return false;
		}
		return true;
	}
	
	public long getValue(String key) throws Exception {
		IncrementCounterDistributedTask<String> tasker = map.get(key);
		if (tasker == null)
			throw new RuntimeException("No such key: " + key);
		return tasker.getCount();
	}
	
	public Map<String,Long> getValues() {
		final var values = new HashMap<String,Long>();
		IMap<String, Long> hmap = RTBServer.getSharedInstance().getMap(MAPNAME);
		hmap.forEach((k,v)->{values.put(k, v);});
		return values;
	}
	
	public void increment(String key,long amount) {
		var tasker = resolve(key);
		tasker.increment(amount);
	}
	
	public void increment(String key) {
		var tasker = resolve(key);
		tasker.increment(1L);
	}
	
	public void remove(String key) {
		IMap<String, Long> hmap = RTBServer.getSharedInstance().getMap(MAPNAME);
		map.remove(key);
		hmap.remove(key);
	}
}
