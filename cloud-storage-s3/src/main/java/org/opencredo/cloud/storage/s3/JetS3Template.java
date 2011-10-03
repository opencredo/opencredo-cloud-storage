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

import org.apache.commons.io.IOUtils;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.ContainerStatus;
import org.opencredo.cloud.storage.StorageCommunicationException;
import org.opencredo.cloud.storage.StorageException;
import org.opencredo.cloud.storage.StorageResponseHandlingException;
import org.opencredo.cloud.storage.StorageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Main class encapsulating invocations to jets3t org.jets3t.service.S3Service.
 *
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * @author David Legge (david.legge@opencredo.com)
 */
public class JetS3Template extends S3Template {
    private final static Logger LOG = LoggerFactory.getLogger(S3Template.class);
    private static final String BUCKET_NAME_CANNOT_BE_NULL = "Bucket name cannot be null";
    private static final String BUCKET_CREATION_PROBLEM = "Bucket creation problem";
    private static final String BUCKET_DELETION_PROBLEM = "Bucket deletion problem";
    private static final String SERVICE_PROBLEM = "Service problem";
    private static final String RECEIVING_FILE_PROBLEM = "Receiving file problem";

    private final S3Service s3Service;

    /**
     * Constructor with AWS (Amazon Web Services) credentials.
     *
     * @param awsCredentials
     * @throws StorageException
     */
    public JetS3Template(final AwsCredentials awsCredentials) throws StorageException {
        this(awsCredentials, null);
    }

    /**
     * @param awsCredentials
     * @param defaultContainerName
     * @throws StorageException
     */
    public JetS3Template(final AwsCredentials awsCredentials, final String defaultContainerName) throws StorageException {
        super(defaultContainerName, awsCredentials);
        try {
            s3Service = new RestS3Service(new org.jets3t.service.security.AWSCredentials(awsCredentials.getAccessKey(),
                    awsCredentials.getSecretAccessKey()));
            // Next statement checks if connection works. This is suggested by jets3t documentation.
            s3Service.listAllBuckets();
        } catch (S3ServiceException e) {
            throw new StorageException("Failed to prepare S3 service.", e);
        }
    }

    // **********************************
    // CONFIGURATION
    // **********************************

    /**
     * @param containerName
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#createContainer(java.lang.String)
     */
    public boolean createContainer(String containerName) throws StorageCommunicationException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
        try {
            final S3Bucket bucket = s3Service.createBucket(new S3Bucket(containerName));
            return bucket != null;
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Bucket creation problem", e);
        }

    }

    /**
     * @param containerName
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#createContainer(java.lang.String)
     */
    public void createPublicContainer(String containerName) throws StorageCommunicationException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
        try {
            // Create a bucket in S3.
            S3Bucket publicBucket = new S3Bucket(containerName);
            s3Service.createBucket(publicBucket);

            // Retrieve the bucket's ACL and modify it to grant public access,
            // ie READ access to the ALL_USERS group.
            AccessControlList bucketAcl = s3Service.getBucketAcl(publicBucket);
            bucketAcl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);

            // Update the bucket's ACL. Now anyone can view the list of objects in this bucket.
            publicBucket.setAcl(bucketAcl);
            s3Service.putBucketAcl(publicBucket);
            LOG.info("Public bucket created - url: http://s3.amazonaws.com/" + publicBucket.getName());

        } catch (S3ServiceException e) {
            throw new StorageCommunicationException(BUCKET_CREATION_PROBLEM, e);
        } catch (ServiceException e) {
            throw new StorageCommunicationException(SERVICE_PROBLEM, e);
        }
    }

    /**
     * @param containerName
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#deleteContainer(java.lang.String)
     */
    public void deleteContainer(String containerName) throws StorageCommunicationException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
        LOG.debug("Delete bucket '{}'", containerName);
        try {
            s3Service.deleteBucket(new S3Bucket(containerName));
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException(BUCKET_DELETION_PROBLEM, e);
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
     * @param containerName
     * @param objectName
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#deleteObject(java.lang.String,
     *      java.lang.String)
     */
    public void deleteObject(String containerName, String objectName) throws StorageCommunicationException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
        LOG.debug("Delete object '{}' in bucket '{}'", objectName, containerName);
        try {
            s3Service.deleteObject(new S3Bucket(containerName), objectName);
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException(BUCKET_DELETION_PROBLEM, e);
        }
    }

    /**
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
     * @param containerName
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#checkContainerStatus(java.lang.String)
     */
    public ContainerStatus checkContainerStatus(String containerName) throws StorageCommunicationException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
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
        } catch (ServiceException e) {
            throw new StorageCommunicationException(SERVICE_PROBLEM, e);
        }
    }

    /**
     * @param containerName
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#listContainerObjectDetails(java.lang.String)
     */
    public List<BlobDetails> listContainerObjectDetails(String containerName) throws StorageCommunicationException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
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
     * @param objectName
     * @param stringToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#send(java.lang.String,
     *      java.lang.String)
     */
    public String send(String objectName, String stringToSend) throws StorageCommunicationException {
        return send(defaultContainerName, objectName, stringToSend);
    }

    /**
     * @param containerName
     * @param objectName
     * @param stringToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#send(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public String send(String containerName, String objectName, String stringToSend) throws StorageCommunicationException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
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
        return objectName;
    }

    // ********************** File send

    /**
     * @param fileToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#send(java.io.File)
     */
    public String send(File fileToSend) throws StorageCommunicationException {
        return send(defaultContainerName, fileToSend);
    }

    /**
     * @param containerName
     * @param fileToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#send(java.lang.String,
     *      java.io.File)
     */
    public String send(String containerName, File fileToSend) throws StorageCommunicationException {
        Assert.notNull(containerName, "Bucket name can not be null");
        Assert.notNull(fileToSend, "File to send can not be null");
        return send(containerName, fileToSend.getName(), fileToSend);
    }

    /**
     * @param containerName
     * @param objectName
     * @param fileToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#send(java.lang.String,
     *      java.lang.String, java.io.File)
     */
    public String send(String containerName, String objectName, File fileToSend) throws StorageCommunicationException {
        return sendAndReceiveUrl(containerName, objectName, fileToSend);
    }

    // ********************** Input stream send

    /**
     * @param objectName
     * @param is
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#send(java.lang.String,
     *      java.io.InputStream)
     */
    public String send(String objectName, InputStream is) throws StorageCommunicationException {
        return send(defaultContainerName, objectName, is);
    }

    /**
     * @param containerName
     * @param objectName
     * @param is
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#send(java.lang.String,
     *      java.lang.String, java.io.InputStream)
     */
    public String send(String containerName, String objectName, InputStream is) throws StorageCommunicationException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
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
        return objectName;
    }

    public String sendAndReceiveUrl(String objectName, String stringToSend) throws StorageCommunicationException {
        return sendAndReceiveUrl(defaultContainerName, objectName, stringToSend);
    }

    public String sendAndReceiveUrl(String containerName, String objectName, String stringToSend) throws StorageCommunicationException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
        Assert.hasText(objectName, "Blob name must be set");
        LOG.debug("Send input-stream to bucket '{}' with key '{}'", containerName, objectName);
        try {

            S3Object s3Object = new S3Object(objectName, stringToSend);
            s3Service.putObject(containerName, s3Object);
            LOG.info("View public object contents here: http://s3.amazonaws.com/" + containerName + "/" + s3Object.getKey());
            return "http://s3.amazonaws.com/" + containerName + "/" + s3Object.getKey();

        } catch (IOException e) {
            throw new StorageCommunicationException("Sending input stream IO problem", e);
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Sending input stream problem", e);
        } catch (NoSuchAlgorithmException e) {
            throw new StorageCommunicationException("Sending input stream problem", e);
        }

    }

    public String sendAndReceiveUrl(String containerName, String objectName, File fileToSend) throws StorageCommunicationException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
        Assert.hasText(objectName, "Blob name must be set");
        Assert.notNull(fileToSend, "File to send can not be null");
        LOG.debug("Send input-stream to bucket '{}' with key '{}'", containerName, objectName);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Send file '{}' to bucket '{}' with key '{}'", new Object[]{fileToSend.getAbsolutePath(), containerName, objectName});
        }

        try {
            S3Object s3Object = new S3Object(fileToSend);
            s3Object.setKey(objectName);
            s3Service.putObject(new S3Bucket(containerName), s3Object);
            LOG.info("View public object contents here: http://s3.amazonaws.com/" + containerName + "/" + s3Object.getKey());
            return "http://s3.amazonaws.com/" + containerName + "/" + s3Object.getKey();

        } catch (IOException e) {
            throw new StorageCommunicationException("Sending input stream IO problem", e);
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Sending input stream problem", e);
        } catch (NoSuchAlgorithmException e) {
            throw new StorageCommunicationException("Sending input stream problem", e);
        }

    }

    public String sendAndReceiveUrl(File fileToSend) throws StorageCommunicationException {
        return sendAndReceiveUrl(defaultContainerName, fileToSend);
    }

    public String sendAndReceiveUrl(String objectName, InputStream is) throws StorageCommunicationException {
        return sendAndReceiveUrl(defaultContainerName, objectName, is);
    }

    public String sendAndReceiveUrl(String containerName, File fileToSend) throws StorageCommunicationException {
        return sendAndReceiveUrl(containerName, fileToSend.getName(), fileToSend);
    }

    public String sendAndReceiveUrl(String containerName, String objectName, InputStream is) throws StorageCommunicationException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
        Assert.hasText(objectName, "Blob name must be set");
        LOG.debug("Send input-stream to bucket '{}' with key '{}'", containerName, objectName);
        try {
            S3Object s3ObjectToSend = new S3Object(objectName);
            s3ObjectToSend.setDataInputStream(is);
            s3ObjectToSend.setKey(objectName);
            s3ObjectToSend.setContentLength(is.available());
            s3Service.putObject(new S3Bucket(containerName), s3ObjectToSend);
            return "http://s3.amazonaws.com/" + containerName + "/" + s3ObjectToSend.getKey();
        } catch (IOException e) {
            throw new StorageCommunicationException("Sending input stream IO problem", e);
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Sending input stream problem", e);
        }
    }

    // ********************** String receive

    /**
     * @param keyName
     * @return
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     *
     * @see org.opencredo.cloud.storage.StorageOperations#receiveAsString(java.lang.String)
     */
    public String receiveAsString(String keyName) throws StorageCommunicationException,
            StorageResponseHandlingException {
        return receiveAsString(defaultContainerName, keyName);
    }

    /**
     * @param containerName
     * @param objectName
     * @return
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     *
     * @see org.opencredo.cloud.storage.StorageOperations#receiveAsString(java.lang.String,
     *      java.lang.String)
     */
    public String receiveAsString(String containerName, String objectName) throws StorageCommunicationException,
            StorageResponseHandlingException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
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
        } catch (ServiceException e) {
            throw new StorageCommunicationException(SERVICE_PROBLEM, e);
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
     * @param objectName
     * @param toFile
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     *
     * @see org.opencredo.cloud.storage.StorageOperations#receiveAndSaveToFile(java.lang.String,
     *      java.io.File)
     */
    public String receiveAndSaveToFile(String objectName, File toFile) throws StorageCommunicationException,
            StorageResponseHandlingException {
        return receiveAndSaveToFile(defaultContainerName, objectName, toFile);
    }

    /**
     * @param containerName
     * @param objectName
     * @param toFile
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     *
     * @see org.opencredo.cloud.storage.StorageOperations#receiveAndSaveToFile(java.lang.String,
     *      java.lang.String, java.io.File)
     */
    public String receiveAndSaveToFile(String containerName, String objectName, File toFile)
            throws StorageCommunicationException, StorageResponseHandlingException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
        Assert.hasText(objectName, "Blob name must be set");
        Assert.notNull(toFile, "File to save received data must be specified");

        if (LOG.isDebugEnabled()) {
            LOG.debug("Receive data from bucket '{}' with key '{}' and save it to file '{}'", new Object[]{
                    containerName, objectName, toFile.getAbsolutePath()});
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
            throw new StorageCommunicationException(RECEIVING_FILE_PROBLEM, e);
        } catch (IOException e) {
            throw new StorageResponseHandlingException("Response data strem to file IO problem", e);
        } catch (ServiceException e) {
            throw new StorageResponseHandlingException(SERVICE_PROBLEM, e);
        } finally {
            if (s3Object != null) {
                try {
                    s3Object.closeDataInputStream();
                } catch (IOException e) {
                    throw new StorageResponseHandlingException("Close response data strem IO problem", e);
                }
            }
        }
        return toFile.getAbsolutePath();
    }

    /**
     * @param objectName
     * @return
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     *
     * @see org.opencredo.cloud.storage.StorageOperations#receiveAsInputStream(java.lang.String)
     */
    public InputStream receiveAsInputStream(String objectName) throws StorageCommunicationException,
            StorageResponseHandlingException {
        return receiveAsInputStream(defaultContainerName, objectName);
    }

    /**
     * @param containerName
     * @param objectName
     * @return
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     *
     * @see org.opencredo.cloud.storage.StorageOperations#receiveAsInputStream(java.lang.String,
     *      java.lang.String)
     */
    public InputStream receiveAsInputStream(String containerName, String objectName)
            throws StorageCommunicationException, StorageResponseHandlingException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
        Assert.hasText(objectName, "Blob name must be set");
        LOG.debug("Receive input-stream from bucket '{}' with key '{}'", containerName, objectName);
        try {
            return s3Service.getObject(new S3Bucket(containerName), objectName).getDataInputStream();
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException("Receiving input stream problem", e);
        } catch (ServiceException e) {
            throw new StorageCommunicationException(SERVICE_PROBLEM, e);
        }
    }

    public String createdSignedUrl(String containerName, String objectName, Date expiryDate) throws StorageCommunicationException {
        try {
            return s3Service.createSignedGetUrl(containerName, objectName, expiryDate, false);
        } catch (S3ServiceException e) {
            throw new StorageCommunicationException(RECEIVING_FILE_PROBLEM, e);
        }
    }

}
