package com.jacamars.dsp.rtb.blocks;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.util.Trie;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.LowerCamelCaseStrategy;

import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressSeqRange;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.ipv4.IPv4Address;
import inet.ipaddr.ipv4.IPv4AddressAssociativeTrie;
import inet.ipaddr.ipv4.IPv4AddressTrie;
import inet.ipaddr.ipv4.IPv4AddressTrie.IPv4TrieNode;

/**
 * A class that enables to get an IP range from CIDR specification. It supports
 * both IPv4 and IPv6.
 */
public class CIDRUtils {
    private  String cidr;

    IPv4AddressTrie trie;
    IPAddressSeqRange range;
    private IPAddress addr;
	private IPAddress lower;
	private IPAddress upper;

    public CIDRUtils(String cidr)  {
    	this.cidr = cidr;
		addr = new IPAddressString(cidr).getAddress();
		int count = addr.getCount().intValue();
		
		for(IPAddress x: addr.getIterable()) {
			if (lower == null)
				lower = x;
			count--;
			if (count == 0)
				upper = x;
		}
		range = lower.toSequentialRange(upper);
    }
    
    /**
     * Create a CIDR map using low and high addresses
     * @param low String. The lower address in the range
     * @param up String. The upper address in the range.
     */
    public CIDRUtils(String low, String up) {
    	lower = new IPAddressString(low).getAddress();
    	upper = new IPAddressString(up).getAddress();
    	range = lower.toSequentialRange(upper);
    }
    
    /**
     * Create a a CIDR object from a list of cidr addresses.
     * @param cidrs List<String>. The list of addresses in CIDR format.
     */
    public CIDRUtils(List<String> cidrs) {
    	trie = new IPv4AddressTrie();
    	
    	cidrs.forEach(cidr->{
    		trie.add(new IPAddressString(cidr).getAddress().toIPv4());
    	});
    		
    }
    
    /**
     * Add a CIDR to the tree.
     * @param addr String. The cidr address
     * @return CIDRUtils. This.
     */
    public CIDRUtils add(String addr) {
    	if (trie == null)
    		trie = new IPv4AddressTrie();
    	trie.add(new IPAddressString(addr).getAddress().toIPv4());
    	return this;
    }
    
    /**
     * Return the start address of the CIDR, unless it is a tree
     * @return long. The lower address as a long.
     */
    public long getStartAddress() {
    	return lower.getValue().longValue();
     
    }
    
    /**
     * Return the end address of the CIDR.
     * @return
     */
    public long getEndAddress() {
    	return upper.getValue().longValue();
    }


    /**
     * Determine if this address is in the CIDR range.
     * @param ipAddress String. The ip address to check.
     * @return boolean. Returns true if in range, otherwise returns false.
     * @throws Exception on badly formed ip address.
     */
    public boolean isInRange(String ipAddress) throws Exception {	
    	
    	if (trie != null) {
    		IPAddressString toFindAddrStr = new IPAddressString(ipAddress);
    		IPv4Address toFindBlockFor = toFindAddrStr.getAddress().toIPv4();
    		IPv4TrieNode containingNode = trie.elementsContaining(toFindBlockFor);
    		if(containingNode != null) {
    			//System.out.println("For address " + toFindBlockFor + 
    			//	" containing block is " + containingNode.getKey() + "\n");
    			return true;
    		}
    		return false;
    	}
    	
    	return range.contains(new IPAddressString(ipAddress).toAddress());
    }
    

    /**
     * Given a string ip address, return the long equivalent.
     * @param ipAddress String. The ip address to convert to long.
     * @return long. The long version of the string ip address.
     */
    public static long getLongAddress(String ipAddress) {
    	var addr = new IPAddressString(ipAddress).getAddress();
    	return addr.getValue().longValue();
    }
    
    /**
     * Given an ip address in long form, return the string dotted decimal equivalent.
     * @param ipAddress long. The long address.
     * @return String. The ip address in dotted decimal doem.
     * @throws Exception if not an ip address.
     */
    public static String longToString(long ipAddress) throws Exception {
    	var i = InetAddress.getByName(String.valueOf(ipAddress));
    	String ip = i.getHostAddress();
    	return ip;
    }
}
