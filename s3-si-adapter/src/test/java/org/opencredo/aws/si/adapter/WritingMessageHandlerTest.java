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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencredo.aws.AwsOperations;
import org.opencredo.aws.s3.TestPropertiesAccessor;
import org.opencredo.aws.si.adapter.WritingMessageHandler;
import org.springframework.integration.message.MessageBuilder;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class WritingMessageHandlerTest {

    private WritingMessageHandler outboundAdapter;

    @Mock
    private AwsOperations mockTemplate;

    private MessageBuilder<File> messageBuilder;

    private String bucketName = TestPropertiesAccessor.getS3DefaultBucketName();

    @Before
    public void init() throws IOException {
        outboundAdapter = new WritingMessageHandler(mockTemplate, bucketName);

        File testHandlerFile = File.createTempFile("testHandler", "tmp");
        testHandlerFile.deleteOnExit();

        messageBuilder = MessageBuilder.withPayload(testHandlerFile);
    }

    @Test
    public void testSetS3ObjectCalled() {
        outboundAdapter.handleMessage(messageBuilder.build());

        verify(mockTemplate, times(1)).send(anyString(), anyString(), any(File.class));
    }

}
