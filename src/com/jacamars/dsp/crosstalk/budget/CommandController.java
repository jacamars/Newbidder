package com.jacamars.dsp.crosstalk.budget;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;
import com.hazelcast.topic.MessageListener;
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
		      commands.addMessageListener(new CommandListener());
		      responses = RTBServer.getSharedInstance().getTopic(RESPONSES);
		      responses.addMessageListener(new ResponseListener());
		}
		return INSTANCE;
	}
	
	public ApiCommand sendCommand(ApiCommand cmd, int timeout) throws Exception {
		commands.publish(cmd);
		String key = cmd.asyncid;
		ApiCommand response = null;
		
		if (timeout == 0)
			return null;
		
		sentUnack.put(cmd.asyncid,cmd);
		
		long et = System.currentTimeMillis() + timeout;
		while (System.currentTimeMillis() < et) {
			if ((response = myResponses.get(key)) != null)
				return response;
			Thread.sleep(250);
		}
		
		return null;
	}
}

/**
 * The Hazelcast commands message listener. If you are the leader and you receive this - execute it and send
 * the command back on the responses channel.
 * the value. Otherwise just return.
 * @author Ben M. Faul
 *
 */
class CommandListener implements MessageListener<ApiCommand> {
    public void onMessage(Message<ApiCommand> m) {
    	ApiCommand r = m.getMessageObject();
    	CommandController.logger.info("Commands Received: {}",r);
    	
    	if (!RTBServer.isLeader()) {
    		return;
    	}
    	
    	r.execute();
    	CommandController.responses.publish(r);
    }
}

/**
 * The Hazelcast message listener. If you are the leader and you receive this - execute it and return
 * the value. Otherwise just return.
 * @author Ben M. Faul
 *
 */
class ResponseListener implements MessageListener<ApiCommand> {
    public void onMessage(Message<ApiCommand> m) {
    	ApiCommand r = m.getMessageObject();
    	CommandController.logger.info("Responses Received: {}",r);   	
    	CommandController.myResponses.put(r.asyncid, r,30000);
    	CommandController.sentUnack.remove(r.asyncid);
		
    }
}
