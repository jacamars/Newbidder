package com.jacamars.dsp.rtb.shared;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.jacamars.dsp.rtb.bidder.RTBServer;

import java.util.concurrent.TimeUnit;

/**
 * A class to replace the self expiring hash set. Use cache2k so we don't have to support more home brew code.
 * Created by Ben M. Faul on 1/1/18.
 */
public enum FreqSetCache  {

    INSTANCE;
	
	private static ObjectMapper mapper = new ObjectMapper();

    /** The cache to contain the frequencu cap. We use IMap instead of ISet, as ISet does not support TTL */
    static private volatile IMap<String, String> cache;

    /** The default expiration time if you add a key */
    static public long expire = 900;

    /** The default backup count if you dont set it */
    static public int backupCount = 3;

    /** The default value to read backups, is true */
    static public boolean readBackup = true;

    public static final String NAME = "FREQUENCY";

    public static FreqSetCache getInstance() {
        if (cache != null)
            return INSTANCE;

        Config config = RTBServer.getSharedInstance().getConfig();
        cache =  RTBServer.getSharedInstance().getMap(NAME);
        config.getMapConfig(NAME)
                .setAsyncBackupCount(backupCount)
                .setReadBackupData(readBackup);
        return INSTANCE;
    }
    
    public static FreqSetCache getClientInstance(HazelcastInstance inst) {
        if (cache != null)
            return INSTANCE;

        Config config = inst.getConfig();
        cache =  RTBServer.getSharedInstance().getMap(NAME);
        return INSTANCE;
    }
    
    public String get(String id) {
    	return cache.get(id);
    }

    /**
     * Add a key, using default timeout. Warning: overrides the expiration and resets to new value/
     * @param id String. The id to set.
     */
    public void add(String id) {
        cache.set(id,id, expire, TimeUnit.MILLISECONDS);
    }

    /**
     * Add a key with a custom timeout.
     * @param id String id.
     * @param timeout long. The time to expire.
     */
    public void add(String id, long timeout) {
        cache.set(id,id, timeout, TimeUnit.MILLISECONDS);

    }

    /**
     * Does this cache contain this key?
     * @param id String. The id to search for.
     * @return boolean. Returns true of the id exists.
     */
    public boolean contains(String id) {
        return cache.containsKey(id);
    }
    
    /**
     * Remove a frequency key
     * @param id String. The key to remove.
     */
    public void remove(String id) {
    	cache.remove(id);
    }

    /**
     * Clear the cache.
     */
    public void clear() {
        cache.clear();
    }

}

