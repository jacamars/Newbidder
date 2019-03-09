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

public class RecordedMiscSerializer implements MySerializer<RecordedMisc>{
	
	private static ObjectMapper mapper = new ObjectMapper();
	 static {
	        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	 }

	@Override
	public Class<RecordedMisc> typeClass() {
		 return RecordedMisc.class;
	}

	@Override
	public RecordedMisc read(ObjectDataInput inp) throws IOException {
		String id = inp.readUTF();
		String value = inp.readUTF();
        Long endtime = inp.readLong();
      
        Object oj = mapper.readValue(value, Object.class);
        return new RecordedMisc(id, oj, endtime);
    		
	}

	@Override
	public void write(ObjectDataOutput out, RecordedMisc obj) throws IOException {
		out.writeUTF(obj.getId());
		
		Object oj = obj.getValue();
		String str = mapper.writeValueAsString(oj);
		
		out.writeUTF(str);
		out.writeLong(obj.getEndtime());
	}

}
