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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.ContainerStatus;
import org.opencredo.cloud.storage.StorageCommunicationException;
import org.opencredo.cloud.storage.StorageException;
import org.opencredo.cloud.storage.StorageOperations;
import org.opencredo.cloud.storage.StorageResponseHandlingException;
import org.opencredo.cloud.storage.StorageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class S3Template implements StorageOperations, InitializingBean {
    private final static Logger LOG = LoggerFactory.getLogger(S3Template.class);

    private final S3Service s3Service;
    private String defaultContainerName;

    /**
     * Constructor with AWS (Amazon Web Services) credentials.
     * 
     * @param awsCredentials
     * @throws StorageException
     */
    public S3Template(final AwsCredentials awsCredentials) throws StorageException {
        this(awsCredentials, null);
    }

    /**
     * 
     * @param awsCredentials
     * @param defaultBucketName
     * @throws StorageException
     */
    public S3Template(final AwsCredentials awsCredentials, final String defaultBucketName) throws StorageException {
        this.defaultContainerName = defaultBucketName;
        try {
            s3Service = new RestS3Service(new org.jets3t.service.security.AWSCredentials(awsCredentials.getAccessKey(),
                    awsCredentials.getSecretAccessKey()));
        } catch (S3ServiceException e) {
            throw new StorageException("Failed to prepare S3 service.", e);
        }
    }

    /**
     * 
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() {
        ContainerStatus containerStatus = checkContainerStatus(defaultContainerName);
        Assert.isTrue(containerStatus != ContainerStatus.ALREADY_CLAIMED, "Default bucket '" + defaultContainerName
                + "' already claimed.");
    }

    // **********************************
    // CONFIGURATION
    // **********************************

    /**
     * 
     * @param containerName
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#createContainer(java.lang.String)
     */
    public void createContainer(String containerName) throws StorageCommunicationException {
        Assert.notNull(containerName, "Bucket name cannot be null");
        try {
            s3Service.createBucket(new S3Bucket(containerName));
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Bucket creation problem", e);
        }
    }

    /**
     * 
     * @param containerName
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#deleteContainer(java.lang.String)
     */
    public void deleteContainer(String containerName) throws StorageCommunicationException {
        Assert.notNull(containerName, "Bucket name cannot be null");
        LOG.debug("Delete bucket '{}'", containerName);
        try {
            s3Service.deleteBucket(new S3Bucket(containerName));
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Bucket deletion problem", e);
        }
    }

    /**
     * @param objectName
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#deleteObject(java.lang.String)
     */
    public void deleteObject(String objectName) throws StorageCommunicationException {
        deleteObject(defaultContainerName, objectName);
    }

    /**
     * 
     * @param containerName
     * @param objectName
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#deleteObject(java.lang.String,
     *      java.lang.String)
     */
    public void deleteObject(String containerName, String objectName) throws StorageCommunicationException {
        Assert.notNull(containerName, "Bucket name cannot be null");
        LOG.debug("Delete object '{}' in bucket '{}'", objectName, containerName);
        try {
            s3Service.deleteObject(new S3Bucket(containerName), objectName);
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Bucket deletion problem", e);
        }
    }

    /**
     * 
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#listContainerNames()
     */
    public List<String> listContainerNames() throws StorageCommunicationException {
        LOG.debug("List buckets");
        try {
            S3Bucket[] s3buckets = s3Service.listAllBuckets();
            List<String> bucketNames = new ArrayList<String>(s3buckets.length);
            for (int i = 0; i < s3buckets.length; i++) {
                bucketNames.add(s3buckets[i].getName());
            }
            return bucketNames;
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Bucket list problem", e);
        }
    }
    
    /**
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#listContainerObjectDetails()
     */
    public List<BlobDetails> listContainerObjectDetails() throws StorageCommunicationException {
        return listContainerObjectDetails(defaultContainerName);
    }

    /**
     * 
     * @param containerName
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#checkContainerStatus(java.lang.String)
     */
    public ContainerStatus checkContainerStatus(String containerName) throws StorageCommunicationException {
        Assert.notNull(containerName, "Bucket name cannot be null");
        LOG.debug("Get bucket '{}' status", containerName);
        try {
            int bucketStatus = this.s3Service.checkBucketStatus(containerName);
            switch (bucketStatus) {
            case S3Service.BUCKET_STATUS__MY_BUCKET:
                return ContainerStatus.MINE;
            case S3Service.BUCKET_STATUS__DOES_NOT_EXIST:
                return ContainerStatus.DOES_NOT_EXIST;
            case S3Service.BUCKET_STATUS__ALREADY_CLAIMED:
                return ContainerStatus.ALREADY_CLAIMED;
            default:
                throw new StorageException("Unrecognised bucket status: " + bucketStatus);
            }
        } catch (S3ServiceException s3E) {
            throw new StorageCommunicationException("Failed to get status of bucket name " + containerName, s3E);
        }
    }

    /**
     * 
     * @param containerName
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#listContainerObjectDetails(java.lang.String)
     */
    public List<BlobDetails> listContainerObjectDetails(String containerName) throws StorageCommunicationException {
        Assert.notNull(containerName, "Bucket name cannot be null");
        LOG.debug("Get objects list for bucket '{}'", containerName);
        try {
            S3Object[] s3Objects = this.s3Service.listObjects(new S3Bucket(containerName));
            List<BlobDetails> keys = new ArrayList<BlobDetails>(s3Objects.length);
            for (S3Object s : s3Objects) {
                keys.add(new BlobDetails(s.getBucketName(), s.getKey(), s.getETag(), s.getLastModifiedDate()));
            }
            return keys;
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Failed to get bucket " + containerName + " object details.", e);
        }
    }

    // **********************************
    // SEND/RECEIVE
    // **********************************

    // ********************** String send

    /**
     * 
     * @param objectName
     * @param stringToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#send(java.lang.String,
     *      java.lang.String)
     */
    public void send(String objectName, String stringToSend) throws StorageCommunicationException {
        send(defaultContainerName, objectName, stringToSend);
    }

    /**
     * 
     * @param containerName
     * @param objectName
     * @param stringToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#send(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void send(String containerName, String objectName, String stringToSend) throws StorageCommunicationException {
        Assert.notNull(containerName, "Bucket name cannot be null");
        Assert.hasText(objectName, "Blob name must be set");
        LOG.debug("Send string to bucket '{}' with key '{}'", containerName, objectName);
        try {
            s3Service.putObject(new S3Bucket(containerName), new S3Object(objectName, stringToSend));
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Sending string problem", e);
        } catch (NoSuchAlgorithmException e) {
            throw new StorageCommunicationException("No such algorithm", e);
        } catch (IOException e) {
            throw new StorageCommunicationException("Sending string IO problem", e);
        }
    }

    // ********************** File send

    /**
     * 
     * @param fileToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#send(java.io.File)
     */
    public void send(File fileToSend) throws StorageCommunicationException {
        send(defaultContainerName, fileToSend);
    }

    /**
     * 
     * @param containerName
     * @param fileToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#send(java.lang.String,
     *      java.io.File)
     */
    public void send(String containerName, File fileToSend) throws StorageCommunicationException {
        Assert.notNull(containerName, "Bucket name can not be null");
        Assert.notNull(fileToSend, "File to send can not be null");
        send(containerName, fileToSend.getName(), fileToSend);
    }

    /**
     * 
     * @param containerName
     * @param objectName
     * @param fileToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#send(java.lang.String,
     *      java.lang.String, java.io.File)
     */
    public void send(String containerName, String objectName, File fileToSend) throws StorageCommunicationException {
        Assert.notNull(containerName, "Bucket name can not be null");
        Assert.hasText(objectName, "Blob name must be set");
        Assert.notNull(fileToSend, "File to send can not be null");

        if (LOG.isDebugEnabled()) {
            LOG.debug("Send file '{}' to bucket '{}' with key '{}'", new Object[] { fileToSend.getAbsolutePath(),
                    containerName, objectName });
        }

        try {
            S3Object object = new S3Object(fileToSend);
            object.setKey(objectName);
            s3Service.putObject(new S3Bucket(containerName), object);
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Sending file problem", e);
        } catch (NoSuchAlgorithmException e) {
            throw new StorageCommunicationException("No such algorithm", e);
        } catch (IOException e) {
            throw new StorageCommunicationException("Sending string IO problem", e);
        }

    }

    // ********************** Input stream send
    /**
     * 
     * @param objectName
     * @param is
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#send(java.lang.String,
     *      java.io.InputStream)
     */
    public void send(String objectName, InputStream is) throws StorageCommunicationException {
        send(defaultContainerName, objectName, is);
    }

    /**
     * 
     * @param containerName
     * @param objectName
     * @param is
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#send(java.lang.String,
     *      java.lang.String, java.io.InputStream)
     */
    public void send(String containerName, String objectName, InputStream is) throws StorageCommunicationException {
        Assert.notNull(containerName, "Bucket name cannot be null");
        Assert.hasText(objectName, "Blob name must be set");
        LOG.debug("Send input-stream to bucket '{}' with key '{}'", containerName, objectName);
        try {
            S3Object s3ObjectToSend = new S3Object(objectName);
            s3ObjectToSend.setDataInputStream(is);
            s3ObjectToSend.setContentLength(is.available());
            s3Service.putObject(new S3Bucket(containerName), s3ObjectToSend);
        } catch (IOException e) {
            throw new StorageCommunicationException("Sending input stream IO problem", e);
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Sending input stream problem", e);
        }
    }

    // ********************** String receive

    /**
     * 
     * @param keyName
     * @return
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     * @see org.opencredo.cloud.storage.StorageOperations#receiveAsString(java.lang.String)
     */
    public String receiveAsString(String keyName) throws StorageCommunicationException,
            StorageResponseHandlingException {
        return receiveAsString(defaultContainerName, keyName);
    }

    /**
     * 
     * @param containerName
     * @param objectName
     * @return
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     * @see org.opencredo.cloud.storage.StorageOperations#receiveAsString(java.lang.String,
     *      java.lang.String)
     */
    public String receiveAsString(String containerName, String objectName) throws StorageCommunicationException,
            StorageResponseHandlingException {
        Assert.notNull(containerName, "Bucket name cannot be null");
        Assert.hasText(objectName, "Blob name must be set");
        LOG.debug("Receive string from bucket '{}' with key '{}'", containerName, objectName);
        S3Object s3Object = null;
        try {
            s3Object = s3Service.getObject(new S3Bucket(containerName), objectName);
            return IOUtils.toString(s3Object.getDataInputStream());
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Receiving as string problem", e);
        } catch (IOException e) {
            throw new StorageResponseHandlingException("Receiving as string IO problem", e);
        } finally {
            if (s3Object != null) {
                try {
                    s3Object.closeDataInputStream();
                } catch (IOException e) {
                    throw new StorageResponseHandlingException("Close response data strem IO problem", e);
                }
            }
        }
    }

    /**
     * 
     * @param objectName
     * @param toFile
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     * @see org.opencredo.cloud.storage.StorageOperations#receiveAndSaveToFile(java.lang.String,
     *      java.io.File)
     */
    public void receiveAndSaveToFile(String objectName, File toFile) throws StorageCommunicationException,
            StorageResponseHandlingException {
        receiveAndSaveToFile(defaultContainerName, objectName, toFile);
    }

    /**
     * 
     * @param containerName
     * @param objectName
     * @param toFile
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     * @see org.opencredo.cloud.storage.StorageOperations#receiveAndSaveToFile(java.lang.String,
     *      java.lang.String, java.io.File)
     */
    public void receiveAndSaveToFile(String containerName, String objectName, File toFile)
            throws StorageCommunicationException, StorageResponseHandlingException {
        Assert.notNull(containerName, "Bucket name cannot be null");
        Assert.hasText(objectName, "Blob name must be set");
        Assert.notNull(toFile, "File to save received data must be specified");
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Receive data from bucket '{}' with key '{}' and save it to file '{}'", new Object[] {
                    containerName, objectName, toFile.getAbsolutePath() });
        }
        
        try {
            StorageUtils.createParentDirs(toFile);
        } catch (IOException e) {
            throw new StorageResponseHandlingException(e, "Failed to create parent directories for file: %s", toFile
                    .getAbsolutePath());
        }

        S3Object s3Object = null;
        try {
            s3Object = s3Service.getObject(new S3Bucket(containerName), objectName);
            StorageUtils.writeStreamToFile(s3Object.getDataInputStream(), toFile);
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Receiving file problem", e);
        } catch (IOException e) {
            throw new StorageResponseHandlingException("Response data strem to file IO problem", e);
        } finally {
            if (s3Object != null) {
                try {
                    s3Object.closeDataInputStream();
                } catch (IOException e) {
                    throw new StorageResponseHandlingException("Close response data strem IO problem", e);
                }
            }
        }
    }

    /**
     * 
     * @param objectName
     * @return
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     * @see org.opencredo.cloud.storage.StorageOperations#receiveAsInputStream(java.lang.String)
     */
    public InputStream receiveAsInputStream(String objectName) throws StorageCommunicationException,
            StorageResponseHandlingException {
        return receiveAsInputStream(defaultContainerName, objectName);
    }

    /**
     * 
     * @param containerName
     * @param objectName
     * @return
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     * @see org.opencredo.cloud.storage.StorageOperations#receiveAsInputStream(java.lang.String,
     *      java.lang.String)
     */
    public InputStream receiveAsInputStream(String containerName, String objectName)
            throws StorageCommunicationException, StorageResponseHandlingException {
        Assert.notNull(containerName, "Bucket name cannot be null");
        Assert.hasText(objectName, "Blob name must be set");
        LOG.debug("Receive input-stream from bucket '{}' with key '{}'", containerName, objectName);
        try {
            return s3Service.getObject(new S3Bucket(containerName), objectName).getDataInputStream();
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Receiving input stream problem", e);
        }
    }

    /**
     * @param defaultBucketName
     *            the defaultBucketName to set
     */
    public void setDefaultContainerName(String defaultBucketName) {
        this.defaultContainerName = defaultBucketName;
    }

    /**
     * @return the defaultBucketName
     */
    public String getDefaultContainerName() {
        return defaultContainerName;
    }
}
