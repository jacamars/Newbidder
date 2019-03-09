package com.jacamars.dsp.rtb.tools;

import java.util.Map;

import com.jacamars.dsp.rtb.commands.BasicCommand;
import com.jacamars.dsp.rtb.jmq.MessageListener;
import com.jacamars.dsp.rtb.jmq.RTopic;;

/**
 * A simple class that sends a log change message to the rtb.
 * <p>
 * Usage: LogCommand [-redis host:port] [-level n] [-to 'instance-name']
 * </p>
 * <p>
 * Defaults: -redis localhost:6379 -level 2 -to '*'
 *
 * @author Ben M. Faul
 */

public class WatchLogs implements MessageListener<BasicCommand> {
    static String endpoint;

    /**
     * Main entry point, see description for usage.
     *
     * @param args String[]. The array of arguments.
     */
    public static void main(String[] args) throws Exception {
        String endpoint = "tcp://*:5580";
        int i = 0;
        WatchLogs tool = null;
        if (args.length > 0) {
            while (i < args.length) {
                if (args[i].equals("-endpoint")) {
                    endpoint = args[i + 1];
                    i += 2;
                } else {
                    System.err.println("Unknown directive: " + args[i]);
                    System.exit(1);
                }
            }
        }
        tool = new WatchLogs(endpoint);
    }

    /**
     * Instantiate a connection to localhost
     * Also contains the listener for responses.
     */
    public WatchLogs(String endpoint) throws Exception {
        RTopic responses = new RTopic(endpoint);
        responses.subscribe("commands");
        responses.addListener(this);
    }

    @Override
    public void onMessage(String channel, BasicCommand msg) {
        try {
            String content = DbTools.mapper
                    .writer()
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(msg);
            System.out.println("<<<<<" + content);
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
}
