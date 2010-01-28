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

package org.opencredo.aws.si.adapter;

import java.io.File;

import org.opencredo.aws.AwsOperations;
import org.opencredo.aws.s3.BucketStatus;
import org.opencredo.aws.si.BlobObjectIdGenerator;
import org.opencredo.aws.si.internal.DefaultBlobObjectIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.util.Assert;

/**
 * MessageHandler for writing S3Objects to a Bucket. Depending on the Message's
 * payload, the relevant handler turns the payload into an S3Object. If the
 * payload is string, the content is written to a default destination. If the
 * payload is S3Object or File, it should have the filename property in it's
 * header.
 * 
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class WritingMessageHandler implements MessageHandler, InitializingBean {
    private final static Logger LOG = LoggerFactory.getLogger(WritingMessageHandler.class);

    private final AwsOperations template;
    private final String bucketName;
    private final BlobObjectIdGenerator idGenerator;

    /**
     * @param template
     * @param bucketName
     */
    public WritingMessageHandler(AwsOperations template, String bucketName) {
        this(template, bucketName, new DefaultBlobObjectIdGenerator());
    }

    /**
     * @param template
     * @param bucketName
     * @param idGenerator
     */
    public WritingMessageHandler(AwsOperations template, String bucketName, BlobObjectIdGenerator idGenerator) {
        super();
        Assert.notNull(template, "'template' should not be null");
        Assert.notNull(idGenerator, "'idGenerator' should not be null");
        this.template = template;
        this.bucketName = bucketName;
        this.idGenerator = idGenerator;
    }

    /**
     * write the content of Message to S3 Bucket with handlers that convert the
     * message payload to s3object.
     * 
     * @param message
     */
    public void handleMessage(Message<?> message) {
        Assert.notNull(message, "message must not be null");
        Assert.notNull(message.getPayload(), "message payload must not be null");

        Object payload = message.getPayload();

        LOG.debug("Message to send: {}", message);

        String blobObjectId = idGenerator.generateBlobObjectId(message);

        try {
            if ((payload instanceof File)) {
                template.send(bucketName, blobObjectId, (File) payload);
            } else if (payload instanceof String) {
                template.send(bucketName, blobObjectId, (String) payload);
            } else {
                throw new MessageHandlingException(message, "unsupported Message payload type ["
                        + payload.getClass().getName() + "]");
            }
        } catch (Exception e) {
            throw new MessageHandlingException(message, "failed to write Message payload to file", e);
        }
    }

    public String getBucketName() {
        return bucketName;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(template.getBucketStatus(bucketName) == BucketStatus.MINE, "Bucket '" + bucketName
                + "' is not accessible.");
    }

}
