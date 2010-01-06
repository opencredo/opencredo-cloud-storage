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
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.model.S3Object;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.util.Assert;

/**
 * MessageHandler for writing S3Objects to a Bucket.
 * Depending on the Message's payload, the relevant handler turns the
 * payload into an S3Object. If the payload is string, the content is written to 
 * a default destination. If the payload is S3Object or File, it should have the
 * filename property in it's header. 
 */
public class S3WritingMessageHandler implements MessageHandler, InitializingBean {
	
	private final Log logger = LogFactory.getLog(S3WritingMessageHandler.class);
	
	private volatile S3KeyNameGenerator s3KeyNameGenerator = new S3DefaultKeyNameGenerator();
	
	private S3Resource s3Resource;

	public S3WritingMessageHandler(S3Resource s3resource){
		this.s3Resource = s3resource;
	}

	/**
	 * write the content of Message to S3 Bucket with handlers that convert the 
	 * message payload to s3object.
	 */
    public void handleMessage(Message<?> message){
    	Assert.notNull(message, "message must not be null");
		Object payload = message.getPayload();
		if (logger.isDebugEnabled()) logger.debug("message: "+message);
		String generatedKeyName = s3KeyNameGenerator.generateKeyName(message);
		Assert.notNull(payload, "message payload must not be null");
		S3Object objectToSend = null;
		try {
			if (payload instanceof File) {
				objectToSend = fileToS3Handler((File) payload);
			}
			else if(payload instanceof String){
				objectToSend = stringToS3Handler((String) payload, generatedKeyName);
			}
			else if(payload instanceof S3Object){
				objectToSend = (S3Object) payload;
				objectToSend.setKey(generatedKeyName);
			}
			else {
				throw new IllegalArgumentException(
						"unsupported Message payload type [" + payload.getClass().getName() + "]");
			}
			s3Resource.setS3Object(objectToSend);
			s3Resource.sendS3ObjectToS3();
		}
		catch (IllegalArgumentException e) {
			throw new S3IntegrationException("Illegal Argument", e);
		}
		catch (Exception e) {
			throw new MessageHandlingException(message, "failed to write Message payload to file", e);
		}	
	}
    
    private S3Object fileToS3Handler(File fileInput){
    	try {
			return new S3Object(fileInput);
		} 
    	catch (NoSuchAlgorithmException e) {
    		throw new S3IntegrationException("fileToS3Handler, No Such Algorithm", e);
		} 
    	catch (IOException e) {
    		throw new S3IntegrationException("fileToS3Handler, IO Problem", e);
		}
    }
    
    private S3Object stringToS3Handler(String stringInput, String generatedKeyName){ 
    	try {
			return new S3Object(generatedKeyName, stringInput);
		} 
    	catch (NoSuchAlgorithmException e) {
    		throw new S3IntegrationException("stringToS3Handler, No Such Algorithm", e);
		} 
    	catch (IOException e) {
    		throw new S3IntegrationException("stringToS3Handler, IO Problem", e);
		}
    }
    
	public void setS3KeyNameGenerator(S3KeyNameGenerator fileNameGenerator) {
		Assert.notNull(fileNameGenerator, "FileNameGenerator must not be null");
		this.s3KeyNameGenerator = fileNameGenerator;
	}
		
	public S3Resource getS3Resource() {
		return s3Resource;
	}

	public void setS3Resource(S3Resource s3Resource) {
		this.s3Resource = s3Resource;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.isTrue(this.s3Resource.exists(),
				"Source directory [" + s3Resource + "] does not exist.");
		Assert.isTrue(this.s3Resource.isReadable(),
				"Source directory [" + this.s3Resource + "] is not readable.");
	}
    
}
