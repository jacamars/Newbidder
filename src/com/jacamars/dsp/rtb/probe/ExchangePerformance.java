package com.jacamars.dsp.rtb.probe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.jacamars.dsp.rtb.commands.PortableEchoFactory;

public class ExchangePerformance implements Portable {
	public static final int CLASS_ID = 3;
	public String exchange;
	public long total;
	public long bids;
	public List<CampaignPerformance> campaigns = new ArrayList();
	
	public ExchangePerformance() {
		
	}

	@Override
	public int getFactoryId() {
		return PortableEchoFactory.FACTORY_ID;
	}

	@Override
	public int getClassId() {
		// TODO Auto-generated method stub
		return CLASS_ID;
	}

	@Override
	public void writePortable(PortableWriter writer) throws IOException {
		
		writer.writeUTF("exchange",exchange);
		writer.writeLong("total",total);
		writer.writeLong("bids",bids);
		
		if(!campaigns.isEmpty()) {
			writer.writePortableArray("campaigns", campaigns.toArray(new Portable[campaigns.size()]));
			writer.writeBoolean("_has__campaigns", true);
	    } 
	}

	@Override
	public void readPortable(PortableReader reader) throws IOException {
		exchange = reader.readUTF("exchange");
		total = reader.readLong("total");
		bids = reader.readLong("bids");
		
		if(reader.readBoolean("_has__campaigns")) {
			Portable[] carray = reader.readPortableArray("campaigns");
	        for (Portable p:carray) {
	        	campaigns.add((CampaignPerformance) p);  
	        }
	     } 
	}
}
