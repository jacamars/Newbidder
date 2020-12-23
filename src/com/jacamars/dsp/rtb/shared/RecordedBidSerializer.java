package com.jacamars.dsp.rtb.shared;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.FrequencyCap;

public class RecordedBidSerializer implements MySerializer<com.jacamars.dsp.rtb.common.RecordedBid>{
	
	private static ObjectMapper mapper = new ObjectMapper();
	 static {
	        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	 }

	@Override
	public Class<com.jacamars.dsp.rtb.common.RecordedBid> typeClass() {
		 return com.jacamars.dsp.rtb.common.RecordedBid.class;
	}

	@Override
	public com.jacamars.dsp.rtb.common.RecordedBid read(ObjectDataInput inp) throws IOException {
		String id = inp.readUTF();
		String capKey = inp.readUTF();
        Long capTimeout = inp.readLong();
        String capUnit = inp.readUTF();
        String price = inp.readUTF();
        String adType = inp.readUTF();
        String fqs = inp.readUTF();
        Long endtime = inp.readLong();
        
        if (capKey.equals("")) {
        	capKey = null;
        	capTimeout = 0L;
        	capUnit = null;
        }
        
        List<FrequencyCap> frequencycap = null;
        if (fqs.equals("")==false) {
        	frequencycap = (List<FrequencyCap>)mapper.readValue(fqs, List.class);
        	frequencycap = mapper.readValue(fqs,
    				mapper.getTypeFactory().constructCollectionType(List.class, FrequencyCap.class));
        } 
        
        return new com.jacamars.dsp.rtb.common.RecordedBid(id, capKey, capTimeout, capUnit, price, adType, frequencycap, endtime);
    		
	}

	@Override
	public void write(ObjectDataOutput out, com.jacamars.dsp.rtb.common.RecordedBid obj) throws IOException {
		out.writeUTF(obj.getId());
		if (obj.getCapKey()==null) {
			out.writeUTF("");
			out.writeInt(0);
			out.writeUTF("");
		} else {
			out.writeUTF(obj.getCapKey());
			out.writeLong(obj.getCapTimeout());
			out.writeUTF(obj.getCapTimeUnit());
		}
		
		out.writeUTF(obj.getPrice());
		out.writeUTF(obj.getAdType());
		
	
		if (obj.getFrequencyCap() == null) {
			out.writeUTF("");
		} else {
			String fqs = mapper.writeValueAsString(obj.getFrequencyCap());
			out.writeUTF("");
		} 
		
		out.writeLong(obj.getEndtime());
	}

}
