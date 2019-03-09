package com.jacamars.dsp.rtb.shared;

import com.hazelcast.core.MapStore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * A class that implements the MapStore backup to the FrequencySetCache using JDBC.
 * @author Ben M. Faul
 *
 */
public class FreqSetCacheStore implements MapStore<Long, String> {

    private Connection con;
    private PreparedStatement allKeysStatement;
    private static volatile FreqSetCacheStore bcs;


    public FreqSetCacheStore(String jdbc) {
        try {
            con = DriverManager.getConnection(jdbc);
            con.createStatement().executeUpdate(
                    "create table if not exists frequencycap (id bigint not null, name varchar(128), primary key (id))");
            allKeysStatement = con.prepareStatement("select id from frequencycap");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void delete(Long key) {
        System.out.println("Delete:" + key);
        try {
            con.createStatement().executeUpdate(
                    format("delete from frequencycap where id = %s", key));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void store(Long key, String value) {
        try {
            con.createStatement().executeUpdate(
                    format("insert into frequencycap values(%s,'%s')", key, value));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void storeAll(Map<Long, String> map) {
        for (Map.Entry<Long, String> entry : map.entrySet()) {
            store(entry.getKey(), entry.getValue());
        }
    }

    public synchronized void deleteAll(Collection<Long> keys) {
        for (Long key : keys) {
            delete(key);
        }
    }

    public synchronized String load(Long key) {
        try {
            ResultSet resultSet = con.createStatement().executeQuery(
                    format("select name from frequencycap where id =%s", key));
            try {
                if (!resultSet.next()) {
                    return null;
                }
                String name = resultSet.getString(1);
                return name;
            } finally {
                resultSet.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized Map<Long, String> loadAll(Collection<Long> keys) {
        Map<Long, String> result = new HashMap<Long, String>();
        for (Long key : keys) {
            result.put(key, load(key));
        }
        return result;
    }

    public Iterable<Long> loadAllKeys() {
        return new StatementIterable<Long>(allKeysStatement);
    }
}