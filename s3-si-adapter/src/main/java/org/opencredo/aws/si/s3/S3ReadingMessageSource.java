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

package org.opencredo.aws.si.s3;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import org.opencredo.aws.AwsCredentials;
import org.opencredo.aws.AwsException;
import org.opencredo.aws.s3.BucketObject;
import org.opencredo.aws.s3.BucketStatus;
import org.opencredo.aws.s3.S3Template;
import org.opencredo.aws.si.s3.filter.BucketObjectFilter;
import org.opencredo.aws.si.s3.filter.internal.AcceptOnceBucketObjectFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageSource;
import org.springframework.util.Assert;

/**
 * {@link MessageSource} that creates messages containing meta-data maps of
 * S3Objects. To prevent messages for certain s3 Objects, you may supply an
 * {@link BucketObjectFilter}. By default, an
 * {@link AcceptOnceBucketObjectFilter} is used. It ensures s3 objects are
 * picked up only once from the directory. A {@link Comparator} can be used to
 * ensure internal ordering of the S3 objects in a queue.
 * 
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class S3ReadingMessageSource implements MessageSource<Map<String, Object>>, InitializingBean {
    private final static Logger LOG = LoggerFactory.getLogger(S3ReadingMessageSource.class);

    private static final int INTERNAL_QUEUE_CAPACITY = 5;

    private final S3Template s3Template;
    private final String bucketName;
    private boolean deleteWhenReceived;

    private final Queue<BucketObject> toBeReceived;
    private volatile BucketObjectFilter filter = new AcceptOnceBucketObjectFilter();

    /**
     * 
     * @param awsCredentials
     * @param bucketName
     */
    public S3ReadingMessageSource(AwsCredentials awsCredentials, String bucketName) {
        this(awsCredentials, new S3ObjectLastModifiedDateComparator(), bucketName);
    }

    /**
     * 
     * @param awsCredentials
     * @param comparator
     * @param bucketName
     */
    public S3ReadingMessageSource(AwsCredentials awsCredentials, Comparator<BucketObject> comparator, String bucketName) {
        this(new S3Template(awsCredentials), comparator, bucketName);
    }

    /**
     * 
     * @param s3template
     * @param bucketName
     */
    public S3ReadingMessageSource(S3Template s3template, String bucketName) {
        this(s3template, new S3ObjectLastModifiedDateComparator(), bucketName);
    }

    /**
     * 
     * @param s3template
     * @param comparator
     * @param bucketName
     */
    public S3ReadingMessageSource(S3Template s3template, Comparator<BucketObject> comparator, String bucketName) {
        this.s3Template = s3template;
        this.toBeReceived = new PriorityBlockingQueue<BucketObject>(INTERNAL_QUEUE_CAPACITY, comparator);
        this.bucketName = bucketName;
        deleteWhenReceived = false;
    }

    public void afterPropertiesSet() {
        Assert.notNull(this.s3Template, "No template");
        Assert.isTrue(s3Template.getBucketStatus(bucketName) == BucketStatus.MINE, "Bucket '" + bucketName
                + "' is not accessible.");
    }

    /**
	 * 
	 */
    public Message<Map<String, Object>> receive() throws AwsException {

        if (toBeReceived.isEmpty()) {
            doReceive();
        }

        if (!toBeReceived.isEmpty()) {
            // TODO: Create and return map
            throw new RuntimeException("Not implemented yet.");
        } else {
            return null;
        }
    }

    /**
     * @param bucketName
     * @return
     */
    public void doReceive() {
        LOG.debug("Receive objects from bucket '{}'", bucketName);

        List<BucketObject> bucketObjects = s3Template.listBucketObjects(bucketName);

        if (filter != null) {
            // Filter bucket objects with provided filter
            bucketObjects = filter.filter(bucketObjects);
        }

        if (bucketObjects != null) {
            toBeReceived.addAll(bucketObjects);
        }
    }

    /**
     * @param filter
     */
    public void setFilter(BucketObjectFilter filter) {
        Assert.notNull(filter, "'filter' should not be null");
        this.filter = filter;
    }

    public Queue<BucketObject> getQueueToBeReceived() {
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
