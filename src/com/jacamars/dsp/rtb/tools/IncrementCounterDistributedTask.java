package com.jacamars.dsp.rtb.tools;

import java.io.Serializable;

import com.hazelcast.core.IMap;
import com.hazelcast.core.PartitionAware;
import com.jacamars.dsp.rtb.bidder.RTBServer;

public final class IncrementCounterDistributedTask<K> implements Runnable, PartitionAware, Serializable {

	private static final long serialVersionUID = 1L;
	private final K key;
    private final String mapName;

    public IncrementCounterDistributedTask(K key, String mapName) {
        this.key = key;
        this.mapName = mapName;
    }

    public int getCount() {
    	  IMap<K, Integer> map = RTBServer.getSharedInstance().getMap(mapName);
          Integer counter = map.get(key);
          return counter;
    }
    
    @Override
    public Object getPartitionKey() {
        return key;
    }

    @Override
    public void run() {
        IMap<K, Integer> map = RTBServer.getSharedInstance().getMap(mapName);
        map.lock(key);
        Integer counter = map.get(key);
        if(counter == null) {
            map.put(key, 1);
        } else {
            map.put(key, ++counter);
        }
        map.unlock(key);
    }
}