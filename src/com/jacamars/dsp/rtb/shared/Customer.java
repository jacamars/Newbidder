package com.jacamars.dsp.rtb.shared;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

public class Customer implements Portable {
    public static final int CLASS_ID = 1;

    public String name;
    public int id;
    public Date lastOrder;
    public List<String> list;

    @Override
    public int getFactoryId() {
        return SamplePortableFactory.FACTORY_ID;
    }

    @Override
    public int getClassId() {
        return CLASS_ID;
    }

    @Override
    public void writePortable(PortableWriter writer) throws IOException {
        writer.writeInt("id", id);
        writer.writeUTF("name", name);
        writer.writeLong("lastOrder", lastOrder.getTime());
        var size = 0;
        if (list != null) {
        	size = list.size();
        }
       
        writer.writeInt("size", size);
        for (var i=0;i<size;i++) {
        	writer.writeUTF("list:"+i,list.get(i));
        }
    }

    @Override
    public void readPortable(PortableReader reader) throws IOException {
        id = reader.readInt("id");
        name = reader.readUTF("name");
        lastOrder = new Date(reader.readLong("lastOrder"));
        var size = reader.readInt("size");
        for (var i=0;i<size;i++) {
        	if (list == null)
        		list = new ArrayList();
        	list.add(reader.readUTF("list:"+i));
        }
    }
}
