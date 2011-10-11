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

import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.ContainerStatus;
import org.opencredo.cloud.storage.StorageOperations;
import org.opencredo.cloud.storage.si.comparator.BlobDetailsComparator;
import org.opencredo.cloud.storage.si.comparator.internal.BlobLastModifiedDateComparator;
import org.opencredo.cloud.storage.si.filter.BlobDetailsFilter;
import org.opencredo.cloud.storage.si.filter.internal.AcceptOnceBlobNameFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.Message;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * {@link MessageSource} that creates messages containing meta-data maps of blob
 * objects from cloud storage. To prevent messages for certain blob objects, you
 * may supply an {@link BlobDetailsFilter}. By default, an
 * {@link AcceptOnceBlobNameFilter} is used. It ensures blob objects are picked
 * up only once from the container. A {@link BlobDetailsComparator} can be used
 * to ensure internal ordering of the blob objects in a queue.
 *
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class ReadingMessageSource implements MessageSource<BlobDetails>, InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(ReadingMessageSource.class);

    private static final int INTERNAL_QUEUE_CAPACITY = 5;

    private final StorageOperations template;
    private final String containerName;
    private final BlobDetailsFilter filter;

    private final Queue<BlobDetails> toBeReceived;

    /**
     * @param template
     * @param containerName
     */
    public ReadingMessageSource(final StorageOperations template, String containerName) {
        this(template, containerName, new AcceptOnceBlobNameFilter());
    }

    public ReadingMessageSource(final StorageOperations template, String containerName, final BlobDetailsFilter filter) {
        this(template, containerName, filter, new BlobLastModifiedDateComparator());
    }

    public ReadingMessageSource(final StorageOperations template, String containerName,
                                final BlobDetailsComparator comparator) {
        this(template, containerName, new AcceptOnceBlobNameFilter(), comparator);
    }

    /**
     * @param template
     * @param containerName
     * @param filter
     * @param comparator
     */
    public ReadingMessageSource(final StorageOperations template, String containerName, final BlobDetailsFilter filter,
                                final BlobDetailsComparator comparator) {
        Assert.notNull(template, "'template' should not be null");
        Assert.notNull(filter, "'filter' should not be null");
        Assert.notNull(comparator, "'comparator' should not be null");

        this.template = template;
        this.containerName = containerName;
        this.filter = filter;
        this.toBeReceived = new PriorityBlockingQueue<BlobDetails>(INTERNAL_QUEUE_CAPACITY, comparator);
    }

    public void afterPropertiesSet() {
        Assert.isTrue(template.checkContainerStatus(containerName) == ContainerStatus.MINE, "Container '"
                + containerName + "' is not accessible.");
    }

    /**
     *
     */
    public Message<BlobDetails> receive() {

        if (toBeReceived.isEmpty()) {
            doReceive();
        }

        if (!toBeReceived.isEmpty()) {
            BlobDetails obj = toBeReceived.poll();
            MessageBuilder<BlobDetails> builder = MessageBuilder.withPayload(obj);
            return builder.build();
        }
        else {
            return null;
        }
    }

    public void doReceive() {
        LOG.debug("Receive objects from container '{}'", containerName);

        List<BlobDetails> cod = template.listContainerObjectDetails(containerName);

        if (filter != null) {
            // Filter container object details with provided filter
            cod = filter.filter(cod);
        }

        if (cod != null) {
            toBeReceived.addAll(cod);
        }
    }

    public Queue<BlobDetails> getQueueToBeReceived() {
        return toBeReceived;
    }

    public String getContainerName() {
        return containerName;
    }
}
