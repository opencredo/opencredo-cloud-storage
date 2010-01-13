/* Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencredo.aws.s3;

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
import org.opencredo.aws.s3.AWSCredentials;
import org.springframework.util.Assert;

//TODO: Verify data transmission
//TODO: Add support for Access control lists
public class S3Template implements S3Operations {

	private S3Service s3Service;
	
	private String defaultBucketName;
	
	public S3Template(AWSCredentials awsCredentials) {
		try {
			Assert.notNull(awsCredentials.getAccessKey(), "Access key is not provided");
			Assert.notNull(awsCredentials.getSecretAccessKey(), "Secret access key is not provided");
			s3Service = new RestS3Service(new org.jets3t.service.security.AWSCredentials(awsCredentials.getAccessKey(), 
					awsCredentials.getSecretAccessKey()));	
		} 
		catch (S3ServiceException e) {
			throw new S3CommunicationException(e);
		}
	}
	
	public void send(String key, String stringToSend) {
		Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
		try {
			s3Service.putObject(new S3Bucket(this.defaultBucketName), new S3Object(key, stringToSend));
		} catch (S3ServiceException e) {
			throw new S3CommunicationException("Sending string problem", e);
		} catch (NoSuchAlgorithmException e) {
			throw new S3CommunicationException("No such algorithm", e);
		} catch (IOException e) {
			throw new S3CommunicationException("Sending string IO problem", e);
		}
	}
	
	public void send(String bucketName, String key, String stringToSend) {
		Assert.notNull(bucketName, "Bucket name cannot be null");
		try {
			s3Service.putObject(new S3Bucket(bucketName), new S3Object(key, stringToSend));
		} catch (S3ServiceException e) {
			throw new S3CommunicationException("Sending string problem", e);
		} catch (NoSuchAlgorithmException e) {
			throw new S3CommunicationException("No such algorithm", e);
		} catch (IOException e) {
			throw new S3CommunicationException("Sending string IO problem", e);
		}
	}
	
	public void send(File fileToSend) {
		Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
		try {
			s3Service.putObject(new S3Bucket(this.defaultBucketName), new S3Object(fileToSend));
		} catch (NoSuchAlgorithmException e) {
			throw new S3CommunicationException("No such algorithm", e);
		} catch (S3ServiceException e) {
			throw new S3CommunicationException("Sending file problem", e);
		} catch (IOException e) {
			throw new S3CommunicationException("Sending file IO problem", e);
		}
	}
	
	public void send(String bucketName, File fileToSend) {
		Assert.notNull(bucketName, "Bucket name cannot be null");
		try {
			s3Service.putObject(new S3Bucket(bucketName), new S3Object(fileToSend));
		} catch (NoSuchAlgorithmException e) {
			throw new S3CommunicationException("No such algorithm", e);
		} catch (S3ServiceException e) {
			throw new S3CommunicationException("Sending file problem", e);
		} catch (IOException e) {
			throw new S3CommunicationException("Sending file IO problem", e);
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
			throw new S3CommunicationException("Sending input stream IO problem", e);
		} catch (S3ServiceException e) {
			throw new S3CommunicationException("Sending input stream problem", e);
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
			throw new S3CommunicationException("Sending input stream IO problem", e);
		} catch (S3ServiceException e) {
			throw new S3CommunicationException("Sending input stream problem", e);
		}	
	}
	
	public String receiveAsString(String keyName) {
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
			throw new S3CommunicationException("Receiving as string problem", e);
		} catch (IOException e) {
			throw new S3CommunicationException("Receiving as string IO problem", e);
		}
	}
	
	public String receiveAsString(String bucketName, String keyName) {
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
			throw new S3CommunicationException("Receiving as string problem", e);
		} catch (IOException e) {
			throw new S3CommunicationException("Receiving as string IO problem", e);
		}
	}
	
	public File receiveAsFile(String key) {
		Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
		try {
			return s3Service.getObject(new S3Bucket(this.defaultBucketName), key).getDataInputFile();
		} catch (S3ServiceException e) {
			throw new S3CommunicationException("Receiving file problem", e);
		}
	}
	
	public File receiveAsFile(String bucketName, String key) {
		Assert.notNull(bucketName, "Bucket name cannot be null");
		try {
			return s3Service.getObject(new S3Bucket(bucketName), key).getDataInputFile();
		} catch (S3ServiceException e) {
			throw new S3CommunicationException("Receiving file problem", e);
		}
	}
	
	public InputStream receiveAsInputStream(String key) {
		Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
		try {
			return s3Service.getObject(new S3Bucket(this.defaultBucketName), key).getDataInputStream();
		} catch (S3ServiceException e) {
			throw new S3CommunicationException("Receiving input stream problem", e);
		}	
	}
	
	public InputStream receiveAsInputStream(String bucketName, String key) {
		Assert.notNull(bucketName, "Bucket name cannot be null");
		try {
			return s3Service.getObject(new S3Bucket(bucketName), key).getDataInputStream();
		} catch (S3ServiceException e) { 
			throw new S3CommunicationException("Receiving input stream problem", e);
		}
	}
	
	public void createBucket(String bucketName) {
		Assert.notNull(bucketName, "Bucket name cannot be null");
		try {
			s3Service.createBucket(new S3Bucket(bucketName));
		} catch (S3ServiceException e) { 
			throw new S3CommunicationException("Bucket creation problem", e);
		}
	}
	
	public void deleteBucket(String bucketName){
		Assert.notNull(bucketName, "Bucket name cannot be null");
		try {
			s3Service.deleteBucket(new S3Bucket(bucketName));
		} catch (S3ServiceException e) {
			throw new S3CommunicationException("Bucket deletion problem", e);
		}
	}
	
	public String[] listBuckets(){
		try {
			S3Bucket[] s3buckets = s3Service.listAllBuckets();
			String bucketNames[] = new String[s3buckets.length];
			for (int i=0; i< s3buckets.length; i++) bucketNames[i]=s3buckets[i].getName();
			return bucketNames;
		} catch (S3ServiceException e) {
			throw new S3CommunicationException("Bucket deletion problem", e);
		}
	}
	
	public void setDefaultBucketName(String bucketName) {
		this.defaultBucketName = bucketName;
	}
	
	public String getDefaultBucketName() {
		return this.defaultBucketName;
	}
	
	public S3Service getS3Service() {
		return s3Service;
	}

	public void setS3Service(S3Service s3Service) {
		this.s3Service = s3Service;
	}

	public void send(S3Object s3Object) {
		Assert.notNull(this.defaultBucketName, "Default bucket name is not provided");
		try {
			s3Service.putObject(new S3Bucket(this.defaultBucketName), s3Object);
		} catch (S3ServiceException e) {
			throw new S3CommunicationException("Bucket deletion problem", e);
		}
	}
	
	public void send(String bucketName, S3Object s3Object) {
		try {
			s3Service.putObject(new S3Bucket(bucketName), s3Object);
		} catch (S3ServiceException e) {
			throw new S3CommunicationException("Bucket deletion problem", e);
		}
	}
    
}
