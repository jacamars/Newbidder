package com.jacamars.dsp.rtb.probe;

import java.io.IOException;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.jacamars.dsp.rtb.shared.PortableJsonFactory;

public class Reason implements Portable {
	public static final int CLASS_ID = 6;
	public String name;
	public long count;
	
	public Reason() {
		
	}

	@Override
	public int getFactoryId() {
		return PortableJsonFactory.FACTORY_ID;
	}

	@Override
	public int getClassId() {
		return CLASS_ID;
	}

	@Override
	public void writePortable(PortableWriter writer) throws IOException {
		writer.writeUTF("name",name);
		writer.writeLong("count",count);
	}

	@Override
	public void readPortable(PortableReader reader) throws IOException {
		name = reader.readUTF("name");
		count = reader.readLong("count");	
	}
}
