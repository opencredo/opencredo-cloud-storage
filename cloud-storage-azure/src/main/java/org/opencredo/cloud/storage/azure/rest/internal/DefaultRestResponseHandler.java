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
package org.opencredo.cloud.storage.azure.rest.internal;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.ContainerStatus;
import org.opencredo.cloud.storage.azure.model.Blob;
import org.opencredo.cloud.storage.azure.model.InputStreamBlob;
import org.opencredo.cloud.storage.azure.rest.AzureRestResponseHandlingException;
import org.opencredo.cloud.storage.azure.rest.AzureRestCommunicationException;
import org.opencredo.cloud.storage.azure.rest.ContainerNamesListFactory;
import org.opencredo.cloud.storage.azure.rest.ContainerObjectDetailsListFactory;
import org.opencredo.cloud.storage.azure.rest.RestResponseHandler;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class DefaultRestResponseHandler implements RestResponseHandler {

    private final ContainerNamesListFactory containerNamesListFactory;
    private final ContainerObjectDetailsListFactory containerObjectDetailsListFactory;

    /**
     * @param containerNamesListFactory
     * @param containerObjectDetailsListFactory
     */
    public DefaultRestResponseHandler(ContainerNamesListFactory containerNamesListFactory,
                                      ContainerObjectDetailsListFactory containerObjectDetailsListFactory) {
        super();
        this.containerNamesListFactory = containerNamesListFactory;
        this.containerObjectDetailsListFactory = containerObjectDetailsListFactory;
    }

    /**
     * @param response
     * @param containerName
     * @throws AzureRestCommunicationException
     * @see org.opencredo.cloud.storage.azure.rest.RestResponseHandler#handleCreateContainerResponse(org.apache.http.HttpResponse,
     *      java.lang.String)
     */
    public void handleCreateContainerResponse(HttpResponse response, String containerName)
            throws AzureRestResponseHandlingException {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
            throw new AzureRestResponseHandlingException("Failed to create Azure container '%s'. Reason: '%s %d: %s'",
                    containerName, response.getStatusLine().getProtocolVersion().getProtocol(), response
                            .getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
        }
    }

    /**
     * @param response
     * @param containerName
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.cloud.storage.azure.rest.RestResponseHandler#handleDeleteContainerResponse(org.apache.http.HttpResponse,
     *      java.lang.String)
     */
    public void handleDeleteContainerResponse(HttpResponse response, String containerName)
            throws AzureRestResponseHandlingException {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
            throw new AzureRestResponseHandlingException("Failed to delete Azure container '%s'. Reason: '%s %d: %s'",
                    containerName, response.getStatusLine().getProtocolVersion().getProtocol(), response
                            .getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
        }
    }

    /**
     * @param response
     * @param containerName
     * @param blobName
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.cloud.storage.azure.rest.RestResponseHandler#handleDeleteObjectResponse(org.apache.http.HttpResponse,
     *      java.lang.String, java.lang.String)
     */
    public void handleDeleteObjectResponse(HttpResponse response, String containerName, String blobName)
            throws AzureRestResponseHandlingException {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
            throw new AzureRestResponseHandlingException(
                    "Failed to delete blob '%s' in Azure container '%s'. Reason: '%s %d: %s'", blobName, containerName,
                    response.getStatusLine().getProtocolVersion().getProtocol(), response.getStatusLine()
                            .getStatusCode(), response.getStatusLine().getReasonPhrase());
        }
    }

    /**
     * @param response
     * @param containerName
     * @param blob
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.cloud.storage.azure.rest.RestResponseHandler#handlePutObjectResponse(org.apache.http.HttpResponse,
     *      java.lang.String, org.opencredo.cloud.storage.azure.model.Blob)
     */
    public void handlePutObjectResponse(HttpResponse response, String containerName, Blob<?> blob)
            throws AzureRestResponseHandlingException {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
            throw new AzureRestResponseHandlingException(
                    "Failed to add blob '%s' in Azure container '%s'. Reason: '%s %d: %s'", blob.getName(),
                    containerName, response.getStatusLine().getProtocolVersion().getProtocol(), response
                            .getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
        }
    }

    /**
     * @param response
     * @param containerName
     * @param blobName
     * @return
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.cloud.storage.azure.rest.RestResponseHandler#handleGetObjectResponse(org.apache.http.HttpResponse,
     *      java.lang.String, java.lang.String)
     */
    public InputStreamBlob handleGetObjectResponse(HttpResponse response, String containerName, String blobName)
            throws AzureRestResponseHandlingException {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new AzureRestResponseHandlingException(
                    "Failed to get blob '%s' in Azure container '%s'. Reason: '%s %d: %s'", blobName, containerName,
                    response.getStatusLine().getProtocolVersion().getProtocol(), response.getStatusLine()
                            .getStatusCode(), response.getStatusLine().getReasonPhrase());
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
     * @throws AzureRestResponseHandlingException
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.cloud.storage.azure.rest.RestResponseHandler#handleListContainerObjectDetailsResponse(org.apache.http.HttpResponse,
     *      java.lang.String)
     */
    public List<BlobDetails> handleListContainerObjectDetailsResponse(HttpResponse response, String containerName)
            throws AzureRestResponseHandlingException, AzureRestResponseHandlingException {

        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new AzureRestResponseHandlingException(
                    "Failed to get list of blobs from Azure container '%s'. Reason: '%s %d: %s'", containerName,
                    response.getStatusLine().getProtocolVersion().getProtocol(), response.getStatusLine()
                            .getStatusCode(), response.getStatusLine().getReasonPhrase());
        }

        try {
            return containerObjectDetailsListFactory.createContainerObjectDetailsList(containerName, response
                    .getEntity());
        } catch (AzureRestResponseHandlingException e) {
            throw new AzureRestResponseHandlingException(
                    "Failed to load blobs list from Azure container's '%s' response.", e);
        }
    }

    /**
     * @param response
     * @return
     * @throws AzureRestResponseHandlingException
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.cloud.storage.azure.rest.RestResponseHandler#handleListContainerNamesResponse(org.apache.http.HttpResponse)
     */
    public List<String> handleListContainerNamesResponse(HttpResponse response)
            throws AzureRestResponseHandlingException, AzureRestResponseHandlingException {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new AzureRestResponseHandlingException("Failed to get Azure containers list. Reason: '%s %d: %s'",
                    response.getStatusLine().getProtocolVersion().getProtocol(), response.getStatusLine()
                            .getStatusCode(), response.getStatusLine().getReasonPhrase());
        }

        try {
            return containerNamesListFactory.createContainerNamesList(response.getEntity());
        } catch (AzureRestResponseHandlingException e) {
            throw new AzureRestResponseHandlingException("Failed to load Azure containers list from response.", e);
        }
    }

    /**
     * 
     * @param response
     * @param containerName
     * @return
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.cloud.storage.azure.rest.RestResponseHandler#handleCheckContainerStatus(org.apache.http.HttpResponse,
     *      java.lang.String)
     */
    public ContainerStatus handleCheckContainerStatus(HttpResponse response, String containerName)
            throws AzureRestResponseHandlingException {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return ContainerStatus.MINE;
        }

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            return ContainerStatus.DOES_NOT_EXIST;
        }

        throw new AzureRestResponseHandlingException("Unexpected Azure containers '{}' status. Reason: '%s %d: %s'",
                containerName, response.getStatusLine().getProtocolVersion().getProtocol(), response.getStatusLine()
                        .getStatusCode(), response.getStatusLine().getReasonPhrase());
    }

}
