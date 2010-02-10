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
package org.opencredo.storage.azure.rest;

import java.util.List;

import org.opencredo.storage.BlobObject;
import org.opencredo.storage.azure.model.Blob;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public interface AzureRestService {

    void createContainer(String containerName) throws AzureRestServiceException;

    void deleteContainer(String containerName) throws AzureRestServiceException;

    /**
     * 
     * @return Returns list of container names, or empty list if containers not
     *         found.
     * @throws AzureRestServiceException
     */
    List<String> listContainers() throws AzureRestServiceException;

    void deleteObject(String containerName, String blobName) throws AzureRestServiceException;

    void putObject(String containerName, Blob blob) throws AzureRestServiceException;

    Blob getObject(String containerName, String blobName) throws AzureRestServiceException;

    /**
     * 
     * @return Returns container objects list, or empty list if container does
     *         not contain any objects.
     * @throws AzureRestServiceException
     */
    List<BlobObject> listContainerObjects(String containerName) throws AzureRestServiceException;
}
