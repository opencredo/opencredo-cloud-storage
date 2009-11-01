package com.opencredo.integration.s3;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.*;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.Constants;
import org.jets3t.service.S3ObjectsChunk;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.junit.Test;


import org.springframework.integration.core.Message;

public class S3FileReadingMessageSourceTest {
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	private static final String bucketName = "sibucket";
	
	private S3FileReadingMessageSource systemUnderTest;
	
	private final S3Bucket s3Bucket = new S3Bucket("sibucket", "LOCATION_EUROPE");
	private S3Object[] s3ObjectArray = new S3Object[]{new S3Object(s3Bucket, "test.txt")};
	
    @Test
    public void testQueueToBeReceivedNotNull() throws S3ServiceException {
    	S3Bucket s3BucketMock = mock(S3Bucket.class);
    	S3Service s3ServiceMock = mock(RestS3Service.class);
    	systemUnderTest = new S3FileReadingMessageSource(s3ServiceMock, s3BucketMock);
    	
    	when(s3BucketMock.getName()).thenReturn("BUCKET_STATUS__MY_BUCKET");
    	when(s3ServiceMock.listObjectsChunked(eq(bucketName),
	             anyString(), anyString(), eq(Constants.DEFAULT_OBJECT_LIST_CHUNK_SIZE), anyString(), eq(true))).thenReturn(new S3ObjectsChunk(null, null, s3ObjectArray, null, null));
	    
    	Message<Map> message = systemUnderTest.receive();
    	
    	assertNotNull("Queue should not be empty at this point.", systemUnderTest.getQueueToBeReceived());   	 
    }
    
}