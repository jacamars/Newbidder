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
import com.jacamars.dsp.rtb.probe.CampaignPerformance;
import com.jacamars.dsp.rtb.probe.CreativePerformance;
import com.jacamars.dsp.rtb.probe.ExchangePerformance;
import com.jacamars.dsp.rtb.probe.Reason;
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
	public List<ExchangePerformance> eperform = new ArrayList();
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
	
	public static void registerWithHazelCast(ClientConfig config) {
		
        config.getSerializationConfig().addPortableFactory(PortableEchoFactory.FACTORY_ID, new PortableEchoFactory());
        
		ClassDefinitionBuilder nestedPortableClassBuilder = new ClassDefinitionBuilder(PortableEchoFactory.FACTORY_ID, Reason.CLASS_ID);
		nestedPortableClassBuilder.addLongField("count");
		nestedPortableClassBuilder.addUTFField("name");
		ClassDefinition nestedPortableClassDefinition = nestedPortableClassBuilder.build();
	    config.getSerializationConfig().addClassDefinition(nestedPortableClassDefinition);
		
		ClassDefinitionBuilder creativeClassDefinitionBuilder = new ClassDefinitionBuilder(PortableEchoFactory.FACTORY_ID, CreativePerformance.CLASS_ID);
		creativeClassDefinitionBuilder.addUTFField("creative");
		creativeClassDefinitionBuilder.addBooleanField("_has__reasons");
		creativeClassDefinitionBuilder.addPortableArrayField("reasons",nestedPortableClassDefinition);
		creativeClassDefinitionBuilder.addLongField("total");
		creativeClassDefinitionBuilder.addLongField("bids");
		ClassDefinition creativeClassDefinition = creativeClassDefinitionBuilder.build();
		config.getSerializationConfig().addClassDefinition(creativeClassDefinition);
		
		ClassDefinitionBuilder campaignClassDefinitioBuilder = new ClassDefinitionBuilder(PortableEchoFactory.FACTORY_ID, CampaignPerformance.CLASS_ID);
		campaignClassDefinitioBuilder.addUTFField("campaign");
		campaignClassDefinitioBuilder.addBooleanField("_has__creatives");
		campaignClassDefinitioBuilder.addPortableArrayField("creatives",creativeClassDefinition);
		campaignClassDefinitioBuilder.addLongField("total");
		campaignClassDefinitioBuilder.addLongField("bids");
		ClassDefinition campaignClassDefinition = campaignClassDefinitioBuilder.build();
		config.getSerializationConfig().addClassDefinition(campaignClassDefinition);
		
		ClassDefinitionBuilder exchangeClassDefinitionBuilder = new ClassDefinitionBuilder(PortableEchoFactory.FACTORY_ID, ExchangePerformance.CLASS_ID);
		exchangeClassDefinitionBuilder.addBooleanField("_has__campaigns");
		exchangeClassDefinitionBuilder.addPortableArrayField("campaigns",campaignClassDefinition);
		exchangeClassDefinitionBuilder.addUTFField("exchange");
		exchangeClassDefinitionBuilder.addLongField("total");
		exchangeClassDefinitionBuilder.addLongField("bids");
		ClassDefinition exchangeClassDefinition = exchangeClassDefinitionBuilder.build();
		config.getSerializationConfig().addClassDefinition(exchangeClassDefinition);
		
		ClassDefinitionBuilder echoClassDefinitionBuilder = new ClassDefinitionBuilder(PortableEchoFactory.FACTORY_ID, Echo.CLASS_ID);
		echoClassDefinitionBuilder.addUTFField("name");
		echoClassDefinitionBuilder.addBooleanField("_has__campaigns");
		echoClassDefinitionBuilder.addUTFArrayField("campaigns");
		echoClassDefinitionBuilder.addIntField("percentage");
		echoClassDefinitionBuilder.addBooleanField("stopped");
		echoClassDefinitionBuilder.addLongField("request");
		echoClassDefinitionBuilder.addLongField("bid");
		echoClassDefinitionBuilder.addLongField("nobid");
		echoClassDefinitionBuilder.addLongField("win");
		echoClassDefinitionBuilder.addLongField("error");
		echoClassDefinitionBuilder.addLongField("handled");
		echoClassDefinitionBuilder.addLongField("unknown");
		echoClassDefinitionBuilder.addIntField("loglevel");
		echoClassDefinitionBuilder.addLongField("clicks");
		echoClassDefinitionBuilder.addLongField("pixels");
		echoClassDefinitionBuilder.addDoubleField("adspend");
		echoClassDefinitionBuilder.addDoubleField("qps");
		echoClassDefinitionBuilder.addDoubleField("avgx");
		echoClassDefinitionBuilder.addLongField("fraud");
		echoClassDefinitionBuilder.addIntField("threads");
		echoClassDefinitionBuilder.addUTFField("memory");
		echoClassDefinitionBuilder.addUTFField("freeDisk");
		echoClassDefinitionBuilder.addUTFField("cpu");
		/** Summary stats by exchanges */
		// public List<Map>exchanges;
		echoClassDefinitionBuilder.addBooleanField("_has__eperform");
		echoClassDefinitionBuilder.addPortableArrayField("eperform",exchangeClassDefinition);
		echoClassDefinitionBuilder.addUTFField("ipaddress");
		echoClassDefinitionBuilder.addBooleanField("leader");
		echoClassDefinitionBuilder.addLongField("lastupdate");
		echoClassDefinitionBuilder.addLongField("total");
		echoClassDefinitionBuilder.addIntField("cores");
		echoClassDefinitionBuilder.addIntField("ncampaigns");
		echoClassDefinitionBuilder.addIntField("ecampaigns");
		echoClassDefinitionBuilder.addBooleanField("nobidreason");	
		ClassDefinition echoClassDefinition = echoClassDefinitionBuilder.build();
		config.getSerializationConfig().addClassDefinition(echoClassDefinition);	
	}
	
	public static void registerWithHazelCast(Config config) {
        config.getSerializationConfig().addPortableFactory(PortableEchoFactory.FACTORY_ID, new PortableEchoFactory());
        
		ClassDefinitionBuilder nestedPortableClassBuilder = new ClassDefinitionBuilder(PortableEchoFactory.FACTORY_ID, Reason.CLASS_ID);
		nestedPortableClassBuilder.addLongField("count");
		nestedPortableClassBuilder.addUTFField("name");
		ClassDefinition nestedPortableClassDefinition = nestedPortableClassBuilder.build();
	    config.getSerializationConfig().addClassDefinition(nestedPortableClassDefinition);
		
		ClassDefinitionBuilder creativeClassDefinitionBuilder = new ClassDefinitionBuilder(PortableEchoFactory.FACTORY_ID, CreativePerformance.CLASS_ID);
		creativeClassDefinitionBuilder.addUTFField("creative");
		creativeClassDefinitionBuilder.addBooleanField("_has__reasons");
		creativeClassDefinitionBuilder.addPortableArrayField("reasons",nestedPortableClassDefinition);
		creativeClassDefinitionBuilder.addLongField("total");
		creativeClassDefinitionBuilder.addLongField("bids");
		ClassDefinition creativeClassDefinition = creativeClassDefinitionBuilder.build();
		config.getSerializationConfig().addClassDefinition(creativeClassDefinition);
		
		ClassDefinitionBuilder campaignClassDefinitioBuilder = new ClassDefinitionBuilder(PortableEchoFactory.FACTORY_ID, CampaignPerformance.CLASS_ID);
		campaignClassDefinitioBuilder.addUTFField("campaign");
		campaignClassDefinitioBuilder.addBooleanField("_has__creatives");
		campaignClassDefinitioBuilder.addPortableArrayField("creatives",creativeClassDefinition);
		campaignClassDefinitioBuilder.addLongField("total");
		campaignClassDefinitioBuilder.addLongField("bids");
		ClassDefinition campaignClassDefinition = campaignClassDefinitioBuilder.build();
		config.getSerializationConfig().addClassDefinition(campaignClassDefinition);
		
		ClassDefinitionBuilder exchangeClassDefinitionBuilder = new ClassDefinitionBuilder(PortableEchoFactory.FACTORY_ID, ExchangePerformance.CLASS_ID);
		exchangeClassDefinitionBuilder.addBooleanField("_has__campaigns");
		exchangeClassDefinitionBuilder.addPortableArrayField("campaigns",campaignClassDefinition);
		exchangeClassDefinitionBuilder.addUTFField("exchange");
		exchangeClassDefinitionBuilder.addLongField("total");
		exchangeClassDefinitionBuilder.addLongField("bids");
		ClassDefinition exchangeClassDefinition = exchangeClassDefinitionBuilder.build();
		config.getSerializationConfig().addClassDefinition(exchangeClassDefinition);
		
		ClassDefinitionBuilder echoClassDefinitionBuilder = new ClassDefinitionBuilder(PortableEchoFactory.FACTORY_ID, Echo.CLASS_ID);
		echoClassDefinitionBuilder.addUTFField("name");
		echoClassDefinitionBuilder.addBooleanField("_has__campaigns");
		echoClassDefinitionBuilder.addUTFArrayField("campaigns");
		echoClassDefinitionBuilder.addIntField("percentage");
		echoClassDefinitionBuilder.addBooleanField("stopped");
		echoClassDefinitionBuilder.addLongField("request");
		echoClassDefinitionBuilder.addLongField("bid");
		echoClassDefinitionBuilder.addLongField("nobid");
		echoClassDefinitionBuilder.addLongField("win");
		echoClassDefinitionBuilder.addLongField("error");
		echoClassDefinitionBuilder.addLongField("handled");
		echoClassDefinitionBuilder.addLongField("unknown");
		echoClassDefinitionBuilder.addIntField("loglevel");
		echoClassDefinitionBuilder.addLongField("clicks");
		echoClassDefinitionBuilder.addLongField("pixels");
		echoClassDefinitionBuilder.addDoubleField("adspend");
		echoClassDefinitionBuilder.addDoubleField("qps");
		echoClassDefinitionBuilder.addDoubleField("avgx");
		echoClassDefinitionBuilder.addLongField("fraud");
		echoClassDefinitionBuilder.addIntField("threads");
		echoClassDefinitionBuilder.addUTFField("memory");
		echoClassDefinitionBuilder.addUTFField("freeDisk");
		echoClassDefinitionBuilder.addUTFField("cpu");
		/** Summary stats by exchanges */
		// public List<Map>exchanges;
		echoClassDefinitionBuilder.addBooleanField("_has__eperform");
		echoClassDefinitionBuilder.addPortableArrayField("eperform",exchangeClassDefinition);
		echoClassDefinitionBuilder.addUTFField("ipaddress");
		echoClassDefinitionBuilder.addBooleanField("leader");
		echoClassDefinitionBuilder.addLongField("lastupdate");
		echoClassDefinitionBuilder.addLongField("total");
		echoClassDefinitionBuilder.addIntField("cores");
		echoClassDefinitionBuilder.addIntField("ncampaigns");
		echoClassDefinitionBuilder.addIntField("ecampaigns");
		echoClassDefinitionBuilder.addBooleanField("nobidreason");	
		ClassDefinition echoClassDefinition = echoClassDefinitionBuilder.build();
		config.getSerializationConfig().addClassDefinition(echoClassDefinition);	
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
		return PortableEchoFactory.FACTORY_ID;
	}

	@Override
	public int getClassId() {
		 return CLASS_ID;
	}


	@Override
	public void writePortable(PortableWriter writer) throws IOException {
		if (!(campaigns.size() == 0)) {
			writer.writeUTFArray("campaigns",campaigns.toArray(new String[campaigns.size()]));
			writer.writeBoolean("_has__campaigns", true);
		}
		writer.writeUTF("name",this.name);
		writer.writeInt("percentage",percentage);
		writer.writeBoolean("stopped",stopped);
		writer.writeLong("request",request);
		writer.writeLong("bid",bid);
		writer.writeLong("nobid",nobid);
		writer.writeLong("win",win);
		writer.writeLong("error",error);
		writer.writeLong("handled",handled);
		writer.writeLong("unknown",unknown);
		writer.writeInt("loglevel",loglevel);
		writer.writeLong("clicks",clicks);
		writer.writeLong("pixels",pixels);
		writer.writeDouble("adspend",adspend);
		writer.writeDouble("qps",qps);
		writer.writeDouble("avgx",avgx);
		writer.writeLong("fraud",fraud);
		writer.writeInt("threads",threads);
		writer.writeUTF("memory",memory);
		writer.writeUTF("freeDisk",freeDisk);
		writer.writeUTF("cpu",cpu);
		writer.writeUTF("ipaddress", ipaddress);
		writer.writeBoolean("leader", leader);
		writer.writeLong("lastupdate", lastupdate);
		writer.writeLong("total", total);
		writer.writeInt("cores", cores);
		writer.writeInt("ncampaigns", ncampaigns);
		writer.writeInt("ecampaigns", ecampaigns);
		writer.writeBoolean("nobidreason",nobidreason);
		
		if(!eperform.isEmpty()) {
			writer.writePortableArray("eperform", eperform.toArray(new Portable[eperform.size()]));
			writer.writeBoolean("_has__eperform", true);
	    } 

		/** Summary stats by exchanges */
		//public List<Map>exchanges;
	}

	@Override
	public void readPortable(PortableReader reader) throws IOException {
		if(reader.readBoolean("_has__campaigns")) {
			String [] c = reader.readUTFArray("campaigns");
			for (String campaign : c) {
				campaigns.add(campaign);
			}
		}
		
		name = reader.readUTF("name");
		percentage = reader.readInt("percentage");
		stopped = reader.readBoolean("stopped");
		request = reader.readLong("request");
		bid = reader.readLong("bid");
		nobid = reader.readLong("nobid");
		win = reader.readLong("win");
		error = reader.readLong("error");
		handled = reader.readLong("handled");
		unknown = reader.readLong("unknown");
		loglevel = reader.readInt("loglevel");
		clicks = reader.readLong("clicks");
		pixels = reader.readLong("pixels");
		adspend = reader.readDouble("adspend");
		qps = reader.readDouble("qps");
		avgx = reader.readDouble("avgx");
		fraud = reader.readLong("fraud");
		threads = reader.readInt("threads");
		memory = reader.readUTF("memory");
		freeDisk = reader.readUTF("freeDisk");
		cpu = reader.readUTF("cpu");
		ipaddress = reader.readUTF("ipaddress");
		leader = reader.readBoolean("leader");
		lastupdate = reader.readLong("lastupdate");
		total = reader.readLong("total");
		cores = reader.readInt("cores");
		ncampaigns = reader.readInt("ncampaigns");
		ecampaigns = reader.readInt("ecampaigns");
		nobidreason = reader.readBoolean("nobidreason");
		
		if(reader.readBoolean("_has__eperform")) {
			Portable[] carray = reader.readPortableArray("eperform");
	        for (Portable p:carray) {
	        	eperform.add((ExchangePerformance) p);  
	        }
	     }
	}
}
