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

package org.opencredo.cloud.storage.si.transformer.internal;

import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.StorageOperations;
import org.opencredo.cloud.storage.si.transformer.AbstractBlobTransformer;
import org.opencredo.cloud.storage.si.transformer.BlobTransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class BlobToStringTransformer extends AbstractBlobTransformer<String> {

    private final static Logger LOG = LoggerFactory.getLogger(BlobToStringTransformer.class);

    /**
     * @param template
     */
    public BlobToStringTransformer(StorageOperations template) {
        super(template);
    }

    /**
     * @param template
     * @param deleteBlob
     */
    public BlobToStringTransformer(StorageOperations template, boolean deleteBlob) {
        super(template, deleteBlob);
    }

    /**
     * @param message
     */
    protected Message<String> doTransform(Message<BlobDetails> message) throws BlobTransformException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Enrich to string: '{}'", String.valueOf(message.getPayload()));
        }

        BlobDetails payload = message.getPayload();

        String blobAsString = getTemplate().receiveAsString(payload.getContainerName(), payload.getName());

        MessageBuilder<String> builder = (MessageBuilder<String>) MessageBuilder.withPayload(blobAsString)//
                .copyHeaders(message.getHeaders());
        Message<String> blobMessage = builder.build();

        return blobMessage;

    }
}