/**
 * Copyright  2006-2009 Soyatec
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * $Id$
 */
package org.soyatec.windows.azure.blob;

import java.net.URI;
import java.util.List;

import org.soyatec.windows.azure.authenticate.StorageAccountInfo;
import org.soyatec.windows.azure.constants.StandardPortalEndpoints;
import org.soyatec.windows.azure.error.StorageException;
import org.soyatec.windows.azure.error.StorageServerException;
import org.soyatec.windows.azure.util.TimeSpan;

/**
 * The entry point of the blob storage API
 * 
 */
public abstract class BlobStorage {
	/**
	 * Indicates whether to use/generate path-style or host-style URIs
	 */
	private final boolean usePathStyleUris;
	/**
	 * The base URI of the blob storage service
	 */
	private final URI baseUri;
	/**
	 * The name of the storage account
	 */
	private final String accountName;

	/**
	 * Authentication key used for signing requests
	 */
	protected String base64Key;

	/**
	 * The time out for each request to the storage service.
	 */
	private TimeSpan timeout;
	/**
	 * The retry policy used for retrying requests
	 */
	private RetryPolicy retryPolicy;

	/**
	 * The default timeout
	 */
	public static final TimeSpan DefaultTimeout = TimeSpan.fromSeconds(30);

	/**
	 * The default retry policy
	 */
	public static final RetryPolicy DefaultRetryPolicy = RetryPolicies
			.noRetry();

	/**
	 * Factory method for BlobStorage
	 * 
	 * @param baseUri
	 *            The base URI of the blob storage service
	 * @param usePathStyleUris
	 *            If true, path-style URIs
	 *            (http://baseuri/accountname/containername/objectname) are
	 *            used. If false host-style URIs
	 *            (http://accountname.baseuri/containername/objectname) are
	 *            used, where baseuri is the URI of the service. If null, the
	 *            choice is made automatically: path-style URIs if host name
	 *            part of base URI is an IP addres, host-style otherwise.
	 * @param accountName
	 *            The name of the storage account
	 * @param base64Key
	 *            Authentication key used for signing requests
	 * @return A newly created BlobStorage instance
	 */
	public static BlobStorage create(URI baseUri, boolean usePathStyleUris,
			String accountName, String base64Key) {
		return new BlobStorageRest(baseUri, usePathStyleUris, accountName,
				base64Key);
	}

	/**
	 * Convient way to create a new BlobStorage.
	 * 
	 * @param usePathStyleUris
	 * @param accountName
	 * @param base64key
	 * @return
	 */
	public static BlobStorage create(boolean usePathStyleUris,
			String accountName, String base64key) {
		URI hostUri = null;
		if (usePathStyleUris) {
			hostUri = URI.create(StandardPortalEndpoints.HttpProtocolPrefix
					+ StandardPortalEndpoints.DevBlobEndpoint);
		} else {
			hostUri = URI.create(StandardPortalEndpoints.HttpProtocolPrefix
					+ StandardPortalEndpoints.BlobStorageEndpoint);
		}
		return new BlobStorageRest(hostUri, usePathStyleUris, accountName,
				base64key);
	}

	/**
	 * Factory method for BlobStorage
	 * 
	 * @param accountInfo
	 *            Account information
	 * @return A newly created BlobStorage instance
	 */
	public static BlobStorage create(StorageAccountInfo accountInfo) {
		return new BlobStorageRest(accountInfo.getBaseUri(), accountInfo
				.isUsePathStyleUris(), accountInfo.getAccountName(),
				accountInfo.getBase64Key());
	}

	/**
	 * Check if the blob container exists
	 * 
	 * @param containerName
	 *            of the BLOB.
	 * @return true if the blob exists, false otherwise.
	 * @throws StorageException
	 */
	public abstract boolean doesContainerExist(String containerName)	throws StorageException;

	/**
	 * Get a reference to a newly created BlobContainer object. This method does
	 * not make any calls to the storage service.
	 * 
	 * @param containerName
	 *            The name of the container
	 * @return A reference to a newly created BlobContainer object
	 */
	public abstract BlobContainer getBlobContainer(String containerName);

	/**
	 * Lists the containers within the account.
	 * 
	 * @return A list of containers
	 * @throws Exception
	 */
	public abstract List<BlobContainer> listBlobContainers()
			throws StorageServerException;

	public abstract ShareAccessUrl createSharedAccessUrl(String containerName,
			String blobName, EResourceType resource, int permissions,
			DateTime start, DateTime expiry, String identifier)
			throws StorageServerException;

	/**
	 * Creates a <code>BlobStorage</code> object with all of its fields set to
	 * the passed-in arguments.
	 * 
	 * @param baseUri
	 *            The base URI of the blob storage service
	 * @param usePathStyleUris
	 *            use/generate path-style or host-style URIs
	 * @param accountName
	 *            The name of the storage account
	 * @param base64Key
	 *            Authentication key used for signing requests
	 */
	protected BlobStorage(URI baseUri, boolean usePathStyleUris,
			String accountName, String base64Key) {
		this.baseUri = baseUri;
		this.accountName = accountName;
		this.base64Key = base64Key;
		this.usePathStyleUris = usePathStyleUris;
		timeout = DefaultTimeout;
		retryPolicy = DefaultRetryPolicy;
	}

	/**
	 * Indicates whether to use/generate path-style or host-style URIs.
	 * 
	 * @return true/false
	 */
	public boolean isUsePathStyleUris() {
		return usePathStyleUris;
	}

	/**
	 * @return The base URI of the blob storate service.
	 */
	public URI getBaseUri() {
		return baseUri;
	}

	/**
	 * @return The name of the storage account.
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * @return Authentication key used for signing requests
	 */
	public String getBase64Key() {
		return base64Key;
	}

	/**
	 * @return The time out for each request to the storage service.
	 */
	public TimeSpan getTimeout() {
		return timeout;
	}

	/**
	 * @return The retry policy used for retrying requests.
	 */
	public RetryPolicy getRetryPolicy() {
		return retryPolicy;
	}

	/**
	 * @param base64Key
	 *            Authentication key used for signing requests
	 */
	public void setBase64Key(String base64Key) {
		this.base64Key = base64Key;
	}

	/**
	 * @param timeout
	 *            The time out for each request to the storage service.
	 */
	public void setTimeout(TimeSpan timeout) {
		this.timeout = timeout;
	}

	/**
	 * @param retryPolicy
	 *            The retry policy used for retrying requests.
	 */
	public void setRetryPolicy(RetryPolicy retryPolicy) {
		this.retryPolicy = retryPolicy;
	}

}
