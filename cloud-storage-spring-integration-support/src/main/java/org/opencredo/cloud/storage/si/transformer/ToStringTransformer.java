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

import static org.opencredo.cloud.storage.si.Constants.BUCKET_NAME;
import static org.opencredo.cloud.storage.si.Constants.DELETE_WHEN_RECEIVED;
import static org.opencredo.cloud.storage.si.Constants.ID;

import java.util.Map;

import org.opencredo.cloud.storage.StorageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.util.Assert;

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
    public Message<String> transform(Message<Map<String, Object>> message) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Transform to string: '{}'", String.valueOf(message.getPayload()));
        }

        Map<String, Object> payload = message.getPayload();

        Assert.notNull(payload.get(BUCKET_NAME), "Bucket name must be specified in the header");
        Assert.notNull(payload.get(ID), "Bucket name must be specified in the header");

        String transformedString = template.receiveAsString(payload.get(BUCKET_NAME).toString(), payload.get(ID)
                .toString());

        MessageBuilder<String> builder = (MessageBuilder<String>) MessageBuilder.withPayload(transformedString);
        Message<String> transformedMessage = builder.build();

        Boolean delete = (Boolean) payload.get(DELETE_WHEN_RECEIVED);
        if (delete != null && delete == true) {
            template.deleteObject(payload.get(BUCKET_NAME).toString(), payload.get(ID).toString());
        }
        return transformedMessage;

    }
}