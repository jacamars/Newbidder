package com.jacamars.dsp.rtb.blocks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeSet;

import com.amazonaws.services.s3.model.S3Object;



/**
 * A Membership class fdor use with the Bidder.
 * @author Ben M. Faul
 *
 */
public class Membership extends LookingGlass {

	// The set that contains the members
	volatile Set<String> tree = new TreeSet<String>();

	
	// The name of the symbol
	String name;
	
	String file;
	
	/**
	 * Generic constructor
	 */
	public Membership() {
		
	}
	
	/**
	 * Create a membership from the file provided. We expect a simple list, one entry per line.
	 * @param name String. The name of the Membership
	 * @param file String. The filename containing the goodies.
	 * @throws Exception on File I/O errors.
	 */
	public Membership(String name, String file) throws Exception {
		symbols.put(name, this);
		this.name = name;
		myMap = null;
		this.file = file;
		BufferedReader br = new BufferedReader(new FileReader(file));

		readData(br);
	}
	
	/**
	 * Create a membership from the file provided. We expect a simple list, one entry per line.
	 * @param name String. The name of the Membership
	 * @param file String. The filename containing the goodies.
	 * @throws Exception on File I/O errors.
	 */
	public Membership(String name, S3Object object) throws Exception {
		this.name = name;
		myMap = null;
		InputStream objectData = object.getObjectContent();
		BufferedReader br=new BufferedReader(new InputStreamReader(objectData));
		readData(br);
	}
	
	/**
	 * Read data and shove into the tree.
	 * @param file String. The filename
	 * @throws Exception on I/O errors.
	 */
	void readData(BufferedReader br) throws Exception {		

		String[] parts = null;	
		String message = "Initialize Simple Membership: " + file + " as " + name;
		for (String line; (line = br.readLine()) != null;) {
			parts = eatquotedStrings(line);
			for (int i=0;i<parts.length;i++) {
				parts[i] = parts[i].replaceAll("\"","");
			}
			tree.add(parts[0]);
		}
		
		// System.out.format("[%s] - %d - %s - %s - %s\n",Controller.sdf.format(new Date()), 1, Configuration.instanceName, this.getClass().getName(),message);
	}
	
	@Override
	public long getMembers() {
		return tree.size();
	}

}
