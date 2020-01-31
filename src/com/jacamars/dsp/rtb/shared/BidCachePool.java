package com.jacamars.dsp.rtb.shared;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.commands.Echo;

import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.RecordedBid;
import com.jacamars.dsp.rtb.common.RecordedVideo;

public enum BidCachePool {

	INSTANCE;
	
	public static final String BIDCACHE = "BIDCACHE";
	public static final String VIDEO = "VIDEO";
	public static final String MISC = "MISC";

	private static final Logger logger = LoggerFactory.getLogger(BidCachePool.class);
	private static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	static Map<String,Map<String,List<WatchInterface>>> watchMap = new HashMap();
	static {
		watchMap.put(MISC, new HashMap());
		watchMap.put(VIDEO, new HashMap());
		watchMap.put(BIDCACHE,  new HashMap());
	}

	/** The cache to contain the general context */
	static private volatile IMap<String, RecordedBid> bidCache;
	/** The cache that contains the membership records of all the bidders */
	static private volatile IMap<String, Echo> memberCache;
	/** The video cache of VAST tags */
	static private volatile IMap<String, RecordedVideo> videoCache;
	/** The cache of miscellaneous stuff that needs to be shared */
	static private volatile IMap<String, RecordedMisc> miscCache;

	/** The default backup count if you dont set it */
	static public int backupCount = 3;

	/** The default value to read backups, is true */
	static public boolean readBackup = true;

	static public String mapstoreJDBC = null;

	public static BidCachePool getInstance(HazelcastInstance inst) {
		if (bidCache == null) {
			MapStoreConfig mapStoreCfg;

			Config config = inst.getConfig();
			
			String name = "BIDCACHE";
			bidCache = RTBServer.getSharedInstance().getMap(name);
			config.getMapConfig(name).setAsyncBackupCount(backupCount).setReadBackupData(readBackup);
			bidCache.addEntryListener(new EntryEvictedListener<String, RecordedBid>() {
				@Override
				public void entryEvicted(EntryEvent<String, RecordedBid> event) {
					if (RTBServer.isLeader() && Configuration.getInstance().mapstoredriver != null) {
						BidCacheStore.evict(event.getKey());
					}
					handleWatch(BIDCACHE, event.getKey());
				}
			}, true);

			///////////////////////////////////////////////////////////////////////////////////////////
			if (RTBServer.isLeader() && Configuration.getInstance().mapstoredriver != null) {
				int k = BidCacheStore.initialize(bidCache);
				logger.info("Bid cache initialized with {} entries", k);
			} else
				logger.info("Bid cache skipped initialization");

			if (Configuration.getInstance().mapstoredriver != null) {
				mapStoreCfg = new MapStoreConfig();
				mapStoreCfg.setClassName(BidCacheStore.class.getName()).setEnabled(true);
				config.getMapConfig(name).setMapStoreConfig(mapStoreCfg);
			}
			//////////////////////////////////////////////////
			
			name = "MEMBER";
			memberCache = RTBServer.getSharedInstance().getMap(name);
			config.getMapConfig(name).setAsyncBackupCount(backupCount).setReadBackupData(readBackup);
		

			name = "VIDEO";
			videoCache = RTBServer.getSharedInstance().getMap(name);
			config.getMapConfig(name).setAsyncBackupCount(backupCount).setReadBackupData(readBackup);
			videoCache.addEntryListener(new EntryEvictedListener<String, RecordedVideo>() {
				@Override
				public void entryEvicted(EntryEvent<String, RecordedVideo> event) {
					if (RTBServer.isLeader() && Configuration.getInstance().mapstoredriver != null) {
						VideoCacheStore.evict(event.getKey());
					}
					handleWatch(VIDEO, event.getKey());
				}
			}, true);

			if (RTBServer.isLeader() && Configuration.getInstance().mapstoredriver != null) {
				int k = VideoCacheStore.initialize(videoCache);
				logger.info("Video cache initialized with {} entries", k);
			} else
				logger.info("Video cache skipped initialization");

			if (Configuration.getInstance().mapstoredriver != null) {
				mapStoreCfg = new MapStoreConfig();
				mapStoreCfg.setClassName(VideoCacheStore.class.getName()).setEnabled(true);
				config.getMapConfig(name).setMapStoreConfig(mapStoreCfg);
			}

			name = "MISC";
			miscCache = RTBServer.getSharedInstance().getMap(name);
			config.getMapConfig(name).setAsyncBackupCount(backupCount).setReadBackupData(readBackup);
			miscCache.addEntryListener(new EntryEvictedListener<String, RecordedMisc>() {
				@Override
				public void entryEvicted(EntryEvent<String, RecordedMisc> event) {
					if (RTBServer.isLeader() && Configuration.getInstance().mapstoredriver != null) {
						MiscCacheStore.evict(event.getKey());
					}
					handleWatch(MISC,event.getKey());
				}
			}, true);

			if (RTBServer.isLeader() && Configuration.getInstance().mapstoredriver != null) {
				int k = MiscCacheStore.initialize(miscCache);
				logger.info("Misc cache initialized with {} entries", k);
			} else
				logger.info("Misc cache skipped initialization");

			if (Configuration.getInstance().mapstoredriver != null) {
				mapStoreCfg = new MapStoreConfig();
				mapStoreCfg.setClassName(MiscCacheStore.class.getName()).setEnabled(true);
				config.getMapConfig(name).setMapStoreConfig(mapStoreCfg);
			}
		}
		return INSTANCE;
	}
	
	static void handleWatch(String category, String key) {
		System.out.println("**** EVICTED: " + category + "/" + key );
		List<WatchInterface> ifcs = watchMap.get(category).get(key);
		if (ifcs != null)
			ifcs.stream().forEach(ifc -> ifc.callback(category, key));
	}

	public static BidCachePool getClientInstance(HazelcastInstance inst) {
		if (bidCache == null) {
			Config config = inst.getConfig();
		//	config.getSerializationConfig()
		//		.addPortableFactory(SamplePortableFactory.FACTORY_ID, new SamplePortableFactory())
		//		.addPortableFactory(PortableEchoFactory.FACTORY_ID, new PortableEchoFactory());

			String name = "BIDCACHE";
			bidCache = inst.getMap(name);

			name = "MEMBER";
			memberCache = inst.getMap(name);

			name = "VIDEO";
			videoCache = inst.getMap(name);

			name = "MISC";
			miscCache = inst.getMap(name);
			
			bidCache.addEntryListener(new EntryEvictedListener<String, RecordedBid>() {
				@Override
				public void entryEvicted(EntryEvent<String, RecordedBid> event) {
					if (RTBServer.isLeader() && Configuration.getInstance().mapstoredriver != null) {
						BidCacheStore.evict(event.getKey());
					}
					handleWatch(BIDCACHE, event.getKey());
				}
			}, true);
			miscCache.addEntryListener(new EntryEvictedListener<String, RecordedMisc>() {
				@Override
				public void entryEvicted(EntryEvent<String, RecordedMisc> event) {
					if (RTBServer.isLeader() && Configuration.getInstance().mapstoredriver != null) {
						MiscCacheStore.evict(event.getKey());
					}
					handleWatch(MISC, event.getKey());
				}
			}, true);
			
			videoCache.addEntryListener(new EntryEvictedListener<String, RecordedVideo>() {
				@Override
				public void entryEvicted(EntryEvent<String, RecordedVideo> event) {
					if (RTBServer.isLeader() && Configuration.getInstance().mapstoredriver != null) {
						VideoCacheStore.evict(event.getKey());
					}
					handleWatch(VIDEO, event.getKey());
				}
			}, true);
		}
		return INSTANCE;
	}
	
	public static BidCachePool getInstance() {
		return INSTANCE;
	}
	
	/**
	 * A hook for watching evictions
	 * @param category String. Which shared interface.
	 * @param key String. The key to watch
	 * @param ifc WatchInterface. The implementation to catch on the watch.
	 * @throws Exception if the 'which' defined map does not exist.
	 */
	public void addWatch(String category, String key, WatchInterface ifc) throws Exception {
		var map = BidCachePool.watchMap.get(category.toUpperCase());
		var list = map.get(key);
		if (list == null) {
			list = new ArrayList();
			map.put(key, list);
		}
		if (list.contains(ifc))
			return;
		list.add(ifc);
	}
	
	/**
	 * Delete all eviction watchers at location key.
	 * @param category String. Which shared map to remove from.
	 * @param key String. The key to remove.
	 * @throws Exception if the 'which' defined map does not exist.
	 */
	public void deleteWatch(String category, String key) throws Exception {
		BidCachePool.watchMap.get(category.toUpperCase()).remove(key);
	}
	
	/**
	 * Delete a single watcher on a key
	 * @param which String. The category of what is being watched.
	 * @param key String. The key being watched.
	 * @param ifc WatchInterface. The callback location.
	 * @throws Exception if the category is not known/
	 */
	public void deleteWatch(String category, String key, WatchInterface ifc) throws Exception{
		var list = BidCachePool.watchMap.get(category.toUpperCase()).get(key);
		if (list == null)
			return;
		list.remove(ifc);
	}

	public void setMemberStatus(String key, Echo member) {
		memberCache.set(key, member, 60, TimeUnit.SECONDS);
	}

	public Echo getMemberStatus(String key) {
		Echo oj = memberCache.get(key);
		if (oj == null)
			return null;
		return mapper.convertValue(oj, Echo.class);
	}

	public int getMembersSize() {
		return memberCache.size();
	}

	public List<String> getMembersNames() {
		List<String> members = new ArrayList();
		memberCache.keySet().forEach(e -> {
			members.add(e);
		});
		return members;
	}

	public void recordBid(RecordedBid br, long timeout) {
		bidCache.setAsync(br.getId(), br, timeout, TimeUnit.SECONDS);
	}

	public RecordedBid getBid(String key) {
		RecordedBid rb = bidCache.get(key);
		return rb;
	}

	public void deleteBid(String key) {
		bidCache.remove(key);
	}

	public RecordedBid getAndDeleteBid(String key) {
		RecordedBid rb = getBid(key);
		if (rb == null)
			return null;
		bidCache.remove(key);
		return rb;
	}

	public void setVideo(String key, String vast, long timeout) {
		RecordedVideo rv = new RecordedVideo(key, vast, System.currentTimeMillis() + timeout * 1000);
		videoCache.setAsync(key, rv);
	}

	public String getVideo(String key) {
		RecordedVideo oj = videoCache.get(key);
		if (oj == null)
			return null;
		return oj.getName();
	}

	public void set(String key, Object arg, long timeout) {
		RecordedMisc msc = new RecordedMisc(key, arg, System.currentTimeMillis() + timeout * 1000);
		miscCache.setAsync(key, msc, timeout, TimeUnit.SECONDS);
	}
	
	public boolean ready() {
		if (miscCache == null)
			return false;
		if (videoCache == null)
			return false;
		if (bidCache == null)
			return false;
		if (memberCache == null)
			return false;
		return true;
	}

	public Object get(String key) {
		RecordedMisc msc = miscCache.get(key);
		if (msc == null)
			return null;
		return msc.getValue();
	}
	
	public long getAsNumber(String key) {
		RecordedMisc msc = miscCache.get(key);
		if (msc == null)
			return 0L;
		return (Long)msc.getValue();
	}

	public void del(String key) {
		miscCache.remove(key);
	}

	public void increment(String key, long timeout, String units) {
		RecordedMisc x = miscCache.get(key);
		Long y = null;
		if (x == null) {
			y = 1L;
		} else {
			y = (Long) x.getValue();
			y++;
			timeout =  x.getEndtime() - System.currentTimeMillis();
			timeout /= 1000;
			if (timeout < 0)
				return;
		}
		set(key, y, timeout);
	}

	public void decrement(String key, long timeout, String units) {
		RecordedMisc x = miscCache.get(key);
		Long y = null;
		if (x == null) {
			y = 0L;
		} else {
			y = (Long) x.getValue();
			y--;
			timeout = System.currentTimeMillis() - x.getEndtime();
			timeout /= 1000;
			if (timeout < 0)
				return;
		}
		set(key, y, timeout);
	}

	public ITopic getReliableTopic(String name) {
		ITopic topic = RTBServer.getSharedInstance().getReliableTopic(name);
		return topic;
	}
	
}
