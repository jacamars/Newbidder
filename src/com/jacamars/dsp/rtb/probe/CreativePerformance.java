package com.jacamars.dsp.rtb.probe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

public class CreativePerformance {
	public String creative;
	public List<Reason> reasons = new ArrayList();
	public long total;
	public long bids;
	
	public CreativePerformance() {
		
	}
	
	public void writePortable(int index, PortableWriter writer) throws IOException {
		StringBuilder key  = getKey(index);
		StringBuilder prefix = getPrefix(index);
		
		StringBuilder sb = new StringBuilder();
		
		writer.writeUTF(sb.append(key).append("-creative").toString(),creative);
		sb.setLength(0);
		writer.writeLong(sb.append(key).append("-total").toString(),total);
		sb.setLength(0);;
		writer.writeLong(sb.append(key).append("-bids").toString(),bids);
		sb.setLength(0);
		writer.writeInt(sb.append(key).append("-Nreasons").toString(),reasons.size());
		sb.setLength(0);
		for (var i = 0; i< reasons.size();i++) {
			String skey = sb.append(prefix).append("-").append(i).append("-name").toString();
			Reason r = reasons.get(i);
			writer.writeUTF(skey, r.name);
			sb.setLength(0);
			skey = sb.append(prefix).append("-").append(i).append("-count").toString();
			writer.writeLong(skey, r.count);
			sb.setLength(0);
		}
		
	}
	
	public StringBuilder getKey(int index) {
		StringBuilder k = new StringBuilder("cperf:").append(index);
		return k;
	}
	
	public StringBuilder getPrefix(int index) {
		StringBuilder k = new StringBuilder("cerf:").append(index).append("-reason");
		return k;
	}
	
	public void readPortable(int index, PortableReader reader) throws IOException {
		StringBuilder key  = getKey(index);
		StringBuilder prefix = getPrefix(index);
		
		StringBuilder sb = new StringBuilder();
		
		creative = reader.readUTF(sb.append(key).append("-creative").toString());
		sb.setLength(0);
		total = reader.readLong(sb.append(key).append("-total").toString());
		sb.setLength(0);;
		bids = reader.readLong(sb.append(key).append("-bids").toString());
		sb.setLength(0);
		var n = reader.readInt(sb.append(key).append("-Nreasons").toString());
		sb.setLength(0);
		for (var i = 0; i< reasons.size();i++) {
			String skey = sb.append(prefix).append("-").append(i).append("-name").toString();
			Reason r = new Reason();
			r.name = reader.readUTF(skey);
			sb.setLength(0);
			skey = sb.append(prefix).append("-").append(i).append("-count").toString();
			r.count = reader.readLong(skey);
			reasons.add(r);
			sb.setLength(0);
		}
		
	}
}
