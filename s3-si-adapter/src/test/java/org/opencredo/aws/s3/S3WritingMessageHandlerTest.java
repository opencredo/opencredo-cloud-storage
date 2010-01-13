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
