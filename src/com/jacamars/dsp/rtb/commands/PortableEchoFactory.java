package com.jacamars.dsp.rtb.commands;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;

public class PortableEchoFactory implements PortableFactory {

    public static final int FACTORY_ID = 2;

    @Override
    public Portable create(int classId) {
        if (classId == Echo.CLASS_ID) {
            return new Echo();
        }
        return null;
    }
}
