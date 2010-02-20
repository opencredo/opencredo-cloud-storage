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

package org.opencredo.cloud.storage.si.enricher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.StorageOperations;
import org.opencredo.cloud.storage.si.enricher.ToStringEnricher;
import org.opencredo.cloud.storage.test.TestPropertiesAccessor;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ToStringEnricherTest {

    private ToStringEnricher enricher;

    @Mock
    private StorageOperations template;
    private final String containerName = TestPropertiesAccessor.getS3DefaultBucketName();
    private final String blobName = "testStringEnricher";
    private final String text = "String Enricher Test";

    @Before
    public void init() {
        enricher = new ToStringEnricher(template);

        when(template.receiveAsString(containerName, blobName)).thenReturn(text);
    }

    @Test
    public void testEnrichToStringMessage() throws IOException {

        BlobDetails payload = new BlobDetails(containerName, blobName, ""+System.currentTimeMillis(), new Date());
        Message<BlobDetails> blobDetailsMessage = MessageBuilder.withPayload(payload).build();
        Message<String> blobMessage = enricher.transform(blobDetailsMessage);

        assertNotNull(blobMessage);
        assertEquals("String lengths do not match", text.length(), blobMessage.getPayload().toString().length());
    }
}
