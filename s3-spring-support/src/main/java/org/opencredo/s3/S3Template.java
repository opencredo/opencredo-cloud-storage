package org.opencredo.s3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
	
	
	private String accessKey;
	private String secretAccessKey;
	private String defaultBucketName;
	/*
	private String devPayUserToken;
	private String devPayProductToken;
	*/
	
	public S3Template() {
		
	}
	
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(accessKey, "Access key must be provided.");
		Assert.notNull(secretAccessKey, "Secret access key must be provided.");
		
		s3Service = new RestS3Service(new AWSCredentials(accessKey, secretAccessKey));
	}
	
	public void send(String key, String stringToSend) throws S3CommunicationException {
		Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
		try {
			s3Service.putObject(new S3Bucket(this.defaultBucketName), new S3Object(key, stringToSend));
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
	
	public void send(String bucketName, String key, String stringToSend) throws S3CommunicationException {
		Assert.notNull(bucketName, "Bucket name cannot be null");
		try {
			s3Service.putObject(new S3Bucket(bucketName), new S3Object(key, stringToSend));
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
	
	public void send(File fileToSend) {
		Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
		try {
			s3Service.putObject(new S3Bucket(this.defaultBucketName), new S3Object(fileToSend));
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
	
	public void send(String bucketName, File fileToSend) {
		Assert.notNull(bucketName, "Bucket name cannot be null");
		try {
			s3Service.putObject(new S3Bucket(bucketName), new S3Object(fileToSend));
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
	
	public void send(String key, InputStream is) {
		Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
		try {
			S3Object s3ObjectToSend = new S3Object(key);
			s3ObjectToSend.setDataInputStream(is);
			s3ObjectToSend.setContentLength(is.available());
			s3Service.putObject(new S3Bucket(this.defaultBucketName), s3ObjectToSend);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (S3ServiceException e) {
			e.printStackTrace();
		}	
	}
	
	public void send(String key, String bucketName, InputStream is) {
		Assert.notNull(bucketName, "Bucket name cannot be null");
		try {
			S3Object s3ObjectToSend = new S3Object(key);
			s3ObjectToSend.setDataInputStream(is);
			s3ObjectToSend.setContentLength(is.available());
			s3Service.putObject(new S3Bucket(bucketName), s3ObjectToSend);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (S3ServiceException e) {
			e.printStackTrace();
		}	
	}
	
	public String receiveAsString(String keyName) throws S3CommunicationException {
		Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
		try {
			Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
			BufferedReader br = new BufferedReader(new InputStreamReader(s3Service.getObject(new S3Bucket(this.defaultBucketName), keyName).getDataInputStream()));
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
	
	public String receiveAsString(String bucketName, String keyName) throws S3CommunicationException {
		Assert.notNull(bucketName, "Bucket name cannot be null");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(s3Service.getObject(new S3Bucket(bucketName), keyName).getDataInputStream()));
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
	
	public File receiveAsFile(String key) {
		Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
		try {
			return s3Service.getObject(new S3Bucket(this.defaultBucketName), key).getDataInputFile();
		} catch (S3ServiceException e) {
			e.printStackTrace();
			throw new S3CommunicationException();
		}
	}
	
	public File receiveAsFile(String bucketName, String key) {
		Assert.notNull(bucketName, "Bucket name cannot be null");
		try {
			return s3Service.getObject(new S3Bucket(bucketName), key).getDataInputFile();
		} catch (S3ServiceException e) {
			e.printStackTrace();
			throw new S3CommunicationException();
		}
	}
	
	public InputStream receiveAsInputStream(String key) {
		Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
		try {
			return s3Service.getObject(new S3Bucket(this.defaultBucketName), key).getDataInputStream();
		} catch (S3ServiceException e) {
			e.printStackTrace();
			throw new S3CommunicationException();
		}	
	}
	
	public InputStream receiveAsInputStream(String bucketName, String key) {
		Assert.notNull(bucketName, "Bucket name cannot be null");
		try {
			return s3Service.getObject(new S3Bucket(bucketName), key).getDataInputStream();
		} catch (S3ServiceException e) {
			e.printStackTrace();
			throw new S3CommunicationException();
		}
	}
	
	public void setDefaultBucketName(String bucketName) {
		this.defaultBucketName = bucketName;
	}
	
	/*
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
    */
    
}
