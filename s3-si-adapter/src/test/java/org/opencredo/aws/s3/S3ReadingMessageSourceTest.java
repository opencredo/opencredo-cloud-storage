package org.opencredo.aws.s3;

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
import org.opencredo.aws.s3.S3ReadingMessageSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.core.Message;
import static  org.jets3t.service.S3Service.*;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class S3ReadingMessageSourceTest {
	
	private S3ReadingMessageSource s3FileReadingMessageSource;
	
	private static final String bucketName = "sibucket";
	private S3Bucket s3Bucket = new S3Bucket(bucketName, "LOCATION_EUROPE");
	private S3Object[] s3ObjectArray = new S3Object[]{new S3Object(s3Bucket, "test.txt")};
	
	private ClassPathXmlApplicationContext context;
	private AWSCredentials awsCredentials;
	
	@Mock
	private S3Service s3ServiceMock;
	
	@Test
	public void testChunkCanBeCreated(){
		context = new ClassPathXmlApplicationContext("credentials-context.xml");
		awsCredentials = (AWSCredentials) context.getBean("awsCredentials");
		S3ObjectsChunk chunk = new S3ObjectsChunk(null, null, s3ObjectArray, null, null);
		assertEquals("not expected bucket", chunk.getObjects()[0].getBucketName(), bucketName);
		assertNotNull("chunk cannot be created.", chunk);
	}
	
    @Test
    public void testReceiveMessage() throws S3ServiceException {

    	//when(s3TemplateMock.getS3Service()).thenReturn(s3ServiceMock, s3ServiceMock, s3ServiceMock);
    
   
    	when(s3ServiceMock.checkBucketStatus(anyString())).thenReturn(BUCKET_STATUS__MY_BUCKET);    	
    	when(s3ServiceMock.listObjectsChunked(anyString(),
	             anyString(), anyString(), anyLong(), anyString(), anyBoolean())).thenReturn(new S3ObjectsChunk(null, null, s3ObjectArray, null, null));
 
    	
    	s3FileReadingMessageSource = new S3ReadingMessageSource(awsCredentials);
    	
    	Message<Map> message = s3FileReadingMessageSource.receive();
    	
    	assertNotNull("Queue should not be empty at this point.", s3FileReadingMessageSource.getQueueToBeReceived());
    	
    	assertEquals("unexpected message content", bucketName, message.getPayload().get("bucketName"));
    	assertEquals("unexpected key", "test.txt", message.getPayload().get("key"));
    }
    
}