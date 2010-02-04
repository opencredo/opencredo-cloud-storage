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
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.soyatec.windows.azure.authenticate.ResourceUriComponents;
import org.soyatec.windows.azure.constants.BlobBlockConstants;
import org.soyatec.windows.azure.error.StorageException;
import org.soyatec.windows.azure.util.HttpUtilities;
import org.soyatec.windows.azure.util.NameValueCollection;
import org.soyatec.windows.azure.util.TimeSpan;
import org.soyatec.windows.azure.util.Utilities;

/**
 * The <code>BlobContainer</code> class is used to access and enumerate blobs in
 * the container. Storage key credentials are needed to access private blobs but
 * not for public blobs.
 * 
 */
public abstract class BlobContainer {

	private final URI baseUri;
	private final String accountName;
	private final String containerName;
	private final boolean usePathStyleUris;
	private URI containerUri;
	private Timestamp lastModifiedTime;
	private TimeSpan timeout;
	private RetryPolicy retryPolicy;

	private long blockSize = BlobBlockConstants.BlockSize;

	/**
	 * When fetch blob data slice by slice, the fetch progress can be stopped by
	 * setting falg to true.
	 */
	private final AtomicBoolean stopFlag = new AtomicBoolean(Boolean.FALSE);

	protected BlobContainer(URI baseUri, String accountName,
			String containerName) {
		this(baseUri, true, accountName, containerName, Utilities.minTime());
	}

	protected BlobContainer(URI baseUri, boolean usePathStyleUris,
			String accountName, String containerName) {
		this(baseUri, usePathStyleUris, accountName, containerName, Utilities
				.minTime());
	}

	/**
	 * 
	 * @param url
	 */
	public abstract void useSharedAccessUrl(ShareAccessUrl url);

	/**
	 * Clear shared access url. User need to call explicitly.
	 */
	public abstract void clearSharedAccessUrl();

	/**
	 * Use this constructor to access private blobs.
	 * 
	 * @param baseUri
	 *            The base Uri for the storage endpoint
	 * @param usePathStyleUris
	 *            If true, path-style URIs
	 *            (http://baseuri/accountname/containername/objectname) are used
	 *            and if false host-style URIs
	 *            (http://accountname.baseuri/containername/objectname) are
	 *            used, where baseuri is the URI of the service
	 * @param accountName
	 *            Name of the storage account
	 * @param containerName
	 *            Name of the container
	 * @param lastModified
	 *            Date of last modification
	 */
	protected BlobContainer(URI baseUri, boolean usePathStyleUris,
			String accountName, String containerName, Timestamp lastModified) {
		if (!Utilities.isValidContainerOrQueueName(containerName)) {
			throw new IllegalArgumentException(
					MessageFormat
							.format(
									"The specified container name \"{0}\" is not valid!"
											+ "Please choose a name that conforms to the naming conventions for containers!",
									containerName));
		}
		this.baseUri = baseUri;
		this.usePathStyleUris = usePathStyleUris;
		this.accountName = accountName;
		this.containerName = containerName;
		this.timeout = BlobStorage.DefaultTimeout;
		this.retryPolicy = BlobStorage.DefaultRetryPolicy;
		this.lastModifiedTime = lastModified;
	}
	
	public String getURL(){
		ResourceUriComponents uriComponents = new ResourceUriComponents(
				getAccountName(), getContainerName(), null);
		URI uri = HttpUtilities.createRequestUri(getBaseUri(), isUsePathStyleUris(),getAccountName(), getContainerName(), null,
				null, new NameValueCollection(), uriComponents);
		return uri.toString();
	}

	/**
	 * Create a new blob or overwrite an existing blob.
	 * 
	 * @param blobProperties
	 *            The properties of the blob
	 * @param blobContents
	 *            The contents of the blob
	 * @param overwrite
	 *            Should this request overwrite an existing blob
	 * @return true if the blob was created. false if the blob already exists
	 *         and parameter "overwrite" was set to false. The LastModifiedTime
	 *         property of <paramref name="blobProperties"/> is set as a result
	 *         of this call. This method also has an effect on the ETag values
	 *         that are managed by the service.
	 * @throws StorageException
	 */
	public abstract boolean createBlob(BlobProperties blobProperties,
			BlobContents blobContents, boolean overwrite)
			throws StorageException;

	/**
	 * Updates an existing blob if it has not been modified since the specified
	 * time which is typically the last modified time of the blob when you
	 * retrieved it. Use this method to implement optimistic concurrency by
	 * avoiding clobbering changes to the blob made by another writer.
	 * 
	 * @param blobProperties
	 *            The properties of the blob. This object should be one
	 *            previously obtained from a call to GetBlob or
	 *            GetBlobProperties and have its LastModifiedTime property set.
	 * @param contents
	 *            The contents of the blob. The contents of the blob should be
	 *            readable
	 * @return true if the blob was updated. false if the blob has changed since
	 *         the last time. The LastModifiedTime property of parameter
	 *         "properties" is set as a result of this call.
	 * @throws StorageException
	 */
	public abstract boolean updateBlobIfNotModified(
			BlobProperties blobProperties, BlobContents contents)
			throws StorageException;

	/**
	 * Set the metadata of an existing blob.
	 * 
	 * @param blobProperties
	 *            The blob properties object whose metadata is to be updated
	 * @throws StorageException
	 */
	public abstract void updateBlobMetadata(BlobProperties blobProperties)
			throws StorageException;

	/**
	 * Set the metadata of an existing blob if it has not been modified since it
	 * was last retrieved.
	 * 
	 * @param blobProperties
	 *            The blob properties object whose metadata is to be updated.
	 *            Typically obtained by a previous call to GetBlob or
	 *            GetBlobProperties
	 * @return true if the blob metadata was updated. false if it was not
	 *         updated because the blob has been modified
	 * @exception StorageException
	 */
	public abstract boolean updateBlobMetadataIfNotModified(
			BlobProperties blobProperties) throws StorageException;

	/**
	 * Create the container if it does not exist. The container is created with
	 * private access control and no metadata.
	 * 
	 * @return true if the container was created. false if the container already
	 *         exists
	 * @throws StorageStorageException
	 */
	public abstract boolean createContainer() throws StorageException;

	/**
	 * Create the container with the specified metadata and access control if it
	 * does not exist. Metadata Names are case-insensitive. If two or more headers with the same name are submitted for a resource, 
	 * the headers will be combined into a single header with a comma delimiting each value.The total size of the metadata, 
	 * including both the name and value together, may not exceed 8 KB in size.
	 * 
	 * @param metadata
	 *            The metadata for the container. Can be null to indicate no
	 *            metadata
	 * @param accessControl
	 *            The access control (public or private) with which to create
	 *            the container
	 * @return true if the container was created. false if the container already
	 *         exists
	 * @throws StorageException
	 */
	public abstract boolean createContainer(NameValueCollection metadata,
			ContainerAccessControl accessControl) throws StorageException;

	public abstract void setContainerMetadata(final NameValueCollection metadata)
			throws StorageException;

	/**
	 * Get the blob contents and properties if the blob exists.
	 * 
	 * @param name
	 *            The name of the blob
	 * @param blobContents
	 *            Object in which the contents are returned. This object should
	 *            contain a writable stream or should be a default constructed
	 *            object.
	 * @param transferAsChunks
	 *            Should the blob be gotten in pieces. This requires more
	 *            round-trips, but will retry smaller pieces in case of failure.
	 * @return The properties of the blob if the blob exists.
	 */
	public abstract BlobProperties getBlob(String name,
			BlobContents blobContents, boolean transferAsChunks)
			throws StorageException;

	/**
	 * Gets the blob contents and properties if the blob has not been modified
	 * since the time specified. Use this method if you have cached the contents
	 * of a blob and want to avoid retrieving the blob if it has not changed
	 * since the last time you retrieved it.
	 * 
	 * @param blobProperties
	 *            The properties of the blob obtained from an earlier call to
	 *            GetBlob. This parameter is updated by the call if the blob has
	 *            been modified
	 * @param blobContents
	 *            Contains the stream to which the contents of the blob are
	 *            written if it has been modified
	 * @param transferAsChunks
	 *            Should the blob be gotten in pieces. This requires more
	 *            round-trips, but will retry smaller pieces in case of failure.
	 * @return true if the blob has been modified, false otherwise
	 */
	public abstract boolean getBlobIfModified(BlobProperties blobProperties,
			BlobContents blobContents, boolean transferAsChunks)
			throws StorageException;

	/**
	 * Get the properties of the blob if it exists. This method is also the
	 * simplest way to check if a blob exists.
	 * 
	 * @param name
	 *            The name of the blob
	 * @return The properties of the blob if it exists. null otherwise. // / The
	 *         properties for the contents of the blob are not set
	 */
	public abstract BlobProperties getBlobProperties(String name)
			throws StorageException;

	/**
	 * Deletes the current container.
	 * 
	 * @return
	 * @throws StorageException
	 */
	public abstract boolean deleteContainer() throws StorageException;

	/**
	 * Check if the blob container exists
	 * 
	 * @return True if the container exists, false otherwise.
	 * @throws StorageException
	 */
	public abstract boolean doesContainerExist() throws StorageException;

	
	/**
	 * Check if the blob exists
	 * 
	 * @param blobName
	 *            of the BLOB.
	 * @return true if the blob exists, false otherwise.
	 * @throws StorageException
	 */
	public abstract boolean doesBlobExist(String blobName)
			throws StorageException;

	/**
	 * Enumerates all blobs with a given prefix.
	 * 
	 * @param prefix
	 * @param combineCommonPrefixes
	 *            If true common prefixes with "/" as separator
	 * @return The list of blob properties and common prefixes
	 * @throws StorageException
	 */
	public abstract Collection<BlobProperties> listBlobs(String prefix,
			boolean combineCommonPrefixes) throws StorageException;

	/**
	 * Enumerates all blobs with a given prefix.
	 * 
	 * @param prefix
	 * @param combineCommonPrefixes
	 *            If true common prefixes with "/" as separator
	 * @param maxResults
	 *            Specifies the maximum number of blobs to return per call to
	 *            Azure storage. This does NOT affect list size returned by this
	 *            function.
	 * @return The list of blob properties and common prefixes
	 * @throws StorageException
	 */
	public abstract Collection<BlobProperties> listBlobs(String prefix,
			boolean combineCommonPrefixes, int maxResults)
			throws StorageException;


	/**
	 * Set the access control permissions associated with the container.
	 * 
	 * @param acl
	 *            The permission to set
	 */
	public abstract void setContainerAccessControl(
			final ContainerAccessControl acl) throws StorageException;

	/**
	 * Get the access control permissions associated with the container.
	 * 
	 * @throws StorageException
	 */
	public abstract ContainerAccessControl getContainerAccessControl()
			throws StorageException;

	/**
	 * Get the properties for the container if it exists.
	 * 
	 * @return The properties for the container if it exists, null otherwise
	 * @throws StorageException
	 */
	public abstract ContainerProperties getContainerProperties()
			throws StorageException;

	/**
	 * Copies a blob to a destination within the storage account. </p>
	 * <strong>Note</strong></br> The Copy Blob operation is available only in
	 * the 2009-04-14 version of the Blob service. It is currently available
	 * only in Windows Azure storage, and not in development storage, nor within
	 * the StorageClient sample included in the Windows Azure SDK.
	 * 
	 * @param destContainer
	 *            the destination blob container;
	 * @param destBlobName
	 *            the destination blob's name;
	 * @param sourceBlobName
	 *            the source blob's name;
	 * @return
	 * @throws StorageException
	 */
	public abstract boolean copyBlob(String destContainer, String destBlobName,
			String sourceBlobName) throws StorageException;

	/**
	 * Copies a blob to a destination within the storage account. </p>
	 * <strong>Note</strong></br> The Copy Blob operation is available only in
	 * the 2009-04-14 version of the Blob service. It is currently available
	 * only in Windows Azure storage, and not in development storage, nor within
	 * the StorageClient sample included in the Windows Azure SDK.
	 * 
	 * @param destContainer
	 *            the destination blob container;
	 * @param destBlobName
	 *            the destination blob's name;
	 * @param sourceBlobName
	 *            the source blob's name;
	 * @param metadata
	 *            The metadata for the Blob. Can be null to indicate no
	 *            metadata;
	 * @param constraints
	 *            The blob constraints for the blob copy operation.
	 * @return
	 * @throws StorageException
	 */
	public abstract boolean copyBlob(String destContainer, String destBlobName,
			String sourceBlobName, final NameValueCollection metadata,
			final BlobConstraints constraints) throws StorageException;

	/**
	 * Delete a blob with the given name.
	 * 
	 * @param name
	 *            The name of the blob
	 * @return true if the blob exists and was successfully deleted, false if
	 *         the blob does not exist
	 * @throws StorageException
	 */
	public abstract boolean deleteBlob(String name) throws StorageException;

	/**
	 * Delete a blob with the given name if the blob has not been modified since
	 * it was last obtained. Use this method for optimistic concurrency to avoid
	 * deleting a blob that has been modified since the last time you retrieved
	 * it
	 * 
	 * @param blob
	 *            A blob object (typically previously obtained from a GetBlob
	 *            call)
	 * @return true if the blob exists and was successfully deleted, false if
	 *         the blob does not exist or was not deleted because the blob was
	 *         modified.
	 * @StorageException If the blob was not deleted because the blob was
	 *                   modified, then throw an storage StorageException.
	 */
	public abstract boolean deleteBlobIfNotModified(BlobProperties blob)
			throws StorageException;

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
	 * @return The name of the blob container.
	 */
	public String getContainerName() {
		return containerName;
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
	 * @return The URI of the container.
	 */
	public URI getContainerUri() {
		return containerUri;
	}

	/**
	 * @return The timestamp for last modification of container.
	 */
	public Timestamp getLastModifiedTime() {
		return lastModifiedTime;
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

	/**
	 * @param lastModifiedTime
	 *            The timestamp for last modification of container.
	 */
	void setLastModifiedTime(Timestamp lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	/**
	 * @param containerUri
	 *            The URI of the container.
	 */
	public void setContainerUri(URI containerUri) {
		this.containerUri = containerUri;
	}

	/**
	 * Set if the fetch process should be stopped.
	 * 
	 * @param stop
	 * 
	 */
	public void stopFetchProgress(boolean stop) {
		stopFlag.set(stop);
	}

	/**
	 * @return Indicates whether the fetch process should be stopped.
	 */
	public boolean isStopped() {
		return stopFlag.get();
	}

	/**
	 * @return The size of the blob.
	 */
	public long getBlockSize() {
		return blockSize;
	}

	/**
	 * @param blockSize
	 *            The size of the blob.
	 */
	public void setBlockSize(long blockSize) {
		this.blockSize = blockSize;
	}

}
