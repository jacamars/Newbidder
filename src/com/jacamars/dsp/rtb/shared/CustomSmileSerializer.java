package com.jacamars.dsp.rtb.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.ByteArraySerializer;

import java.io.IOException;
import java.io.InputStream;

public class CustomSmileSerializer
        implements ByteArraySerializer<ObjectNode> {

    ObjectMapper mapper = new ObjectMapper(new SmileFactory());

    public int getTypeId() {
        return 5;
    }

    public void write(ObjectDataOutput out, ObjectNode object)
            throws IOException {
        byte[] data = mapper.writeValueAsBytes(object);
        System.out.println("Size is " + data.length);
        out.write(data);
    }

    public ObjectNode read(ObjectDataInput in) throws IOException {
        return mapper.readValue((InputStream) in,
                ObjectNode.class);
    }

    public void destroy() {
    }

    @Override
    public byte[] write(ObjectNode customer) throws IOException {
        return mapper.writeValueAsBytes(customer);
    }

    @Override
    public ObjectNode read(byte[] bytes) throws IOException {
        return mapper.readValue(bytes, ObjectNode.class);
    }
}