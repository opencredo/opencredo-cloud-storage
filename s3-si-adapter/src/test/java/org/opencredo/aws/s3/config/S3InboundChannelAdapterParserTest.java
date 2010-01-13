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

import static org.junit.Assert.*;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Object;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencredo.aws.s3.AcceptOnceS3ObjectListFilter;
import org.opencredo.aws.s3.S3ReadingMessageSource;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.core.MessageChannel;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class S3InboundChannelAdapterParserTest {
	
	@Autowired(required = true)
	private ApplicationContext context;
	
	@Autowired(required = true)
	private S3ReadingMessageSource source;
	
	/*
	@Autowired(required = true)
	private AWSCredentials awsCredentials;
	*/

	private DirectFieldAccessor accessor;
	
	
	private final String bucketName = "sibucket";

	@Before
	public void init() throws S3ServiceException {
		accessor = new DirectFieldAccessor(source);
	}

	@Test
	public void channelNameSet() throws Exception {
		MessageChannel channel = (MessageChannel) context.getBean("inputS3Poller");
		assertEquals("Channel should be available under specified id", "inputS3Poller", channel.getName());
	}
	
	@Test
	public void inputDirectorySet() {
		String expectedBucketName = bucketName;
		String actualBucketName = accessor.getPropertyValue("bucketName").toString();
		assertEquals("'inputDirectory' should be set", expectedBucketName, actualBucketName);
	}
	
	@Test
	public void filterSet() throws Exception {
		assertTrue("'filter' should be set", accessor.getPropertyValue("filter") instanceof AcceptOnceS3ObjectListFilter);
	}

	@Test
	public void comparatorSet() throws Exception {
		Object priorityQueue = accessor.getPropertyValue("toBeReceived");
		assertEquals(PriorityBlockingQueue.class, priorityQueue.getClass());
		Object expected = context.getBean("testComparator");
		Object innerQueue = new DirectFieldAccessor(priorityQueue).getPropertyValue("q");
		Object actual = new DirectFieldAccessor(innerQueue).getPropertyValue("comparator");
		assertSame("comparator reference not set, ", expected, actual);
	}
	
	@Test
	public void deleteWhenReceivedSet () {
		assertTrue("'deleteWhenReceived' should be set", accessor.getPropertyValue("deleteWhenReceived") instanceof String);
	}
	
	@Test
	public void emptyService () {
		
	}

	static class TestComparator implements Comparator<S3Object> {

		public int compare(S3Object o1, S3Object o2) {
			return 0;
		}
	}

}
