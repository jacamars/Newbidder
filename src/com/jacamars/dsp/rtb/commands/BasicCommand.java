package com.jacamars.dsp.rtb.commands;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.common.Configuration;

/**
 * This is the basic command and response object in POJO form of commands is sent over REDIS
 * @author Ben M. Faul
 *
 */
public class BasicCommand {
	/** a name */
	public String name = null;
	/** Default command, -1 means uninitialized. Extending objects need to set this. */
	public Integer cmd = -1;
	/** The instance name obtained from the configurarion */
	public String from = "na";
	/** The id of whom the response is sent to */
	public String to = "*";
	/** A unique ID used for this command */
	public String id = UUID.randomUUID().toString();
	/** The message that is associated with the command */
	public String msg = "undefined";
	/** The return status code, assume the best */
	public String status = "ok";
	/** The type of the return, we assume status */
	public String logtype = "status";
	/** timestamp */
	public long timestamp;
	// The price field
	public double price;
	
	/** The target, if any. Corresponds to instance name. If null, all bidders respond, otherwise, only those bidders matching will execute ans respond */
	public String target = "na";
	
	/**
	 * Empty constructor. Sets the command from to the bidder's instance name.
	 * own command/command response.
	 */
	public BasicCommand() {

	}
	
	/**
	 * Constructor, specifying an arbitrary name.
	 * @param to the instance string of the sender.
	 */
	public BasicCommand(String to) {
		this.to = to;
	}
	
	/**
	 * Returns a JSON representation of the command/command response.
	 */
	
	@Override
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString;
		try {
			jsonString = mapper.writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return jsonString;
	}
	
	/** 
	 * Set the command target.
	 * @param target String. the REGEX target for the command.
	 */
	public void setTarget(String target) {
		this.target = target;
	}
}
