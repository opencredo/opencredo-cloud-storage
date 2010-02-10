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
package org.opencredo.storage.azure;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.opencredo.storage.BlobObject;
import org.opencredo.storage.ContainerStatus;
import org.opencredo.storage.StorageCommunicationException;
import org.opencredo.storage.StorageOperations;
import org.opencredo.storage.azure.model.Blob;
import org.opencredo.storage.azure.rest.AzureRestService;
import org.opencredo.storage.azure.rest.internal.DefaultAzureRestService;
import org.opencredo.storage.azure.rest.internal.XPathContainerListFactory;
import org.opencredo.storage.azure.rest.internal.XPathContainerObjectListFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.xml.xpath.Jaxp13XPathTemplate;
import org.springframework.xml.xpath.XPathOperations;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class AzureTemplate implements StorageOperations {
    private final static Logger LOG = LoggerFactory.getLogger(AzureTemplate.class);

    private static final String DEFAULT_CONATINER_NAME = "container1";

    private final String defaultContainerName;

    private final AzureRestService restService;

    /**
     * 
     */
    public AzureTemplate(final AzureCredentials credentials) {
        this(credentials, DEFAULT_CONATINER_NAME);
    }

    /**
     * @param defaultContainerName
     * @param restService
     */
    public AzureTemplate(final AzureCredentials credentials, String defaultContainerName) {
        super();
        this.defaultContainerName = defaultContainerName;

        XPathOperations xpathOperations = new Jaxp13XPathTemplate();
        restService = new DefaultAzureRestService(credentials, new XPathContainerListFactory(xpathOperations),
                new XPathContainerObjectListFactory(xpathOperations));
    }

    /**
     * @param containerName
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#checkContainerStatus(java.lang.String)
     */
    public ContainerStatus checkContainerStatus(String containerName) throws StorageCommunicationException {
        throw new RuntimeException("Not implementated");
    }

    /**
     * @param containerName
     * @param objectName
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#deleteObject(java.lang.String,
     *      java.lang.String)
     */
    public void deleteObject(String containerName, String objectName) throws StorageCommunicationException {
        restService.deleteObject(containerName, objectName);
    }

    /**
     * @param containerName
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#listContainerObjects(java.lang.String)
     */
    public List<BlobObject> listContainerObjects(String containerName) throws StorageCommunicationException {
        return restService.listContainerObjects(containerName);
    }

    /**
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#listContainers()
     */
    public String[] listContainers() throws StorageCommunicationException {
        List<String> listContainers = restService.listContainers();

        String[] c = new String[listContainers.size()];

        return listContainers.toArray(c);
    }

    /**
     * @param containerName
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#createContainer(java.lang.String)
     */
    public void createContainer(String containerName) throws StorageCommunicationException {
        restService.createContainer(containerName);
    }

    /**
     * @param containerName
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#deleteContainer(java.lang.String)
     */
    public void deleteContainer(String containerName) throws StorageCommunicationException {
        restService.deleteContainer(containerName);
    }

    /**
     * @param objectName
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#receiveAsFile(java.lang.String)
     */
    public File receiveAsFile(String objectName) throws StorageCommunicationException {
        return receiveAsFile(defaultContainerName, objectName);
    }

    /**
     * @param containerName
     * @param objectName
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#receiveAsFile(java.lang.String,
     *      java.lang.String)
     */
    public File receiveAsFile(String containerName, String objectName) throws StorageCommunicationException {
        throw new RuntimeException("Not implementated");
    }

    /**
     * @param objectName
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#receiveAsInputStream(java.lang.String)
     */
    public InputStream receiveAsInputStream(String objectName) throws StorageCommunicationException {
        Assert.notNull(this.defaultContainerName, "Default container name is not provided");
        return receiveAsInputStream(defaultContainerName, objectName);
    }

    /**
     * @param containerName
     * @param objectName
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#receiveAsInputStream(java.lang.String,
     *      java.lang.String)
     */
    public InputStream receiveAsInputStream(String containerName, String objectName)
            throws StorageCommunicationException {
        throw new RuntimeException("Not implementated");
    }

    /**
     * @param objectName
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#receiveAsString(java.lang.String)
     */
    public String receiveAsString(String objectName) throws StorageCommunicationException {
        Assert.notNull(this.defaultContainerName, "Default container name is not provided");
        return receiveAsString(defaultContainerName, objectName);
    }

    /**
     * @param containerName
     * @param objectName
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#receiveAsString(java.lang.String,
     *      java.lang.String)
     */
    public String receiveAsString(String containerName, String objectName) throws StorageCommunicationException {
        Blob blob = restService.getObject(containerName, objectName);
        return blob.getStringContent();
    }

    /**
     * @param objectName
     * @param stringToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#send(java.lang.String,
     *      java.lang.String)
     */
    public void send(String objectName, String stringToSend) throws StorageCommunicationException {
        Assert.notNull(this.defaultContainerName, "Default container name is not provided");
        send(defaultContainerName, objectName, stringToSend);
    }

    /**
     * @param containerName
     * @param objectName
     * @param stringToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#send(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void send(String containerName, String objectName, String stringToSend) throws StorageCommunicationException {
        restService.putObject(containerName, new Blob(objectName, stringToSend));
    }

    /**
     * @param fileToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#send(java.io.File)
     */
    public void send(File fileToSend) throws StorageCommunicationException {
        Assert.notNull(this.defaultContainerName, "Default container name is not provided");
        send(defaultContainerName, fileToSend);
    }

    /**
     * @param containerName
     * @param fileToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#send(java.lang.String,
     *      java.io.File)
     */
    public void send(String containerName, File fileToSend) throws StorageCommunicationException {
        Assert.notNull(this.defaultContainerName, "Default container name is not provided");
        send(defaultContainerName, fileToSend.getName(), fileToSend);
    }

    /**
     * @param containerName
     * @param objectName
     * @param fileToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#send(java.lang.String,
     *      java.lang.String, java.io.File)
     */
    public void send(String containerName, String objectName, File fileToSend) throws StorageCommunicationException {
        throw new RuntimeException("Not implementated");

    }

    /**
     * @param objectName
     * @param is
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#send(java.lang.String,
     *      java.io.InputStream)
     */
    public void send(String objectName, InputStream is) throws StorageCommunicationException {
        Assert.notNull(this.defaultContainerName, "Default container name is not provided");
        send(defaultContainerName, objectName, is);
    }

    /**
     * @param containerName
     * @param objectName
     * @param is
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#send(java.lang.String,
     *      java.lang.String, java.io.InputStream)
     */
    public void send(String containerName, String objectName, InputStream is) throws StorageCommunicationException {
        throw new RuntimeException("Not implementated");

    }

}
