package com.jacamars.dsp.rtb.tools;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by ben on 12/23/17.
 */
public class WatchKafka {
    int count = 0;
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String [] args) throws Exception {

        String address = "kafka://[$BROKERLIST]&topic=logs";

        if (args.length > 0)
            address = args[0];

        address = Env.substitute(address);

        System.out.println("WatchKafka starting: " + address);
        new WatchKafka(address);
    }

    public WatchKafka(String address)throws Exception  {
        com.jacamars.dsp.rtb.jmq.RTopic channel = new com.jacamars.dsp.rtb.jmq.RTopic(address);
        channel.addListener(new com.jacamars.dsp.rtb.jmq.MessageListener<Object>() {
            @Override
            public void onMessage(String channel, Object data) {
            	try {
            		String sdata = mapper.writeValueAsString(data);
            		System.out.println(channel + " [" + count + "] = " + sdata);
            	} catch (Exception error) {
            		System.out.println(channel + " [" + count + "] = " + data);
            	}
                count++;
            }
        });

    }
}
