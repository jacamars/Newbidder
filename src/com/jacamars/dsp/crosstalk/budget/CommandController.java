package com.jacamars.dsp.crosstalk.budget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.jacamars.dsp.crosstalk.api.ApiCommand;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.blocks.SelfExpiringHashMap;

public enum CommandController {
	
	INSTANCE;
	
	/** This class's logger */
	static final Logger logger = LoggerFactory.getLogger(CommandController.class);
	
	public static final String COMMANDS = "commands";
	public static final String RESPONSES = "responses";
	
	static SelfExpiringHashMap<String,ApiCommand> myResponses = new SelfExpiringHashMap<String,ApiCommand>();
	static SelfExpiringHashMap<String,ApiCommand> sentUnack = new SelfExpiringHashMap<String,ApiCommand>();
	
	static ITopic<ApiCommand> commands;
	static ITopic<ApiCommand> responses;
	
	public static CommandController  getInstance() {
		if (commands == null) {
			  commands = RTBServer.getSharedInstance().getTopic(COMMANDS);
		      commands.addMessageListener(new MessageListenerImpl());
		      responses = RTBServer.getSharedInstance().getTopic(RESPONSES);
		}
		return INSTANCE;
	}
	
	public ApiCommand sendCommand(ApiCommand cmd, int timeout) throws Exception {
		commands.publish(cmd);
		String key = cmd.asyncid;
		ApiCommand response = null;
		
		sentUnack.put(cmd.asyncid,cmd);
		
		if (timeout == 0)
			return null;
		
		long et = System.currentTimeMillis() + timeout;
		while (System.currentTimeMillis() < et) {
			if ((response = myResponses.get(key)) != null)
				return response;
			Thread.sleep(250);
		}
		
		return null;
	}
	
    private static class MessageListenerImpl implements MessageListener<ApiCommand> {
        public void onMessage(Message<ApiCommand> m) {
        	ApiCommand r = m.getMessageObject();
        	logger.info("Received: {}",r);
        	myResponses.put(r.asyncid, r,30000);
        	
        	sentUnack.remove(r.asyncid);
        }
    }
}
