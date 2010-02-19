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

import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.StorageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class ToStringTransformer {

    private final static Logger LOG = LoggerFactory.getLogger(ToStringTransformer.class);

    private final StorageOperations template;

    /**
     * @param template
     */
    public ToStringTransformer(StorageOperations template) {
        this.template = template;
    }

    /**
     * @param message
     */
    public Message<String> transform(Message<BlobDetails> message) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Transform to string: '{}'", String.valueOf(message.getPayload()));
        }

        BlobDetails payload = message.getPayload();

        String transformedString = template.receiveAsString(payload.getContainerName(), payload.getName());

        MessageBuilder<String> builder = (MessageBuilder<String>) MessageBuilder.withPayload(transformedString);
        Message<String> transformedMessage = builder.build();

        return transformedMessage;

    }
}