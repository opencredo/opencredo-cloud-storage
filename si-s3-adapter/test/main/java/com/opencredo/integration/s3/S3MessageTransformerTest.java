package com.opencredo.integration.s3;

import org.jets3t.service.model.S3Object;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

public class S3MessageTransformerTest {
	
	 private Message<S3Object> message; 
	
     private S3MessageTransformer systemUnderTest;
	
	 @Before
     public void doBeforeEachTestCase() {
		 S3Object s3Object = new S3Object();
		 s3Object.setBucketName("sibucket");
		 //TODO: Complete the s3object properties
		 message = MessageBuilder.withPayload(s3Object).build(); 
         systemUnderTest = new S3MessageTransformer();
     }
	 
	 @Test
	 public void testTransform() {
	     Message<S3Object> transformedMessage = systemUnderTest.transform(message);
	     assertNotNull("returned message shouldn't be null", transformedMessage);
	    	 
	 }
	 
}
