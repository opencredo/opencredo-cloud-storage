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

import static org.opencredo.cloud.storage.si.Constants.CONTAINER_NAME;
import static org.opencredo.cloud.storage.si.Constants.DELETE_WHEN_RECEIVED;
import static org.opencredo.cloud.storage.si.Constants.CONATINER_OBJECT_NAME;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.ContainerStatus;
import org.opencredo.cloud.storage.StorageCommunicationException;
import org.opencredo.cloud.storage.StorageOperations;
import org.opencredo.cloud.storage.si.comparator.BlobDetailsComparator;
import org.opencredo.cloud.storage.si.comparator.internal.BlobLastModifiedDateComparator;
import org.opencredo.cloud.storage.si.filter.BlobDetailsFilter;
import org.opencredo.cloud.storage.si.filter.internal.AcceptOnceBlobNameFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageSource;
import org.springframework.util.Assert;

/**
 * {@link MessageSource} that creates messages containing meta-data maps of blob
 * objects from cloud storage. To prevent messages for certain s3 Objects, you
 * may supply an {@link BlobDetailsFilter}. By default, an
 * {@link AcceptOnceBlobNameFilter} is used. It ensures s3 objects are picked
 * up only once from the directory. A {@link Comparator} can be used to ensure
 * internal ordering of the S3 objects in a queue.
 * 
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class ReadingMessageSource implements MessageSource<Map<String, Object>>, InitializingBean {
    private final static Logger LOG = LoggerFactory.getLogger(ReadingMessageSource.class);

    private static final int INTERNAL_QUEUE_CAPACITY = 5;

    private final StorageOperations template;
    private final String containerName;
    private final BlobDetailsFilter filter;

    private boolean deleteWhenReceived;

    private final Queue<BlobDetails> toBeReceived;

    /**
     * 
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
     * 
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
        this.deleteWhenReceived = false;
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
    public Message<Map<String, Object>> receive() throws StorageCommunicationException {

        if (toBeReceived.isEmpty()) {
            doReceive();
        }

        if (!toBeReceived.isEmpty()) {
            BlobDetails obj = toBeReceived.poll();
            Map<String, Object> map = new HashMap<String, Object>(3);
            map.put(CONTAINER_NAME, obj.getContainerName());
            map.put(CONATINER_OBJECT_NAME, obj.getName());
            map.put(DELETE_WHEN_RECEIVED, deleteWhenReceived);

            MessageBuilder<Map<String, Object>> builder = MessageBuilder.withPayload(map);

            return builder.build();
        } else {
            return null;
        }
    }

    /**
     * @param containerName
     * @return
     */
    public void doReceive() throws StorageCommunicationException {
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

    /**
     * @return the deleteWhenReceived
     */
    public boolean isDeleteWhenReceived() {
        return deleteWhenReceived;
    }

    /**
     * @param deleteWhenReceived
     *            the deleteWhenReceived to set
     */
    public void setDeleteWhenReceived(boolean deleteWhenReceived) {
        this.deleteWhenReceived = deleteWhenReceived;
    }
}
