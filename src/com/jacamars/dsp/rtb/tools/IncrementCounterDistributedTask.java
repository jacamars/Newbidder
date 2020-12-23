package com.jacamars.dsp.rtb.tools;

import java.io.Serializable;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import com.hazelcast.map.IMap;
import com.hazelcast.partition.PartitionAware;
import com.jacamars.dsp.rtb.bidder.RTBServer;

public final class IncrementCounterDistributedTask<K> implements Runnable, PartitionAware, Serializable {

	private static final long serialVersionUID = 1L;
	static ExecutorService service = Executors.newFixedThreadPool(256);
	ConcurrentLinkedQueue<Long> queue = new ConcurrentLinkedQueue<>();
	private final K key;
    private final String mapName;

    public IncrementCounterDistributedTask(K key, String mapName) {
        this.key = key;
        this.mapName = mapName;
    }
    
    public void increment(final long value) {
        try {
        	queue.add(value);
        	service.execute(this);
        } catch (RejectedExecutionException ignored) {
        	
        }
    }

    public long getCount() {
    	  IMap<K, Long> map = RTBServer.getSharedInstance().getMap(mapName);
          Long counter = map.get(key);
          return counter;
    }
    
    @Override
    public Object getPartitionKey() {
        return key;
    }

    @Override
    public void run() {
    	Long value = queue.poll();
    	if (value == null)
    		return;
    	
        IMap<K, Long> map = RTBServer.getSharedInstance().getMap(mapName);
        map.lock(key);
        Long counter = map.get(key);
        if(counter == null) {
            map.put(key, value);
        } else {
            map.put(key, counter + value);
        }
        map.unlock(key);
    }
}