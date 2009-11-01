package com.opencredo.integration.s3;

import java.io.File;
import java.io.IOException;

import org.jets3t.service.model.S3Object;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

import static org.mockito.Mockito.*;

public class S3WritingMessageHandlerTest {

	S3Resource s3Resource;
	S3WritingMessageHandler systemUnderTest;
	
	@Before
	public void init(){
		s3Resource = mock(S3Resource.class);		
		systemUnderTest = new S3WritingMessageHandler(s3Resource);
	}
	
	@Test
	public void handleMessageCallsSetS3ObjectOnS3Resource(){
		try {
			MessageBuilder<File> messageBuilder;
			messageBuilder = MessageBuilder.withPayload(File.createTempFile("test", null));
		
			systemUnderTest.handleMessage(messageBuilder.build());
		
			verify(s3Resource).setS3Object((S3Object) anyObject());
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
