package com.opencredo.integration.s3;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.springframework.integration.core.Message;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.message.MessageBuilder;

public class S3KeyNameGeneratorTest {
	
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
		MessageBuilder<File> builder = MessageBuilder.withPayload(File.createTempFile("test", "test"));
		message = builder.build();
		S3KeyNameGenerator generator = new S3KeyNameGenerator();
		
		assertNotNull(message.getHeaders().get(FileHeaders.FILENAME));
		assertEquals("not expected key name", "test.test", generator.generateFileName(message));
	}
}
