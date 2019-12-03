package com.jacamars.dsp.rtb.logtap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.jacamars.dsp.rtb.jmq.MessageListener;
import com.jacamars.dsp.rtb.pojo.BidResponse;

public class ShortSubscriber extends WebMQSubscriber {
	String topic;
	public ShortSubscriber(HttpServletResponse response, String topic) throws Exception {
		super(response, topic);
		this.topic = topic;
      //  response.getWriter().println("{\"a\":100}");
     //   response.flushBuffer();
	}
	
	@Override
	 public  WebMQSubscriber channelHandler(HttpServletResponse response, String address) throws Exception  {
	        chan = new com.jacamars.dsp.rtb.jmq.RTopic(address);
	        chan.addListener(new MessageListener<Object>() {
	            @Override
	            public void onMessage(String channel, Object data) {
	            	System.out.println("====>" + data);
	            	Map m = null;
	                try {
	                	switch(topic) {
	                	case "bids":
	                		BidResponse br = (BidResponse)data;
	                		m = new HashMap();
	                		m.put("x", br.lat);
	                		m.put("y", br.lon);
	                		m.put("price", br.cost);
	                		break;
	                	case "wins":
	                		break;
	                	case "conversions":
	                		break;
	                	}
	                	if (m == null)
	                		return;
	                	
	                    String contents = mapper.writeValueAsString(m);
	                    System.out.println("------ web ---->" + contents);
	                    response.getWriter().println(contents);
	                    response.flushBuffer();
	                    ticks = 0;
	                    return;
	                } catch (Exception e) {
	                    // The other side closed, we are outta here!
	                    chan.shutdown();
	                    running = false;
	                    return;
	                }
	            }
	        });
	        return this;
	    }
	}
