package com.jacamars.dsp.rtb.tools;

import com.hazelcast.core.HazelcastInstance;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.jmq.ZPublisher;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to log log4j messages to memory, for use by the web api.
 * See: https://mytechattempts.wordpress.com/2011/05/10/log4j-custom-memory-appender
 * @author Ben M. Faul
 *
 */

@Plugin(name = "ZPublisher4J2",
		category = Core.CATEGORY_NAME,
		elementType = Appender.ELEMENT_TYPE,
		printObject = true)
public final class ZPublisher4J2 extends AbstractAppender {

	/** Formatter for the date time string */
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	/** The publisher to be used by this Log4j appender */
	ZPublisher publisher;

	protected ZPublisher4J2(String name, Filter filter, String publisher) {
		super(name, filter, null);
		setPublisher(publisher);
	}
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

			HazelcastInstance hz = null;
			publisher = new ZPublisher(hz, str);
		} catch (Exception error) {
			error.printStackTrace();
			System.out.println("Log4j failed to open ZPublisher: " + str);
		}
	}

	/**
	 * Append the log to ZPublisher
	 * @param event LoggingEvent. The log event to format and send to the log receiver.
	 */
	@Override
	public void append(LogEvent event) {
		if (publisher == null)
			return;
		String name = event.getSource().getClassName() + ":" + event.getSource().getMethodName();
		//String name = event.getLocationInformation().getClassName();
		//name = name.substring(name.lastIndexOf(".")+1);
		String level = event.getLevel().toString();
		//String message = event.getRenderedMessage();
		String message = event.getMessage().getFormattedMessage();
		String line = Integer.toString(event.getSource().getLineNumber());
		//String line = event.getLocationInformation().getLineNumber();
		Map s = new HashMap();
		s.put("instance", Configuration.instanceName);
		s.put("sev", level);
		s.put("source", name);
		s.put("field", line);
		s.put("message", message);

		publisher.add(s);
	}

	@PluginFactory
	public static ZPublisher4J2 createAppender(
			@PluginAttribute("name") String name,
			@PluginAttribute("publisher") String publisher,
			@PluginElement("Filter") Filter filter) {
		if (name == null) {
			LOGGER.error("No name provided for ZPublisher4J2");
			return null;
		}
		return new ZPublisher4J2(name, filter, publisher);
	}
}