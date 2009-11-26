package com.opencredo.integration.s3;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;
import java.util.Map;

import org.jets3t.service.S3ObjectsChunk;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.integration.core.Message;
import static  org.jets3t.service.S3Service.*;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class S3ReadingMessageSourceTests {
	
	private S3ReadingMessageSource s3FileReadingMessageSource;
	
	private static final String bucketName = "sibucket";
	private S3Bucket s3Bucket = new S3Bucket(bucketName, "LOCATION_EUROPE");
	private S3Object[] s3ObjectArray = new S3Object[]{new S3Object(s3Bucket, "test.txt")};
	
	@Mock
	private S3Bucket s3BucketMock;
	
	@Mock
	private S3Service s3ServiceMock;
	
	@Mock
	private S3Resource s3resourceMock;
	
	@Test
	public void testChunkCanBeCreated(){
		S3ObjectsChunk chunk = new S3ObjectsChunk(null, null, s3ObjectArray, null, null);
		assertEquals("not expected bucket", chunk.getObjects()[0].getBucketName(), bucketName);
		assertNotNull("chunk cannot be created.", chunk);
	}
	
    @Test
    public void testReceiveMessage() throws S3ServiceException {

    	when(s3resourceMock.getS3Service()).thenReturn(s3ServiceMock, s3ServiceMock, s3ServiceMock);
    	when(s3resourceMock.getS3Bucket()).thenReturn(s3BucketMock, s3BucketMock, s3BucketMock);
    	
    	when(s3ServiceMock.checkBucketStatus(anyString())).thenReturn(BUCKET_STATUS__MY_BUCKET);    	
    	when(s3ServiceMock.listObjectsChunked(anyString(),
	             anyString(), anyString(), anyLong(), anyString(), anyBoolean())).thenReturn(new S3ObjectsChunk(null, null, s3ObjectArray, null, null));
    	when(s3BucketMock.getName()).thenReturn(bucketName,bucketName);
    	
    	s3FileReadingMessageSource = new S3ReadingMessageSource();
    	s3FileReadingMessageSource.setS3Resource(s3resourceMock);
    	
    	Message<Map> message = s3FileReadingMessageSource.receive();
    	
    	assertNotNull("Queue should not be empty at this point.", s3FileReadingMessageSource.getQueueToBeReceived());
    	
    	assertEquals("unexpected message content", bucketName, message.getPayload().get("bucketName"));
    	assertEquals("unexpected key", "test.txt", message.getPayload().get("key"));
    }
    
}