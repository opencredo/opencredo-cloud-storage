package com.opencredo.integration.s3;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.jets3t.service.S3Service.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.integration.core.Message;

public class S3FileReadingMessageSourceTest {
	
	private static final String bucketName = "sibucket";
	
	private S3FileReadingMessageSource systemUnderTest;
	
	@Mock
	S3Service s3ServiceMock;
	
	@Mock
	S3Bucket s3BucketMock;

	@Before
    public void doBeforeEachTestCase() {
        MockitoAnnotations.initMocks(this);
        systemUnderTest = new S3FileReadingMessageSource(s3ServiceMock, s3BucketMock);
    }
 
    @Test
    public void testConnectionToBundle() throws S3ServiceException {
        when(s3ServiceMock.checkBucketStatus(bucketName)).thenReturn(BUCKET_STATUS__MY_BUCKET);
        verify(s3ServiceMock).checkBucketStatus(bucketName);
    }
 
    @Test
    public void testReceive() throws S3ServiceException {
    	 Message<S3Object> message = systemUnderTest.receive();
    	 assertNotNull("returned message shouldn't be null", message);
    	 
    }

}