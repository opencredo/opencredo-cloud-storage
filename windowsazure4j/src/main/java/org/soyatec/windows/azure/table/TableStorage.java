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
package org.soyatec.windows.azure.table;

import java.net.URI;
import java.util.List;

import org.soyatec.windows.azure.authenticate.SharedKeyCredentials;
import org.soyatec.windows.azure.authenticate.StorageAccountInfo;
import org.soyatec.windows.azure.blob.RetryPolicies;
import org.soyatec.windows.azure.blob.RetryPolicy;
import org.soyatec.windows.azure.constants.StandardPortalEndpoints;
import org.soyatec.windows.azure.error.StorageException;
import org.soyatec.windows.azure.util.TimeSpan;

/**
 * API entry point for using structured storage. The underlying usage pattern is
 * designed to be similar to the one used in blob and queue services in this
 * library. Users create a TableStorage object by calling the static Create()
 * method passing account credential information to this method.
 */
public abstract class TableStorage {

	/**
	 * Indicates whether to use/generate path-style or host-style URIs
	 */
	private boolean usePathStyleUris;

	/**
	 * The base URI of the table storage service
	 */
	private URI baseUri;

	/**
	 * The name of the storage account
	 */
	private String accountName;

	private String base64Key;

	/**
	 * The retry policy used for retrying requests
	 */
	private RetryPolicy retryPolicy;

	/**
	 * The time out for each request to the storage service.
	 */
	private TimeSpan timeout;

	private SharedKeyCredentials credentials;

	/**
	 * The default retry policy
	 */
	public static final RetryPolicy DefaultRetryPolicy = RetryPolicies
			.noRetry();

	/**
	 * The default timeout
	 */
	public static final TimeSpan DefaultTimeout = TimeSpan.fromSeconds(30);

	protected TableStorage(URI baseUri, boolean usePathStyleUris,
			String accountName, String base64Key) {
		this.baseUri = baseUri;
		this.usePathStyleUris = usePathStyleUris;
		this.accountName = accountName;
		this.base64Key = base64Key;
		this.retryPolicy = DefaultRetryPolicy;
		this.timeout = DefaultTimeout;
	}

	/**
	 * Creates a TableStorage service object. This object is the entry point
	 * into the table storage API.
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
	 * @return A newly created TableStorage instance
	 */
	public static TableStorage create(URI baseUri, boolean usePathStyleUris,
			String accountName, String base64Key) {
		return new TableStorageRest(baseUri, usePathStyleUris, accountName,
				base64Key);
	}

	/**
	 * Creates a TableStorage service object. This object is the entry point
	 * into the table storage API.
	 * 
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
	 * @return A newly created TableStorage instance
	 */
	public static TableStorage create(boolean usePathStyleUris,
			String accountName, String base64Key) {
		URI hostUri = null;
		if (usePathStyleUris) {
			hostUri = URI.create(StandardPortalEndpoints.HttpProtocolPrefix
					+ StandardPortalEndpoints.DevTableEndpoint);
		} else {
			hostUri = URI.create(StandardPortalEndpoints.HttpProtocolPrefix
					+ StandardPortalEndpoints.TableStorageEndpoint);
		}
		return new TableStorageRest(hostUri, usePathStyleUris, accountName,
				base64Key);
	}

	/**
	 * Creates a TableStorage service object. This object is the entry point
	 * into the table storage API.
	 * 
	 * @param info
	 *            {@link StorageAccountInfo}
	 * @return A newly created TableStorage instance
	 */
	public static TableStorage create(StorageAccountInfo info) {
		return TableStorage.create(info.getBaseUri(),
				info.isUsePathStyleUris(), info.getAccountName(), info
						.getBase64Key());
	}

	/**
	 * Lists all the tables under this service's URL
	 */
	public abstract List<String> listTables() throws StorageException;

	/**
	 * Get a reference to a Azure Table object with a specified name. The method
	 * does not make call to a table service.
	 * 
	 * @param tableName
	 *            The name of the table
	 * @return A newly created table object
	 */
	public abstract AzureTable getAzureTable(String tableName);

	/**
	 * Indicates whether to use/generate path-style or host-style URIs
	 * 
	 * @return
	 */
	public boolean isUsePathStyleUris() {
		return usePathStyleUris;
	}

	/**
	 * Get The base URI of the table storage service
	 * 
	 * @return
	 */
	public URI getBaseUri() {
		return baseUri;
	}

	/**
	 * Get the name of storage account
	 * 
	 * @return
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * Get the authenticate key for of storage account
	 * 
	 * @return
	 */
	public String getBase64Key() {
		return base64Key;
	}

	/**
	 * Set the authenticate key for of storage account
	 * 
	 * @param base64Key
	 */
	public void setBase64Key(String base64Key) {
		this.base64Key = base64Key;
	}

	/**
	 * Get the {@link RetryPolicy}
	 * 
	 * @return
	 */
	public RetryPolicy getRetryPolicy() {
		return retryPolicy;
	}

	/**
	 * Set the {@link RetryPolicy}
	 * 
	 * @param retryPolicy
	 */
	public void setRetryPolicy(RetryPolicy retryPolicy) {
		this.retryPolicy = retryPolicy;
	}

	/**
	 * Get time out per request
	 * 
	 * @return
	 */
	public TimeSpan getTimeout() {
		return timeout;
	}

	/**
	 * Set timeout per request
	 * 
	 * @param timeout
	 */
	public void setTimeout(TimeSpan timeout) {
		this.timeout = timeout;
	}

	/**
	 * Set the credential
	 * 
	 * @return
	 */
	public SharedKeyCredentials getCredentials() {
		return credentials;
	}

	/**
	 * Get credential
	 * 
	 * @param credentials
	 */
	public void setCredentials(SharedKeyCredentials credentials) {
		this.credentials = credentials;
	}

}
