package com.opencredo.integration.s3;

import java.io.File;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.core.Message;


/** 
 * MessageSource that creates messages from a Simple Queue Service.
 */
public class S3FileReadingMessageSource extends FileReadingMessageSource {

	private S3Service s3Service;
	private S3Bucket sBucket;
	private String filename;
	
    public S3FileReadingMessageSource(String bucketName, String filename, String awsAccessKeyId, String awsSecretKey){
    	try {
			s3Service = new RestS3Service(new AWSCredentials(awsAccessKeyId, awsSecretKey));
			sBucket = s3Service.getBucket(bucketName);
			this.filename = filename;
		} 
    	catch (S3ServiceException e) {
			e.printStackTrace();
		}
    }
    
   
	public Message<File> receive(){
		
		try {
			S3Object s3Object = s3Service.getObject(sBucket, filename);
			MessageBuilder<File> builder = MessageBuilder.withPayload(s3Object.getDataInputFile());
			return builder.build();
		} 
		catch (S3ServiceException e) {
			e.printStackTrace();
		}
		return null;
	} 
	        	          
}
