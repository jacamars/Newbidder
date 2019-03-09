package com.jacamars.dsp.rtb.jmq;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.jacamars.dsp.rtb.tools.Env;


public class KinesisConfig {

    private String aws_access_key = null;
    private String aws_secret_key = null;
    private int sleep_time = 1000;
    private String aws_region = Regions.US_WEST_2.getName();
    private String stream = null;
    private String partition = "part-1";
    private String shard = "shardId-000000000000";
    private String iterator_type = "LATEST";
    private int record_limit = 1;

    BasicAWSCredentials awsCreds;
    AmazonKinesis amazonKinesis;

    public KinesisConfig(String address) throws Exception {

        // Chop off kineses://
        address = address.substring(10);

        // Substitute from environment variales;
        address = Env.substitute(address);

        String parts[] = address.split("&");
        for (int i = 0; i < parts.length; i++) {
            String t[] = parts[i].trim().split("=");
            if (t.length != 2) {
                throw new Exception("Not a proper parameter (a=b)");
            }
            t[0] = t[0].trim();
            t[1] = t[1].trim();
            switch (t[0]) {
                case "aws_access_key":
                    aws_access_key = t[1].trim();
                    break;
                case "aws_secret_key":
                    aws_secret_key = t[1].trim();
                    break;
                case "aws_region":
                    aws_region = t[1].trim();
                    break;
                case "topic":
                case "stream":
                    stream = t[1].trim();
                    break;
                case "partition":
                    partition = t[1].trim();
                    break;
                case "iterator_type":
                    iterator_type = t[1].trim();
                    break;
                case "record_limit":
                    record_limit = Integer.parseInt(t[1].trim());
                    break;
                case "sleep_time":
                    sleep_time = Integer.parseInt(t[1].trim());
                    break;
            }
        }

        if (aws_access_key == null)
            throw new Exception("aws_access_key is not defined");

        if (aws_secret_key == null)
            throw new Exception("aws_secret_key is not defined");

        if (stream == null)
            throw new Exception("No stream defined");

        awsCreds = new BasicAWSCredentials(aws_access_key, aws_secret_key);

        amazonKinesis = AmazonKinesisClientBuilder
                .standard().withRegion(Regions.fromName(aws_region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }

    public AmazonKinesis getKinesis() {
        return amazonKinesis;
    }

    public String getStream() {
        return stream;
    }

    public String getPartition() {
        return partition;
    }

    public String getShard() {
        return shard;
    }

    public String getIterator_type() {
        return iterator_type;
    }

    public int getRecord_limit() {
        return record_limit;
    }

    public int getSleep_time() { return sleep_time; }

    public BasicAWSCredentials getAwsCreds() {
        return awsCreds;
    }
}
