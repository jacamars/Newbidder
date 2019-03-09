package com.jacamars.dsp.rtb.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.ByteArraySerializer;
import com.jacamars.dsp.rtb.common.Campaign;

import java.io.IOException;
import java.io.InputStream;

public class CustomCampaignSmileSerializer
        implements ByteArraySerializer<ObjectNode> {

    ObjectMapper mapper = new ObjectMapper(new SmileFactory());

    public int getTypeId() {
        return 6;
    }

    public void write(ObjectDataOutput out, Campaign object)
            throws IOException {
        byte[] data = mapper.writeValueAsBytes(object);
        out.write(data);
    }

    public Campaign read(ObjectDataInput in) throws IOException {
        Campaign c = mapper.readValue((InputStream) in,
                Campaign.class);
        return c;
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