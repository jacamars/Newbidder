package com.jacamars.dsp.rtb.probe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

public class ExchangePerformance {
	public String exchange;
	public long total;
	public long bids;
	public List<CampaignPerformance> campaigns = new ArrayList();
	
	public ExchangePerformance() {
		
	}
	
	public StringBuilder getKey(int index) {
		StringBuilder k = new StringBuilder("exchange:").append(index);
		return k;
	}
	
	public void writePortable(int index, PortableWriter writer) throws IOException {
		StringBuilder key  = getKey(index);
		StringBuilder sb = new StringBuilder();
		
		writer.writeUTF(sb.append(key).append("-name").toString(),exchange);
		sb.setLength(0);
		writer.writeLong(sb.append(key).append("-total").toString(),total);
		sb.setLength(0);;
		writer.writeLong(sb.append(key).append("-bids").toString(),bids);
		sb.setLength(0);
		writer.writeInt(sb.append(key).append("-Ncampaigns").toString(),campaigns.size());
		sb.setLength(0);
		for (var i = 0; i< campaigns.size();i++) {
			CampaignPerformance cp = campaigns.get(i);
			cp.writePortable(index,i, writer);
		}
	}
	
	public void readPortable(int index, PortableReader reader) throws IOException {
		StringBuilder key  = getKey(index);
		
		StringBuilder sb = new StringBuilder();
		exchange = reader.readUTF(sb.append(key).append("-name").toString());
		sb.setLength(0);
		total = reader.readLong(sb.append(key).append("-total").toString());
		sb.setLength(0);;
		bids = reader.readLong(sb.append(key).append("-bids").toString());
		sb.setLength(0);
		var n = reader.readInt(sb.append(key).append("-Ncampaigns").toString());
		sb.setLength(0);
		
		for (var i = 0; i< n;i++) {
			CampaignPerformance cp = new CampaignPerformance(); 
			cp.readPortable(index,i, reader);
		}
	}
}
