package com.jacamars.dsp.rtb.probe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

public class CampaignPerformance {
	public String campaign;
	public long total;
	public long bids;
	public List<CreativePerformance> creatives = new ArrayList();
	
	public CampaignPerformance() {
		
	}
	
	public void writePortable(int eindex, int index, PortableWriter writer) throws IOException {
		StringBuilder key  = getKey(eindex,index);
		StringBuilder sb = new StringBuilder();
		
		writer.writeUTF(sb.append(key).append("-campaign").toString(),campaign);
		sb.setLength(0);
		writer.writeLong(sb.append(key).append("-total").toString(),total);
		sb.setLength(0);;
		writer.writeLong(sb.append(key).append("-bids").toString(),bids);
		sb.setLength(0);
		writer.writeInt(sb.append(key).append("-Ncreatives").toString(),creatives.size());
		sb.setLength(0);
		for (var i = 0; i< creatives.size();i++) {
			CreativePerformance cp = creatives.get(i);
			cp.writePortable(eindex,index,i, writer);
		}
	}
	
	public void readPortable(int eindex, int index, PortableReader reader) throws IOException {
		StringBuilder key  = getKey(eindex,index);
		StringBuilder sb = new StringBuilder();
		
		campaign = reader.readUTF(sb.append(key).append("-campaign").toString());
		sb.setLength(0);
		total = reader.readLong(sb.append(key).append("-total").toString());
		sb.setLength(0);;
		bids = reader.readLong(sb.append(key).append("-bids").toString());
		sb.setLength(0);
		var n = reader.readInt(sb.append(key).append("-Ncreatives").toString());
		sb.setLength(0);
		for (var i = 0; i< n;i++) {
			CreativePerformance cp = new CreativePerformance();
			cp.readPortable(eindex,index,i,reader);
			creatives.add(cp);
		}
	}
	
	public StringBuilder getKey(int eindex, int index) {
		StringBuilder k = new StringBuilder("exchange:").append(eindex).append("campaign:").append(index);
		return k;
	}
}
