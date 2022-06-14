package com.jacamars.dsp.rtb.shared;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.map.IMap;
import com.hazelcast.map.MapStore;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.FrequencyCap;
import com.jacamars.dsp.rtb.common.RecordedBid;
import com.jacamars.dsp.rtb.tools.Env;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

import java.io.IOException;

/**
 * A class that implements the MapStore backup to the recorded bids using JDBC.
 * @author Ben M. Faul
 *
 */
public class BidCacheStore implements MapStore<String, RecordedBid> {

	private static ObjectMapper mapper = new ObjectMapper();
	 static {
	        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	 }

	 
    private Connection con;
    private PreparedStatement allKeysStatement;
    private static volatile BidCacheStore bcs;

    public static int initialize(IMap<String, RecordedBid> bidCache) {
    	if (bcs == null)
    		bcs = new BidCacheStore();
		var keys = bcs.loadAllKeys();
		List<String> target = new ArrayList<>();
		keys.iterator().forEachRemaining(target::add);
		long now = System.currentTimeMillis();
		int k = 0;
		for (String key : target) {
			RecordedBid b = bcs.load(key);
			if (b.getEndtime() <= now) {
				bcs.delete(key);
			} else {
				long ttl = now - b.getEndtime();
				b.setEndtime(now+ttl);
				ttl /= 1000;
				bidCache.setAsync(key, b, ttl, TimeUnit.SECONDS);
				k++;
			}
			
		}
		return k;
    }
    
    public static void evict(String key) {
    	if (bcs == null)
    		bcs = new BidCacheStore();
    	bcs.delete(key);
    }
    
    public BidCacheStore()  {
        try {

            con = DriverManager.getConnection(Configuration.getInstance().mapstorejdbc);
            con.createStatement().executeUpdate(
                    "create table if not exists recordedbids (id text not null, capkey text not null, captimeout bigint not null, captimeunit text not null, price text not null, adtype text not null, frequencycap text not null, endtime bigint not null,  primary key (id))");
            allKeysStatement = con.prepareStatement("select id from recordedbids");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void delete(String key) {
        //System.out.println("Delete:" + key);
        try {
            con.createStatement().executeUpdate(
                    format("delete from recordedbids where id = '%s'", key));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void store(String key, String value) {
        try {
            con.createStatement().executeUpdate(
                    format("insert into recordedbids values('%s','%s')", key, value));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void storeAll(Map<String, RecordedBid> map) {
        for (Map.Entry<String, RecordedBid> entry : map.entrySet()) {
            store(entry.getKey(), entry.getValue());
        }
    }

    public synchronized void deleteAll(Collection<String> keys) {
        for (String key : keys) {
            delete(key);
        }
    }

    public synchronized RecordedBid load(String key) {
        try {
            ResultSet resultSet = con.createStatement().executeQuery(
                    format("select * from recordedbids where id ='%s'", key));
            try {
                if (!resultSet.next()) {
                    return null;
                }
                
                String capkey = resultSet.getString(2);
                long captimeout = resultSet.getLong(3);
                String captimeunit = resultSet.getString(4);
                String price = resultSet.getString(5);
                String adtype = resultSet.getString(5); 
                String frequencycap = resultSet.getString(7);
                long endtime = resultSet.getLong(8);
                
                List<FrequencyCap> fqs = null;
                if (! frequencycap.equals("")) {
                	try {
						fqs = mapper.readValue(frequencycap,
								mapper.getTypeFactory().constructCollectionType(List.class, FrequencyCap.class));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                } 
              
                return new RecordedBid(key, capkey, captimeout, captimeunit, price, adtype, fqs, endtime);
            } finally {
                resultSet.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized Map<String, RecordedBid> loadAll(Collection<String> keys) {
        Map<String, RecordedBid> result = new HashMap<String, RecordedBid>();
        for (String key : keys) {
            result.put(key, load(key));
        }
        return result;
    }

    public Iterable<String> loadAllKeys() {
        return new StatementIterable<String>(allKeysStatement);
    }
    


	@Override
	public void store(String key, RecordedBid obj) {
		
		 String capkey = obj.getCapKey();
         Long captimeout = obj.getCapTimeout();
         String captimeunit = obj.getCapTimeUnit();
         String price = obj.getPrice();
         String adtype = obj.getAdType();
         String frequencycap = obj.getFrequencyCapAsString();
         Long endtime = obj.getEndtime();
         
         if (captimeout == null) 
        	 captimeout = 0L;
 		
         if (capkey == null) 
        	 capkey = "";
         
         if (captimeunit == null)
        	 captimeunit = "";
         
		 try {
			 	con.createStatement().executeUpdate(format("delete from recordedbids where id='%s'",key));
	            con.createStatement().executeUpdate(
	                    format("insert into recordedbids(id,capkey,captimeout,captimeunit,price,adtype,frequencycap,endtime) values('%s', '%s', %s, '%s', '%s', '%s', '%s', %s)", 
	                    		key, capkey, captimeout, captimeunit, price, adtype, frequencycap, endtime));
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
		
	}
}