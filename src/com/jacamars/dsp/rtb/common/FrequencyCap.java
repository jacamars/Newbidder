package com.jacamars.dsp.rtb.common;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.tools.DbTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A calls that handles frequency capping in Hazelcast.
 * Created by Ben M. Faul on 7/21/17.
 */
public class FrequencyCap implements Serializable, com.hazelcast.nio.serialization.DataSerializable {
    public List<String> getCapSpecification() {
		return capSpecification;
	}

	public void setCapSpecification(List<String> capSpecification) {
		this.capSpecification = capSpecification;
	}

	public int getCapFrequency() {
		return capFrequency;
	}

	public void setCapFrequency(int capFrequency) {
		this.capFrequency = capFrequency;
	}

	public int getCapTimeout() {
		return capTimeout;
	}

	public void setCapTimeout(int capTimeout) {
		this.capTimeout = capTimeout;
	}

	public String getCapTimeUnit() {
		return capTimeUnit;
	}

	public void setCapTimeUnit(String capTimeUnit) {
		this.capTimeUnit = capTimeUnit;
	}

	public String getCapKey() {
		return capKey;
	}

	public void setCapKey(String capKey) {
		this.capKey = capKey;
	}

	protected static final Logger logger = LoggerFactory.getLogger(FrequencyCap.class);

    /** Cap specification, a list of cap specification keys, e.q. ["device.ip"] */
    public List<String> capSpecification;
    
    /** Cap frequency count, the upper limit on the allowed count */
    public int capFrequency = 0;
    
    /** Cap timeout in seconds */
    public int capTimeout; 
    
    /** NOT USED */
    public String capTimeUnit;

    /** The computed capKey that will be used in hazelcast. eg: "capped_block-test166.137.138.18" 
     *  This is the key that persists in misc. The value of this key is current count.
     */
    public String capKey;

    /**
     * Default constructor for JSON
     */
    public FrequencyCap() {

    }

    /**
     * Return a copy of this frequency acap.
     * @return FrequencyCap. The new frequency cap to return.
     */
    public FrequencyCap copy() {
        FrequencyCap c = new FrequencyCap();
        c.capSpecification = capSpecification;         // can reuse this
        c.capFrequency = capFrequency;
        c.capTimeout = capTimeout;
        c.capTimeUnit = capTimeUnit;
        return c;
    }
    
    public void init(FrequencyCap c) {
       capSpecification =  c.capSpecification;         // can reuse this
       capFrequency = c.capFrequency;
       capTimeout =  c.capTimeout;
       capTimeUnit = c.capTimeUnit;
    }

    /**
     * Is this creative capped on the IP address in this bid request?
     * @param br BidRequest. The bid request to query.
     * @param capSpecs Map. The current cap spec.
     * @param adId String. The ad id being frequency checked.
     * @return boolean. Returns true if the IP address is capped, else false.
     */
    public boolean isCapped(BidRequest br, Map<String, String> capSpecs, String adId) {
        if (capSpecification == null)
            return false;

        StringBuilder value = new StringBuilder();
        try {
            for (int i=0;i<capSpecification.size();i++) {
                value.append(BidRequest.getStringFrom(br.database.get(capSpecification.get(i))));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

        StringBuilder bs = new StringBuilder("capped_");
        bs.append(adId);
        bs.append(value);
        int k = 0;
        String cap = null;
        try {
            cap = bs.toString();
            //System.out.println("---------------------> " + cap);
            capSpecs.put(adId, cap);
            k = getCapValue(cap);
            if (k < 0)
                return false;
        } catch (Exception e) {
            logger.error("ERROR GETTING FREQUENCY CAP: {}, error: {}", cap,e.toString());
            return true;
        }

        if (k >= capFrequency)
            return true;
        return false;

    }

    /**
     * Returns the number of seconds between the date string and now.
     * @param dateString String. The number of SECONDS from now (e.g. "15") OR a date string as "yyyy-MM-dd hh:mm", which will return
     *                   the number of SECONDS between the now and the dateStting. If you pass in "1" it will return 1. If you send in
     *                   yyyy-mm-hh then the result returns is datetime ((converted to milliseconds) - epoch (in millliseconds)/1000 thus
     *                   returning seconds.
     * @return int. The number of seconds to apply for the expiry.
     * @throws Exception on date parsing errors.
     */
    public static int returnTimeout(String dateString) throws Exception {
        int n = 0;
        try {
            n = Integer.parseInt(dateString);
        } catch (Exception error) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                Date parsedDate = dateFormat.parse(dateString);
                long now = System.currentTimeMillis();

                n = (int) (now - parsedDate.getTime()) / 1000;
            } catch (Exception error1) {
                throw error1;
            }
        }
        return n;
    }

    /**
     * Return the Cap value
     *
     * @param capSpec String key for the count
     * @return int. The Integer value of the capSpec
     */
    public static int getCapValue(String capSpec) throws Exception {
        Number cap = (Number)Controller.bidCachePool.get(capSpec);
        return cap != null? cap.intValue() : -1;
    }

    /**
     * Handle expiration of a cap specification.
     * @param capSpec String. The frequency specification.
     * @param capTimeout int. The number of the seconds to timeout the specification.
     * @param capTimeUnit String. Cap time unit, will be one of ("minutes", "hours", "days", "lifetime").
     * @throws Exception on Hazelcast errors.
     */
    public static void handleExpiry(String capSpec, int capTimeout, String capTimeUnit) throws Exception {
        Controller.bidCachePool.increment(capSpec, capTimeout, capTimeUnit);
    }
    
    @Override
	public void readData(ObjectDataInput arg) throws IOException {
		String buf = arg.readUTF();
		FrequencyCap cap = DbTools.mapper.readValue(buf,FrequencyCap.class);
		try {
			init(cap);
		} catch (Exception error) {
			throw (IOException)error;
		}
		
	}

	@Override
	public void writeData(ObjectDataOutput arg) throws IOException {
		String buf = DbTools.mapper.writeValueAsString(this);
		arg.writeUTF(buf);
	}
}
