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

package org.opencredo.aws.si.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencredo.storage.StorageOperations;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ToByteArrayTransformerTest {

    private ToByteArrayTransformer transformer;

    @Mock
    private StorageOperations template;

    private final String bucketName = "testBucket";
    private final String key = "testFile.test";

    String testData = "some test data";

    @Before
    public void init() throws IOException {
        transformer = new ToByteArrayTransformer(template);

        when(template.receiveAsInputStream(bucketName, key)).thenReturn(
                new ByteArrayInputStream(testData.getBytes("UTF-8")));
    }

    @Test
    public void testTransformToByteArrayMessage() throws IOException {

        Map<String, Object> testMetaData = new HashMap<String, Object>();
        testMetaData.put("bucketName", bucketName);
        testMetaData.put("key", key);
        Message<Map<String, Object>> message = MessageBuilder.withPayload(testMetaData).build();
        Message<byte[]> messageTransformed = transformer.transform(message);

        assertNotNull(messageTransformed);
        assertEquals("Byte array not correctly formed ", testData, new String(messageTransformed.getPayload(), "UTF-8"));
    }

    @After
    public void after() {
        template.deleteObject(bucketName, key);
    }
}
