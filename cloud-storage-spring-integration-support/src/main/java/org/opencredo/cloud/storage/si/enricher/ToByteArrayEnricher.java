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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
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
public class ToByteArrayEnricher {

    private final static Logger LOG = LoggerFactory.getLogger(ToByteArrayEnricher.class);

    private StorageOperations template;

    public ToByteArrayEnricher(StorageOperations template) {
        this.template = template;
    }

    /**
     * @param message
     * @throws IOException
     */
    public Message<byte[]> transform(Message<BlobDetails> message) throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Transform to byte array: '{}'", String.valueOf(message.getPayload()));
        }
        BlobDetails payload = message.getPayload();

        MessageBuilder<byte[]> builder;
        InputStream input = template.receiveAsInputStream(payload.getContainerName(), payload.getName());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        IOUtils.copy(input, output);

        builder = (MessageBuilder<byte[]>) MessageBuilder.withPayload(output.toByteArray());
        Message<byte[]> transformedMessage = builder.build();

        return transformedMessage;
    }

}
