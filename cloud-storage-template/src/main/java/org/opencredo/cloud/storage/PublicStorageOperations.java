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

/**
 * This is extending interface defining possible interactions with Cloud Storage.
 * 
 * @author David Legge (david.legge@opencredo.com)
 * 
 */
public interface PublicStorageOperations {

    // **********************************
    // CONFIGURATION
    // **********************************

    /**
     * Create a new container with the provided container name.
     *
     * @param containerName
     * @throws org.opencredo.cloud.storage.StorageCommunicationException
     */
    public void createPublicContainer(String containerName) throws StorageCommunicationException;

    // **********************************
    // SEND/RECEIVE
    // **********************************

    /**
     * Invokes {@link #sendAndReceiveUrl(String, String, String)} with default container name
     * which must be provided in template (implementation class).
     *
     * @param objectName
     * @param stringToSend
     */
    public String sendAndReceiveUrl(String objectName, String stringToSend) throws StorageCommunicationException;

    /**
     * Send string data to the cloud storage container.
     *
     * @param containerName
     *            The name of the cloud storage container.
     * @param objectName
     *            The name of object in the cloud storage container.
     * @param stringToSend
     * @throws org.opencredo.cloud.storage.StorageCommunicationException
     */
    public String sendAndReceiveUrl(String containerName, String objectName, String stringToSend) throws StorageCommunicationException;

    /**
     * Invokes {@link #sendAndReceiveUrl(String, java.io.File)} with default container name which
     * must be provided in template (implementation class).
     *
     * @param fileToSend
     * @throws org.opencredo.cloud.storage.StorageCommunicationException
     *
     */
    public String sendAndReceiveUrl(File fileToSend) throws StorageCommunicationException;

    /**
     * Invokes {@link #sendAndReceiveUrl(String, java.io.File)} where object name is File
     * name.
     *
     * @param containerName
     * @param fileToSend
     * @throws org.opencredo.cloud.storage.StorageCommunicationException
     */
    public String sendAndReceiveUrl(String containerName, File fileToSend) throws StorageCommunicationException;

    /**
     * Send File to the cloud storage container.
     *
     * @param containerName
     *            The name of the cloud storage container.
     * @param objectName
     *            The name of object in the cloud storage container.
     * @param fileToSend
     * @throws org.opencredo.cloud.storage.StorageCommunicationException
     */
    public String sendAndReceiveUrl(String containerName, String objectName, File fileToSend) throws StorageCommunicationException;

    /**
     * Invokes {@link #sendAndReceiveUrl(String, String, java.io.InputStream)} with default container
     * name which must be provided in template (implementation class).
     *
     * @param objectName
     * @param is
     * @throws org.opencredo.cloud.storage.StorageCommunicationException
     */
    public String sendAndReceiveUrl(String objectName, InputStream is) throws StorageCommunicationException;

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
     * @throws org.opencredo.cloud.storage.StorageCommunicationException
     */
    public String sendAndReceiveUrl(String containerName, String objectName, InputStream is) throws StorageCommunicationException;

    public String createdSignedUrl(String containerName, String objectName, Date expiryDate) throws StorageCommunicationException;

}
