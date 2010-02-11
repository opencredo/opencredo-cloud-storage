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

package org.opencredo.cloud.storage.si.adapter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencredo.cloud.storage.BlobObject;
import org.opencredo.cloud.storage.StorageOperations;
import org.opencredo.cloud.storage.si.Constants;
import org.opencredo.cloud.storage.test.TestPropertiesAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.PollableChannel;
import org.springframework.integration.core.Message;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ReadingMessageSourceTest {

    private static final String BUCKET_NAME_PREFIX = "bucketName-";
    private static final String ID_PREFIX = "id-";
    private static final String E_TAG_PREFIX = "eTag-";

    private final String bucketName = TestPropertiesAccessor.getS3DefaultBucketName();

    private BlobObject[] blobObjs;
    private long currentTime;

    private int msgCount = 4;

    @Autowired
    @Qualifier("mockTemplate")
    private StorageOperations template;

    @Autowired
    @Qualifier("inputChannel")
    PollableChannel inputChannel;

    @Before
    public void setUp() {
        currentTime = System.currentTimeMillis();
        long dayInMils = 24 * 60 * 60 * 1000;

        blobObjs = new BlobObject[msgCount];

        for (int i = 0; i < msgCount; i++) {
            blobObjs[i] = new BlobObject(BUCKET_NAME_PREFIX + i, ID_PREFIX + i, E_TAG_PREFIX + i, new Date(currentTime
                    - (dayInMils * (i + 1))));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testReceiveMessage() throws InterruptedException {
        assertNotNull(template);
        assertNotNull(inputChannel);

        when(template.listContainerObjects(bucketName)).thenReturn(Arrays.asList(blobObjs));

        Thread.sleep(3000);

        Message<Map<String, Object>> msg;
        Map<String, Object> payload;
        for (int i = 0; i < msgCount; i++) {
            msg = (Message<Map<String, Object>>) inputChannel.receive(2000);
            System.out.println("Message from channel: " + msg);
            assertNotNull("Message expected", msg);

            payload = msg.getPayload();
            assertNotNull("Message map should contain: " + Constants.BUCKET_NAME, payload.get(Constants.BUCKET_NAME));
            assertTrue("Message bucket name should start with prefix: " + BUCKET_NAME_PREFIX, payload.get(
                    Constants.BUCKET_NAME).toString().startsWith(BUCKET_NAME_PREFIX));

            assertNotNull("Message map should contain: " + Constants.ID, payload.get(Constants.ID));
            assertTrue("Message id should start with prefix: " + ID_PREFIX, payload.get(Constants.ID).toString()
                    .startsWith(ID_PREFIX));

            assertNotNull("Message map should contain: " + Constants.DELETE_WHEN_RECEIVED, payload
                    .get(Constants.DELETE_WHEN_RECEIVED));
            assertFalse((Boolean) payload.get(Constants.DELETE_WHEN_RECEIVED));
        }

    }
}