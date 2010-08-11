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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.StorageOperations;
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

    private static final String CONTAINER_NAME_PREFIX = "containerName-";
    private static final String ID_PREFIX = "id-";
    private static final String E_TAG_PREFIX = "eTag-";

    private final String containerName = TestPropertiesAccessor.getDefaultContainerName();

    private BlobDetails[] blobObjs;
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

        blobObjs = new BlobDetails[msgCount];

        for (int i = 0; i < msgCount; i++) {
            blobObjs[i] = new BlobDetails(CONTAINER_NAME_PREFIX + i, ID_PREFIX + i, E_TAG_PREFIX + i, new Date(
                    currentTime - (dayInMils * (i + 1))));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testReceiveMessage() throws InterruptedException {
        assertNotNull(template);
        assertNotNull(inputChannel);

        when(template.listContainerObjectDetails(containerName)).thenReturn(Arrays.asList(blobObjs));

        Thread.sleep(3000);

        Message<BlobDetails> msg;
        BlobDetails payload;
        for (int i = 0; i < msgCount; i++) {
            msg = (Message<BlobDetails>) inputChannel.receive(2000);
            System.out.println("Message from channel: " + msg);
            assertNotNull("Message expected", msg);

            payload = msg.getPayload();
            assertNotNull("Message payload should be not null", payload);
            assertNotNull("BlobDetails container name is missing", payload.getContainerName());
            assertTrue("BlobDetails container name should start with prefix: " + CONTAINER_NAME_PREFIX, payload
                    .getContainerName().startsWith(CONTAINER_NAME_PREFIX));

            assertNotNull("BlobDetails name is missing", payload.getName());
            assertTrue("BlobDetails name should start with prefix: " + ID_PREFIX, payload.getName().startsWith(
                    ID_PREFIX));
        }

    }
}