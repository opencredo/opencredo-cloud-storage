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
package org.opencredo.storage.azure.rest.internal;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.opencredo.storage.BlobObject;
import org.opencredo.storage.ContainerStatus;
import org.opencredo.storage.azure.model.Blob;
import org.opencredo.storage.azure.model.InputStreamBlob;
import org.opencredo.storage.azure.rest.AzureRestResponseHandlingException;
import org.opencredo.storage.azure.rest.AzureRestServiceException;
import org.opencredo.storage.azure.rest.ContainerListFactory;
import org.opencredo.storage.azure.rest.ContainerObjectListFactory;
import org.opencredo.storage.azure.rest.RestResponseHandler;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class DefaultRestResponseHandler implements RestResponseHandler {

    private final ContainerListFactory containerListFactory;
    private final ContainerObjectListFactory containerObjectListFactory;

    /**
     * @param containerListFactory
     * @param containerObjectListFactory
     */
    public DefaultRestResponseHandler(ContainerListFactory containerListFactory,
                                      ContainerObjectListFactory containerObjectListFactory) {
        super();
        this.containerListFactory = containerListFactory;
        this.containerObjectListFactory = containerObjectListFactory;
    }

    /**
     * @param response
     * @param containerName
     * @throws AzureRestServiceException
     * @see org.opencredo.storage.azure.rest.RestResponseHandler#handleCreateContainerResponse(org.apache.http.HttpResponse,
     *      java.lang.String)
     */
    public void handleCreateContainerResponse(HttpResponse response, String containerName)
            throws AzureRestServiceException {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
            throw new AzureRestServiceException("Failed to create Azure container '%s'. Reason: '%s %d: %s'",
                    containerName, response.getStatusLine().getProtocolVersion().getProtocol(), response
                            .getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
        }
    }

    /**
     * @param response
     * @param containerName
     * @throws AzureRestServiceException
     * @see org.opencredo.storage.azure.rest.RestResponseHandler#handleDeleteContainerResponse(org.apache.http.HttpResponse,
     *      java.lang.String)
     */
    public void handleDeleteContainerResponse(HttpResponse response, String containerName)
            throws AzureRestServiceException {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
            throw new AzureRestServiceException("Failed to delete Azure container '%s'. Reason: '%s %d: %s'",
                    containerName, response.getStatusLine().getProtocolVersion().getProtocol(), response
                            .getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
        }
    }

    /**
     * @param response
     * @param containerName
     * @param blobName
     * @throws AzureRestServiceException
     * @see org.opencredo.storage.azure.rest.RestResponseHandler#handleDeleteObjectResponse(org.apache.http.HttpResponse,
     *      java.lang.String, java.lang.String)
     */
    public void handleDeleteObjectResponse(HttpResponse response, String containerName, String blobName)
            throws AzureRestServiceException {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
            throw new AzureRestServiceException(
                    "Failed to delete blob '%s' in Azure container '%s'. Reason: '%s %d: %s'", blobName, containerName,
                    response.getStatusLine().getProtocolVersion().getProtocol(), response.getStatusLine()
                            .getStatusCode(), response.getStatusLine().getReasonPhrase());
        }
    }

    /**
     * @param response
     * @param containerName
     * @param blob
     * @throws AzureRestServiceException
     * @see org.opencredo.storage.azure.rest.RestResponseHandler#handlePutObjectResponse(org.apache.http.HttpResponse,
     *      java.lang.String, org.opencredo.storage.azure.model.Blob)
     */
    public void handlePutObjectResponse(HttpResponse response, String containerName, Blob<?> blob)
            throws AzureRestServiceException {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
            throw new AzureRestServiceException("Failed to add blob '%s' in Azure container '%s'. Reason: '%s %d: %s'",
                    blob.getName(), containerName, response.getStatusLine().getProtocolVersion().getProtocol(),
                    response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
        }
    }

    /**
     * @param response
     * @param containerName
     * @param blobName
     * @return
     * @throws AzureRestServiceException
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.storage.azure.rest.RestResponseHandler#handleGetObjectResponse(org.apache.http.HttpResponse,
     *      java.lang.String, java.lang.String)
     */
    public InputStreamBlob handleGetObjectResponse(HttpResponse response, String containerName, String blobName)
            throws AzureRestServiceException, AzureRestResponseHandlingException {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new AzureRestServiceException("Failed to get blob '%s' in Azure container '%s'. Reason: '%s %d: %s'",
                    blobName, containerName, response.getStatusLine().getProtocolVersion().getProtocol(), response
                            .getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
        }

        try {
            return new InputStreamBlob(blobName, response.getEntity().getContent());
        } catch (IllegalStateException e) {
            throw new AzureRestResponseHandlingException("Failed to get content", e);
        } catch (IOException e) {
            throw new AzureRestResponseHandlingException("Unexpected IO exception while creating blob", e);
        }
    }

    /**
     * @param response
     * @param containerName
     * @return
     * @throws AzureRestServiceException
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.storage.azure.rest.RestResponseHandler#handleListContainerObjectsResponse(org.apache.http.HttpResponse,
     *      java.lang.String)
     */
    public List<BlobObject> handleListContainerObjectsResponse(HttpResponse response, String containerName)
            throws AzureRestServiceException, AzureRestResponseHandlingException {

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new AzureRestServiceException(
                    "Failed to get list of blobs from Azure container '%s'. Reason: '%s %d: %s'", containerName,
                    response.getStatusLine().getProtocolVersion().getProtocol(), response.getStatusLine()
                            .getStatusCode(), response.getStatusLine().getReasonPhrase());
        }

        try {
            return containerObjectListFactory.createContainerObjectsList(containerName, response.getEntity());
        } catch (AzureRestResponseHandlingException e) {
            throw new AzureRestResponseHandlingException(
                    "Failed to load blobs list from Azure container's '%s' response.", e);
        }
    }

    /**
     * @param response
     * @return
     * @throws AzureRestServiceException
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.storage.azure.rest.RestResponseHandler#handleListContainersResponse(org.apache.http.HttpResponse)
     */
    public List<String> handleListContainersResponse(HttpResponse response) throws AzureRestServiceException,
            AzureRestResponseHandlingException {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new AzureRestServiceException("Failed to get Azure containers list. Reason: '%s %d: %s'", response
                    .getStatusLine().getProtocolVersion().getProtocol(), response.getStatusLine().getStatusCode(),
                    response.getStatusLine().getReasonPhrase());
        }

        try {
            return containerListFactory.createContainersList(response.getEntity());
        } catch (AzureRestResponseHandlingException e) {
            throw new AzureRestResponseHandlingException("Failed to load Azure containers list from response.", e);
        }
    }

    /**
     * 
     * @param response
     * @param containerName
     * @return
     * @throws AzureRestServiceException
     * @see org.opencredo.storage.azure.rest.RestResponseHandler#handleCheckContainerStatus(org.apache.http.HttpResponse,
     *      java.lang.String)
     */
    public ContainerStatus handleCheckContainerStatus(HttpResponse response, String containerName)
            throws AzureRestServiceException {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return ContainerStatus.MINE;
        }

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            return ContainerStatus.DOES_NOT_EXIST;
        }

        throw new AzureRestServiceException("Unexpected Azure containers '{}' status. Reason: '%s %d: %s'",
                containerName, response.getStatusLine().getProtocolVersion().getProtocol(), response.getStatusLine()
                        .getStatusCode(), response.getStatusLine().getReasonPhrase());
    }

}
