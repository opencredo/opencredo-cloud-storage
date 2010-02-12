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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.ContainerStatus;
import org.opencredo.cloud.storage.azure.AzureCredentials;
import org.opencredo.cloud.storage.azure.model.Blob;
import org.opencredo.cloud.storage.azure.model.InputStreamBlob;
import org.opencredo.cloud.storage.azure.rest.AzureRestCommunicationException;
import org.opencredo.cloud.storage.azure.rest.AzureRestRequestCreationException;
import org.opencredo.cloud.storage.azure.rest.AzureRestResponseHandlingException;
import org.opencredo.cloud.storage.azure.rest.AzureRestService;
import org.opencredo.cloud.storage.azure.rest.ContainerNamesListFactory;
import org.opencredo.cloud.storage.azure.rest.ContainerObjectDetailsListFactory;
import org.opencredo.cloud.storage.azure.rest.RestResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of basic interactions with Azure Blob REST API.
 * 
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class DefaultAzureRestService implements AzureRestService {
    private final static Logger LOG = LoggerFactory.getLogger(DefaultAzureRestService.class);

    private String blobUrlFormat = "http://%s.blob.core.windows.net/%s";
    private final RestResponseHandler responseHandler;

    private final AzureCredentials credentials;
    private final RequestAuthorizationInterceptor authorizationInterceptor;

    /**
     * 
     * @param credentials
     *            Azure cloud storage credentials required to sign request.
     * @param containerNamesListFactory
     * @param containerObjectDetailsListFactory
     */
    public DefaultAzureRestService(final AzureCredentials credentials,
                                   final ContainerNamesListFactory containerNamesListFactory,
                                   final ContainerObjectDetailsListFactory containerObjectDetailsListFactory) {
        this.responseHandler = new DefaultRestResponseHandler(containerNamesListFactory,
                containerObjectDetailsListFactory);
        this.credentials = credentials;
        this.authorizationInterceptor = new RequestAuthorizationInterceptor(credentials);
    }

    /**
     * @param containerName
     * @throws AzureRestCommunicationException
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.cloud.storage.azure.rest.AzureRestService#createContainer(java.lang.String)
     */
    public void createContainer(String containerName) throws AzureRestCommunicationException,
            AzureRestResponseHandlingException {
        LOG.debug("Create Azure container '{}'", containerName);

        HttpClient client = createClient();
        HttpPut req = new HttpPut(String.format(blobUrlFormat, credentials.getAccountName(), containerName
                + "?restype=container"));

        try {
            HttpResponse response = client.execute(req);
            LOG
                    .debug("Create Azure container '{}' response status line: '{}'", containerName, response
                            .getStatusLine());
            responseHandler.handleCreateContainerResponse(response, containerName);
        } catch (ClientProtocolException e) {
            throw new AzureRestCommunicationException(e, "Unexpected protocol error while creating container '%s'.",
                    containerName);
        } catch (IOException e) {
            throw new AzureRestCommunicationException(e, "Unexpected IO error while creating container '%s'.",
                    containerName);
        }
    }

    /**
     * @param containerName
     * @throws AzureRestCommunicationException
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.cloud.storage.azure.rest.AzureRestService#deleteContainer(java.lang.String)
     */
    public void deleteContainer(String containerName) throws AzureRestCommunicationException,
            AzureRestResponseHandlingException {
        LOG.debug("Delete Azure container '{}'", containerName);

        HttpClient client = createClient();
        HttpDelete req = new HttpDelete(String.format(blobUrlFormat, credentials.getAccountName(), containerName
                + "?restype=container"));

        try {
            HttpResponse response = client.execute(req);
            LOG
                    .debug("Delete Azure container '{}' response status line: '{}'", containerName, response
                            .getStatusLine());
            responseHandler.handleDeleteContainerResponse(response, containerName);
        } catch (ClientProtocolException e) {
            throw new AzureRestCommunicationException(e, "Unexpected protocol error while deleting container '%s'.",
                    containerName);
        } catch (IOException e) {
            throw new AzureRestCommunicationException(e, "Unexpected IO error while deleting container '%s'.",
                    containerName);
        }
    }

    /**
     * @param containerName
     * @param blobName
     * @throws AzureRestCommunicationException
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.cloud.storage.azure.rest.AzureRestService#deleteObject(java.lang.String,
     *      java.lang.String)
     */
    public void deleteObject(String containerName, String blobName) throws AzureRestCommunicationException,
            AzureRestResponseHandlingException {
        LOG.debug("Delete Azure blob '{}' from container '{}'", blobName, containerName);

        HttpClient client = createClient();
        HttpDelete req = new HttpDelete(String.format(blobUrlFormat, credentials.getAccountName(), containerName + "/"
                + blobName));

        try {
            HttpResponse response = client.execute(req);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Delete Azure blob '{}' from container '{}' response status line: '{}'", new Object[] {
                        blobName, containerName, response.getStatusLine() });
            }

            responseHandler.handleDeleteObjectResponse(response, containerName, blobName);
        } catch (ClientProtocolException e) {
            throw new AzureRestCommunicationException(e,
                    "Unexpected protocol error while deleting blob '%s' in container '%s'.", blobName, containerName);
        } catch (IOException e) {
            throw new AzureRestCommunicationException(e,
                    "Unexpected IO error while deleting blob '%s' in container '%s'.", blobName, containerName);
        }
    }

    /**
     * @param containerName
     * @param blobName
     * @return
     * @throws AzureRestCommunicationException
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.cloud.storage.azure.rest.AzureRestService#getObject(java.lang.String,
     *      java.lang.String)
     */
    public InputStreamBlob getObject(String containerName, String blobName) throws AzureRestCommunicationException,
            AzureRestResponseHandlingException {

        LOG.debug("Receive blob '{}' from Azure container '{}' as string", blobName, containerName);

        HttpClient client = createClient();
        HttpGet req = new HttpGet(String.format(blobUrlFormat, credentials.getAccountName(), containerName + "/"
                + blobName));

        req.addHeader("x-ms-blob-type", "BlockBlob");

        try {
            HttpResponse response = client.execute(req);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Receive blob '{}' from Azure container '{}' as string response status line: '{}'",
                        new Object[] { blobName, containerName, response.getStatusLine() });
            }

            return responseHandler.handleGetObjectResponse(response, containerName, blobName);
        } catch (ClientProtocolException e) {
            throw new AzureRestCommunicationException(e,
                    "Unexpected protocol error while getting blob '%s' from container '%s'.", blobName, containerName);
        } catch (IOException e) {
            throw new AzureRestCommunicationException(e,
                    "Unexpected IO error while getting blob '%s' from container '%s'.", blobName, containerName);
        }
    }

    /**
     * @return
     * @throws AzureRestCommunicationException
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.cloud.storage.azure.rest.AzureRestService#listContainerNames()
     */
    public List<String> listContainerNames() throws AzureRestCommunicationException, AzureRestResponseHandlingException {
        LOG.debug("List Azure containers");

        HttpClient client = createClient();
        HttpGet req = new HttpGet(String.format(blobUrlFormat, credentials.getAccountName(), "?comp=list"));

        try {
            HttpResponse response = client.execute(req);
            LOG.debug("List Azure containers response status line: '{}'", response.getStatusLine());

            return responseHandler.handleListContainerNamesResponse(response);
        } catch (ClientProtocolException e) {
            throw new AzureRestCommunicationException(
                    "Unexpected protocol error while getting list of containe names.", e);
        } catch (IOException e) {
            throw new AzureRestCommunicationException("Unexpected IO error while getting list of containe names.", e);
        }
    }

    /**
     * @param containerName
     * @param blob
     * @throws AzureRestRequestCreationException
     * @throws AzureRestCommunicationException
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.cloud.storage.azure.rest.AzureRestService#putObject(java.lang.String,
     *      org.opencredo.cloud.storage.azure.model.Blob)
     */
    public void putObject(String containerName, Blob<?> blob) throws AzureRestRequestCreationException,
            AzureRestCommunicationException, AzureRestResponseHandlingException {

        LOG.debug("Send string as blob '{}' to Azure container '{}'", blob.getName(), containerName);

        HttpEntity entity;
        try {
            entity = blob.createRequestBody();
        } catch (AzureRestRequestCreationException e) {
            throw new AzureRestRequestCreationException(e,
                    "Failed to create request body as blob '{}' in container '{}'", blob.getName(), containerName);
        }

        HttpClient client = createClient();
        HttpPut req = new HttpPut(String.format(blobUrlFormat, credentials.getAccountName(), containerName + "/"
                + blob.getName(), entity));

        req.addHeader("x-ms-blob-type", "BlockBlob");
        req.setEntity(entity);

        try {
            HttpResponse response = client.execute(req);
            LOG.debug("Response status: '{}'", response.getStatusLine());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Send blob '{}' to Azure container '{}' response status line: '{}'", new Object[] {
                        blob.getName(), containerName, response.getStatusLine() });
            }

            responseHandler.handlePutObjectResponse(response, containerName, blob);
        } catch (ClientProtocolException e) {
            throw new AzureRestCommunicationException(e,
                    "Unexpected protocol error while sending blob '%s' to container '%s'.", blob.getName(),
                    containerName);
        } catch (IOException e) {
            throw new AzureRestCommunicationException(e,
                    "Unexpected IO error while sending blob '%s' to container '%s'.", blob.getName(), containerName);
        }

    }

    /**
     * @param containerName
     * @return
     * @throws AzureRestCommunicationException
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.cloud.storage.azure.rest.AzureRestService#listContainerObjectDetails(java.lang.String)
     */
    public List<BlobDetails> listContainerObjectDetails(String containerName) throws AzureRestCommunicationException,
            AzureRestResponseHandlingException {
        LOG.debug("List objects in Azure container '{}'", containerName);

        HttpClient client = createClient();
        HttpGet req = new HttpGet(String.format(blobUrlFormat, credentials.getAccountName(), containerName
                + "?restype=container&comp=list"));

        try {
            HttpResponse response = client.execute(req);
            LOG.debug("List objects in Azure container '{}' response status line: '{}'", containerName, response
                    .getStatusLine());

            return responseHandler.handleListContainerObjectDetailsResponse(response, containerName);
        } catch (ClientProtocolException e) {
            throw new AzureRestCommunicationException(e,
                    "Unexpected protocol error while getting list of blob details from container '%s'.", containerName);
        } catch (IOException e) {
            throw new AzureRestCommunicationException(e,
                    "Unexpected IO error while getting list of blob details from container '%s'.", containerName);
        }
    }

    /**
     * 
     * @return
     */
    private HttpClient createClient() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.addRequestInterceptor(authorizationInterceptor);
        return httpClient;
    }

    /**
     * @param containerName
     * @return
     * @throws AzureRestCommunicationException
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.cloud.storage.azure.rest.AzureRestService#checkContainerStatus(java.lang.String)
     */
    public ContainerStatus checkContainerStatus(String containerName) throws AzureRestCommunicationException,
            AzureRestResponseHandlingException {
        HttpClient client = createClient();
        HttpGet req = new HttpGet(String.format(blobUrlFormat, credentials.getAccountName(), containerName
                + "?restype=container&comp=list"));

        try {
            HttpResponse response = client.execute(req);
            LOG.debug("Check status for Azure container '{}' response status line: '{}'", containerName, response
                    .getStatusLine());

            return responseHandler.handleCheckContainerStatus(response, containerName);
        } catch (ClientProtocolException e) {
            throw new AzureRestCommunicationException(e,
                    "Unexpected protocol error while checking status for container '%s'.", containerName);
        } catch (IOException e) {
            throw new AzureRestCommunicationException(e,
                    "Unexpected IO error while checking status for container '%s'.", containerName);
        }
    }
}
