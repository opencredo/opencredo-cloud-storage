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

package org.opencredo.aws.si.s3;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.opencredo.aws.si.s3.S3DefaultKeyNameGenerator;
import org.opencredo.aws.si.s3.S3KeyNameGenerator;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.util.Assert;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
@Ignore ("Tomas")
public class S3KeyNameGeneratorTest {
	
	
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
		System.out.println(testFile);
		MessageBuilder<File> builder = MessageBuilder.withPayload(testFile);
		message = builder.build();
		S3KeyNameGenerator generator = new S3DefaultKeyNameGenerator();
		
		assertEquals("not expected key name", testFile.getName(), generator.generateKeyName(message));
	}
}
