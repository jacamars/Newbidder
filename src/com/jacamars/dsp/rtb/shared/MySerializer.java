package com.jacamars.dsp.rtb.shared;

import java.io.IOException;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

interface MySerializer<T> {
    Class<T> typeClass();
    T read(ObjectDataInput inp) throws IOException;
    void write(ObjectDataOutput out, T obj) throws IOException;
}
