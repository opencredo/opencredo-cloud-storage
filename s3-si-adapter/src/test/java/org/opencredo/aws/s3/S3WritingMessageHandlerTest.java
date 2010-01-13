package org.opencredo.aws.s3;

import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencredo.aws.s3.S3WritingMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.message.MessageBuilder;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class S3WritingMessageHandlerTest {

	//private final Log logger = LogFactory.getLog(this.getClass());
	
	private S3WritingMessageHandler systemUnderTest;
	
	@Autowired(required=true)
	private AWSCredentials awsCredentials;
	
	@Mock
	private S3Template s3Template;
	
	private MessageBuilder<File> messageBuilder;
	
	private String bucketName = "oc-test";
	
	@Before
	public void init() throws IOException{
		systemUnderTest = new S3WritingMessageHandler(awsCredentials);
		systemUnderTest.setS3Template(s3Template);
		File testHandlerFile = File.createTempFile("testHandler", "tmp");
		testHandlerFile.deleteOnExit();
		messageBuilder = MessageBuilder.withPayload(testHandlerFile);
	}
	
	@Test
	public void testSetS3ObjectCalled(){	
		systemUnderTest.handleMessage(messageBuilder.build());
		
		verify(s3Template, times(1)).send(any(File.class));

	}
		
}
