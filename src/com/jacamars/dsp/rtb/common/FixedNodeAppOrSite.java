package com.jacamars.dsp.rtb.common;

import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.Impression;
import com.jacamars.dsp.rtb.probe.Probe;

import java.util.ArrayList;
import java.util.List;

/**
 * Fixed node implements a chunk of fixed code, once found in the Creative
 * This handles video.
 */
public class FixedNodeAppOrSite extends Node {

	List<String> type = new ArrayList<>();
    public FixedNodeAppOrSite(String type) {
        super();
        this.type.add(type);
        name = "FixedNodeAppOrSite";
        this.hierarchy = type;
    }

    @Override
    public boolean test(BidRequest br, Creative creative, String adId, Impression imp,
                        StringBuilder errorString, Probe probe, List<Deal> deals) throws Exception {
    	
        if (br.walkTree(type) == null) {    
        	  probe.process(br.getExchange(), adId, creative.impid, Probe.SITE_OR_APP_DOMAIN);
              if (errorString != null)
                  errorString.append(Probe.SITE_OR_APP_DOMAIN);
              falseCount.incrementAndGet();
        	return false;
        }

        return true;
    }
}
