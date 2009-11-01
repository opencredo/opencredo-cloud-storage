package com.opencredo.integration.s3;

import java.util.HashMap;
import java.util.Map;

import org.jets3t.service.model.S3Object;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.integration.core.Message;

public class S3MessageTransformerTest {
	
     private S3MessageTransformer systemUnderTest;
	
	 @Before
     public void init() { 
         systemUnderTest = new S3MessageTransformer();
     }
	 
	 @Test
	 public void testTransformedMessageNotNull() {
		 
		 Message<Map> messageToTransformMock = mock(Message.class);
		 Map<String, Object> testMetaData = new HashMap<String, Object>();
		 testMetaData.put("bucketName", "sibucket");
		 testMetaData.put("key", "test.txt");
		 when(messageToTransformMock.getPayload()).thenReturn(testMetaData);
		 
		 Message<S3Object> messageTransformed = systemUnderTest.transform(messageToTransformMock);
		 
		 assertNotNull(messageTransformed);
	 }
	 
}
