package com.opencredo.integration.s3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.springframework.core.io.Resource;


public class S3Resource implements Resource{

	//The directory that keeps the files while being handled and before they are sent to a bucket
	public final File tempDestinationDirectory = new File("resources/si-s3-temp_destination");
	
	private S3Service s3Service;
	private S3Bucket s3Bucket;
	private String fileName = null;
	
	public S3Resource(String bucketName, String awsAccessKeyId, String awsSecretKey){
		try {
			s3Service = new RestS3Service(new AWSCredentials(awsAccessKeyId, awsSecretKey));	
			s3Bucket = s3Service.getBucket(bucketName);
		} 
		catch (S3ServiceException e) {
			e.printStackTrace();
		}
	}
	
	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	
	public Resource createRelative(String arg0) throws IOException {
		
		return null;
	}

	public boolean exists() {
		try {
			return s3Service.isBucketAccessible(s3Bucket.getName());
		} 
		catch (S3ServiceException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getDescription() {
		return new String("JetS3t Properties: " + s3Service.getJetS3tProperties().toString());
	}

	public File getFile() throws IOException {
		return tempDestinationDirectory;
	}

	public String getFilename() {
		
		return tempDestinationDirectory.getName();
	}

	public URI getURI() throws IOException {
		
		return null;
	}

	public URL getURL() throws IOException {
		return null;
	}

	public boolean isOpen() {
		return false;
	}

	public boolean isReadable() {
		
		return true;
	}

	public long lastModified() throws IOException {
		//TODO: TimeStamp/long conversion
		try {
			return Long.parseLong(s3Service.getObjectDetails(s3Bucket, fileName).getMetadata("METADATA_HEADER_LAST_MODIFIED_DATE").toString());
		} catch (S3ServiceException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public InputStream getInputStream() throws IOException {
		
		return null;
	}

	public S3Bucket getBucket(String bucketName) {
		
		try {
			return s3Service.getBucket(bucketName);
		} 
		catch (S3ServiceException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setBucket(String bucketName) {
		try {
			s3Bucket = s3Service.getBucket(bucketName);
		} 
		catch (S3ServiceException e) {
			e.printStackTrace();
		}
		
	}
	
	public void sendFileToBucket(String filename){
		try {
			s3Service.putObject(s3Bucket, new S3Object(new File(tempDestinationDirectory, filename)));
		} 
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		catch (S3ServiceException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {			
			e.printStackTrace();
		}
	}

}
