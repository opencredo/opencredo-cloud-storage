package com.opencredo.integration.s3;

import org.jets3t.service.model.S3Object;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.springframework.integration.core.Message;

public class S3MessageTransformerTest {
	
	 
     private S3MessageTransformer systemUnderTest;
	
	 @Before
     public void init() { 
         systemUnderTest = new S3MessageTransformer();
     }
	 
	 @Test
	 public void testTransformedMessageNotNull() {
		 
		 Message<S3Object> messageToTransformMock = mock(Message.class);
		 when(messageToTransformMock.getPayload()).thenReturn(new S3Object());
		 Message<S3Object> messageTransformed = systemUnderTest.transform(messageToTransformMock);
		 assertNotNull(messageTransformed);
	 }
	 
}
