package com.jacamars.dsp.rtb.shared;

import java.util.concurrent.TimeUnit;

import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.bidder.RTBServer;

/**
 * A class that handles timebase synchronization across the bidder instances.
 * @author ben
 *
 */
public enum SharedTimer implements Runnable, WatchInterface {

	INSTANCE;

	String key;
	Long value;
	int interval;
	Thread me;

	public static SharedTimer getInstance(String key, int interval) {
		INSTANCE.key = key;
		INSTANCE.interval = interval;

		INSTANCE.start();
		return INSTANCE;
	}
	
	public static SharedTimer getInstance() {
		return INSTANCE;
	}

	public void start() {
		try {
			BidCachePool.getInstance().addWatch(BidCachePool.MISC, key, this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		me = new Thread(this);
		me.start();
	}

	public long getValue() {
		return value;
	}

	@Override
	public void run() {
		try {
			while (BidCachePool.getInstance() == null)
				Thread.sleep(1000);
			Thread.sleep(1000);
			bump();
			value = BidCachePool.getInstance().getAsNumber(key);
			Controller.getInstance().setMemberStatus();
			while (true) {
				TimeUnit.MILLISECONDS.sleep(interval);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void callback(String category, String key) {
		try {
			Controller.getInstance().setMemberStatus();
			bump();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void bump() {
		if (RTBServer.isLeader()) {
			Long newValue = System.currentTimeMillis();
			BidCachePool.getInstance().set(key, newValue, interval);
		}
	}

}
