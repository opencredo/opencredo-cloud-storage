package com.opencredo.integration.s3;

import static org.jets3t.service.S3Service.BUCKET_STATUS__MY_BUCKET;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Map;

import org.jets3t.service.Constants;
import org.jets3t.service.S3ObjectsChunk;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3BucketLoggingStatus;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.model.S3Owner;
import org.jets3t.service.security.AWSCredentials;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import org.springframework.integration.core.Message;


public class S3FileReadingMessageSourceTest {
	
	private static final String bucketName = "sibucket";
	
	private S3FileReadingMessageSource systemUnderTest;
	
	private final S3Bucket s3Bucket = new S3Bucket("sibucket", "LOCATION_EUROPE");
	private S3Object[] s3ObjectArray = new S3Object[]{new S3Object(s3Bucket, "test.txt")};
	
	private S3Service s3ServiceMock;
	private S3Bucket s3BucketMock;

	@Before
    public void doBeforeTests() throws S3ServiceException {
        //MockitoAnnotations.initMocks(this);
		//s3Service = new RestS3Service(new AWSCredentials("AKIAJJC4KITQHSAY43MQ","U0H0Psg7aS5qrKpLFqZXFUUOq2rK6l2xAfHxZWTd"));
		//s3Bucket = new S3Bucket("sibucket", "LOCATION_EUROPE");
		s3ServiceMock = mock(RestS3Service.class);
		s3BucketMock = mock(S3Bucket.class);
        systemUnderTest = new S3FileReadingMessageSource(s3ServiceMock, s3BucketMock);
	}
 
    @Test
    public void testReceiveNotNull() throws S3ServiceException {
    	
    	Message<S3Object> message = systemUnderTest.receive();
    	assertNotNull("returned message shouldn't be null", message);   	 
    }
    
    @Test
    public void testReceiveUnsentObjectsAreDetected() throws S3ServiceException {
    	when(s3ServiceMock.listObjectsChunked(bucketName,
	             null, null, Constants.DEFAULT_OBJECT_LIST_CHUNK_SIZE, null, true)).thenReturn(new S3ObjectsChunk(null, null, s3ObjectArray, null, null));
    	
    	Message<S3Object> message = systemUnderTest.receive();
    	
    	Assert.assertFalse(systemUnderTest.getQueueToBeReceived().isEmpty());
    		 
    }

}