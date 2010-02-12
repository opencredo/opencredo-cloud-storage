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
 * This is core interface defining possible interactions with Cloud Storage.
 * 
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public interface StorageOperations {

    // **********************************
    // CONFIGURATION
    // **********************************

    /**
     * List all container names in the cloud storage.
     * 
     * @return Return list of container names or empty list if no containers
     *         exist.
     * @throws StorageCommunicationException
     */
    public List<String> listContainerNames() throws StorageCommunicationException;

    /**
     * List all objects in the cloud storage container.
     * 
     * @param containerName
     * @return Return list of container object (blob) details or empty list if
     *         container does not have any objects (blobs).
     * 
     * @throws StorageCommunicationException
     */
    public List<BlobDetails> listContainerObjectDetails(String containerName) throws StorageCommunicationException;

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
     * Invokes {@link #send(String, String, String)} with default container name
     * which must be provided in template (implementation class).
     * 
     * @param objectName
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
     * Invokes {@link #send(String, File)} with default container name which
     * must be provided in template (implementation class).
     * 
     * @param fileToSend
     * @throws StorageCommunicationException
     * 
     */
    public void send(File fileToSend) throws StorageCommunicationException;

    /**
     * Invokes {@link #send(String, String, File)} where object name is File
     * name.
     * 
     * @param containerName
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
     * Invokes {@link #send(String, String, InputStream)} with default container
     * name which must be provided in template (implementation class).
     * 
     * @param objectName
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
     *            Input stream as container object content.
     * 
     * @throws StorageCommunicationException
     */
    public void send(String containerName, String objectName, InputStream is) throws StorageCommunicationException;

    /**
     * Invokes {@link #receiveAsString(String, String)} with default container
     * name which must be provided in template (implementation class).
     * 
     * @param objectName
     * @return
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
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
     * @return Container object data as a string.
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     *             Will be thrown if handling of response data fails (e.g.
     *             Failure to convert response byte stream to String).
     */
    public String receiveAsString(String containerName, String objectName) throws StorageCommunicationException,
            StorageResponseHandlingException;

    /**
     * Invokes {@link #receiveAndSaveToFile(String, String, File)} with default
     * container name which must be provided in template (implementation class).
     * 
     * @param objectName
     * @param toFile
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
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
     *            The file where response should be saved.
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
     *             Will be thrown if handling of response data fails (e.g.
     *             Failure to write response byte stream to File).
     */
    public void receiveAndSaveToFile(String containerName, String objectName, File toFile)
            throws StorageCommunicationException, StorageResponseHandlingException;

    /**
     * Invokes {@link #receiveAsInputStream(String, String)} with default
     * container name which must be provided in template (implementation class).
     * 
     * @param objectName
     * @return
     * @throws StorageCommunicationException
     * @throws StorageResponseHandlingException
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
     *             Will be thrown if handling of response data fails (e.g.
     *             Failure to close response byte stream).
     */
    public InputStream receiveAsInputStream(String containerName, String objectName)
            throws StorageCommunicationException, StorageResponseHandlingException;

    /**
     * Invokes {@link #deleteObject(String, String)} with default container name
     * which must be provided in template (implementation class).
     * 
     * @param objectName
     * @throws StorageCommunicationException
     */
    public void deleteObject(String objectName) throws StorageCommunicationException;

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
