package com.jacamars.dsp.rtb.blocks;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.amazonaws.services.s3.model.S3Object;
import com.google.common.collect.Maps;

public class NavMap extends LookingGlass implements Set {

	// The backing TreeMap.
	TreeMap tree = Maps.newTreeMap();

	// The name of the object
	String name;
	
	CIDRUtils cidr;

	/**
	 * Older form of constructor where you are told what kind it is cidr or not
	 * 
	 * @param name
	 *            String. The name of the key.
	 * @param file
	 *            String. The file name.
	 * @param cidr
	 *            boolean. Is a cidr is true. Else false is range
	 * @throws Exception
	 *             on I/O errors.
	 */
	public NavMap(String name, String file, String type) throws Exception {
		this.name = name;
		fileName = file;
		symbols.put(name, this);
		if (type.equalsIgnoreCase("cidr")) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			doCidr(br);
		} else {
			BufferedReader br = new BufferedReader(new FileReader(file));
			doRanges(br);
		}
	}

	/**
	 * A navigable hashmap for handling CIDR lists and range maps from an S3
	 * object.
	 * 
	 * @param name
	 *            String. The name of the object.
	 * @param object
	 *            S3Object. The S3 object for this..
	 * @throws Exception
	 *             on I/O errors.
	 */
	public NavMap(String name, S3Object object, String type) throws Exception {
		this.name = name;
		s3 = object.getBucketName() + "/" + object.getKey();
		String file = object.getBucketName();
		InputStream objectData = object.getObjectContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(objectData));
		if (type.equalsIgnoreCase("cidr")) {
			doCidr(br);
		} else if (type.equalsIgnoreCase("range")) {
			doRanges(br);
		} else
			throw new Exception(file + " Not in range or CIDR form");
		
		symbols.put(name, this);
	}

	/**
	 * Process CIDR lists.
	 * 
	 * @param br
	 *            BufferedReader. The line-by-line reader.
	 * @throws Exception
	 *             on I/O errors.
	 */
	void doCidr(BufferedReader br) throws Exception {
		
		cidr = new CIDRUtils(new ArrayList<String>());
		
		
		long start;
		long end;
		String messagel;
		XRange r;
		int k;

		k = 0;
		for (String line; (line = br.readLine()) != null;) {
			if (!(line.startsWith("#") || line.length() < 10)) {
				cidr.add(line.trim());
			}
		}
	}

	/**
	 * Search the navmap using a long representation of the
	 * 
	 * @param key
	 * @return
	 */
	public Boolean search(long key) {
		Map.Entry<Long, XRange> entry = tree.floorEntry(key);
		if (entry == null)
			return false;
		else if (key <= entry.getValue().upper) {
			return true; // return entry.getValue().value;
		} else {
			return false;
		}
	}

	public static long ipToLong(String ipAddress) {
		return CIDRUtils.getLongAddress(ipAddress);
	}

	public static String longToIp(long ip) throws Exception {
		if (ip > 4294967295L || ip < 0) {
			throw new IllegalArgumentException("invalid ip");
		}
		return CIDRUtils.longToString(ip);
	}

	void doRanges(BufferedReader br) throws Exception {
		long oldstart = 0;
		long oldend = 0;
		long start = 0;
		long end = 0;
		String message = null;

		String[] parts = null;

		XRange r = null;
		long over = 0;
		int linek = 9;
		int k = 0;

		for (String line; (line = br.readLine()) != null;) {
			if (!(line.startsWith("#") || line.length() < 10)) {
				parts = line.split("-");
				if (parts[0].length() > 0) {

					start = CIDRUtils.getLongAddress(parts[0]);
					end = CIDRUtils.getLongAddress(parts[1]);

					if (oldstart == 0) {
						oldstart = start;
						oldend = end;
					} else {
						if (start == oldend + 1) {
							over++;
							oldend = end;
						} else {
							r = new XRange(oldend, k);
							k++;

							tree.put(oldstart, r);

							oldstart = start;
							oldend = end;
						}
					}

				}
				linek++;
			}
		}

		r = new XRange(end, k);
		tree.put(start, r);

		double d = (double) over / (double) linek;
	}

	@Override
	public int size() {
		return tree.size();
	}

	@Override
	public boolean isEmpty() {
		if (tree.size() == 0)
			return true;
		return false;
	}

	public boolean searchTable(Object key) {
		return contains(key);
	}

	@Override
	public boolean contains(Object key) {
		// System.out.println("Looking for: " + key);
		String value = null;
		boolean p = false;
		if (key instanceof String str) {
			value = str;
		} else {
			try {
				value = CIDRUtils.longToString((Long)key);
			} catch (Exception error) {
				return false;
			}
		}
		
		try {
			p = cidr.isInRange(value);
		} catch (Exception err) {
			err.printStackTrace();
		}
		return p;
		
	}

	@Override
	public Iterator iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray(Object[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(Object e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public long getMembers() {
		return tree.size();
	}
}
