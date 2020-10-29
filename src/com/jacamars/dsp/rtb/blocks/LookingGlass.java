package com.jacamars.dsp.rtb.blocks;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.model.S3Object;

public class LookingGlass {

	static Logger logger = LoggerFactory.getLogger(LookingGlass.class);
	// The symbol table used throughout the bidder
	public static volatile Map<String, Object> symbols = new ConcurrentHashMap<String, Object>();
	
	// My map
	protected Map myMap = new ConcurrentHashMap<String, String[]>();
	
	
	public String fileName;
	public String s3;
	/**
	 * Default constructor
	 */
	public LookingGlass() {
		
	}

    /**
     * Evict a symbol
     * @param key String. The symbol name to remove.
     */
	public static void remove(String key) {
	    symbols.remove(key);
    }
	
	/**
	 * A Class that implements a map from a 2 element comma separated list.
	 * @param name String. The symbol name this object is known by in the bidder.
	 * @param file String. The filename of the csv data file.
	 * @throws Exception on File I/O errors.
	 */
	public LookingGlass(String name, String file) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(file));
		fileName = file;
		symbols.put(name, this);
		String[] parts = null;	
		String message = "Initialize Simple Map: " + file + " as " + name;
		for (String line; (line = br.readLine()) != null;) {
			parts = eatquotedStrings(line);
			for (int i=0;i<parts.length;i++) {
				parts[i] = parts[i].replaceAll("\"","");
			}
			myMap.put(parts[0], parts);
		}
		br.close();
		logger.info("{}",message);
	}
	
	public LookingGlass(String name, S3Object obj) throws Exception {
		symbols.put(name, this);
		s3 = obj.getBucketName() + "/" + obj.getKey();
		InputStream objectData = obj.getObjectContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(objectData));

		String[] parts = null;	
		String message = "Initialize Simple Map: " + obj.getBucketName() + " as " + name;
		
		for (String line; (line = br.readLine()) != null;) {
			parts = eatquotedStrings(line);
			for (int i=0;i<parts.length;i++) {
				parts[i] = parts[i].replaceAll("\"","");
			}
			myMap.put(parts[0], parts);
		}
		br.close();
		logger.info("{}",message);
	}
	
	/**
	 * Return the value stored at key.
	 * @param key Object. The key to use in the lookup.
	 * @return Object. Returns the value at key, or null if not in the list.
	 */
	public Object query(String key) {
		if (key == null)
			return null;
		return myMap.get(key);
	}
	
	/**
	 * Read comma separated items, except, ignore all comments in double quoted strings.
	 * @param line String. The line to parse.
	 * @return String[]. The tokens parsed from the line.
	 */
	public static String[] eatquotedStrings(String line) {

		String otherThanQuote = " [^\"] ";
		String quotedString = String.format(" \" %s* \" ", otherThanQuote);
		String regex = String.format(
				"(?x) " + // enable comments, ignore white spaces
						",                         " + // match a comma
						"(?=                       " + // start positive look
														// ahead
						"  (?:                     " + // start non-capturing
														// group 1
						"    %s*                   " + // match 'otherThanQuote'
														// zero or more times
						"    %s                    " + // match 'quotedString'
						"  )*                      " + // end group 1 and repeat
														// it zero or more times
						"  %s*                     " + // match 'otherThanQuote'
						"  $                       " + // match the end of the
														// string
						")                         ", // stop positive look
														// ahead
				otherThanQuote, quotedString, otherThanQuote);

        //for (String t : tokens) {
		//	System.out.println("> " + t);
		// }
		return line.split(regex, -1);
	}
	
	/**
	 * Return the size
	 * @return
	 */
	public long getMembers() {
		return myMap.size();
	}
	
	public static Object get(String name) {
		Object x = symbols.get(name);
		return x;
	}

	public static List<String> getAllSymbolNames() {
		return new ArrayList<>(symbols.keySet());
	}
	
}
