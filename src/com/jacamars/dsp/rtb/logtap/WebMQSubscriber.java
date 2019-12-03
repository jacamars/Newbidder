package com.jacamars.dsp.rtb.logtap;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.jmq.MessageListener;
import com.jacamars.dsp.rtb.jmq.RTopic;
import com.jacamars.dsp.rtb.tools.Env;

public class WebMQSubscriber {

	HttpServletResponse response;
    ObjectMapper mapper = new ObjectMapper();
    static volatile int count  = 1;
    RTopic chan;
    volatile boolean running = true;
    volatile int ticks = 0;

    public WebMQSubscriber(HttpServletResponse response, String topic) throws Exception {
        // Prepare our context and subscriber
    	this.response = response;

        String address = "kafka://[$BROKERLIST]&topic="+topic+"&groupid=webreader"+count;
        address = Env.substitute(address);
        channelHandler(response,address);
        count++;
        
        
    }
    
    public WebMQSubscriber channelHandler(HttpServletResponse response, String address) throws Exception  {
        chan = new com.jacamars.dsp.rtb.jmq.RTopic(address);
        chan.addListener(new MessageListener<Object>() {
            @Override
            public void onMessage(String channel, Object data) {
                try {
                    String contents = mapper.writeValueAsString(data);
                    response.getWriter().println(contents);
                    response.flushBuffer();
                    ticks = 0;
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
    
    public void run() {
    	  while (running) {
              try {
                  Thread.sleep(1000);
                  if (ticks++ > 15) {
                	  System.out.println("TICKS > 15");
                	  ticks = 0;
                	  response.getWriter().println(" ");
                      response.flushBuffer();
                  }
              } catch (InterruptedException e) {
                  e.printStackTrace();
                  return;
              } catch (Exception x) {
            	  // client shut down
            	  return;
              }
          }
    }
}
