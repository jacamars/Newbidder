package com.jacamars.dsp.rtb.tools;


import com.jacamars.dsp.rtb.commands.PixelClickConvertLog;
import com.jacamars.dsp.rtb.jmq.MessageListener;
import com.jacamars.dsp.rtb.jmq.RTopic;
import com.jacamars.dsp.rtb.pojo.BidResponse;
import com.jacamars.dsp.rtb.pojo.WinObject;

/**
 * A simple class that watches for clicks, conversions and pixels from the
 * bidders. Arguments:
 * <p>
 * [-redis host:port] Sets the redis host/port, uses localhost as default
 * <p>
 * [-watch pixels|clicks|conversions] What to watch for
 * <p>
 * eg java -jar xrtb.jar tools.WatchPixelClickConvert pixels - to watch pixel
 * loads
 * <p>
 * By default, no arguments means connect to localhost, watch for pixels, clicks
 * and conversions
 * 
 * @author Ben M. Faul
 *
 */

public class WatchPixelClickConvert {
	
	public static String BIDS =    "tcp://localhost:5571";
	public static String WINS =   "tcp://*:5572&wins";
	public static String CLICKS = "tcp://*:5573&clicks";
	public static String PIXELS = "tcp://*:5576&pixels";

	/**
	 * The main program entry.
	 * 
	 * @param args
	 *            String [] args. See the description for usage.
	 */
	public static void main(String[] args) throws Exception {
		String port = null; 
		String channel = null;
		int what = PixelClickConvertLog.CLICK;

		int i = 0;
			while (i < args.length) {
				if (args[i].equals("-h")) {
					System.out
							.println("-endpoint tcp://host:n [Set the host and port number to use]");
					System.out
							.println("-channel <channelname> [The channel to use: bids, wins, clicks, default is clicks");
					System.out
							.println("-watch                 [pixel|clicks|conversions, use when channel is clicks default is clicks]");
					System.exit(1);
				} else
				if (args[i].equals("-endpoint")) {
					port = args[i + 1];
					i += 2;
				} else
				if (args[i].equals("-channel")) {
					channel = args[i + 1];
					i += 2;
					if (channel.equals("bids"))
						what = -1;
					if (channel.equals("wins"))
						what = -2;
				} else
				if (args[i].equals("-watch")) {
					String str = args[i + 1];
					if (str.equals("clicks"))
						what = PixelClickConvertLog.CLICK;
					if (str.equals("pixels"))
						what = PixelClickConvertLog.PIXEL;
					if (str.equals("conversions"))
						what = PixelClickConvertLog.CONVERT;
					
					System.out.println("Watching: " + str);
					i += 2;
				} else {
					System.out.println("Huh? " + args[i]);
					System.exit(0);;
				}
			}
		if (channel == null) {
			channel = "bids";
			port = BIDS;
		} else {
			if (channel.equals("bids") && port == null)
				port = BIDS;
			if (channel.equals("wins") && port == null)
				port = WINS;
			if (channel.equals("pixels") && port == null)
				port = PIXELS;
			if (channel.equals("clicks") && port == null)
				port = CLICKS;
		}
		if (what > 0)
			new PixelClickConvert(port,channel, what);
		if (what == -1) 
			new BidWatch(port,channel);
		if (what == -2) 
			new WinWatch(port,channel);
	}
}

/**
 * Instantiate a connection to localhost (Redisson) Also contains the listener
 * for the pixels, clicks and conversions.
 *
 */

class PixelClickConvert {

	/** which to watch, click, convert or pixel, or all? */
	int watch;

	public PixelClickConvert(String port, String channel, int what)
			throws Exception {


		watch = what;
		RTopic topic = new RTopic(port);
		topic.subscribe(channel);
		if (channel.equals("bids")) {
			topic.addListener(new MessageListener<BidResponse>() {
				@Override
				public void onMessage(String channel, BidResponse msg) {
						try {
							String content = DbTools.mapper.writer()
									.withDefaultPrettyPrinter()
									.writeValueAsString(msg);
							System.out.println(content);
						} catch (Exception error) {
							error.printStackTrace();
						}
				}
			});
			
		}
		topic.addListener(new MessageListener<PixelClickConvertLog>() {
			@Override
			public void onMessage(String channel, PixelClickConvertLog msg) {
				if (watch == -1 || msg.type == watch) {
					try {
						String content = DbTools.mapper.writer()
								.withDefaultPrettyPrinter()
								.writeValueAsString(msg);
						System.out.println(content);
					} catch (Exception error) {
						error.printStackTrace();
					}
				}
			}
		});
	}
}

/**
 * Instantiate a connection to localhost (Redisson) Also contains the listener
 * for the pixels, clicks and conversions.
 * 
 * @param redis
 *            String. The redis host:port string.
 * @param channel
 *            String. The topic of what we are looking for.
 * @param what
 *            int. The integer type of what we are looking for.
 */

class BidWatch {
	int watch;

	public BidWatch(String port, String channel) throws Exception {

		RTopic t = new RTopic(port);
		t.subscribe(channel);
		t.addListener(new MessageListener<BidResponse>() {
			@Override
			public void onMessage(String channel, BidResponse msg) {
				try {
					String content = DbTools.mapper.writer()
							.withDefaultPrettyPrinter().writeValueAsString(msg);
					System.out.println(content);
				} catch (Exception error) {
					error.printStackTrace();
				}
			}
		});
	}
}

class WinWatch {
	int watch;

	public WinWatch(String port, String channel) throws Exception {

		RTopic t = new RTopic(port);
		t.subscribe(channel);
		t.addListener(new MessageListener<WinObject>() {
			@Override
			public void onMessage(String channel, WinObject msg) {
				try {
					String content = DbTools.mapper.writer()
							.withDefaultPrettyPrinter().writeValueAsString(msg);
					System.out.println(content);
				} catch (Exception error) {
					error.printStackTrace();
				}
			}
		});
	}
}
