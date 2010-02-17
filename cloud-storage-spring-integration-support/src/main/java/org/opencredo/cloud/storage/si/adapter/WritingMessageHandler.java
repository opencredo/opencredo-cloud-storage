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

import java.io.File;

import org.opencredo.cloud.storage.ContainerStatus;
import org.opencredo.cloud.storage.StorageOperations;
import org.opencredo.cloud.storage.si.BlobNameGenerator;
import org.opencredo.cloud.storage.si.internal.DefaultBlobNameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.util.Assert;

/**
 * MessageHandler for writing blobs to the cloud storage. Depending on the
 * Message's payload, the relevant handler turns the payload into an Blob.
 * 
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class WritingMessageHandler implements MessageHandler, InitializingBean {
    private final static Logger LOG = LoggerFactory.getLogger(WritingMessageHandler.class);

    private final StorageOperations template;
    private final String containerName;
    private final BlobNameGenerator blobNameGenerator;

    /**
     * @param template
     * @param containerName
     */
    public WritingMessageHandler(StorageOperations template, String containerName) {
        this(template, containerName, new DefaultBlobNameGenerator());
    }

    /**
     * @param template
     * @param containerName
     * @param blobNameGenerator
     */
    public WritingMessageHandler(StorageOperations template, String containerName, BlobNameGenerator blobNameGenerator) {
        super();
        Assert.notNull(template, "'template' should not be null");
        Assert.notNull(blobNameGenerator, "'blob name generator' should not be null");
        this.template = template;
        this.containerName = containerName;
        this.blobNameGenerator = blobNameGenerator;
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

        String blobName = blobNameGenerator.generateBlobName(message);
        LOG.debug("Message to send '{}' with name '{}'", message, blobName);

        if ((payload instanceof File)) {
            template.send(containerName, blobName, (File) payload);
        } else if (payload instanceof String) {
            template.send(containerName, blobName, (String) payload);
        } else {
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
