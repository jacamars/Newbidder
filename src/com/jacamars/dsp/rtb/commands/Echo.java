package com.jacamars.dsp.rtb.commands;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.probe.CreativePerformance;
import com.jacamars.dsp.rtb.probe.ExchangePerformance;
import com.jacamars.dsp.rtb.shared.SamplePortableFactory;
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
	public long pixel;
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
		writer.writeInt("Lcampaigns",campaigns.size());
		for (var i=0;i<campaigns.size();i++){
			writer.writeUTF("campaigns:"+i, campaigns.get(i));
		}
		writer.writeUTF("name", Configuration.getInstance().instanceName);
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
		writer.writeLong("pixel",pixel);
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

		writer.writeInt("Nperformance", eperform.size());
		for (int i=0;i<eperform.size();i++) {
			ExchangePerformance e = eperform.get(i);
			e.writePortable(i, writer);
		}

		/** Summary stats by exchanges */
		//public List<Map>exchanges;
		/** Campaign/creative performance map */
		//public List cperform;
	}

	@Override
	public void readPortable(PortableReader reader) throws IOException {
		var size = reader.readInt("Lcampaigns");
		for (var i=0; i < size; i++) {
			campaigns.add(reader.readUTF("campaigns:"+i));
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
		pixel = reader.readLong("pixel");
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
		
		var n = reader.readInt("Nperformance");
		for (int i=0;i<n;i++) {
			ExchangePerformance p = new ExchangePerformance();
			p.readPortable(i, reader);
			eperform.add(p);
		}
	}
}
