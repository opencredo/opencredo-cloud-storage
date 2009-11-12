package com.opencredo.integration.s3;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

public class S3KeyNameGeneratorTest {
	
	private final Log logger = LogFactory.getLog(S3KeyNameGeneratorTest.class);
	
	Message<?> message;
	
	@Test
	public void testKeyNameForString(){
		MessageBuilder<String> builder = MessageBuilder.withPayload(new String("test string"));
		message = builder.build();
		S3KeyNameGenerator generator = new S3KeyNameGenerator();
		
		assertEquals("not expected key name", generator.defaultStringKey, generator.generateFileName(message));
	}
	
	@Test
	public void testKeyNameForFile() throws IOException{
		File testFile = File.createTempFile("test", ".tmp");
		testFile.deleteOnExit();
		if (logger.isDebugEnabled()) logger.debug(testFile);
		MessageBuilder<File> builder = MessageBuilder.withPayload(testFile);
		message = builder.build();
		S3KeyNameGenerator generator = new S3KeyNameGenerator();
		
		assertEquals("not expected key name", testFile.getName(), generator.generateFileName(message));
	}
}
