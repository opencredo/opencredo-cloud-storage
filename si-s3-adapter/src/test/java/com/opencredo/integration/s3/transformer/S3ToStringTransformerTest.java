package com.opencredo.integration.s3.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Object;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.core.Message;

import com.opencredo.integration.s3.S3Resource;

public class S3ToStringTransformerTest {
	
	private S3ToStringTransformer systemUnderTest;
	
	private final String bucketName = "oc-test";
	private final String key = "testStringTransformer";
	private final String text = "String Transformer Test";
	
	
	@Before
    public void init() throws S3ServiceException, IOException, NoSuchAlgorithmException { 
        systemUnderTest = new S3ToStringTransformer();
        RestS3Service service = new RestS3Service(S3Resource.awsCredentials);
		S3Object s3ObjectToUpload = new S3Object(key, text);
		service.putObject(bucketName, s3ObjectToUpload); 
    }
    
	
	@SuppressWarnings("unchecked")
	@Test
	public void testTransformToStringMessage() throws IOException {

		Message<Map> messageToTransformMock = mock(Message.class);
		Map<String, Object> testMetaData = new HashMap<String, Object>();
		testMetaData.put("bucketName", bucketName);
		testMetaData.put("key", key);
		//testMetaData.put("deleteWhenReceived", "true");
		when(messageToTransformMock.getPayload()).thenReturn(testMetaData);
		
		Message<String> messageTransformed = systemUnderTest.transform(messageToTransformMock);
		
		assertNotNull(messageTransformed);
		assertEquals("String lengths do not match", text.length(), messageTransformed.getPayload().toString().length());
	 }
	
	 @After
	 public void after() throws S3ServiceException{
		 RestS3Service service = new RestS3Service(S3Resource.awsCredentials);
		 service.deleteObject(bucketName, key);
	 }
	 
}
