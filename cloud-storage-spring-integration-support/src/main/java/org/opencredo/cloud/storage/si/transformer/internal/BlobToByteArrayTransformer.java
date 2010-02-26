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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
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
public class BlobToByteArrayTransformer extends AbstractBlobTransformer<byte[]> {

    private final static Logger LOG = LoggerFactory.getLogger(BlobToByteArrayTransformer.class);

    /**
     * @param template
     */
    public BlobToByteArrayTransformer(StorageOperations template) {
        super(template);
    }

    /**
     * @param template
     * @param deleteBlob
     */
    public BlobToByteArrayTransformer(StorageOperations template, boolean deleteBlob) {
        super(template, deleteBlob);
    }

    /**
     * @param message
     * @throws IOException
     */
    public Message<byte[]> doTransform(Message<BlobDetails> message) throws BlobTransformException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Transform blob to byte array: '{}'", String.valueOf(message.getPayload()));
        }
        BlobDetails payload = message.getPayload();

        MessageBuilder<byte[]> builder;
        InputStream input = getTemplate().receiveAsInputStream(payload.getContainerName(), payload.getName());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            IOUtils.copy(input, output);
        } catch (IOException e) {
            throw new BlobTransformException("Failed to copy blob [" + payload + "] byte stream to byte array", e);
        }
       
        builder = (MessageBuilder<byte[]>) MessageBuilder.withPayload(output.toByteArray())//
                .copyHeaders(message.getHeaders());
        Message<byte[]> blobMessage = builder.build();

        return blobMessage;
    }
}
