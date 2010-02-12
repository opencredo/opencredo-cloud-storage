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

import static org.opencredo.cloud.storage.si.Constants.BUCKET_NAME;
import static org.opencredo.cloud.storage.si.Constants.DELETE_WHEN_RECEIVED;
import static org.opencredo.cloud.storage.si.Constants.ID;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.ContainerStatus;
import org.opencredo.cloud.storage.StorageCommunicationException;
import org.opencredo.cloud.storage.StorageException;
import org.opencredo.cloud.storage.StorageOperations;
import org.opencredo.cloud.storage.si.comparator.BlobObjectComparator;
import org.opencredo.cloud.storage.si.comparator.internal.BlobObjectLastModifiedDateComparator;
import org.opencredo.cloud.storage.si.filter.BlobObjectFilter;
import org.opencredo.cloud.storage.si.filter.internal.AcceptOnceBlobObjectFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageSource;
import org.springframework.util.Assert;

/**
 * {@link MessageSource} that creates messages containing meta-data maps of
 * S3Objects. To prevent messages for certain s3 Objects, you may supply an
 * {@link BlobObjectFilter}. By default, an {@link AcceptOnceBlobObjectFilter}
 * is used. It ensures s3 objects are picked up only once from the directory. A
 * {@link Comparator} can be used to ensure internal ordering of the S3 objects
 * in a queue.
 * 
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class ReadingMessageSource implements MessageSource<Map<String, Object>>, InitializingBean {
    private final static Logger LOG = LoggerFactory.getLogger(ReadingMessageSource.class);

    private static final int INTERNAL_QUEUE_CAPACITY = 5;

    private final StorageOperations template;
    private final String bucketName;
    private final BlobObjectFilter filter;

    private boolean deleteWhenReceived;

    private final Queue<BlobDetails> toBeReceived;

    /**
     * 
     * @param template
     * @param bucketName
     */
    public ReadingMessageSource(final StorageOperations template, String bucketName) {
        this(template, bucketName, new AcceptOnceBlobObjectFilter());
    }

    public ReadingMessageSource(final StorageOperations template, String bucketName, final BlobObjectFilter filter) {
        this(template, bucketName, filter, new BlobObjectLastModifiedDateComparator());
    }

    public ReadingMessageSource(final StorageOperations template, String bucketName, final BlobObjectComparator comparator) {
        this(template, bucketName, new AcceptOnceBlobObjectFilter(), comparator);
    }

    /**
     * 
     * @param template
     * @param bucketName
     * @param filter
     * @param comparator
     */
    public ReadingMessageSource(final StorageOperations template, String bucketName, final BlobObjectFilter filter,
                                final BlobObjectComparator comparator) {
        Assert.notNull(template, "'template' should not be null");
        Assert.notNull(filter, "'filter' should not be null");
        Assert.notNull(comparator, "'comparator' should not be null");

        this.template = template;
        this.bucketName = bucketName;
        this.deleteWhenReceived = false;
        this.filter = filter;
        this.toBeReceived = new PriorityBlockingQueue<BlobDetails>(INTERNAL_QUEUE_CAPACITY, comparator);
    }

    public void afterPropertiesSet() {
        Assert.isTrue(template.checkContainerStatus(bucketName) == ContainerStatus.MINE, "Bucket '" + bucketName
                + "' is not accessible.");
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
            map.put(BUCKET_NAME, obj.getContainerName());
            map.put(ID, obj.getName());
            map.put(DELETE_WHEN_RECEIVED, deleteWhenReceived);

            MessageBuilder<Map<String, Object>> builder = MessageBuilder.withPayload(map);

            return builder.build();
        } else {
            return null;
        }
    }

    /**
     * @param bucketName
     * @return
     */
    public void doReceive() throws StorageCommunicationException {
        LOG.debug("Receive objects from bucket '{}'", bucketName);

        List<BlobDetails> bucketObjects = template.listContainerObjectDetails(bucketName);

        if (filter != null) {
            // Filter bucket objects with provided filter
            bucketObjects = filter.filter(bucketObjects);
        }

        if (bucketObjects != null) {
            toBeReceived.addAll(bucketObjects);
        }
    }

    public Queue<BlobDetails> getQueueToBeReceived() {
        return toBeReceived;
    }

    public String getBucketName() {
        return bucketName;
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
