package com.jacamars.dsp.rtb.shared;

import com.hazelcast.nio.serialization.Portable;

import com.hazelcast.nio.serialization.PortableFactory;
import com.jacamars.dsp.crosstalk.budget.AtomicBigDecimal;
import com.jacamars.dsp.rtb.commands.Echo;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.probe.Reason;

public class PortableJsonFactory implements PortableFactory {

    public static final int FACTORY_ID = 2;

    @Override
    public Portable create(int classId) {
        if (classId == Echo.CLASS_ID) {
            return new Echo();
        } 
        if (classId == Campaign.CLASS_ID) {
        	return new Campaign();
        }
        if (classId == AtomicBigDecimal.CLASS_ID) {
        	return new AtomicBigDecimal();
        }
        return null;
    }
}
