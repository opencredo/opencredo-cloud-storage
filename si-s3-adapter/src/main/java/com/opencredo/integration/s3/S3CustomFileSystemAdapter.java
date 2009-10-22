package com.opencredo.integration.s3;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.core.MessageChannel;

//s3Service = new RestS3Service(new AWSCredentials(awsAccessKeyId, awsSecretKey))

public class S3CustomFileSystemAdapter {
	
	private RestS3Service s3Service;
	private S3Bucket s3bucket;
	private Map<String, S3Object> metaDataMap;
	private ArrayList<String> sentKeys;  // S3Object keys that were already sent to the channel
	
	public S3CustomFileSystemAdapter(String awsAccessKeyId, String awsSecretKey, String bucket){
		try {
			this.s3Service = new RestS3Service(new AWSCredentials(awsAccessKeyId, awsSecretKey));
			this.s3bucket = this.s3Service.getBucket(bucket);
			this.metaDataMap = this.s3bucket.getMetadataMap();
		} 
		catch (S3ServiceException e) {
			
			e.printStackTrace();
		}
	}
	
	public void checkS3(){
		//TODO: check the bucket and send new objects to the channel 
		
		Iterator<Entry<String, S3Object>> it = this.metaDataMap.entrySet().iterator();
		Map.Entry<String, S3Object> pair;
	    while (it.hasNext()) {
	    	pair = it.next();
	        if (!sentKeys.contains(pair.getKey())){
	        	ApplicationContext ctx = new ClassPathXmlApplicationContext("s3-integration.xml");
	        	MessageChannel s3InputChannel2 = (MessageChannel) ctx.getBean("s3InputChannel2");
	        	s3InputChannel2.send(pair.getValue().);
	        	//TODO: send pair.getValue();
	        	sentKeys.add(pair.getKey().toString());
	        }
	    }
		
		
	}
}
