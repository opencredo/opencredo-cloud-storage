package org.opencredo.aws.s3.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
	
import org.opencredo.aws.s3.S3KeyNameGenerator;
import org.opencredo.aws.s3.S3Resource;
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
		String actualBucket = ((S3Resource) handlerAccessor.getPropertyValue("s3Resource")).getS3Bucket().getName();
		assertEquals(expectedBucket, actualBucket);
		assertTrue(handlerAccessor.getPropertyValue("s3KeyNameGenerator") instanceof S3KeyNameGenerator);
	}

	@Test
	public void adapterWithCustomKeyNameGenerator() {
		DirectFieldAccessor adapterAccessor = new DirectFieldAccessor(adapterWithCustomKeyNameGenerator);
		S3WritingMessageHandler handler = (S3WritingMessageHandler) adapterAccessor.getPropertyValue("handler");
		DirectFieldAccessor handlerAccessor = new DirectFieldAccessor(handler);
		String expectedBucket = "oc-test";
		String actualBucket = ((S3Resource) handlerAccessor.getPropertyValue("s3Resource")).getS3Bucket().getName();
		assertEquals(expectedBucket, actualBucket);
		assertTrue(handlerAccessor.getPropertyValue("s3KeyNameGenerator") instanceof CustomKeyNameGenerator);
	}

	static class CustomKeyNameGenerator implements S3KeyNameGenerator {
		public String generateKeyName(Message<?> message) {
			return "test";
		}
	}
	
}
