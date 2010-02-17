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

package org.opencredo.cloud.storage.si.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.opencredo.cloud.storage.si.Constants.CONTAINER_NAME;
import static org.opencredo.cloud.storage.si.Constants.DELETE_WHEN_RECEIVED;
import static org.opencredo.cloud.storage.si.Constants.CONATINER_OBJECT_NAME;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencredo.cloud.storage.StorageOperations;
import org.opencredo.cloud.storage.test.TestPropertiesAccessor;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ToStringTransformerTest {

    private ToStringTransformer transformer;

    @Mock
    private StorageOperations s3Template;
    private final String containerName = TestPropertiesAccessor.getS3DefaultBucketName();
    private final String key = "testStringTransformer";
    private final String text = "String Transformer Test";
    private final Boolean deleteWhenReceived = true;

    @Before
    public void init() {
        transformer = new ToStringTransformer(s3Template);

        when(s3Template.receiveAsString(containerName, key)).thenReturn(text);
    }

    @Test
    public void testTransformToStringMessage() throws IOException {

        Map<String, Object> testMetaData = new HashMap<String, Object>();
        testMetaData.put(CONTAINER_NAME, containerName);
        testMetaData.put(CONATINER_OBJECT_NAME, key);
        testMetaData.put(DELETE_WHEN_RECEIVED, deleteWhenReceived);
        Message<Map<String, Object>> message = MessageBuilder.withPayload(testMetaData).build();
        Message<String> messageTransformed = transformer.transform(message);

        assertNotNull(messageTransformed);
        assertEquals("String lengths do not match", text.length(), messageTransformed.getPayload().toString().length());
    }
}
