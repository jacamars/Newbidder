package com.jacamars.dsp.rtb.tools;

import java.nio.charset.StandardCharsets;


import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.common.Campaign;

/**
 * Simple program to manipulate the Redisson map used as the database in
 * RTB4FREE Arguments:
 * <p>
 * [-redis host:port] Sets the redis host/port, uses localhost as default
 * <p>
 * [-clear] Drop the database in Redis
 * <p>
 * [-dump] Print the contents of the database.
 * <p>
 * [-load fname] Load the file into the database
 * <p>
 * [-write fname] Write the database to the file (in JSON form)
 * <p>
 * eg java -jar xrtb.jar tools.DbTools -clear -load mydb.json -dump
 * <p>
 * By default, no arguments loads database.json into the redisson database
 * 
 */

public class DbTools {
	/** jackson object mapper */
	public static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	/** The redisson backed shared map that represents this database */
	List<Campaign> listc;
	Set<String> set;

	/**
	 * Execute the database commands. With no arguments loads database.json into
	 * the Redisson map stored in Redis on local host.
	 * 
	 * @param args
	 *            String[]. List of arguments.
	 * @throws Exception
	 *             on I/O or Redis errors.
	 */
	public static void main(String args[]) throws Exception {
		String db = "database.json";
		String blist = "blacklist.json";
		String spike = "localhost:3000";

		int i = 0;
		DbTools tool = null;
		if (args.length > 0) {
			while (i < args.length) {
				if (args[i].equals("-h")) {
					System.out.println(
							"-aero <host:port>           [Sets the host:port string of the cache                     ]");
					System.out.println(
							"-clear                      [Clears the cache database                                  ]");
					System.out.println(
							"-print                      [Print the cache database to stdout                         ]");
					System.out.println(
							"-db <file-name>             [Loads the cache from a JSON file (default: ./database.json]]");
					System.out.println(
							"-load-blacklist <file-name> [Loads the blacklist from a list of domains                 ]");
					System.out.println(
							"-write <filename>           [Writes cache of database to the named file                 ]");
					System.out.println(
							"-write-blacklist <filename> [Writes redis blacklist to the named file                   ]");
					System.exit(0);
				} else if (args[i].equals("-db")) {
					i++;
					db = args[i];
					i++;
				} else if (args[i].equals("-bl")) {
					i++;
					blist = args[i];
					i++;
				}
			}
		}

		System.exit(0);
	}

	/**
	 * Drop the database in memory
	 */
	public void clear() {
		if (listc == null)
			return;
		listc.clear();
	}

	public void shutdown() {

	}

	/**
	 * Simple constructor. Used to setup redisson to local host.
	 * 
	 * @param rc RedissonClient. The redisson client object.
	 * @throws Exception on json parse errors
	 */

	public DbTools() throws Exception {
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/**
	 * Load a JSON string database into the Redisson (redis) map.
	 * 
	 * @param db
	 *            String. The JSON file to load into redis.
	 * @throws Exception
	 *             on I/O errors.
	 */
	public void loadDatabase(String db) throws Exception {
		
	}

	/**
	 * Remove a campaign by ad id.
	 * 
	 * @param adId
	 *            String. The ad id.
	 * @throws Exception
	 *             if the adid does not exist.
	 */
	public void deleteCampaign(String adId) throws Exception {
		
	}

	/**
	 * Print the contents of the REDIS database to stdout.
	 */
	public void printDatabase() throws Exception {
	

	}

	/**
	 * Save the redis database to disk of the given name.
	 * 
	 * @param db
	 *            String. The name of the file to contain the database.
	 * @throws Exception
	 *             on IO errors.
	 */
	public void saveDatabase(String db) throws Exception {
		write(db);
	}

	/**
	 * Read the database.json file into this object.
	 * 
	 * @param db
	 *            String. The JSON string database to load into Redis.
	 * @return List. A list of users in the database file.
	 * @throws Exception
	 *             on file errors.
	 */
	public List<Campaign> read(String db) throws Exception {
		String content = new String(Files.readAllBytes(Paths.get(db)), StandardCharsets.UTF_8);

		//System.out.println(content);

		List<Campaign> list = mapper.readValue(content,
				mapper.getTypeFactory().constructCollectionType(List.class, Campaign.class));
		return list;
	}

	/**
	 * Write the database object to the database.json file.
	 * 
	 * @param dbName
	 *            String. The filename to contain the Redis database.
	 * @throws Exception
	 *             on file errors.
	 */
	public void write(String dbName) throws Exception {

	}

}
