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

public class RecordedVideoSerializer implements MySerializer<com.jacamars.dsp.rtb.common.RecordedVideo>{
	
	private static ObjectMapper mapper = new ObjectMapper();
	 static {
	        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	 }

	@Override
	public Class<com.jacamars.dsp.rtb.common.RecordedVideo> typeClass() {
		 return com.jacamars.dsp.rtb.common.RecordedVideo.class;
	}

	@Override
	public com.jacamars.dsp.rtb.common.RecordedVideo read(ObjectDataInput inp) throws IOException {
		String id = inp.readUTF();
		String name = inp.readUTF();
        Long endtime = inp.readLong();
      
        return new com.jacamars.dsp.rtb.common.RecordedVideo(id, name, endtime);
    		
	}

	@Override
	public void write(ObjectDataOutput out, com.jacamars.dsp.rtb.common.RecordedVideo obj) throws IOException {
		out.writeUTF(obj.getId());
		out.writeUTF(obj.getName());
		out.writeLong(obj.getEndtime());
	}

}
