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
package org.opencredo.cloud.storage;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public interface StorageOperations {

    // **********************************
    // CONFIGURATION
    // **********************************

    /**
     * List all containers in the cloud storage.
     * 
     * @return
     * @throws StorageCommunicationException
     */
    public String[] listContainers() throws StorageCommunicationException;

    /**
     * List all objects in the cloud storage container.
     * 
     * @param containerName
     * @return
     * @throws StorageCommunicationException
     */
    public List<BlobObject> listContainerObjects(String containerName) throws StorageCommunicationException;

    /**
     * Get the status of the bucket.
     * 
     * @param containerName
     * @return
     */
    public ContainerStatus checkContainerStatus(String containerName) throws StorageCommunicationException;

    /**
     * Create a new container with the provided container name.
     * 
     * @param containerName
     * @throws StorageCommunicationException
     */
    public void createContainer(String containerName) throws StorageCommunicationException;

    /**
     * Delete the container with the provided container name.
     * 
     * @param containerName
     * @throws StorageCommunicationException
     */
    public void deleteContainer(String containerName) throws StorageCommunicationException;

    // **********************************
    // SEND/RECEIVE
    // **********************************

    /**
     * Send string data to the cloud storage container. Default container name
     * must be provided in the template.
     * 
     * @param objectName
     *            The name of object in the cloud storage container.
     * @param stringToSend
     */
    public void send(String objectName, String stringToSend) throws StorageCommunicationException;

    /**
     * Send string data to the cloud storage container.
     * 
     * @param containerName
     *            The name of the cloud storage container.
     * @param objectName
     *            The name of object in the cloud storage container.
     * @param stringToSend
     * @throws StorageCommunicationException
     */
    public void send(String containerName, String objectName, String stringToSend) throws StorageCommunicationException;

    /**
     * Send File to the cloud storage container. Default container name must be
     * provided in the template.
     * 
     * @param fileToSend
     * @throws StorageCommunicationException
     */
    public void send(File fileToSend) throws StorageCommunicationException;

    /**
     * Send File to the cloud storage container.
     * 
     * @param containerName
     *            The name of the cloud storage container.
     * @param fileToSend
     * @throws StorageCommunicationException
     */
    public void send(String containerName, File fileToSend) throws StorageCommunicationException;

    /**
     * Send File to the cloud storage container.
     * 
     * @param containerName
     *            The name of the cloud storage container.
     * @param objectName
     *            The name of object in the cloud storage container.
     * @param fileToSend
     * @throws StorageCommunicationException
     */
    public void send(String containerName, String objectName, File fileToSend) throws StorageCommunicationException;

    /**
     * Send InputStream to the cloud storage container. Default container name
     * must be provided in the template.
     * 
     * @param objectName
     *            The name of object in default container.
     * @param is
     * @throws StorageCommunicationException
     */
    public void send(String objectName, InputStream is) throws StorageCommunicationException;

    /**
     * Send InputStream to the cloud storage container.
     * 
     * @param containerName
     *            The name of the cloud storage container.
     * @param objectName
     *            The name of object in the cloud storage container.
     * @param is
     * 
     * @throws StorageCommunicationException
     */
    public void send(String containerName, String objectName, InputStream is) throws StorageCommunicationException;

    /**
     * Receive the object as String from cloud storage container. Default bucket
     * name must be provided in the template.
     * 
     * @param objectName
     *            The name of object in the cloud storage container to be
     *            received.
     * @return
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     *             TODO
     */
    public String receiveAsString(String objectName) throws StorageCommunicationException,
            StorageResponseHandlingException;

    /**
     * Receive the object as String from cloud storage container.
     * 
     * @param containerName
     *            The name of the cloud storage container.
     * @param objectName
     *            The name of object in the cloud storage container to be
     *            received.
     * @return
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     *             TODO
     */
    public String receiveAsString(String containerName, String objectName) throws StorageCommunicationException,
            StorageResponseHandlingException;

    /**
     * Receive the object as File from cloud storage container. Default bucket
     * name must be provided in the template.
     * 
     * @param objectName
     *            The name of object in the cloud storage container to be
     *            received.
     * @param toFile
     *            TODO
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     *             TODO
     */
    public void receiveAndSaveToFile(String objectName, File toFile) throws StorageCommunicationException,
            StorageResponseHandlingException;

    /**
     * Receive the object from cloud storage container and save it to specified
     * file.
     * 
     * @param containerName
     *            The name of the cloud storage container.
     * @param objectName
     *            The name of object in the cloud storage container to be
     *            received.
     * @param toFile
     *            TODO
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     *             TODO
     */
    public void receiveAndSaveToFile(String containerName, String objectName, File toFile)
            throws StorageCommunicationException, StorageResponseHandlingException;

    /**
     * Receive the object as InputStream from cloud storage container. Default
     * bucket name must be provided in the template.
     * 
     * @param objectName
     *            The name of object in the cloud storage container to be
     *            received.
     * @return
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     *             TODO
     */
    public InputStream receiveAsInputStream(String objectName) throws StorageCommunicationException,
            StorageResponseHandlingException;

    /**
     * Receive the object as InputStream from cloud storage container.
     * 
     * @param containerName
     *            The name of the cloud storage container.
     * @param objectName
     *            The name of object in the cloud storage container to be
     *            received.
     * @return
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     *             TODO
     */
    public InputStream receiveAsInputStream(String containerName, String objectName)
            throws StorageCommunicationException, StorageResponseHandlingException;

    /**
     * Delete the object from cloud storage container.
     * 
     * @param containerName
     *            The name of the cloud storage container.
     * @param objectName
     *            The name of object in the cloud storage container to be
     *            received.
     * @throws StorageCommunicationException
     */
    public void deleteObject(String containerName, String objectName) throws StorageCommunicationException;
}
