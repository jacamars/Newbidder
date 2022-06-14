package com.jacamars.dsp.rtb.exchanges.azerion;

import java.io.InputStream;

import java.util.Map;

import com.jacamars.dsp.rtb.pojo.BidRequest;

/**
 * A class to handle Azerion ad exchange. Not much different, except, Azerion uses encrypted pricing.
 * @author Ben M. Faul
 *
 */

public class Azerion extends BidRequest {

    public static final String AZERION = "azerion";
        public Azerion() {
                super();
                parseSpecial();
        }

        /**
         * Make a Azerion bid request using a String.
         * @param in String. The JSON bid request for Epom
         * @throws Exception on JSON errors.
         */
        public Azerion(String  in) throws Exception  {
                super(in);
                parseSpecial();
        }

        /**
         * Make a Azerion bid request using an input stream.
         * @param in InputStream. The contents of a HTTP post.
         * @throws Exception on JSON errors.
         */
        public Azerion(InputStream in) throws Exception {
                super(in);
                parseSpecial();
        }

        /**
         * Create a Azerion bid request from a string builder buffer
         * @param in StringBuilder. The text.
         * @throws Exception on parsing errors.
         */
        public Azerion(StringBuilder in) throws Exception {
			super(in);
            multibid = BidRequest.usesMultibids(AZERION);
			parseSpecial();
		}

		/**
         * Process special Azerion stuff, sets the exchange name. Sets encoding.
         */
        @Override
        public boolean parseSpecial() {
                setExchange(AZERION);
                usesEncodedAdm = false;
                return true;
        }
        
    	/**
    	 * Create a new Azerion object from this class instance.
    	 * @throws Exception on stream reading errors
    	 */
    	@Override
    	public Azerion copy(InputStream in) throws Exception  {
    		Azerion copy = new Azerion(in);
    		copy.usesEncodedAdm = usesEncodedAdm;
            copy.multibid = BidRequest.usesMultibids(AZERION);
    		copy.usesGzipResponse = usesGzipResponse;
            return copy;
    	}

    /**
     * The configuration requires an e_key and an i_key. Unlike google, Openx uses hex chars instead of
     * web safe base 64 encoded keys.
     */
    @Override
    public void handleConfigExtensions(Map extension)  {
        String ekey = (String) extension.get("e_key");
        String ikey = (String) extension.get("i_key");
        if (ekey == null || ikey == null || ekey.length()==0  || ikey.length()==0) {
        	logger.warn("Azerion ekey and ikeys are not set");
        	return;
        }
        try {
            AzerionWinObject.setKeys(ekey, ikey);
        } catch (Exception error) {
            logger.error("Error setting OpenX ekey: '{}'. ikey: '{}'. reason: {}",ekey,ikey,error.toString());
        }
    }
}



        
