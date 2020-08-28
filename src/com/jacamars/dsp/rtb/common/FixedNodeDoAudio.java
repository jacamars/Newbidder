package com.jacamars.dsp.rtb.common;

import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.Impression;
import com.jacamars.dsp.rtb.probe.Probe;

import java.util.List;

/**
 * Fixed node implements a chunk of fixed code, once found in the Creative
 * This handles video.
 */
public class FixedNodeDoAudio extends Node {

    public FixedNodeDoAudio() {
        super();
        name = "FixedNodeDoVideo";
    }

    @Override
    public boolean test(BidRequest br, Creative creative, String adId, Impression imp,
                        StringBuilder errorString, Probe probe, List<Deal> deals) throws Exception {
    	
        if (imp.audio != null) {                                                // FixedNodeDoVideo
        	return imp.audio.matched(creative,  br, adId,  errorString,  probe);
        }

        return false;
    }
}
