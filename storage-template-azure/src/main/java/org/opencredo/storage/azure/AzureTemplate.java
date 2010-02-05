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
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.opencredo.storage.BlobObject;
import org.opencredo.storage.ContainerStatus;
import org.opencredo.storage.StorageCommunicationException;
import org.opencredo.storage.StorageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class AzureTemplate implements StorageOperations {
    private final static Logger LOG = LoggerFactory.getLogger(AzureTemplate.class);

    private String blobUrlFormat = "http://%s.blob.core.windows.net/%s";

    private final AzureCredentials credentials;
    private final RequestAuthorizationInterceptor authorizationInterceptor;

    /**
     * 
     */
    public AzureTemplate(AzureCredentials credentials) {
        this.credentials = credentials;
        authorizationInterceptor = new RequestAuthorizationInterceptor(credentials);
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
        LOG.debug("Delete Azure blob '{}' to container '{}' from string", objectName, containerName);

        HttpClient client = createClient();
        HttpDelete req = new HttpDelete(String.format(blobUrlFormat, credentials.getAccountName(), containerName + "/"
                + objectName));

        try {
            HttpResponse response = client.execute(req);
            LOG.debug("Response status: '{}'", response.getStatusLine());
            response.getEntity().writeTo(System.out);
        } catch (ClientProtocolException e) {
            throw new StorageCommunicationException(e, "Delete blob '%s' from container '%s' problem", objectName,
                    containerName);
        } catch (IOException e) {
            throw new StorageCommunicationException(e, "Delete blob '%s' from container '%s' IO problem", objectName,
                    containerName);
        }

    }

    /**
     * @param containerName
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#listContainerObjects(java.lang.String)
     */
    public List<BlobObject> listContainerObjects(String containerName) throws StorageCommunicationException {
        LOG.debug("List objects in Azure container '{}'", containerName);

        HttpClient client = createClient();
        HttpGet req = new HttpGet(String.format(blobUrlFormat, credentials.getAccountName(), containerName
                + "?restype=container&comp=list"));

        try {
            HttpResponse response = client.execute(req);
            LOG.debug("Response status: '{}'", response.getStatusLine());
            // TODO Validate response status

            response.getEntity().writeTo(System.out);
        } catch (ClientProtocolException e) {
            throw new StorageCommunicationException(e, "List objects in container '%s' problem", containerName);
        } catch (IOException e) {
            throw new StorageCommunicationException(e, "List objects in container '%s' IO problem", containerName);
        }

        return null;
    }

    /**
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#listContainers()
     */
    public String[] listContainers() throws StorageCommunicationException {
        LOG.debug("List Azure containers");

        HttpClient client = createClient();
        HttpGet req = new HttpGet(String.format(blobUrlFormat, credentials.getAccountName(), "?comp=list"));

        try {
            HttpResponse response = client.execute(req);
            LOG.debug("Response status: '{}'", response.getStatusLine());
            response.getEntity().writeTo(System.out);
            // TODO Validate response status
        } catch (ClientProtocolException e) {
            throw new StorageCommunicationException("List containers problem", e);
        } catch (IOException e) {
            throw new StorageCommunicationException("List containers IO problem", e);
        }

        // TODO ccreate the response
        return null;
    }

    /**
     * @param containerName
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#createContainer(java.lang.String)
     */
    public void createContainer(String containerName) throws StorageCommunicationException {
        LOG.debug("Create Azure container '{}'", containerName);

        HttpClient client = createClient();
        HttpPut req = new HttpPut(String.format(blobUrlFormat, credentials.getAccountName(), containerName
                + "?restype=container"));

        try {
            HttpResponse response = client.execute(req);
            LOG.debug("Response status: '{}'", response.getStatusLine());
            response.getEntity().writeTo(System.out);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CONFLICT) {
                LOG.warn("The specified container '" + containerName + "' already exists.");
            }
        } catch (ClientProtocolException e) {
            throw new StorageCommunicationException(e, "Create container '%s' problem", containerName);
        } catch (IOException e) {
            throw new StorageCommunicationException(e, "Create container '%s' IO problem", containerName);
        }
    }

    /**
     * @param containerName
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#deleteContainer(java.lang.String)
     */
    public void deleteContainer(String containerName) throws StorageCommunicationException {
        LOG.debug("Delete Azure container '{}'", containerName);

        HttpClient client = createClient();
        HttpDelete req = new HttpDelete(String.format(blobUrlFormat, credentials.getAccountName(), containerName
                + "?restype=container"));

        try {
            HttpResponse response = client.execute(req);
            LOG.debug("Response status: '{}'", response.getStatusLine());
            response.getEntity().writeTo(System.out);
        } catch (ClientProtocolException e) {
            throw new StorageCommunicationException(e, "Delete container '%s' problem", containerName);
        } catch (IOException e) {
            throw new StorageCommunicationException(e, "Delete container '%s' IO problem", containerName);
        }
    }

    /**
     * @param objectName
     * @return
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#receiveAsFile(java.lang.String)
     */
    public File receiveAsFile(String objectName) throws StorageCommunicationException {
        throw new RuntimeException("Not implementated");
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
        throw new RuntimeException("Not implementated");
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
        throw new RuntimeException("Not implementated");
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
        LOG.debug("Get Azure blob '{}' to container '{}' as string", objectName, containerName);

        HttpClient client = createClient();
        HttpGet req = new HttpGet(String.format(blobUrlFormat, credentials.getAccountName(), containerName + "/"
                + objectName));

        req.addHeader("x-ms-blob-type", "BlockBlob");

        try {
            HttpResponse response = client.execute(req);
            LOG.debug("Response status: '{}'", response.getStatusLine());
            response.getEntity().writeTo(System.out);
        } catch (ClientProtocolException e) {
            throw new StorageCommunicationException(e, "Receive blob '%s' from container '%s' problem", objectName,
                    containerName);
        } catch (IOException e) {
            throw new StorageCommunicationException(e, "Receive blob '%s' from container '%s' IO problem", objectName,
                    containerName);
        }

        return null;
    }

    /**
     * @param objectName
     * @param stringToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#send(java.lang.String,
     *      java.lang.String)
     */
    public void send(String objectName, String stringToSend) throws StorageCommunicationException {
        throw new RuntimeException("Not implementated");

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
        LOG.debug("Add Azure blob '{}' to container '{}' from string", objectName, containerName);

        HttpEntity entity;
        try {
            entity = new StringEntity(stringToSend);
        } catch (UnsupportedEncodingException e) {
            throw new StorageCommunicationException("Usupported encoding of string to be send to container '"
                    + containerName + "' as blob '" + objectName + "'.", e);
        }

        HttpClient client = createClient();
        HttpPut req = new HttpPut(String.format(blobUrlFormat, credentials.getAccountName(), containerName + "/"
                + objectName, entity));

        req.addHeader("x-ms-blob-type", "BlockBlob");
        req.setEntity(entity);

        try {
            HttpResponse response = client.execute(req);
            LOG.debug("Response status: '{}'", response.getStatusLine());
            response.getEntity().writeTo(System.out);
        } catch (ClientProtocolException e) {
            throw new StorageCommunicationException(e, "Add blob '%s' to container '%s' problem", objectName,
                    containerName);
        } catch (IOException e) {
            throw new StorageCommunicationException(e, "Add blob '%s' to container '%s' IO problem", objectName,
                    containerName);
        }

    }

    /**
     * @param fileToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#send(java.io.File)
     */
    public void send(File fileToSend) throws StorageCommunicationException {
        throw new RuntimeException("Not implementated");

    }

    /**
     * @param containerName
     * @param fileToSend
     * @throws StorageCommunicationException
     * @see org.opencredo.storage.StorageOperations#send(java.lang.String,
     *      java.io.File)
     */
    public void send(String containerName, File fileToSend) throws StorageCommunicationException {
        throw new RuntimeException("Not implementated");

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
        throw new RuntimeException("Not implementated");

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

    /**
     * 
     * @return
     */
    private HttpClient createClient() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.addRequestInterceptor(authorizationInterceptor);
        return httpClient;
    }
}
