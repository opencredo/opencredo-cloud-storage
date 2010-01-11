package org.opencredo.aws.s3;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.model.S3Object;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencredo.aws.s3.S3Resource;
import org.opencredo.aws.s3.S3WritingMessageHandler;
import org.springframework.integration.message.MessageBuilder;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class S3WritingMessageHandlerTest {

	private final Log logger = LogFactory.getLog(this.getClass());
	
	S3Resource s3Resource;
	
	S3WritingMessageHandler systemUnderTest;
	
	MessageBuilder<File> messageBuilder;
	
	@Before
	public void init() throws IOException{
		s3Resource = mock(S3Resource.class);		
		systemUnderTest = new S3WritingMessageHandler(s3Resource);
		File testHandler = File.createTempFile("testHandler", "tmp");
		testHandler.deleteOnExit();
		messageBuilder = MessageBuilder.withPayload(testHandler);
	}
	
	@Test
	public void testSetS3ObjectCalled(){	
		systemUnderTest.handleMessage(messageBuilder.build());
		
		verify(s3Resource).setS3Object((S3Object) anyObject());
				
	}
		
}
