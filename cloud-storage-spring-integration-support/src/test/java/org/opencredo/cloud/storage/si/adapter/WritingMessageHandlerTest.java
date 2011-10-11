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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencredo.cloud.storage.StorageOperations;
import org.opencredo.cloud.storage.test.TestPropertiesAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.MessageHandlingException;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class WritingMessageHandlerTest {

    @Autowired
    @Qualifier("mockTemplate")
    private StorageOperations template;

    @Autowired
    @Qualifier("outputChannel")
    MessageChannel outputChannel;

    @Test
    public void testSendString() {
        String payload = "test-payload-" + System.currentTimeMillis();
        MessageBuilder<String> mb = MessageBuilder.withPayload(payload);

        boolean send = outputChannel.send(mb.build(), 3000);
        assertTrue("Message should be sent", send);

        verify(template).send(eq(TestPropertiesAccessor.getDefaultContainerName()), anyString(), eq(payload));
    }

    @Test
    public void testSendFile() throws IOException {
        File payload = File.createTempFile("test_file_to_send", null);
        payload.deleteOnExit();
        MessageBuilder<File> mb = MessageBuilder.withPayload(payload);

        boolean send = outputChannel.send(mb.build(), 3000);
        assertTrue("Message should be sent", send);

        verify(template).send(eq(TestPropertiesAccessor.getDefaultContainerName()), anyString(), eq(payload));
    }

    @Test(expected = MessageHandlingException.class)
    public void testSendObject() throws IOException {
        MessageBuilder<Integer> mb = MessageBuilder.withPayload(new Integer(100));
        boolean send = outputChannel.send(mb.build(), 3000);
        assertTrue("Message should be sent", send);
    }
}
