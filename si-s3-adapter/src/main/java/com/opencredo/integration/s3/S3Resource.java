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
import org.springframework.util.Assert;


public class S3Resource implements Resource{

	private S3Service s3Service;
	private S3Bucket s3Bucket;
	private File s3File = null;
	private S3Object s3Object = null;
	public static final AWSCredentials awsCredentials = new AWSCredentials("AKIAJJC4KITQHSAY43MQ", "U0H0Psg7aS5qrKpLFqZXFUUOq2rK6l2xAfHxZWTd");
	
	public S3Resource(String bucketName, String awsAccessKeyId, String awsSecretKey){
		try {
			s3Service = new RestS3Service(awsCredentials);	
			s3Bucket = s3Service.getBucket(bucketName);
		} 
		catch (S3ServiceException e) {
			e.printStackTrace();
		}
	}

	public S3Service getS3Service() {
		return s3Service;
	}
	
	public void setS3Service(S3Service s3Service) {	
		this.s3Service = s3Service;	
	}
	
	public S3Bucket getS3Bucket() {
		return s3Bucket;
	}

	public void setS3Bucket(S3Bucket s3Bucket) {
		this.s3Bucket = s3Bucket;
	}
	
	public File getFile() throws IOException {
		return s3File;
	}
	
	public void setFile(File file) {
		this.s3File = file;
	}
	
	public File getS3Object() throws IOException {
		return s3File;
	}
	
	public void setS3Object(S3Object s3Object) {
		this.s3Object = s3Object;
	}

	public String getFilename() {
		
		return s3File.getName();
	}
	
	public void sendFileToBucket(){
		try {
			Assert.notNull(s3File, "s3File should not be null");
			s3Service.putObject(s3Bucket, new S3Object(s3File));
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
	
	public void sendS3ObjectToBucket(){
		try {
			Assert.notNull(s3Object, "s3File should not be null");
			s3Service.putObject(s3Bucket, s3Object);
		} 
		catch (S3ServiceException e) {
			e.printStackTrace();
		}
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
		Assert.notNull(s3File.getName(), "File name should be known");
		try {
			return Long.parseLong(s3Service.getObjectDetails(s3Bucket, s3File.getName()).getMetadata("METADATA_HEADER_LAST_MODIFIED_DATE").toString());
		} catch (S3ServiceException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public InputStream getInputStream() throws IOException {
		
		return null;
	}

}
