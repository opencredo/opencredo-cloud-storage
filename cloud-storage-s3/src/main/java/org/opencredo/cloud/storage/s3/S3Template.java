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
package org.opencredo.cloud.storage.s3;

import org.opencredo.cloud.storage.ContainerStatus;
import org.opencredo.cloud.storage.StorageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public abstract class S3Template implements StorageOperations, InitializingBean {
    protected static final Logger LOG = LoggerFactory.getLogger(S3Template.class);
    protected static final String BUCKET_NAME_CANNOT_BE_NULL = "Bucket name cannot be null";
    private static final String BUCKET_CREATION_PROBLEM = "Bucket creation problem";
    private static final String BUCKET_DELETION_PROBLEM = "Bucket deletion problem";
    private static final String SERVICE_PROBLEM = "Service problem";
    private static final String RECEIVING_FILE_PROBLEM = "Receiving file problem";
    private String defaultContainerName;
    private AwsCredentials awsCredentials;

    public S3Template(final String defaultContainerName, final AwsCredentials awsCredentials) {
        this.defaultContainerName = defaultContainerName;
        this.awsCredentials = awsCredentials;
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() {
        ContainerStatus containerStatus = checkContainerStatus(defaultContainerName);
        Assert.isTrue(containerStatus != ContainerStatus.ALREADY_CLAIMED, "Default bucket '" + defaultContainerName + "' already claimed.");
    }

    public abstract ContainerStatus checkContainerStatus(String containerName);

    /**
     * @param defaultContainerName the defaultContainerName to set
     */
    public void setDefaultContainerName(String defaultContainerName) {
        this.defaultContainerName = defaultContainerName;
    }

    /**
     * @return the defaultContainerName
     */
    public String getDefaultContainerName() {
        return defaultContainerName;
    }
}
