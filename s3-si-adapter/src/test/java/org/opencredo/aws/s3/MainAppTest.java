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

import java.io.IOException;
import java.util.Map;

import junit.framework.Assert;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.junit.Before;
import org.junit.Test;
import org.opencredo.aws.s3.S3ReadingMessageSource;
import org.opencredo.aws.s3.S3WritingMessageHandler;
import org.opencredo.aws.s3.transformer.S3ToStringTransformer;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;


/**
 * Main application test class used to test if the adapter produces the 
 * expected behaviour.
 * 
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */

public class MainAppTest {
	S3ReadingMessageSource messageSource;
	S3ToStringTransformer transformer;
	S3WritingMessageHandler handler;
	
	AWSCredentials awsCredentials;
	
	String bucketName;
	S3Template template;
	
	@Before
	public void init() throws IOException, S3ServiceException{
		bucketName = new String("sibucket");
		template = new S3Template(awsCredentials);
		messageSource = new S3ReadingMessageSource(awsCredentials);
		transformer = new S3ToStringTransformer();
		handler = new S3WritingMessageHandler(awsCredentials);
	}
	
	/*
	 * When receive is called, the next unread file in the S3Bucket is read. 
	 * The information that is read does not contain the actual content, but a meta-data map 
	 * that contains the location of the actual Object. S3MessageTransformer is used to 
	 * transform this message into a Message<S3Object>. 
	 * In this test scenario, the content is edited, and uploaded again to the S3 Bucket.
	 * Writing to the bucket is done by the S3WritingMessageHandler.
	 */
	@Test
	public void testFileContentsReadFromS3AndUploadedToS3AsString() throws IOException, S3ServiceException {
		String testString = new String("AppendedTestString");
		Message<Map> receivedMessage = messageSource.receive();
		Message<String> transformedMessage = transformer.transform(receivedMessage);
		
		String payload = transformedMessage.getPayload();
		payload.concat("\nAppended Test String");
		
		S3Object updatedS3ObjectToSend = new S3Object(payload);
		MessageBuilder<S3Object> builder = MessageBuilder.withPayload(updatedS3ObjectToSend);
		handler.handleMessage(builder.build());
		
		S3Object newObject = template.getS3Service().getObject(new S3Bucket(bucketName), updatedS3ObjectToSend.getKey());
		Assert.assertEquals(payload, newObject.getKey());
	
	}

}