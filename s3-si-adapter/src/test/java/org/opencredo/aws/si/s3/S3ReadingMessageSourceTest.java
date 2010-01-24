/* Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencredo.aws.si.s3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.jets3t.service.S3ObjectsChunk;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencredo.aws.s3.S3Template;
import org.opencredo.aws.si.s3.S3ReadingMessageSource;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class S3ReadingMessageSourceTest {
	
	private S3ReadingMessageSource s3FileReadingMessageSource;
	
	private final String bucketName = "sibucket";
	private S3Bucket s3Bucket = new S3Bucket(bucketName, "LOCATION_EUROPE");
	private S3Object[] s3ObjectArray = new S3Object[]{new S3Object(s3Bucket, "test.txt")};
	
	@Mock
	private S3Template s3Template;
	
	@Test
	public void testChunkCanBeCreated(){
		S3ObjectsChunk chunk = new S3ObjectsChunk(null, null, s3ObjectArray, null, null);
		assertEquals("not expected bucket", chunk.getObjects()[0].getBucketName(), bucketName);
		assertNotNull("chunk cannot be created.", chunk);
	}
	
	@Ignore
    @Test
    public void testReceiveMessage() throws S3ServiceException {
	    fail("Not implemented yet. Method is commented by Tomas");
//    	//when(s3TemplateMock.getS3Service()).thenReturn(s3ServiceMock, s3ServiceMock, s3ServiceMock);
//    
//   
//    	when(s3Template.(anyString())).thenReturn(BUCKET_STATUS__MY_BUCKET);    	
//    	when(s3ServiceMock.listObjectsChunked(anyString(),
//	             anyString(), anyString(), anyLong(), anyString(), anyBoolean())).thenReturn(new S3ObjectsChunk(null, null, s3ObjectArray, null, null));
// 
//    	
//    	s3FileReadingMessageSource = new S3ReadingMessageSource(s3Template, bucketName);
//    	
//    	Message<Map> message = s3FileReadingMessageSource.receive();
//    	
//    	assertNotNull("Queue should not be empty at this point.", s3FileReadingMessageSource.getQueueToBeReceived());
//    	
//    	assertEquals("unexpected message content", bucketName, message.getPayload().get("bucketName"));
//    	assertEquals("unexpected key", "test.txt", message.getPayload().get("key"));
    }
    
}