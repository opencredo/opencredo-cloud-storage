package com.opencredo.integration.s3;

import java.io.File;
import java.io.IOException;

import org.jets3t.service.model.S3Object;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
//import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.integration.message.MessageBuilder;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class S3WritingMessageHandlerTest {

	S3Resource s3Resource;
	
	S3WritingMessageHandler systemUnderTest;
	
	MessageBuilder<File> messageBuilder;
	
	@Before
	public void init() throws IOException{
		s3Resource = mock(S3Resource.class);		
		systemUnderTest = new S3WritingMessageHandler(s3Resource);
			
		messageBuilder = MessageBuilder.withPayload(File.createTempFile("test", null));
	}
	
	@Test
	public void fileUploadedToBucketTest(){
		
		systemUnderTest.handleMessage(messageBuilder.build());
		
		verify(s3Resource).setS3Object((S3Object) anyObject());
				
	}
		
}
