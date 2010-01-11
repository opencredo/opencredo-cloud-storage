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
import org.opencredo.aws.s3.S3Resource;
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

	private DirectFieldAccessor accessor;
	
	private final String bucketName = "sibucket";

	@Before
	public void init() throws S3ServiceException {
		//context = new FileSystemXmlApplicationContext("S3InboundChannelAdapterParserTest-context.xml");
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
		String actualBucketName = ((S3Resource) accessor.getPropertyValue("s3Resource")).getS3Bucket().getName();
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
		assertTrue("'deleteWhenReceived' should be set", accessor.getPropertyValue("deleteWhenReceived") instanceof Boolean);
	}
	
	@Test
	public void deleteWhenReceived () throws InterruptedException {
		//Thread.sleep(180000);
	}

	static class TestComparator implements Comparator<S3Object> {

		public int compare(S3Object o1, S3Object o2) {
			return 0;
		}
	
	}

}
