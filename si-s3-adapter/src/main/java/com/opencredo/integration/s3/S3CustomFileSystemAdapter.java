package com.opencredo.integration.s3;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.security.AWSCredentials;

//s3Service = new RestS3Service(new AWSCredentials(awsAccessKeyId, awsSecretKey))

public class S3CustomFileSystemAdapter {
	
	public S3CustomFileSystemAdapter(String awsAccessKeyId, String awsSecretKey){
		try {
			RestS3Service s3Service = new RestS3Service(new AWSCredentials(awsAccessKeyId, awsSecretKey));
		} 
		catch (S3ServiceException e) {
			
			e.printStackTrace();
		}
	}
	
	public void checkS3(){
		//TODO: check the bucket and send new objects to the channel
	}
}
