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

import org.apache.http.HttpResponse;
import org.opencredo.cloud.storage.BlobObject;
import org.opencredo.cloud.storage.ContainerStatus;
import org.opencredo.cloud.storage.azure.model.Blob;
import org.opencredo.cloud.storage.azure.model.InputStreamBlob;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public interface RestResponseHandler {

    /**
     * 
     * @param response
     * @param containerName
     * @throws AzureRestServiceException
     */
    void handleCreateContainerResponse(HttpResponse response, String containerName) throws AzureRestServiceException;

    /**
     * 
     * @param response
     * @param containerName
     * @throws AzureRestServiceException
     */
    void handleDeleteContainerResponse(HttpResponse response, String containerName) throws AzureRestServiceException;

    /**
     * 
     * @param response
     * @param containerName
     * @param blobName
     * @throws AzureRestServiceException
     */
    void handleDeleteObjectResponse(HttpResponse response, String containerName, String blobName)
            throws AzureRestServiceException;

    /**
     * 
     * @param response
     * @param containerName
     * @param blobName
     * @return
     * @throws AzureRestServiceException
     * @throws AzureRestResponseHandlingException
     */
    InputStreamBlob handleGetObjectResponse(HttpResponse response, String containerName, String blobName)
            throws AzureRestServiceException, AzureRestResponseHandlingException;

    /**
     * 
     * @param response
     * @return
     * @throws AzureRestServiceException
     * @throws AzureRestResponseHandlingException
     */
    List<String> handleListContainersResponse(HttpResponse response) throws AzureRestServiceException, AzureRestResponseHandlingException;

    /**
     * 
     * @param response
     * @param containerName
     * @return
     * @throws AzureRestServiceException
     * @throws AzureRestResponseHandlingException
     */
    List<BlobObject> handleListContainerObjectsResponse(HttpResponse response, String containerName)
            throws AzureRestServiceException, AzureRestResponseHandlingException;

    /**
     * 
     * @param response
     * @param containerName
     * @param blob
     * @throws AzureRestServiceException
     */
    void handlePutObjectResponse(HttpResponse response, String containerName, Blob<?> blob)
            throws AzureRestServiceException;

    /**
     * 
     * @param response
     * @param containerName
     * @return
     * @throws AzureRestServiceException
     */
    ContainerStatus handleCheckContainerStatus(HttpResponse response, String containerName) throws AzureRestServiceException;
}
