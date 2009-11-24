/* Copyright 2008 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.opencredo.integration.s3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


public class S3Resource implements Resource{

	private S3Service s3Service;
	private S3Bucket s3Bucket;
	private File s3File = null;
	private S3Object s3Object = null;
	public static final AWSCredentials awsCredentials = new AWSCredentials("AKIAJJC4KITQHSAY43MQ", "U0H0Psg7aS5qrKpLFqZXFUUOq2rK6l2xAfHxZWTd");
	
	private final Log logger = LogFactory.getLog(S3WritingMessageHandler.class);
	
	public S3Resource(String bucketName){
		try {
			s3Service = new RestS3Service(awsCredentials);	
			s3Bucket = s3Service.getBucket(bucketName);
			Assert.notNull(s3Bucket, "bucket should not be null");
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
	
	public void sendFileToS3() throws NoSuchAlgorithmException, S3ServiceException, IOException{
		Assert.notNull(s3File, "s3File should not be null");
		s3Service.putObject(s3Bucket, new S3Object(s3File));
	}
	
	public void sendFileToS3(File file) throws NoSuchAlgorithmException, S3ServiceException, IOException{
		setFile(file);
		sendFileToS3();
	}
	
	public void sendS3ObjectToS3() throws S3ServiceException{
		Assert.notNull(s3Object, "s3File should not be null");
		s3Service.putObject(s3Bucket, s3Object);
	}
	
	public void sendS3ObjectToS3(S3Object s3object) throws S3ServiceException{
		setS3Object(s3object);
		sendS3ObjectToS3();
	}
	
	/*
	 * Changes the key value of the S3Object that resides in the same bucket
	 */
	public S3Resource createRelative(String relativePath) throws IOException {
		S3Resource newResource;
		newResource = new S3Resource(this.s3Bucket.getName());
		if (this.getFile() != null){
			if (logger.isDebugEnabled()) logger.debug("filePath:"+ this.s3File.getPath());
			newResource.setFile(new File(StringUtils.applyRelativePath(this.s3File.getPath(), relativePath)));
			return newResource;
		}
		else return null;
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

	/*
	 * return the URL in place of URI
	 * @see org.springframework.core.io.Resource#getURI()
	 */
	public URI getURI() throws IOException {
		try {
			return getURL().toURI();
		} 
		catch (URISyntaxException e) {
			if (logger.isDebugEnabled()) e.printStackTrace();
			return null;
		}
	}

	/*
	 * URL for publicly accessible S3Objects
	 * @see org.springframework.core.io.Resource#getURL()
	 */
	public URL getURL() throws IOException {
		if (this.s3File != null)
			return new URL("http://"+this.s3Bucket.getName()+".s3.amazonaws.com/"+this.s3File.getName());
		else if (this.s3Object != null)
			return new URL("http://"+this.s3Bucket.getName()+".s3.amazonaws.com/"+this.s3Object.getKey());
		else return null;
	}

	/*
	 * This resource does not represent a handle with an open stream,
	 * However contents of an S3Object can be read as an inputStream.
	 */
	public boolean isOpen() {
		return false;
	}

	public boolean isReadable() {
		return true;
	}

	public long lastModified() throws IOException {
		Assert.notNull(s3File, "File shouldn't be null");
		try {
			if (logger.isDebugEnabled()) logger.debug(s3Service.getObjectDetails(s3Bucket, s3File.getName()).getMetadata("METADATA_HEADER_LAST_MODIFIED_DATE"));
			return Long.parseLong(s3Service.getObjectDetails(s3Bucket, s3File.getName()).getMetadata("METADATA_HEADER_LAST_MODIFIED_DATE").toString());
		} 
		catch (S3ServiceException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public InputStream getInputStream() throws IOException {				
		try {
			return s3Object.getDataInputStream();
		} 
		catch (S3ServiceException e) {
			e.printStackTrace();
			return null;
		}
	}

}
