/* Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencredo.cloud.storage.si.adapter;

import org.opencredo.cloud.storage.ContainerStatus;
import org.opencredo.cloud.storage.StorageOperations;
import org.opencredo.cloud.storage.si.BlobNameBuilder;
import org.opencredo.cloud.storage.si.internal.DefaultBlobNameBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHandlingException;
import org.springframework.integration.core.MessageHandler;
import org.springframework.util.Assert;

import java.io.File;

/**
 * MessageHandler for writing blobs to the cloud storage. Depending on the
 * Message's payload, the relevant handler turns the payload into an Blob.
 *
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class WritingMessageHandler implements MessageHandler, InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(WritingMessageHandler.class);

    private final StorageOperations template;
    private final String containerName;
    private final BlobNameBuilder blobNameBuilder;

    /**
     * @param template
     * @param containerName
     */
    public WritingMessageHandler(StorageOperations template, String containerName) {
        this(template, containerName, new DefaultBlobNameBuilder());
    }

    /**
     * @param template
     * @param containerName
     * @param blobNameBuilder
     */
    public WritingMessageHandler(StorageOperations template, String containerName, BlobNameBuilder blobNameBuilder) {
        super();
        Assert.notNull(template, "'template' should not be null");
        Assert.notNull(blobNameBuilder, "'blob name builder' should not be null");
        this.template = template;
        this.containerName = containerName;
        this.blobNameBuilder = blobNameBuilder;
    }

    /**
     * write the content of Message to the cloud storage with handlers that
     * convert the message payload to Blob.
     *
     * @param message
     */
    public void handleMessage(Message<?> message) {
        Assert.notNull(message, "message must not be null");
        Assert.notNull(message.getPayload(), "message payload must not be null");
        Object payload = message.getPayload();

        String blobName = blobNameBuilder.createBlobName(message);
        LOG.debug("Message to send '{}' with name '{}'", message, blobName);

        if ((payload instanceof File)) {
            template.send(containerName, blobName, (File) payload);
        }
        else if (payload instanceof String) {
            template.send(containerName, blobName, (String) payload);
        }
        else {
            throw new MessageHandlingException(message, "unsupported Message payload type ["
                    + payload.getClass().getName() + "]");
        }
    }

    public String getContainerName() {
        return containerName;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(template.checkContainerStatus(containerName) == ContainerStatus.MINE, "Container '"
                + containerName + "' is not accessible.");
    }

}
