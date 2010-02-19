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

package org.opencredo.cloud.storage.si.internal;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.opencredo.cloud.storage.si.internal.DefaultBlobNameBuilder;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class DefaultBlobNameBuilderTest {

    Message<?> message;

    @Test
    public void testKeyNameForString() {
        MessageBuilder<String> builder = MessageBuilder.withPayload(new String("test string"));
        message = builder.build();
        DefaultBlobNameBuilder nameBuilder = new DefaultBlobNameBuilder();

        assertTrue("not expected 'id' name", nameBuilder.createBlobName(message).startsWith(
                nameBuilder.getKeyPrefix()));
    }
}
