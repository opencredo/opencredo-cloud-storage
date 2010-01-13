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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.opencredo.aws.s3.S3DefaultKeyNameGenerator;
import org.opencredo.aws.s3.S3KeyNameGenerator;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.util.Assert;

public class S3KeyNameGeneratorTest {
	
	private final Log logger = LogFactory.getLog(S3KeyNameGeneratorTest.class);
	
	Message<?> message;
	
	@Test
	public void testKeyNameForString(){
		MessageBuilder<String> builder = MessageBuilder.withPayload(new String("test string"));
		message = builder.build();
		S3DefaultKeyNameGenerator generator = new S3DefaultKeyNameGenerator();
		
		Assert.isTrue(generator.generateKeyName(message).startsWith(generator.getDefaultStringKeyInitial()), "not expected key name");
	}
	
	@Test
	public void testKeyNameForFile() throws IOException{
		File testFile = File.createTempFile("test", ".tmp");
		testFile.deleteOnExit();
		if (logger.isDebugEnabled()) logger.debug(testFile);
		MessageBuilder<File> builder = MessageBuilder.withPayload(testFile);
		message = builder.build();
		S3KeyNameGenerator generator = new S3DefaultKeyNameGenerator();
		
		assertEquals("not expected key name", testFile.getName(), generator.generateKeyName(message));
	}
}
