package com.jacamars.dsp.rtb.shared;

import com.jacamars.dsp.rtb.common.RecordedBid;
import com.jacamars.dsp.rtb.pojo.BidRequest;

/**
 * A class that implements a frequency governer. The first concept is, we have a SelfExpiringHashSet that adds a key
 * to the Set. This key will expire in 1 second. The next bid that comes in: If the key still exists, then we no bid
 * on that campaign; if the key is no longer present it can be considered for a bid.
 * <p>
 * When a key is added, the class will transmit the key to any subscribers to this 0MQ port. Thus multiple bidders can
 * share the keys across them. So if a user gets a bid, then all other bidders know about it. Then if within 1 second a
 * bid comes in and this key matches, the second bidder will not bid on it.
 * <p>
 * Created by Ben M. Faul on 10/4/17.
 */
public class FrequencyGoverner {

    private volatile FreqSetCache eset = FreqSetCache.getInstance();

    /**
     * Use this flag to turn off the contains() so it always returns false. Good for testing.
     */
    public static volatile boolean  silent = false;

    /**
     * Useful for debugging
     */
    public String name;

    /**
     * Object timeout in milli seconds
     */
    long timeout = 900;

    /**
     * Set a frequency governer using a list of bidders, and on  specified porta. Used for debugging on the same 0MQ system.
     *
     * @param timeout   long. The timeout to use in ms., if the default is not used.
     * @throws Exception on 0MQ errors.
     */
    public FrequencyGoverner(long timeout) throws Exception {
        this.timeout = timeout;
    }

    /**
     * Empty constructor for JSON
     */
    public FrequencyGoverner() {

    }

    /**
     * Add a string value to the Set.
     *
     * @param id String. The value to add.
     */
    public void add(String id) {
        eset.add(id, timeout);
    }

    /**
     * Add a string value to the set, make a key from the campaign + the bid request synthkey
     *
     * @param camp String. The campaign id.
     * @param br   BidRequest. The bid request providing the synthkey
     */
    public void add(String camp, BidRequest br) {
        StringBuilder sb = new StringBuilder(camp);
        sb.append(":");
        sb.append(br.synthkey);
        String key = sb.toString();

        add(key);
    }

    /**
     * Does the Set contain a key based on campaign id + br.synthkey.
     *
     * @param adId String. The campaign ad it.
     * @param br   BidRequest. The bid request providing the synthkey
     * @return boolean. Returns true if the Set still has the value. Otherwise return false.
     */
    public boolean contains(String adId, BidRequest br) {
        if (silent)
            return false;

        StringBuilder sb = new StringBuilder(adId);
        sb.append(":");
        sb.append(br.synthkey);
        String key = sb.toString();
        return contains(key);
    }
   
    public boolean containsRecordedBid(String id) throws Exception {
    	RecordedBid bid = BidCachePool.getInstance().getBid(id);
    	if (bid != null)
    		return true;
    	return false; 	
    }

    /**
     * Return whether a string key exists.
     *
     * @param id String. The value of the key.
     * @return boolean. Returns true if the key still exists, or false if it ages out.
     */
    public boolean contains(String id) {

        return eset.contains(id);
    }

    public void clear() {
        eset.clear();
    }

    /**
     * A simple test progra, to demonstrate how the 0MQ
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        FrequencyGoverner fg1 = new FrequencyGoverner( 900);
        fg1.name = "FG1";
        FrequencyGoverner fg2 = new FrequencyGoverner(900);
        fg2.name = "FG2";

        Thread.sleep(1000);

        fg1.add("hello");
        Thread.sleep(100);
        System.out.println(fg2.contains("hello"));
        Thread.sleep(100);
        System.out.println(fg2.contains("hello"));

    }

}
