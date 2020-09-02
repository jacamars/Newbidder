package com.jacamars.dsp.rtb.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class CopyToS3 {
    

    public static void main(String[] args) throws Exception {
    	execute(args[0]); 
    	
    	/*"endpoint=http://localhost:9000"
    			+"&aws_access_key=AKIAIOSFODNN7EXAMPLE"
    			+"&aws_secret_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
    			+"&bucket=cidr"
    			+"&filename=data/METHBOT.txt"
    			+"&key=METHBOT.txt");*/
    } 
    
    public static void execute(String address) throws Exception {
        String parts[] = address.split("&");
        String aws_access_key = null;
        String aws_secret_key = null;
        String region = null;
        String bucket = null;
        String fileName = null;
        String key = null;
        String endpoint = null;
        for (int i = 0; i < parts.length; i++) {
            String t[] = parts[i].trim().split("=");
            if (t.length != 2) {
                throw new Exception("Not a proper parameter (a=b) " + parts[i]);
            }
            t[0] = t[0].trim();
            t[1] = t[1].trim();
            switch (t[0]) {
                case "aws_access_key":
                    aws_access_key = t[1];
                    break;
                case "aws_secret_key":
                    aws_secret_key = t[1];
                    break;
                case "aws_region":
                    region = t[1];
                    break;
                case "bucket":
                    bucket = t[1];
                    break;
                case "key":
                    key = t[1];
                    break;
                case "endpoint":
                    endpoint = t[1];
                    break;
                case "filename":
                	fileName = t[1];
                	break;
            }
        }

        if (aws_access_key == null)
            throw new Exception("aws_access_key is not defined");

        if (aws_secret_key == null)
            throw new Exception("aws_secret_key is not defined");

        if (bucket == null)
            throw new Exception("No bucket defined");

        if (key == null)
            throw new Exception("No key defined");
        
        AmazonS3 s3Client = null;
        AWSCredentials credentials = new BasicAWSCredentials(aws_access_key, aws_secret_key);
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setSignerOverride("AWSS3V4SignerType");

        if (region == null)
        	region = Regions.US_EAST_1.name();
        
        if (endpoint != null) {
        	s3Client = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(clientConfiguration)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
        } else {
        	 s3Client = AmazonS3ClientBuilder
                     .standard().withRegion(Regions.fromName(region))
                     .withCredentials(new AWSStaticCredentialsProvider(credentials))
                     .build();
        }

        try {
        	// Download file
        	
        	if (!s3Client.doesBucketExistV2(bucket)) {
                // Because the CreateBucketRequest object doesn't specify a region, the
                // bucket is created in the region specified in the client.
                s3Client.createBucket(new CreateBucketRequest(bucket));

                // Verify that the bucket was created by retrieving it and checking its location.
                String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucket));
                System.out.println("Bucket location: " + bucketLocation);
            }
        	
        	  PutObjectRequest request = new PutObjectRequest(bucket, key, new File(fileName));
              ObjectMetadata metadata = new ObjectMetadata();
              metadata.setContentType("plain/text");
              metadata.addUserMetadata("title", "someTitle");
              request.setMetadata(metadata);
              s3Client.putObject(request);
              
              
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " + "means your request made it "
                    + "to Amazon S3, but was rejected with an error response" + " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());

        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " + "means the client encountered " + "an internal error while trying to "
                    + "communicate with S3, " + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());

        }

    }

}
