package com.jacamars.dsp.logtap;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.jmq.MessageListener;
import com.jacamars.dsp.rtb.jmq.RTopic;
import com.jacamars.dsp.rtb.tools.Env;

public class WebMQSubscriber {

    ObjectMapper mapper = new ObjectMapper();
    static volatile int count  = 1;
    RTopic chan;
    volatile boolean running = true;

    public WebMQSubscriber(HttpServletResponse response, String topic) throws Exception {
        // Prepare our context and subscriber

        String address = "kafka://[$BROKERLIST]&topic="+topic+"&groupid=webreader"+count;
        address = Env.substitute(address);
        count++;
        chan = new com.jacamars.dsp.rtb.jmq.RTopic(address);
        chan.addListener(new MessageListener<Object>() {
            @Override
            public void onMessage(String channel, Object data) {
            	System.out.println("====>" + data);
                try {
                    String contents = mapper.writeValueAsString(data);
                    response.getWriter().println(contents);
                    response.flushBuffer();
                } catch (Exception e) {
                    // The other side closed, we are outta here!
                    chan.shutdown();
                    running = false;
                    return;
                }
            }
        });
        while (running) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
