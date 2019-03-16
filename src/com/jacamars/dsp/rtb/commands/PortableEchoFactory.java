package com.jacamars.dsp.rtb.commands;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import com.jacamars.dsp.rtb.probe.CampaignPerformance;
import com.jacamars.dsp.rtb.probe.CreativePerformance;
import com.jacamars.dsp.rtb.probe.ExchangePerformance;
import com.jacamars.dsp.rtb.probe.Reason;

public class PortableEchoFactory implements PortableFactory {

    public static final int FACTORY_ID = 2;

    @Override
    public Portable create(int classId) {
        if (classId == Echo.CLASS_ID) {
            return new Echo();
        } else
        if (classId == ExchangePerformance.CLASS_ID) {
        	return new ExchangePerformance();
        } else
        if (classId == CampaignPerformance.CLASS_ID) {
        	return new CampaignPerformance();
        } else
        if (classId == CreativePerformance.CLASS_ID) {
        	return new CreativePerformance();
        } else
        if (classId == Reason.CLASS_ID) {
        	return new Reason();
        }
        return null;
    }
}
