package org.opencredo.s3;

import org.jets3t.service.S3Service;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class S3TemplateTests {

	private S3Template s3Template;
	
	@Mock
	private S3Service s3Service;
	
	@Before
	public void init(){
		s3Template = new S3Template();
	}
	
	@Test
	public void sendString(){
		
		
		String testStringToSent = new String("Test string");
		s3Template.send("testKey1", testStringToSent);
		
	}
	
	/*
	@Test
	public void testReceiveString(){
		
	}
	*/
}
