package com.opencredo.integration.s3;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;


import org.jets3t.service.Constants;
import org.jets3t.service.S3ObjectsChunk;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import static  org.jets3t.service.S3Service.*;

import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageSource;
import org.springframework.integration.core.Message;


/** 
 * MessageSource that creates messages from a Simple Queue Service.
 */
public class S3FileReadingMessageSource implements MessageSource<S3Object> {
	
	class S3ObjectLastModifiedDateComparator implements Comparator<S3Object>{

		public int compare(S3Object s3obj1, S3Object s3obj2){
			Date s3obj1LastModifiedDate = s3obj1.getLastModifiedDate();
			Date s3obj2LastModifiedDate = s3obj2.getLastModifiedDate();
			
			if( s3obj1LastModifiedDate.after(s3obj2LastModifiedDate) )
				return 1;
			else if( s3obj1LastModifiedDate.before(s3obj2LastModifiedDate) )
				return -1;
			else
				return 0;
		}
	}

	private S3Service s3Service;
	private S3Bucket s3Bucket;
	
	private Map<String, String> sentKeysMap; // (etag, last_modified_timestamp)
	
	public S3Service getS3Service() {
		return s3Service;
	}

	public void setS3Service(S3Service s3Service) {
		this.s3Service = s3Service;
	}

	public S3Bucket getsBucket() {
		return s3Bucket;
	}

	public void setsBucket(S3Bucket sBucket) {
		this.s3Bucket = sBucket;
	}

    public S3FileReadingMessageSource(String bucketName, String awsAccessKeyId, String awsSecretKey){
    	try {
			s3Service = new RestS3Service(new AWSCredentials(awsAccessKeyId, awsSecretKey));
			s3Bucket = s3Service.getBucket(bucketName);
			//TODO: initialise sentKeysMap
			
		} 
    	catch (S3ServiceException e) {
			e.printStackTrace();
		}
    }
    
    public S3FileReadingMessageSource(S3Service s3Service, S3Bucket sBucket){
    	this.s3Service = s3Service;
    	this.s3Bucket = sBucket;
    	//TODO: initialise sentKeysMap
    }
    
	public Message<S3Object> receive(){
		S3Object[] objectsInBucket = null;
		//TODO: poll, get fileName and uri on new files 
		
		try {
			if (s3Service.checkBucketStatus(s3Bucket.getName()) == BUCKET_STATUS__MY_BUCKET){
				//objectsInBucket contain only minimal information, not the actual content
				S3ObjectsChunk chunk = s3Service.listObjectsChunked(s3Bucket.getName(),
			             null, null, Constants.DEFAULT_OBJECT_LIST_CHUNK_SIZE, null, true);
			    objectsInBucket = chunk.getObjects();
				Arrays.sort(objectsInBucket, new S3ObjectLastModifiedDateComparator());
				S3Object firstObjectWithUnsentKey = findFirstObjectWithUnsentKey(objectsInBucket);
				if (firstObjectWithUnsentKey != null){
					//TODO
					MessageBuilder<S3Object> builder = MessageBuilder.withPayload(firstObjectWithUnsentKey);
					return builder.build();
				}
				else return null;
			}
			else return null;
		} 
		catch (S3ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	private S3Object findFirstObjectWithUnsentKey(S3Object[] objectsInBucket) {
		int i =0;
		while (sentKeysMap.containsKey(objectsInBucket[i]) && (i<objectsInBucket.length) ) i++; 
		if (i < objectsInBucket.length) 
			return objectsInBucket[i];
		else return null;
	} 
	        	          
}
