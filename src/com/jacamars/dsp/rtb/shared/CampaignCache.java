package com.jacamars.dsp.rtb.shared;

import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.config.Config;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.predicates.SqlPredicate;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.tools.DbTools;

import scala.annotation.meta.setter;

public enum CampaignCache  {

    INSTANCE;
	
	protected static final Logger logger = LoggerFactory.getLogger(CampaignCache.class);
	
	transient public static String DB_NAME = "database.json";
	
	private static ObjectMapper mapper = new ObjectMapper();
	 static {
	        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	 }

    /** The cache to contain the general context info on running campaigns*/
    static private volatile IMap<String, Campaign> cache;

    /** The default backup count if you dont set it */
    static public int backupCount = 3;

    /** The default value to read backups, is true */
    static public boolean readBackup = true;

    public static final String NAME = "CONTEXT";


    public static CampaignCache getInstance() {
        if (cache != null)
            return INSTANCE;

        Config config = RTBServer.getSharedInstance().getConfig();
        cache =  RTBServer.getSharedInstance().getMap(NAME);
        config.getMapConfig(NAME)
                .setAsyncBackupCount(backupCount)
                .setReadBackupData(readBackup);
        
        /**
         * Register my custom serializers (recorded bid)
         */
        MySerializers.register(config.getSerializationConfig());
        
        cache.addEntryListener(new EntryAddedListener<String, Campaign>() {

            @Override
            public void entryAdded(EntryEvent<String, Campaign> event) {
            	try {
            	
            		if (Configuration.getInstance().containsCampaign(event.getValue()))
            				return;
            		
            		Configuration.getInstance().addCampaign(event.getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
            }
        }, true);
        cache.addEntryListener(new EntryUpdatedListener<String, Campaign>() {

            @Override
            public void entryUpdated(EntryEvent<String, Campaign> event) {
            	try {
					Configuration.getInstance().recompile();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	logger.info("Campaign Updated: : {}",event.getKey());
            }
        }, true);
        cache.addEntryListener(new EntryRemovedListener<String, Campaign>() {

            @Override
            public void entryRemoved(EntryEvent<String, Campaign> event) {
            	try {
					Configuration.getInstance().recompile();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	logger.info("Campaign Removed: : {}",event.getKey());
            }
        }, true);
        cache.addEntryListener(new EntryEvictedListener<String, Campaign>() {

            @Override
            public void entryEvicted(EntryEvent<String, Campaign> event) {
//            	System.out.println("**** EVICTED: " + event.getKey());
            }
        }, true);
        
        return INSTANCE;
    }
    
    
    public static CampaignCache getClientInstance(HazelcastInstance hz) {
    	if (cache != null)
            return INSTANCE;

        Config config = RTBServer.getSharedInstance().getConfig();
        cache =  RTBServer.getSharedInstance().getMap(NAME);
        return INSTANCE;
    }
    
    public List<Campaign> getCampaigns() {
    	List<Campaign> list = new ArrayList();
    	
    	System.out.println("LIST CACHESIZE: " + cache.size());
    	cache.entrySet().forEach(e->{
    		list.add(e.getValue());
    	});
    		
    	return list;
    }
    
    public IMap getMap() {
    	return cache;
    }
    
    public int size() {
    	return cache.size();
    }
    
    public Campaign get(String id) {
    	return cache.get(id);
    }
    
    public Creative getCampaignFromCreative(String id, String type) {
    	Creative cr = null;
    	List<Campaign> camps = getCampaigns();
    	for (Campaign c: camps) {
    		cr = c.getCreative(id,type);
    		return cr;
    	}
    	return null;
    }
    
    public void deleteCampaign(Campaign c ) {
    	cache.remove(""+c.id);
    }
    
    public void deleteCampaign(String c ) {
    	cache.remove(c);;
    }
    
    public void loadDatabase(String fileName) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
            List<Campaign> list = mapper.readValue(content,
                    mapper.getTypeFactory().constructCollectionType(List.class, Campaign.class));
            
            for (Campaign c : list) {
            	cache.set(c.name, c);
            }
           
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
    
    public void addCampaign(Campaign c) {
    	cache.put(c.id+"",c);
    }
    
    public void deleteCampaigns() throws Exception {
    	List<Campaign> camps = getCampaigns();
    	for (Campaign c : camps) {
    		System.out.println("=-->" + c.name);
    		deleteCampaign(c);
    	}
    }
    
    public void loadFromString(String content) throws Exception {
    	 List<Campaign> list = mapper.readValue(content,
                 mapper.getTypeFactory().constructCollectionType(List.class, Campaign.class));
         
         for (Campaign c : list) {
         	cache.set(c.name, c);
         	Campaign x = cache.get(c.name);
         }
         
    }
    
    /**
	 * Return a campaign of the given name and adId.
	 * @param id String. the adId of the campaign to return.
	 * @return Campaign. The campaign to return.
	 */
	public Campaign getCampaign(String id) throws Exception {
		return cache.get(id);
	}
    
    /**
     * Edit a campaign in place
     * @param x Campaign. The campaign to replace into the list.
     * @return Campaign. Returns x if adId was found and x is swapped in. else returns null.
     * @throws Exception on access errors.
     */
    public Campaign editCampaign(Campaign x) throws Exception  {
    	cache.set(x.name, x);
        return x;
    }
    
    /**
	 * Create a stub campaign from 'stub.json'
	 * @param name String. The user name.
	 * @param id String. The adId to use for this campaign.
	 * @return Campaign. The campaign that was created.
	 * @throws Exception on file errors.
	 */
	public Campaign createStub(String name, String id) throws Exception {
		String content = new String(Files.readAllBytes(Paths.get("stub.json")));
		Campaign c = new Campaign(content);
		c.name = name + ":" + id;
		return c;
	}
	
	/**
	 * Read the database.json file into this object.
	 * @param db String. The name of the JSON database file to read
	 * @return List. A list of user objects in the database.
	 * @throws Exception on file errors.
	 */
	public List<Campaign> read(String db) throws Exception {
		String content = new String(Files.readAllBytes(Paths.get(db)));

		return mapper.readValue(content,
					mapper.getTypeFactory().constructCollectionType(List.class, Campaign.class));
	}
	
	/**
	 * Write the database object to the database.json file.
	 * @throws Exception on file errors.
	 */
	public void write() throws Exception{
		String content = DbTools.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(getCampaigns());
	    Files.write(Paths.get(DB_NAME), content.getBytes());
	}
	
	
	public Set query(String sql) throws Exception {
		Predicate predicate = null;
		if (sql != null && sql.trim().length() > 0)
			predicate = new SqlPredicate(sql);
		if (predicate == null)
			return cache.entrySet();
		else
			return cache.entrySet(predicate);
	}
}

