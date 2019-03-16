package com.jacamars.dsp.rtb.probe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.jacamars.dsp.rtb.commands.PortableEchoFactory;

public class CreativePerformance implements Portable {
	public static final int CLASS_ID = 5;
	public String creative;
	public List<Reason> reasons = new ArrayList();
	public long total;
	public long bids;
	
	public CreativePerformance() {
		
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
		writer.writeUTF("creative",creative);
		writer.writeLong("total",total);
		writer.writeLong("bids",bids);	
		
		 if(!reasons.isEmpty()) {
			 writer.writePortableArray("reasons", reasons.toArray(new Portable[reasons.size()]));
	         writer.writeBoolean("_has__reasons", true);
	     } else {
	    	 writer.writeBoolean("_has__reasons", false);
	     }
		
	}

	@Override
	public void readPortable(PortableReader reader) throws IOException {
		creative = reader.readUTF("creative");
		total = reader.readLong("total");
		bids = reader.readLong("bids");
		
		if(reader.readBoolean("_has__reasons")) {
			Portable[] carray = reader.readPortableArray("reasons");
	        for (Portable p:carray) {
	        	reasons.add((Reason) p);  
	        }
	     }
		
	}
}
