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

package org.opencredo.aws.s3.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
	
import org.opencredo.aws.s3.AWSCredentials;
import org.opencredo.aws.s3.S3KeyNameGenerator;
import org.opencredo.aws.s3.S3WritingMessageHandler;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.core.Message;
import org.springframework.integration.endpoint.EventDrivenConsumer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class S3OutboundChannelAdapterParserTest {
	
	@Autowired(required = true)
	private AWSCredentials awsCredentials;
	
	@Autowired
	@Qualifier("simpleAdapter")
	EventDrivenConsumer simpleAdapter;

	@Autowired
	@Qualifier("adapterWithCustomNameGenerator")
	EventDrivenConsumer adapterWithCustomKeyNameGenerator;

	@Test
	public void simpleAdapter() {
		DirectFieldAccessor adapterAccessor = new DirectFieldAccessor(simpleAdapter);
		S3WritingMessageHandler handler = (S3WritingMessageHandler) adapterAccessor.getPropertyValue("handler");
		DirectFieldAccessor handlerAccessor = new DirectFieldAccessor(handler);
		String expectedBucket = "oc-test";
		String actualBucket = handlerAccessor.getPropertyValue("bucketName").toString();
		assertEquals(expectedBucket, actualBucket);
		assertTrue(handlerAccessor.getPropertyValue("s3KeyNameGenerator") instanceof S3KeyNameGenerator);
	}

	@Test
	public void adapterWithCustomKeyNameGenerator() {
		DirectFieldAccessor adapterAccessor = new DirectFieldAccessor(adapterWithCustomKeyNameGenerator);
		S3WritingMessageHandler handler = (S3WritingMessageHandler) adapterAccessor.getPropertyValue("handler");
		DirectFieldAccessor handlerAccessor = new DirectFieldAccessor(handler);
		String expectedBucket = "oc-test";
		String actualBucket = handlerAccessor.getPropertyValue("bucketName").toString();
		assertEquals(expectedBucket, actualBucket);
		assertTrue(handlerAccessor.getPropertyValue("s3KeyNameGenerator") instanceof CustomKeyNameGenerator);
	}

	static class CustomKeyNameGenerator implements S3KeyNameGenerator {
		public String generateKeyName(Message<?> message) {
			return "test";
		}
	}
	
}
