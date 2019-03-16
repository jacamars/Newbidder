package com.jacamars.dsp.rtb.probe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.jacamars.dsp.rtb.commands.PortableEchoFactory;

public class CampaignPerformance implements Portable {
	public static final int CLASS_ID = 4;
	public String campaign;
	public long total;
	public long bids;
	public List<CreativePerformance> creatives = new ArrayList();
	
	public CampaignPerformance() {
		
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
		writer.writeUTF("campaign",campaign);
		writer.writeLong("total",total);
		writer.writeLong("bids",bids);	
		
		 if(!creatives.isEmpty()) {
			 writer.writePortableArray("creatives", creatives.toArray(new Portable[creatives.size()]));
			 writer.writeBoolean("_has__creatives", true);
	     }
	}

	@Override
	public void readPortable(PortableReader reader) throws IOException {
		campaign = reader.readUTF("campaign");
		total = reader.readLong("total");
		bids = reader.readLong("bids");
		
		if(reader.readBoolean("_has__creatives")) {
			Portable[] carray = reader.readPortableArray("creatives");
	        for (Portable p:carray) {
	        	creatives.add((CreativePerformance) p);  
	        }
	     }
	}
}
