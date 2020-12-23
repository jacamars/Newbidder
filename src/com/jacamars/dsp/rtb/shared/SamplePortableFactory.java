package com.jacamars.dsp.rtb.shared;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;

public class SamplePortableFactory implements PortableFactory {

    public static final int FACTORY_ID = 1;

    @Override
    public Portable create(int classId) {
        if (classId == Customer.CLASS_ID) {
            return new Customer();
        }
        return null;
    }
}
