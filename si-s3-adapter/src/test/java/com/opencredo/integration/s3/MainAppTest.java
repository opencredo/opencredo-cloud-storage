package com.opencredo.integration.s3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import junit.framework.Assert;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Object;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

import com.opencredo.integration.s3.transformer.S3ToStringTransformer;

/*
 * Main application test class used to test if the adapter produces the 
 * expected behaviour.
 */

public class MainAppTest {
	S3ReadingMessageSource messageSource;
	S3ToStringTransformer transformer;
	S3WritingMessageHandler handler;
	
	String bucketName;
	S3Resource resource;
	
	@Before
	public void init() throws IOException, S3ServiceException{
		bucketName = new String("sibucket");
		resource = new S3Resource(bucketName);
		messageSource = new S3ReadingMessageSource();
		messageSource.setS3Resource(resource);
		transformer = new S3ToStringTransformer();
		handler = new S3WritingMessageHandler(resource);
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
		
		S3Object newObject = resource.getS3Service().getObject(resource.getS3Bucket(), updatedS3ObjectToSend.getKey());
		Assert.assertEquals(payload, newObject.getKey());
	
	}
	
	@Test
	public void testS3ObjectDeletedIfAdapterFlagIsSet() {
		
	}

}
