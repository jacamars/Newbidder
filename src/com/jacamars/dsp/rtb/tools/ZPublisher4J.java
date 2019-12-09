package com.jacamars.dsp.rtb.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.jmq.ZPublisher;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A class to log log4j messages to another publisher (in the case of the bidder, this is usually to kafka logs topic)/
 * See: https://mytechattempts.wordpress.com/2011/05/10/log4j-custom-memory-appender
 * @author Ben M. Faul
 *
 */

public class ZPublisher4J extends AppenderSkeleton {
	/** Formatter for the date time string */
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	/** The publisher to be used by this Log4j appender */
	ZPublisher publisher;
	
	/**
	 * Set the Publisher using the url string
	 * @param str String. The ZPublisher string.
	 */
	public void setPublisher(String str) {
		try {
			str = Env.substitute(str);

			if (str == null) {    // no kafka configured, ignore it.
				return;
			}

			publisher = new ZPublisher(RTBServer.getSharedInstance(),str);
		} catch (Exception error) {
			error.printStackTrace();
			System.out.println("Log4j failed to open ZPublisher: " + str);
		}
	}
	/**
	 * Close the appender
	 */
	public synchronized void close() {
		if (this.closed) {
			return;
		}
		this.closed = true;
	}

	/**
	 * Indicate we want the layout
	 */
	public boolean requiresLayout() {
		return true;
	}

	/**
	 * Make sure we can append
	 * @return boolean. Returns true if we are accepting logs.
	 */
	protected boolean checkEntryConditions() {
		if (this.closed) {
			return false;
		}
		return true;
	}

	/**
	 * Append the log to ZPublisher
	 * @param event LoggingEvent. The log event to format and send to the log receiver.
	 */
	protected void append(LoggingEvent event) {
		if (publisher == null)
			return;
		String name = event.getLocationInformation().getClassName();
		name = name.substring(name.lastIndexOf(".")+1);
		String level = event.getLevel().toString();
		String message = event.getRenderedMessage();
		String line = event.getLocationInformation().getLineNumber();
		var s = new HashMap<String,String>();
		s.put("instance",Configuration.instanceName);
		s.put("sev", level);
		s.put("source", name);
		s.put("field", line);
		s.put("message", message);

		publisher.add(s);
		
		/**
		 * Copy high severity alarms to the member's hazelcast record
		 */
		if (level.equals("ERROR")) {
			s.put("time", new Date().toString());
			RTBServer.events.add(s);
		}
	}
}