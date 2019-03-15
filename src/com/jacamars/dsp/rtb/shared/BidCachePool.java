package com.jacamars.dsp.rtb.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.commands.Echo;
import com.jacamars.dsp.rtb.commands.PortableEchoFactory;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.RecordedBid;
import com.jacamars.dsp.rtb.common.RecordedVideo;
import com.jacamars.dsp.rtb.tools.DbTools;
import com.jacamars.dsp.rtb.tools.Env;

public enum BidCachePool {

	INSTANCE;

	private static final Logger logger = LoggerFactory.getLogger(BidCachePool.class);
	private static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/** The cache to contain the general context */
	static private volatile IMap<String, RecordedBid> bidCache;
	static private volatile IMap<String, Echo> memberCache;
	static private volatile IMap<String, RecordedVideo> videoCache;
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
					System.out.println("**** EVICTED: " + event.getKey());
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

	        config.getSerializationConfig()
            	.addPortableFactory(SamplePortableFactory.FACTORY_ID, new SamplePortableFactory())
            	.addPortableFactory(PortableEchoFactory.FACTORY_ID, new PortableEchoFactory());
	        name = "CUSTOMERS";
			IMap<String,Customer> junk = RTBServer.getSharedInstance().getMap(name);
			Customer crud = new Customer();
			crud.name = "Ben";
			crud.id = 123;
			crud.lastOrder = new Date();
			crud.list = new ArrayList();
			crud.list.add("Heidi");
			crud.list.add("Hodaka");
			junk.put("Ben", crud);
			
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
					System.out.println("**** EVICTED: " + event.getKey());
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
					System.out.println("**** EVICTED: " + event.getKey());
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
		}
		return INSTANCE;
	}
	
	public static BidCachePool getInstance() {
		return INSTANCE;
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
