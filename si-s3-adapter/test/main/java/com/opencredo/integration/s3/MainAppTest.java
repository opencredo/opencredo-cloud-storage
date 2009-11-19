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

/*
 * Main application test class used to test if the adapter produces the 
 * expected behaviour.
 */

public class MainAppTest {
	S3InboundAdapter messageSource;
	S3MessageTransformer transformer;
	S3OutboundAdapter handler;
	
	String bucketName;
	S3Resource resource;
	
	@Before
	public void init() throws IOException, S3ServiceException{
		bucketName = new String("sibucket");
		resource = new S3Resource(bucketName);
		messageSource = new S3InboundAdapter();
		messageSource.setS3Resource(resource);
		transformer = new S3MessageTransformer();
		handler = new S3OutboundAdapter(resource);
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
		Message<S3Object> transformedMessage = transformer.transform(receivedMessage);
		
		S3Object payload = transformedMessage.getPayload();
		InputStream is = payload.getDataInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) { 
			sb.append(line + "\n"); 
		}
		sb.append(testString);

		S3Object updatedS3ObjectToSend = new S3Object(sb.toString());
		MessageBuilder<S3Object> builder = MessageBuilder.withPayload(updatedS3ObjectToSend);
		handler.handleMessage(builder.build());
		
		S3Object newObject = resource.getS3Service().getObject(resource.getS3Bucket(), updatedS3ObjectToSend.getKey());
		//Assert.assertEquals(sb.length(), new BufferedReader(new InputStreamReader(resource.getS3Service().getObject(resource.getS3Bucket(), receivedMessage.getPayload().get("key").toString()).getDataInputStream())).);
		Assert.assertEquals(sb.toString(), newObject.getKey());
	
		reader.close();
	}

}
