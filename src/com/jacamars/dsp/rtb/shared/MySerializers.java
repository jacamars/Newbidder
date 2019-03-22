package com.jacamars.dsp.rtb.shared;


import java.io.IOException;

import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

/**
 * Non-portable serializers
 * @author Ben M. Faul
 *
 */
public enum MySerializers implements StreamSerializer {
    RecordedBid(new RecordedBidSerializer()),
	RecordedVideo(new RecordedVideoSerializer()),
	RecordedMisc(new RecordedMiscSerializer());
	
    private final MySerializer serializer;
    private MySerializers(MySerializer serializer) {
        this.serializer = serializer;
    }
    
    public int getTypeId() {
        // Leverage ordinal to keep track of ids.
        // Hazelcast requires > 0, so we add 1
        return ordinal() + 1;
    }
    
    public void destroy() {} // Nothing to clean up.
    public Object read(ObjectDataInput inp) throws IOException {
        return this.serializer.read(inp); // Forward to implementation
    }
    public void write(ObjectDataOutput out, Object obj) throws IOException {
        this.serializer.write(out, obj); // Forward to implementation
    }
    /** Register all serializers. */
    public static void register(SerializationConfig config) {
        for (MySerializers ser : MySerializers.values()) {
            SerializerConfig sc = new SerializerConfig();
            sc.setImplementation(ser).setTypeClass(ser.serializer.typeClass());
            config.addSerializerConfig(sc);
        }
    }
}