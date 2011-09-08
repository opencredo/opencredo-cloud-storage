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

package org.opencredo.cloud.storage.jcloud;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.io.Payload;
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
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Main class encapsulating invocations to jclouds
 *
 * @author David Legge (david.legge@opencredo.com)
 */
public class JCloudTemplate implements StorageOperations, InitializingBean {
    private final static Logger LOG = LoggerFactory.getLogger(JCloudTemplate.class);
    private static final String BUCKET_NAME_CANNOT_BE_NULL = "Bucket name cannot be null";
    private static final String BUCKET_CREATION_PROBLEM = "Bucket creation problem";
    private static final String BUCKET_DELETION_PROBLEM = "Bucket deletion problem";
    private static final String SERVICE_PROBLEM = "Service problem";
    private static final String RECEIVING_FILE_PROBLEM = "Receiving file problem";
    private static final String jcloud_S3 = "aws-s3";

    private BlobStoreContext context;
    private BlobStore thisBlobStore;

    private String defaultContainerName;
    private JCloudCredentials jcloudCredentials;

    /**
     * Constructor with jcloud (Amazon Web Services) credentials.
     *
     * @param jcloudCredentials
     * @throws StorageException
     */
    public JCloudTemplate(final CloudProvider cloudProvider, final JCloudCredentials jcloudCredentials) throws StorageException {
        this(cloudProvider, jcloudCredentials, null);
    }

    /**
     * @param cloudProvider
     * @param jcloudCredentials
     * @param defaultContainerName
     * @throws StorageException
     */
    public JCloudTemplate(final CloudProvider cloudProvider, final JCloudCredentials jcloudCredentials, final String defaultContainerName) throws StorageException {
        this.defaultContainerName = defaultContainerName;
        this.jcloudCredentials = jcloudCredentials;
        context = new BlobStoreContextFactory().createContext(cloudProvider.getString(), jcloudCredentials.getAccessKey(), jcloudCredentials.getSecretAccessKey());

    }

    /**
     * @param cloudProvider
     * @param jcloudProperties
     * @param defaultContainerName
     * @throws StorageException
     */
    public JCloudTemplate(final CloudProvider cloudProvider, final Properties jcloudProperties, final String defaultContainerName) throws StorageException {
        this.defaultContainerName = defaultContainerName;
//           this.jcloudProperties = jcloudProperties;
        context = new BlobStoreContextFactory().createContext(cloudProvider.getString(), jcloudProperties);

    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() {
        ContainerStatus containerStatus = checkContainerStatus(defaultContainerName);
        Assert.isTrue(containerStatus != ContainerStatus.ALREADY_CLAIMED, "Default bucket '" + defaultContainerName + "' already claimed.");
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
        final BlobStore blobStore = getStore();
        return blobStore.createContainerInLocation(null, containerName);
    }

    private BlobStore getStore() {
        if (thisBlobStore != null) {
            return thisBlobStore;
        }
        thisBlobStore = context.getBlobStore();
        return thisBlobStore;
    }

    /**
     * @param containerName
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#createContainer(java.lang.String)
     */
    public void createPublicContainer(String containerName) throws StorageCommunicationException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
        final BlobStore blobStore = getStore();
        blobStore.createContainerInLocation(null, containerName);

    }

    /**
     * @param containerName
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#deleteContainer(java.lang.String)
     */
    public void deleteContainer(String containerName) throws StorageCommunicationException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
        LOG.debug("Delete bucket '{}'", containerName);
        final BlobStore blobStore = getStore();
        blobStore.deleteContainer(containerName);

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
        removeBlob(containerName, objectName);

    }

    private void removeBlob(String containerName, String objectName) {
        final BlobStore blobStore = getStore();
        try {
            blobStore.removeBlob(containerName, objectName);
        } catch (ContainerNotFoundException e) {
            LOG.info("ERROR: Container Not Found");
            throw new StorageCommunicationException(e);
        }
    }

    /**
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.cloud.storage.StorageOperations#listContainerNames()
     */
    public List<String> listContainerNames() throws StorageCommunicationException {
        LOG.debug("List buckets");
        final BlobStore blobStore = getStore();
        final PageSet<? extends StorageMetadata> list = blobStore.list();

        List<String> returnList = new ArrayList<String>(list.size());
        for (StorageMetadata entry : list) {
            returnList.add(entry.getName());
        }

        return returnList;
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
            final String testObject = send(containerName, "test", "test");
            removeBlob(containerName, "test");
            return ContainerStatus.MINE;
        } catch (StorageCommunicationException e) {
            if (e.getCause() instanceof ContainerNotFoundException) {
                return ContainerStatus.DOES_NOT_EXIST;
            }
            throw new StorageException("Unrecognised bucket status: " + containerName);
        }
//      return ContainerStatus.ALREADY_CLAIMED; TODO - not sure how to detect this
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
        final BlobStore blobStore = getStore();
        final PageSet<? extends StorageMetadata> list = blobStore.list(containerName);

        List<BlobDetails> returnList = new ArrayList<BlobDetails>(list.size());
        for (StorageMetadata entry : list) {
            final BlobDetails blobDetails = new BlobDetails(entry.getName(), entry.getName(), entry.getETag(), entry.getLastModified());
            returnList.add(blobDetails);
        }

        return returnList;
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

        return buildBlobAndSend(containerName, objectName, stringToSend);

    }

    private String buildBlobAndSend(String containerName, String objectName, String stringToSend) {
        final BlobStore blobStore = getStore();
        final BlobBuilder blobBuilder = blobStore.blobBuilder(objectName);
        blobBuilder.payload(stringToSend);

        return putBlob(containerName, blobStore, blobBuilder);
    }

    private String putBlob(String containerName, BlobStore blobStore, BlobBuilder blobBuilder) {
        try {
            return blobStore.putBlob(containerName, blobBuilder.build());
        } catch (ContainerNotFoundException e) {
            LOG.info("ERROR: Container Not Found");
            throw new StorageCommunicationException(e);
        }
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

        return buildBlobAndSend(containerName, objectName, is);

    }

    private String buildBlobAndSend(String containerName, String objectName, InputStream is) {
        final BlobStore blobStore = getStore();
        final BlobBuilder blobBuilder = blobStore.blobBuilder(objectName);

        blobBuilder.payload(is);

        return putBlob(containerName, blobStore, blobBuilder);
    }

    private String buildBlobAndSend(String containerName, String objectName, File fileToSend) {
        final BlobStore blobStore = getStore();
        final BlobBuilder blobBuilder = blobStore.blobBuilder(objectName);

        blobBuilder.payload(fileToSend);

        return putBlob(containerName, blobStore, blobBuilder);
    }

    public String sendAndReceiveUrl(String objectName, String stringToSend) throws StorageCommunicationException {
        return sendAndReceiveUrl(defaultContainerName, objectName, stringToSend);
    }

    public String sendAndReceiveUrl(String containerName, String objectName, String stringToSend) throws StorageCommunicationException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
        Assert.hasText(objectName, "Blob name must be set");
        LOG.debug("Send input-stream to bucket '{}' with key '{}'", containerName, objectName);

        final String remoteName = buildBlobAndSend(containerName, objectName, stringToSend);

        return getUrl(containerName, objectName);

    }

    private String getUrl(String containerName, String remoteName) {
        final BlobStore blobStore = getStore();
        final BlobMetadata blobMetadata = blobStore.blobMetadata(containerName, remoteName);
        final URI publicUri = blobMetadata.getUri();

        if (publicUri != null && StringUtils.hasText(publicUri.toString())) {
            final String uri = publicUri.toString();
            LOG.debug(uri);
            return uri;
        }
        return null;
    }

    public String sendAndReceiveUrl(String containerName, String objectName, File fileToSend) throws StorageCommunicationException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
        Assert.hasText(objectName, "Blob name must be set");
        Assert.notNull(fileToSend, "File to send can not be null");
        LOG.debug("Send input-stream to bucket '{}' with key '{}'", containerName, objectName);

        final String remoteName = buildBlobAndSend(containerName, objectName, fileToSend);

        return getUrl(containerName, objectName);

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

        final String remoteName = buildBlobAndSend(containerName, objectName, is);

        return getUrl(containerName, remoteName);
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
    public String receiveAsString(String keyName) throws StorageCommunicationException, StorageResponseHandlingException {
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
    public String receiveAsString(String containerName, String objectName) throws StorageCommunicationException, StorageResponseHandlingException {
        Assert.notNull(containerName, BUCKET_NAME_CANNOT_BE_NULL);
        Assert.hasText(objectName, "Blob name must be set");
        LOG.debug("Receive string from bucket '{}' with key '{}'", containerName, objectName);

        return receiveString(containerName, objectName);

    }

    private String receiveString(String containerName, String objectName) {
        final BlobStore blobStore = getStore();

        final Blob blob = blobStore.getBlob(containerName, objectName);
        final Payload payload = blob.getPayload();
        return payload.getRawContent().toString();
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

        return receiveFile(containerName, objectName, toFile);
    }

    private String receiveFile(String containerName, String objectName, File toFile) {
        try {
            StorageUtils.createParentDirs(toFile);
        } catch (IOException e) {
            throw new StorageResponseHandlingException(e, "Failed to create parent directories for file: %s", toFile
                    .getAbsolutePath());
        }

        final BlobStore blobStore = getStore();
        final PageSet<? extends StorageMetadata> list = blobStore.list(containerName);

        final Blob blob = blobStore.getBlob(containerName, objectName);
        final Payload payload = blob.getPayload();
        final Object rawContent = payload.getRawContent();
        try {
            if (rawContent instanceof InputStream) {
                StorageUtils.writeStreamToFile((InputStream) rawContent, toFile);
                return toFile.getAbsolutePath();
            }
            else if (rawContent instanceof File) {
                final String absolutePath = toFile.getAbsolutePath();
                StorageUtils.writeFileToFile((File) rawContent, toFile);

                return absolutePath;
            }

        } catch (IOException e) {
            throw new StorageResponseHandlingException("Response data stream to file IO problem", e);
        }
        return null;
    }

    /**
     * @param objectName
     * @return
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     *
     * @see org.opencredo.cloud.storage.StorageOperations#receiveAsInputStream(java.lang.String)
     */
    public InputStream receiveAsInputStream(String objectName) throws StorageCommunicationException, StorageResponseHandlingException {
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

        return receiveInputStream(objectName, containerName);
    }

    private InputStream receiveInputStream(String containerName, String objectName) {

        final BlobStore blobStore = getStore();
        final PageSet<? extends StorageMetadata> list = blobStore.list(containerName);

        final Blob blob = blobStore.getBlob(containerName, objectName);
        final Payload payload = blob.getPayload();
        return payload.getInput();

    }

    public String createdSignedUrl(String containerName, String objectName, Date expiryDate) throws StorageCommunicationException {
        return "";
    }

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
