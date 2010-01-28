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

package org.opencredo.aws.si.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jets3t.service.S3ServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencredo.aws.BlobObject;
import org.opencredo.aws.s3.BucketStatus;
import org.opencredo.aws.s3.S3Template;
import org.opencredo.aws.si.adapter.ReadingMessageSource;
import org.springframework.integration.core.Message;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ReadingMessageSourceTest {

    private ReadingMessageSource inboundAdaper;

    private final String bucketName = "sibucket";
    private BlobObject[] bucketObjects = new BlobObject[] { new BlobObject(bucketName, "test.txt", null, new Date(
            System.currentTimeMillis())) };

    @Mock
    private S3Template template;

    @Test
    public void testReceiveMessage() throws S3ServiceException {
	    List<BlobObject> l = Arrays.asList(bucketObjects);
	    
    	when(template.getBucketStatus(anyString())).thenReturn(BucketStatus.MINE);    	
    	when(template.listBucketObjects(anyString())).thenReturn(l);
    	
    	inboundAdaper = new ReadingMessageSource(template, bucketName);
    	
    	Message<Map<String, Object>> message = inboundAdaper.receive();
    	
    	assertNotNull("Queue should not be empty at this point.", inboundAdaper.getQueueToBeReceived());
    	
    	assertEquals("unexpected message content", bucketName, message.getPayload().get("bucketName"));
    	assertEquals("unexpected key", "test.txt", message.getPayload().get("key"));
    }
}