package org.opencredo.s3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

//TODO: Verify data transmission
//TODO: Add support for Access control lists
public class S3Template implements S3Operations, InitializingBean {

	private S3Service s3Service;
	private S3Bucket s3Bucket;
	
	private String accessKey;
	private String secretAccessKey;
	private String bucketName;
	private String devPayUserToken;
	private String devPayProductToken;
	
	public S3Template() {
		
	}
	
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(accessKey, "Access key must be provided.");
		Assert.notNull(secretAccessKey, "Secret access key must be provided.");
		Assert.notNull(bucketName, "Bucket name must be provided.");
		
		s3Service = new RestS3Service(new AWSCredentials(accessKey, secretAccessKey));
		s3Bucket = new S3Bucket(bucketName);
	}
	
	public void sendString(String key, String stringToSend) throws S3CommunicationException {
		try {
			s3Service.putObject(s3Bucket, new S3Object(key, stringToSend));
		} catch (S3ServiceException e) {
			e.printStackTrace();
			throw new S3CommunicationException();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new S3CommunicationException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new S3CommunicationException();
		}
	}
	
	public String receiveString(String keyName) throws S3CommunicationException {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(s3Service.getObject(s3Bucket, keyName).getDataInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
			}
			br.close();
			return sb.toString();
		} catch (S3ServiceException e) {
			e.printStackTrace();
			throw new S3CommunicationException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new S3CommunicationException();
		}
	}
	
	public void sendFile(File fileToSend) {
		try {
			s3Service.putObject(s3Bucket, new S3Object(fileToSend));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new S3CommunicationException();
		} catch (S3ServiceException e) {
			e.printStackTrace();
			throw new S3CommunicationException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new S3CommunicationException();
		}
	}

	public File receiveFile(String key) {
		try {
			return s3Service.getObject(s3Bucket, key).getDataInputFile();
		} catch (S3ServiceException e) {
			e.printStackTrace();
			throw new S3CommunicationException();
		}
	}
	
    public void setDevPayUserToken(String userToken) {
        this.devPayUserToken = userToken;
    }
    
    public String getDevPayUserToken() {
        return this.devPayUserToken;
    }
    
    public void setDevPayProductToken(String productToken) {
        this.devPayProductToken = productToken;
    }

    public String getDevPayProductToken() {
        return this.devPayProductToken;
    }
    
}
