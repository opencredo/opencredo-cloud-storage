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
package org.opencredo.cloud.storage.azure.rest;

import java.util.List;

import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.ContainerStatus;
import org.opencredo.cloud.storage.azure.model.Blob;
import org.opencredo.cloud.storage.azure.model.InputStreamBlob;

/**
 * Interface specifying basic interactions with Azure Blob REST API.
 * 
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public interface AzureRestService {

    /**
     * Create container in Azure cloud storage.
     * 
     * @param containerName
     * @throws AzureRestCommunicationException
     * @throws AzureRestResponseHandlingException 
     */
    void createContainer(String containerName) throws AzureRestCommunicationException, AzureRestResponseHandlingException;

    /**
     * Delete container from Azure cloud storage.
     * 
     * @param containerName
     * @throws AzureRestCommunicationException
     * @throws AzureRestResponseHandlingException 
     */
    void deleteContainer(String containerName) throws AzureRestCommunicationException, AzureRestResponseHandlingException;

    /**
     * List container names in Azure cloud storage.
     * 
     * @return Returns list of container names, or empty list if containers not
     *         found.
     * @throws AzureRestCommunicationException
     * @throws AzureRestResponseHandlingException 
     */
    List<String> listContainerNames() throws AzureRestCommunicationException, AzureRestResponseHandlingException;

    /**
     * Delete object from Azure cloud storage container.
     * 
     * @param containerName
     * @param blobName
     * @throws AzureRestCommunicationException
     * @throws AzureRestResponseHandlingException 
     */
    void deleteObject(String containerName, String blobName) throws AzureRestCommunicationException, AzureRestResponseHandlingException;

    /**
     * Add object into Azure cloud storage container.
     * 
     * @param containerName
     * @param blob
     * @throws AzureRestRequestCreationException
     * @throws AzureRestCommunicationException
     * @throws AzureRestResponseHandlingException 
     */
    void putObject(String containerName, Blob<?> blob) throws AzureRestRequestCreationException, AzureRestCommunicationException, AzureRestResponseHandlingException;

    /**
     * Get object from Azure cloud storage container.
     * 
     * @param containerName
     * @param blobName
     * @return
     * @throws AzureRestCommunicationException
     * @throws AzureRestResponseHandlingException
     */
    InputStreamBlob getObject(String containerName, String blobName) throws AzureRestCommunicationException,
            AzureRestResponseHandlingException;

    /**
     * List object details in specified Azure cloud storage container.
     * 
     * @return Returns container object details list, or empty list if container
     *         does not contain any objects.
     * @throws AzureRestCommunicationException
     * @throws AzureRestResponseHandlingException 
     */
    List<BlobDetails> listContainerObjectDetails(String containerName) throws AzureRestCommunicationException, AzureRestResponseHandlingException;

    /**
     * Check the status of the container.
     * 
     * @param containerName
     * @return
     * @throws AzureRestCommunicationException 
     * @throws AzureRestResponseHandlingException 
     */
    ContainerStatus checkContainerStatus(String containerName) throws AzureRestCommunicationException, AzureRestResponseHandlingException;
}
