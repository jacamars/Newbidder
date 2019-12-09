package com.jacamars.dsp.rtb.commands;
import java.io.IOException;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.jacamars.dsp.rtb.bidder.Controller;

import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.probe.Probe;
import com.jacamars.dsp.rtb.probe.Reason;
import com.jacamars.dsp.rtb.shared.PortableJsonFactory;
import com.jacamars.dsp.rtb.tools.DbTools;

/**
 * This is the echo command and response format. It provides basic statistical info plus
 * all of the campaigns currently loaded in the system.
 * @author Ben M. Faul
 *
 */

public class Echo extends BasicCommand implements Portable {
	public static final int CLASS_ID = 2;
	
	/** The list of campaign objects, that are currently loaded in the systen */
	public List<String> campaigns = new ArrayList();;
	/** The current setting of percentage */
	public int percentage;
	/** Indicates whether the bidder is processing any bid requests */
	public boolean stopped;
	/** the count of bid requests procesed */
	public long request;
	/** The count of bids currently send */
	public long bid;
	/** The count of no-bids current sent */
	public long nobid;
	/** The number of win notifications */
	public long win;
	/** The count of errors accessing the bidder */
	public long error;
	/** Number of total requests */
	public long handled;
	/** Number of unknown requests */
	public long unknown;
	/** The current log level */
	public int loglevel;
	/** The number of clicks */
	public long clicks;
	/** The number of pixels */
	public long pixels;
	/** the adpsend */
	public double adspend;
	/** relative qps */
	public double qps;
	/** avg xtime */
	public double avgx;
	/** Fraud count */
	public long fraud;
	/** Number of threads */
	public int threads;
	/** Percentage of memory used by the VM */
	public String memory;
	/** Percentage of disk free */
	public String freeDisk;
	/** Disk usage percentage */
	public String cpu;
	/** Summary stats by exchanges */
	public List<Map>exchanges;
	/** Campaign/creative performance map */
	public Probe probe;
	/** ip address */
	public String ipaddress;
	/** is the leader */
	public boolean leader;
	/** last write */
	public long lastupdate;
	/** total http handled */
	public long total;
	/** number of cores */
	public int cores;
	/** number f camoaigns */
	public int ncampaigns;
	/** number of effective campaigns */
	public int ecampaigns;
	/** no bid readon flag */
	public boolean nobidreason;
	/** High severity events */
	public List<Map<String,String>> events;
	
	public transient String json;
	
	/**
	 * Register the portable hazelcast serializeable object. Call this before hazelcast is initialized!
	 * @param config ClientConfig. The configuration for the user.
	 */
	public static void registerWithHazelCast(ClientConfig config) {
        config.getSerializationConfig().addPortableFactory(PortableJsonFactory.FACTORY_ID, new PortableJsonFactory());
        ClassDefinitionBuilder portableEchoClassBuilder = new ClassDefinitionBuilder(PortableJsonFactory.FACTORY_ID, Echo.CLASS_ID);
		portableEchoClassBuilder.addUTFField("json");

		ClassDefinition portableEchoClassDefinition = portableEchoClassBuilder.build();
	    config.getSerializationConfig().addClassDefinition(portableEchoClassDefinition);
	}
	
	/**
	 * Register the portable hazelcast serializeable object. Call this before hazelcast is initialized!
	 * @param config ClientConfig. The configuration for the member.
	 */
	public static void registerWithHazelCast(Config config) {
		config.getSerializationConfig().addPortableFactory(PortableJsonFactory.FACTORY_ID, new PortableJsonFactory());
	    ClassDefinitionBuilder portableEchoClassBuilder = new ClassDefinitionBuilder(PortableJsonFactory.FACTORY_ID, Echo.CLASS_ID);
	    portableEchoClassBuilder.addUTFField("json");

		ClassDefinition portableEchoClassDefinition = portableEchoClassBuilder.build();
		config.getSerializationConfig().addClassDefinition(portableEchoClassDefinition);
	}
	
	// https://github.com/codeset-projects/portable-tricks/blob/master/src/test/java/codeset/portable/tips/Case2Test.java
	public Echo() {
		super();
		cmd = Controller.ECHO;
		status = "ok";
		
	}
	
	public Echo(String s) {
		super(s);
		cmd = Controller.ECHO;
		status = "ok";
	}
	
	/**
	 * Return a pretty printed JSON object
	 * @return String. A pretty printed JSON string of this object 
	 */
	public String toJson() {
		try {
			return DbTools.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String [] args) {
		Echo e = new Echo();
		System.out.println(e);
	}

	@Override
	public int getFactoryId() {
		return PortableJsonFactory.FACTORY_ID;
	}

	@Override
	public int getClassId() {
		 return CLASS_ID;
	}


	@Override
	public void writePortable(PortableWriter writer) throws IOException {
		json = DbTools.mapper.writeValueAsString(this);
		writer.writeUTF("json",json);
	}

	@Override
	public void readPortable(PortableReader reader) throws IOException {
		json = reader.readUTF("json");
		Echo e = DbTools.mapper.readValue(json, Echo.class);
		for (String c : e.campaigns) {
			campaigns.add(c);
		}
		
		name = e.name;
		percentage = e.percentage;
		stopped = e.stopped;
		request = e.request;
		bid = e.bid;
		nobid = e.nobid;
		win = e.win;
		error = e.error;
		handled = e.handled;
		unknown = e.unknown;
		loglevel = e.loglevel;
		clicks = e.clicks;
		pixels = e.pixels;
		adspend = e.adspend;
		qps = e.qps;
		avgx = e.avgx;
		fraud = e.fraud;
		threads = e.threads;
		memory = e.memory;
		freeDisk = e.freeDisk;
		cpu = e.cpu;
		ipaddress = e.ipaddress;
		leader = e.leader;
		lastupdate = e.lastupdate;
		total = e.total;
		cores = e.cores;
		ncampaigns = e.ncampaigns;
		ecampaigns = e.ecampaigns;
		nobidreason = e.nobidreason;
		events = e.events;
		
		probe = e.probe;
		exchanges = e.exchanges;
	}
}
