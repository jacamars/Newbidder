package com.jacamars.dsp.rtb.pojo;

import java.util.List;

import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.probe.Probe;

/**
 * Companion ad object
 * @author ben
 *
 */

public class Banner {
	
	public List<Format> format;
	public Integer w;
	public Integer h;
	public List<Integer> btype;
	public List<Integer> battr;
	public Integer pos;
	public List<String> mimes;
	public Integer topframe;
	public List<Integer> api;
	public String id;
	
	
	public Banner() {
		
	}
	
	public boolean matched(Creative c, BidRequest br, String adId,  StringBuilder errorString, Probe probe) throws Exception {
	
		return false;
	}

}
