package com.jacamars.dsp.rtb.tools;

import com.amazonaws.regions.Regions;
import com.jacamars.dsp.rtb.common.Configuration;

import java.util.Map;

public enum  Env {
    INSTANCE();

    private Env() {

    }

    public static Env getInstance() {
        return INSTANCE;
    }

    /**
     * Substitute the macros and environment variables found in the the string.
     * @param address String. The address being queries/
     * @return String. All found environment vars will be substituted.
     * @throws Exception on parsing errors.
     */
    public static String substitute(String address) throws Exception {

    	if (address == null)
			return address;
    	
		///////////////////////////////////////////////////////////////////////////////////////////////////
		
		 while (address.contains("$BIDSCHANNEL"))
			address = GetEnvironmentVariable(address, "$BIDSCHANNEL", "kafka://[$BROKERLIST]&topic=bids");
		 while (address.contains("$WINSCHANNEL"))
			address = GetEnvironmentVariable(address, "$WINSCHANNEL", "kafka://[$BROKERLIST]&topic=wins");
		 while (address.contains("$REQUESTSCHANNEL"))
			address = GetEnvironmentVariable(address, "$REQUESTSCHANNEL", "kafka://[$BROKERLIST]&topic=requests");
		 while (address.contains("$CLICKSCHANNEL"))
			address = GetEnvironmentVariable(address, "$CLICKSCHANNEL", "kafka://[$BROKERLIST]&topic=clicks");
		 while (address.contains("$PIXELSCHANNEL"))
			address = GetEnvironmentVariable(address, "$PIXELSCHANNEL", "kafka://[$BROKERLIST]&topic=pixels");
		 while (address.contains("$VIDEOEVENTSCHANNEL"))
			address = GetEnvironmentVariable(address, "$VIDEOEVENTSCHANNEL", "kafka://[$BROKERLIST]&topic=videoevents");
		 while (address.contains("$POSTBACKEVENTSCHANNEL"))
			address = GetEnvironmentVariable(address, "$POSTBACKEVENTSCHANNEL", "kafka://[$BROKERLIST]&topic=postbackevents");
		 while (address.contains("$STATUSCHANNEL"))
			address = GetEnvironmentVariable(address, "$STATUSCHANNEL", "kafka://[$BROKERLIST]&topic=status");
		 while (address.contains("$REASONSCHANNEL"))
			address = GetEnvironmentVariable(address, "$REASONSCHANNEL", "kafka://[$BROKERLIST]&topic=reasons");
		 while (address.contains("$LOGCHANNEL"))
				address = GetEnvironmentVariable(address, "$LOGCHANNEL", "kafka://[$BROKERLIST]&topic=logs");
		 
		 //////////////////////////////////////////////////////////////////////////////////////////////////////////

		while (address.contains("$GEOPATCH"))
			address = GetEnvironmentVariable(address, "$GEOPATCH", "");
		
		while (address.contains("$MASTERCIDR"))
			address = GetEnvironmentVariable(address, "$MASTERCIDR", "");

		while (address.contains("$FRAUDTYPE"))
			address = GetEnvironmentVariable(address, "$FRAUDTYPE", "");
		while (address.contains("$FRAUDTHRESHOLD"))
			address = GetEnvironmentVariable(address, "$FRAUDTHRESHOLD", "100");
		while (address.contains("$FRAUDKEY"))
			address = GetEnvironmentVariable(address, "$FRAUDKEY", "");
		while (address.contains("$FRAUDENDPOINT"))
			address = GetEnvironmentVariable(address, "$FRAUDENDPOINT", "");
		while (address.contains("$FRAUDCONNECTIONS"))
			address = GetEnvironmentVariable(address, "$FRAUDCONNECTIONS", "100");
		while (address.contains("$FRAUDWATCHLIST"))
			address = GetEnvironmentVariable(address, "$FRAUDWATCHLIST", "");

		while (address.contains("$BIDSWITCH_ID"))
			address = GetEnvironmentVariable(address, "$BIDSWITCH_ID", "bidswitch-id");

		/////////////////////////////////////////////////////////////////////////////    	
		
		while(address.contains("$MAPSTOREDRIVER"))
			address = GetEnvironmentVariable(address,"$MAPSTOREDRIEVER",null);
		
		while(address.contains("$MAPSTOREJDBC"))
			address = GetEnvironmentVariable(address,"$MAPSTOREJDBC",null);
		
		while(address.contains("$BIDSWITCH_ID"))
			address = GetEnvironmentVariable(address,"$BIDSWITCH_ID","bidswitch-id");
		
		
		while(address.contains("$S3BUCKET"))
	        address = GetEnvironmentVariable(address,"$S3BUCKET", null);

        while(address.contains("$AWSACCESSKEY"))
            address = GetEnvironmentVariable(address,"$AWSACCESSKEY", null);

        while(address.contains("$AWSSECRETKEY"))
            address = GetEnvironmentVariable(address,"$AWSSECRETKEY", null);

        while(address.contains("$AWSREGION"))
            address = GetEnvironmentVariable(address,"$AWSREGION", Regions.US_EAST_1.getName());

        while(address.contains("$AWSKINESIS_STREAM"))
            address = GetEnvironmentVariable(address,"$AWS_KINESIS_STREAM", null);

        while(address.contains("$AWSKINESIS_PARTITION"))
            address = GetEnvironmentVariable(address,"$AWS_KINESESIS_PARTITION", "part-1");

        while(address.contains("AWSKINESIS_SHARD"))
            address = GetEnvironmentVariable(address,"$AWS_KINESIS_SHARD", "shardId-000000000000");

        while(address.contains("AWS_KINESIS_ITERATOR"))
            address = GetEnvironmentVariable(address,"$AWS_KENESIS_ITERATOR", "LATEST");

        while(address.contains("AWSKINESIS_RECORDS"))
            address = GetEnvironmentVariable(address,"$AWS_KENSIS_RECORDS", "1");
        
    	while (address.contains("$FREQGOV"))
			address = GetEnvironmentVariable(address, "$FREQGOV", "true");

		while (address.contains("$GDPR_MODE"))
			address = GetEnvironmentVariable(address, "$GDPR_MODE", "false");

		while (address.contains("$HOSTNAME"))
			address = GetEnvironmentVariable(address, "$HOSTNAME", Configuration.instanceName);
		while (address.contains("$BROKERLIST"))
			address = GetEnvironmentVariable(address, "$BROKERLIST", "localhost:9092");
		while (address.contains("$PUBSUB"))
			address = GetEnvironmentVariable(address, "$PUBSUB", "localhost");

		while (address.contains("$WIN"))
			address = GetEnvironmentVariable(address, "$WIN", "localhost");
		while (address.contains("$PIXEL"))
			address = GetEnvironmentVariable(address, "$PIXEL", "localhost");
		while (address.contains("$VIDEO"))
			address = GetEnvironmentVariable(address, "$VIDEO", "localhost");
		while (address.contains("$BID"))
			address = GetEnvironmentVariable(address, "$BID", "localhost");
		while (address.contains("$EXTERNAL"))
			address = GetEnvironmentVariable(address, "$EXTERNAL", "http://localhost:8080");

		while (address.contains("$PUBPORT"))
			address = GetEnvironmentVariable(address, "$PUBPORT", "6000");
		while (address.contains("$SUBPORT"))
			address = GetEnvironmentVariable(address, "$SUBPORT", "6001");
		while (address.contains("$INITPORT"))
			address = GetEnvironmentVariable(address, "$INITPORT", "6002");
		while (address.contains("$THREADS"))
			address = GetEnvironmentVariable(address, "$THREADS", "2000");
		while (address.contains("$CONCURRENCY"))
			address = GetEnvironmentVariable(address, "$CONCURRENCY", "3");
		while (address.contains("$ADMINPORT"))
			address = GetEnvironmentVariable(address, "$ADMINPORT", "8155");
		while (address.contains("$REQUESTSTRATEGY"))
			address = GetEnvironmentVariable(address, "$REQUESTSTRATEGY", "100");
		while (address.contains("$ACCOUNTING"))
			address = GetEnvironmentVariable(address, "$ACCOUNTING", "accountingsystem");
		while (address.contains("$INDEXPAGE"))
			address = GetEnvironmentVariable(address, "$INDEXPAGE", "/index.html");
		while (address.contains("$THROTTLE"))
			address = GetEnvironmentVariable(address, "$THROTTLE", "100");
		while (address.contains("$NOBIDREASON"))
			address = GetEnvironmentVariable(address, "$NOBIDREASON", "false");

		while (address.contains("$TRACKER"))
			address = GetEnvironmentVariable(address, "$TRACKER", "localhost:8080");

		while (address.contains("$ADX_EKEY"))
			address = GetEnvironmentVariable(address, "$ADX_EKEY", "");
		while (address.contains("$ADX_IKEY"))
			address = GetEnvironmentVariable(address, "$ADX_IKEY", "");
		while (address.contains("$OPENX_EKEY"))
			address = GetEnvironmentVariable(address, "$OPENX_EKEY", "");
		while (address.contains("$OPENX_IKEY"))
			address = GetEnvironmentVariable(address, "$OPENX_IKEY", "");
		while (address.contains("$GOOGLE_EKEY"))
			address = GetEnvironmentVariable(address, "$GOOGLE_EKEY", "");
		while (address.contains("$GOOGLE_IKEY"))
			address = GetEnvironmentVariable(address, "$GOOGLE_IKEY", "");

		while (address.contains("$GOOGLE_IKEY"))
			address = GetEnvironmentVariable(address, "$GOOGLE_IKEY", "");
		
		address = GetEnvironmentVariable(address, "$TRACE", "false");

        return address;
    }

    /**
     * Retrieve a variable from the environment variables
     * @param address String. The address string to change.
     * @param varName String. The name of the environment variable, begins with $
     * @return String. The address string modified.
     */
    public static String GetEnvironmentVariable(String address, String varName) {
        if (address.contains(varName)) {
            String sub = varName.substring(1);
            Map<String, String> env = System.getenv();
            if (env.get(sub) != null) {
                address = address.replace(varName, env.get(sub));
                return address;
            }
            return null;
        }
        return address;
    }

    /**
     * Retrieve a variable from the environment variables, and if it exists, use that, else use the alternate.
     * @param address String. The address string to change.
     * @param varName String. The name of the environment variable, begins with $
     * @param altName String. The name to use if the environment variables is not defined.
     * @return String. The address string modified.
     */
    public static String GetEnvironmentVariable(String address, String varName, String altName) {
        String test = GetEnvironmentVariable(address,varName);
        if (test == null) {
        	if (altName == null)
        		altName = "";
        	test = address.replace(varName, altName);
        }
        return test;
    }
    
    public static String GetEnvironment(String var, String alt) {
        Map<String, String> env = System.getenv();
        String v = env.get(var);
        if (v == null)
        	return alt;
        return v;
    }
}
