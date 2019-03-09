package com.jacamars.dsp.rtb.shared;

import com.hazelcast.core.IMap;
import com.hazelcast.core.MapStore;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.RecordedVideo;
import com.jacamars.dsp.rtb.tools.DbTools;

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

/**
 * A class that implements the MapStore backup to the Video using JDBC.
 * @author Ben M. Faul
 *
 */
public class MiscCacheStore implements MapStore<String, RecordedMisc> {

    private Connection con;
    private PreparedStatement allKeysStatement;
    private static volatile MiscCacheStore  bcs;
    
    public MiscCacheStore() {
        try {
        	Class.forName(Configuration.getInstance().mapstoredriver); 
            con = DriverManager.getConnection(Configuration.getInstance().mapstorejdbc);
            con.createStatement().executeUpdate(
                    "create table if not exists misc (id text not null, value text not null, endtime bigint not null, primary key (id))");
            allKeysStatement = con.prepareStatement("select id from video");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void evict(String key) {
    	if (bcs == null)
    		bcs = new MiscCacheStore();
    	bcs.delete(key);
    }
    
    public static int initialize(IMap<String, RecordedMisc> miscCache) {
    	if (bcs == null)
    		bcs = new MiscCacheStore();
		var keys = bcs.loadAllKeys();
		List<String> target = new ArrayList<>();
		keys.iterator().forEachRemaining(target::add);
		long now = System.currentTimeMillis();
		int k = 0;
		for (String key : target) {
			RecordedMisc b = bcs.load(key);
			if (b.getEndtime() <= now) {
				bcs.delete(key);
			} else {
				long ttl = now - b.getEndtime();
				b.setEndtime(now+ttl);
				ttl /= 1000;
				miscCache.setAsync(key, b, ttl, TimeUnit.SECONDS);
				k++;
			}
		}
		return k;
    }

    public synchronized void delete(String key) {
        System.out.println("Delete:" + key);
        try {
            con.createStatement().executeUpdate(
                    format("delete from misc where id = '%s'", key));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void store(String key, RecordedMisc oj) {
        try {
        	con.createStatement().executeUpdate(format("delete from misc where id='%s'",key));
            con.createStatement().executeUpdate(
                    format("insert into misc values('%s','%s', %s)", key, oj.getValueAsString(), oj.getEndtime()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void storeAll(Map<String, RecordedMisc> map) {
        for (Map.Entry<String, RecordedMisc> entry : map.entrySet()) {
            store(entry.getKey(), entry.getValue());
        }
    }

    public synchronized void deleteAll(Collection<String> keys) {
        for (String key : keys) {
            delete(key);
        }
    }

    public synchronized RecordedMisc load(String key) {
        try {
            ResultSet resultSet = con.createStatement().executeQuery(
                    format("select value, endtime from misc where id ='%s'", key));
            try {
                if (!resultSet.next()) {
                    return null;
                }
                String str = resultSet.getString(1);
                Object oj = DbTools.mapper.readValue(str, Object.class);
                Long endtime = resultSet.getLong(2);
                return new RecordedMisc(key, oj, endtime);
            } finally {
                resultSet.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized Map<String, RecordedMisc> loadAll(Collection<String> keys) {
        Map<String, RecordedMisc> result = new HashMap<String, RecordedMisc>();
        for (String key : keys) {
            result.put(key, load(key));
        }
        return result;
    }

    public Iterable<String> loadAllKeys() {
        return new StatementIterable<String>(allKeysStatement);
    }
}