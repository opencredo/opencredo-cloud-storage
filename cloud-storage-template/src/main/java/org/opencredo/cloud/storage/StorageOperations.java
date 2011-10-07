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
import java.util.Date;
import java.util.List;

/**
 * This is core interface defining possible interactions with Cloud Storage.
 *
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public interface StorageOperations {

    /**
     * @return Default container (bucket) name.
     */
    String getDefaultContainerName();

    // **********************************
    // CONFIGURATION
    // **********************************

    /**
     * List all container names in the cloud storage.
     *
     * @return Return list of container names or empty list if no containers
     *         exist.
     */
    List<String> listContainerNames();

    /**
     * List all objects in the cloud storage in default container. Default
     * container name should be provided in implementation.
     *
     * @return
     */
    List<BlobDetails> listContainerObjectDetails();

    /**
     * List all objects in the cloud storage container.
     *
     * @param containerName
     * @return Return list of container object (blob) details or empty list if
     *         container does not have any objects (blobs).
     */
    List<BlobDetails> listContainerObjectDetails(String containerName);

    /**
     * Get the status of the bucket.
     *
     * @param containerName
     * @return
     */
    ContainerStatus checkContainerStatus(String containerName);

    /**
     * Create a new container with the provided container name.
     *
     * @param containerName
     */
    boolean createContainer(String containerName);

    /**
     * Delete the container with the provided container name.
     *
     * @param containerName
     */
    void deleteContainer(String containerName);

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
    String send(String objectName, String stringToSend);

    /**
     * Send string data to the cloud storage container.
     *
     * @param containerName The name of the cloud storage container.
     * @param objectName    The name of object in the cloud storage container.
     * @param stringToSend
     */
    String send(String containerName, String objectName, String stringToSend);

    /**
     * Invokes {@link #send(String, File)} with default container name which
     * must be provided in template (implementation class).
     *
     * @param fileToSend
     */
    String send(File fileToSend);

    /**
     * Invokes {@link #send(String, String, File)} where object name is File
     * name.
     *
     * @param containerName
     * @param fileToSend
     */
    String send(String containerName, File fileToSend);

    /**
     * Send File to the cloud storage container.
     *
     * @param containerName The name of the cloud storage container.
     * @param objectName    The name of object in the cloud storage container.
     * @param fileToSend
     */
    String send(String containerName, String objectName, File fileToSend);

    /**
     * Invokes {@link #send(String, String, InputStream)} with default container
     * name which must be provided in template (implementation class).
     *
     * @param objectName
     * @param is
     */
    String send(String objectName, InputStream is);

    /**
     * Send InputStream to the cloud storage container.
     *
     * @param containerName The name of the cloud storage container.
     * @param objectName    The name of object in the cloud storage container.
     * @param is            Input stream as container object content.
     */
    String send(String containerName, String objectName, InputStream is);

    /**
     * Invokes {@link #receiveAsString(String, String)} with default container
     * name which must be provided in template (implementation class).
     *
     * @param objectName
     * @return
     * @throws StorageResponseHandlingException
     *
     */
    String receiveAsString(String objectName);

    /**
     * Receive the object as String from cloud storage container.
     *
     * @param containerName The name of the cloud storage container.
     * @param objectName    The name of object in the cloud storage container to be
     *                      received.
     * @return Container object data as a string.
     * @throws StorageResponseHandlingException
     *          Will be thrown if handling of response data fails (e.g.
     *          Failure to convert response byte stream to String).
     */
    String receiveAsString(String containerName, String objectName) throws StorageResponseHandlingException;

    /**
     * Invokes {@link #receiveAndSaveToFile(String, String, File)} with default
     * container name which must be provided in template (implementation class).
     *
     * @param objectName
     * @param toFile
     * @throws StorageResponseHandlingException
     *
     */
    String receiveAndSaveToFile(String objectName, File toFile);

    /**
     * Receive the object from cloud storage container and save it to specified
     * file.
     *
     * @param containerName The name of the cloud storage container.
     * @param objectName    The name of object in the cloud storage container to be
     *                      received.
     * @param toFile        The file where response should be saved.
     * @throws StorageResponseHandlingException
     *          Will be thrown if handling of response data fails (e.g.
     *          Failure to write response byte stream to File).
     */
    String receiveAndSaveToFile(String containerName, String objectName, File toFile);

    /**
     * Invokes {@link #receiveAsInputStream(String, String)} with default
     * container name which must be provided in template (implementation class).
     *
     * @param objectName
     * @return
     * @throws StorageResponseHandlingException
     *
     */
    InputStream receiveAsInputStream(String objectName);

    /**
     * Receive the object as InputStream from cloud storage container.
     *
     * @param containerName The name of the cloud storage container.
     * @param objectName    The name of object in the cloud storage container to be
     *                      received.
     * @return
     * @throws StorageResponseHandlingException
     *          Will be thrown if handling of response data fails (e.g.
     *          Failure to close response byte stream).
     */
    InputStream receiveAsInputStream(String containerName, String objectName);

    /**
     * Invokes {@link #deleteObject(String, String)} with default container name
     * which must be provided in template (implementation class).
     *
     * @param objectName
     */
    void deleteObject(String objectName);

    /**
     * Delete the object from cloud storage container.
     *
     * @param containerName The name of the cloud storage container.
     * @param objectName    The name of object in the cloud storage container to be
     *                      received.
     */
    void deleteObject(String containerName, String objectName);

    String createdSignedUrl(String containerName, String objectName, Date expiryDate);
}
