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

import java.io.IOException;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.soyatec.windows.azure.authenticate.AccessPolicy;
import org.soyatec.windows.azure.authenticate.Base64;
import org.soyatec.windows.azure.authenticate.HttpRequestAccessor;
import org.soyatec.windows.azure.authenticate.ResourceUriComponents;
import org.soyatec.windows.azure.authenticate.SharedKeyCredentials;
import org.soyatec.windows.azure.authenticate.SharedKeyCredentialsWrapper;
import org.soyatec.windows.azure.authenticate.SignedIdentifier;
import org.soyatec.windows.azure.blob.io.MemoryStream;
import org.soyatec.windows.azure.blob.io.Stream;
import org.soyatec.windows.azure.constants.BlobBlockConstants;
import org.soyatec.windows.azure.constants.CompConstants;
import org.soyatec.windows.azure.constants.ConstChars;
import org.soyatec.windows.azure.constants.HeaderNames;
import org.soyatec.windows.azure.constants.HeaderValues;
import org.soyatec.windows.azure.constants.HttpMethod;
import org.soyatec.windows.azure.constants.ListingConstants;
import org.soyatec.windows.azure.constants.QueryParams;
import org.soyatec.windows.azure.constants.XmsVersion;
import org.soyatec.windows.azure.constants.XmlElementNames;
import org.soyatec.windows.azure.error.StorageErrorCode;
import org.soyatec.windows.azure.error.StorageException;
import org.soyatec.windows.azure.error.StorageServerException;
import org.soyatec.windows.azure.internal.HttpWebResponse;
import org.soyatec.windows.azure.internal.OutParameter;
import org.soyatec.windows.azure.util.HttpUtilities;
import org.soyatec.windows.azure.util.Logger;
import org.soyatec.windows.azure.util.NameValueCollection;
import org.soyatec.windows.azure.util.TimeSpan;
import org.soyatec.windows.azure.util.Utilities;
import org.soyatec.windows.azure.util.xml.AtomUtil;
import org.soyatec.windows.azure.util.xml.XPathQueryHelper;
import org.soyatec.windows.azure.util.xml.XmlUtil;

public class BlobContainerRest extends BlobContainer {

	private byte[] key;
	private SharedKeyCredentials credentials;
	private ShareAccessUrl shareAccessUrl;

	public BlobContainerRest(URI baseUri, boolean usePathStyleUris,
			String accountName, String containerName, String base64Key,
			Timestamp lastModified, TimeSpan timeOut, RetryPolicy retryPolicy) {
		super(baseUri, usePathStyleUris, accountName, containerName,
				lastModified);
		ResourceUriComponents uriComponents = new ResourceUriComponents(
				accountName, containerName, null);
		URI containerUri = HttpRequestAccessor.constructResourceUri(baseUri,
				uriComponents, usePathStyleUris);
		setContainerUri(containerUri);
		if (base64Key != null)
			key = Base64.decode(base64Key);
		credentials = new SharedKeyCredentials(accountName, key);
		setTimeout(timeOut);
		setRetryPolicy(retryPolicy);
	}

	/**
	 * Create a new blob or overwrite an existing blob.
	 * 
	 * @throws StorageException
	 */
	public boolean createBlob(BlobProperties blobProperties,
			BlobContents blobContents, boolean overwrite)
			throws StorageException {
		if (blobProperties.getName() == null
				|| blobProperties.getName().equals("")) {
			throw new IllegalArgumentException("Blob name is empty.");
		}
		try {
			return putBlobImpl(blobProperties, blobContents.getStream(),
					overwrite, null);
		} catch (Exception e) {
			throw HttpUtilities.translateWebException(e);
		}
	}

	/**
	 * Updates an existing blob if it has not been modified since the specified
	 * time which is typically the last modified time of the blob when you
	 * retrieved it.
	 * 
	 * @throws StorageException
	 */
	public boolean updateBlobIfNotModified(BlobProperties blobProperties,
			BlobContents contents) throws StorageException {
		try {
			return putBlobImpl(blobProperties, contents.getStream(), true,
					blobProperties.getETag());
		} catch (Exception e) {
			throw HttpUtilities.translateWebException(e);
		}
	}

	private boolean putBlobImpl(final BlobProperties blobProperties,
			final Stream stream, final boolean overwrite, final String eTag)
			throws Exception {
		if (blobProperties == null)
			throw new IllegalArgumentException(
					"Blob properties cannot be null or empty!");

		if (stream == null)
			throw new IllegalArgumentException(
					"Stream cannot be null or empty!");

		// If the blob is large, we should use blocks to upload it in pieces.
		// This will ensure that a broken connection will only impact a single
		// piece
		final long originalPosition = stream.getPosition();
		final long length = stream.length() - stream.getPosition();
		if (length > BlobBlockConstants.MaximumBlobSizeBeforeTransmittingAsBlocks)
			return putLargeBlobImpl(blobProperties, stream, overwrite, eTag);

		boolean retval = false;
		RetryPolicy policy = stream.canSeek() ? this.getRetryPolicy()
				: RetryPolicies.noRetry();
		retval = (Boolean) policy.execute(new Callable<Boolean>() {

			public Boolean call() throws Exception {
				if (stream.canSeek())
					stream.setPosition(originalPosition);

				return uploadData(blobProperties, stream, length, overwrite,
						eTag, new NameValueCollection());
			}

		});

		return retval;
	}

	private boolean uploadData(BlobProperties blobProperties, Stream stream,
			long length, boolean overwrite, String eTag,
			NameValueCollection queryParameters) throws Exception {
		ResourceUriComponents uriComponents = new ResourceUriComponents(
				getAccountName(), getContainerName(), blobProperties.getName());
		URI blobUri = HttpUtilities.createRequestUri(getBaseUri(), this
				.isUsePathStyleUris(), getAccountName(), getContainerName(),
				blobProperties.getName(), getTimeout(), queryParameters,
				uriComponents);

		HttpRequest request = createHttpRequestForPutBlob(blobUri,
				HttpMethod.Put, blobProperties, length, overwrite, eTag);
		credentials.signRequest(request, uriComponents);
		boolean retval = false;
		Stream requestStream = new MemoryStream();
		Utilities.copyStream(stream, requestStream, (int) length);
		((HttpEntityEnclosingRequest) request).setEntity(new ByteArrayEntity(
				requestStream.getBytes()));
		HttpWebResponse response = HttpUtilities.getResponse(request);
		if (response.getStatusCode() == HttpStatus.SC_CREATED) {
			retval = true;
		} else if (!overwrite
				&& (response.getStatusCode() == HttpStatus.SC_PRECONDITION_FAILED || response
						.getStatusCode() == HttpStatus.SC_NOT_MODIFIED)) {
			retval = false;
		} else {
			retval = false;
			HttpUtilities.processUnexpectedStatusCode(response);
		}

		blobProperties.setLastModifiedTime(response.getLastModified());
		blobProperties.setETag(response.getHeader(HeaderNames.ETag));
		requestStream.close();
		return retval;
	}

	private HttpRequest createHttpRequestForPutBlob(URI blobUri,
			String httpMethod, BlobProperties blobProperties,
			long contentLength, boolean overwrite, String eTag) {
		HttpRequest request = HttpUtilities.createHttpRequestWithCommonHeaders(
				blobUri, httpMethod, getTimeout());
		if (blobProperties.getContentEncoding() != null)
			request.addHeader(HeaderNames.ContentEncoding, blobProperties
					.getContentEncoding());
		if (blobProperties.getContentLanguage() != null)
			request.addHeader(HeaderNames.ContentLanguage, blobProperties
					.getContentLanguage());
		if (blobProperties.getContentType() != null)
			request.addHeader(HeaderNames.ContentType, blobProperties
					.getContentType());
		if (eTag != null)
			request.addHeader(HeaderNames.IfMatch, eTag);

		if (blobProperties.getMetadata() != null
				&& blobProperties.getMetadata().size() > 0) {
			HttpUtilities.addMetadataHeaders(request, blobProperties
					.getMetadata());
		}
		// request.addHeader(HeaderNames.ContentLength,
		// String.valueOf(contentLength));

		if (!overwrite) {
			request.addHeader(HeaderNames.IfNoneMatch, "*");
		}
		return request;
	}

	private boolean putLargeBlobImpl(final BlobProperties blobProperties,
			final Stream stream, final boolean overwrite, final String eTag)
			throws Exception {
		boolean retval = false;
		// Since we got a large block, chunk it into smaller pieces called
		// blocks
		final long blockSize = BlobBlockConstants.BlockSize;
		final long startPosition = stream.getPosition();
		final long length = stream.length() - startPosition;
		int numBlocks = (int) Math.ceil((double) length / blockSize);
		String[] blockIds = new String[numBlocks];

		// We can retry only if the stream supports seeking. An alternative is
		// to buffer the data in memory
		// but we do not do this currently.
		RetryPolicy policy = stream.canSeek() ? this.getRetryPolicy()
				: RetryPolicies.noRetry();
		// Upload each of the blocks, retrying any failed uploads
		for (int i = 0; i < numBlocks; ++i) {
			String generateBlockId = generateBlockId(i);
			final String blockId = Base64.encode(generateBlockId
					.getBytes("UTF-8"));
			blockIds[i] = blockId;
			Logger.debug("Block Id:" + blockIds[i]);
			final int index = i;
			retval = (Boolean) policy.execute(new Callable<Boolean>() {
				public Boolean call() throws Exception {
					// Rewind the stream to appropriate location in case this is
					// a retry
					if (stream.canSeek())
						stream.setPosition(startPosition + index * blockSize);
					NameValueCollection params = new NameValueCollection();
					params.put(QueryParams.QueryParamComp, CompConstants.Block);
					params.put(QueryParams.QueryParamBlockId, blockId);
					long blockLength = Math.min(blockSize, length
							- stream.getPosition());
					return uploadData(blobProperties, stream, blockLength,
							overwrite, eTag, params);

				}
			});
		}

		// Now commit the list
		// First create the output
		Document doc = DocumentHelper.createDocument();
		Element blockListElement = doc.addElement(XmlElementNames.BlockList);
		for (String id : blockIds) {
			blockListElement.addElement(XmlElementNames.Block).setText(id);
		}

		NameValueCollection params = new NameValueCollection();
		params.put(QueryParams.QueryParamComp, CompConstants.BlockList);
		Stream buffer = new MemoryStream(doc.asXML().getBytes());
		retval = uploadData(blobProperties, buffer, buffer.length(), overwrite,
				eTag, params);

		return retval;
	}

	/**
	 * For a given blob, the length of the value specified for the blockid
	 * parameter must be the same size for each block.
	 * 
	 * For more, see <a
	 * href="http://msdn.microsoft.com/en-us/library/dd135726.aspx">Put
	 * block</a>
	 * 
	 * 
	 * @param i
	 * @return
	 */
	protected String generateBlockId(int i) {
		String value = String.valueOf(i);
		while (value.length() < 64) {
			value = "0" + value;
		}
		return value;
	}

	@Override
	public boolean createContainer() throws StorageException {
		return createContainerImpl(null, ContainerAccessControl.Private);
	}

	private boolean createContainerImpl(final NameValueCollection metadata,
			final ContainerAccessControl accessControl) throws StorageException {
		boolean result = false;
		try {
			result = (Boolean) getRetryPolicy().execute(new Callable<Object>() {
				public Object call() throws Exception {
					ResourceUriComponents uriComponents = new ResourceUriComponents(
							getAccountName(), getContainerName(), null);
					URI uri = HttpUtilities.createRequestUri(getBaseUri(),
							isUsePathStyleUris(), getAccountName(),
							getContainerName(), null, getTimeout(),
							new NameValueCollection(), uriComponents);
					HttpRequest request = HttpUtilities
							.createHttpRequestWithCommonHeaders(uri,
									HttpMethod.Put, getTimeout());
					if (metadata != null) {
						HttpUtilities.addMetadataHeaders(request, metadata);
					}
					if (accessControl.isPublic()) {
						request.addHeader(HeaderNames.PublicAccess, "true");
					}
					credentials.signRequest(request, uriComponents);
					try {
						HttpWebResponse response = HttpUtilities
								.getResponse(request);
						if (response.getStatusCode() == HttpStatus.SC_CREATED)
							return true;
						else if (response.getStatusCode() == HttpStatus.SC_CONFLICT)
							return false;
						else {
							HttpUtilities.processUnexpectedStatusCode(response);
							return false; // Can't return
						}
					} catch (StorageException we) {
						throw HttpUtilities.translateWebException(we);
					}
				}
			});
		} catch (StorageException e) {
			throw e;
		}
		return result;
	}

	public void setContainerMetadata(final NameValueCollection metadata) {
		try {
			getRetryPolicy().execute(new Callable<Object>() {
				public Object call() throws Exception {
					ResourceUriComponents uriComponents = new ResourceUriComponents(
							getAccountName(), getContainerName(), null);

					NameValueCollection queryParams = new NameValueCollection();
					queryParams.put(QueryParams.QueryParamComp,
							CompConstants.Metadata);

					URI uri = HttpUtilities.createRequestUri(getBaseUri(),
							isUsePathStyleUris(), getAccountName(),
							getContainerName(), null, getTimeout(),
							queryParams, uriComponents);
					HttpRequest request = HttpUtilities
							.createHttpRequestWithCommonHeaders(uri,
									HttpMethod.Put, getTimeout());
					if (metadata != null) {
						HttpUtilities.addMetadataHeaders(request, metadata);
					}

					credentials.signRequest(request, uriComponents);
					try {
						HttpWebResponse response = HttpUtilities
								.getResponse(request);
						if (response.getStatusCode() == HttpStatus.SC_OK)
							return true;
						else {
							HttpUtilities.processUnexpectedStatusCode(response);
							return false; // Can't return
						}
					} catch (StorageException we) {
						throw HttpUtilities.translateWebException(we);
					}
				}
			});
		} catch (StorageException e) {
			throw e;
		}
	}

	/**
	 * Create the container with the specified access control if it does not
	 * exist
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
	@Override
	public boolean createContainer(NameValueCollection metadata,
			ContainerAccessControl accessControl) throws StorageException {
		return createContainerImpl(metadata, accessControl);
	}

	@Override
	public boolean doesContainerExist() throws StorageException {
		boolean result = false;
		result = (Boolean) getRetryPolicy().execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccountName(), getContainerName(), null);
				URI uri = HttpUtilities.createRequestUri(getBaseUri(),
						isUsePathStyleUris(), getAccountName(),
						getContainerName(), null, getTimeout(),
						new NameValueCollection(), uriComponents);
				HttpRequest request = HttpUtilities
						.createHttpRequestWithCommonHeaders(uri,
								HttpMethod.Get, getTimeout());
				credentials.signRequest(request, uriComponents);
				try {
					HttpWebResponse response = HttpUtilities
							.getResponse(request);
					if (response.getStatusCode() == HttpStatus.SC_OK)
						return true;
					else if (response.getStatusCode() == HttpStatus.SC_GONE
							|| response.getStatusCode() == HttpStatus.SC_NOT_FOUND)
						return false;
					else {
						HttpUtilities.processUnexpectedStatusCode(response);
						return false;
					}
				} catch (StorageException we) {
					throw HttpUtilities.translateWebException(we);
				}
			}
		});
		return result;
	}

	public boolean doesBlobExist(String blobName) throws StorageException {
		try {
			return getBlobProperties(blobName) != null;
		} catch (Exception e) {
			if (e instanceof StorageException) {
				StorageException se = (StorageException) e;
				if (se.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
					return false;
				} else {
					throw se;
				}
			}
		}
		return false;
	}

	// / <summary>
	// / Get the properties for the container if it exists.
	// / </summary>
	// / <returns>The metadata for the container if it exists, null
	// otherwise</returns>
	public ContainerProperties getContainerProperties() throws StorageException {
		ContainerProperties result = null;
		try {
			result = (ContainerProperties) getRetryPolicy().execute(
					new Callable<ContainerProperties>() {
						public ContainerProperties call() throws Exception {
							ResourceUriComponents uriComponents = new ResourceUriComponents(
									getAccountName(), getContainerName(), null);
							URI uri = HttpUtilities.createRequestUri(
									getBaseUri(), isUsePathStyleUris(),
									getAccountName(), getContainerName(), null,
									getTimeout(), new NameValueCollection(),
									uriComponents);
							HttpRequest request = HttpUtilities
									.createHttpRequestWithCommonHeaders(uri,
											HttpMethod.Get, getTimeout());
							credentials.signRequest(request, uriComponents);

							HttpWebResponse response = HttpUtilities
									.getResponse(request);
							if (response.getStatusCode() == HttpStatus.SC_OK)
								return containerPropertiesFromResponse(response);
							else if (response.getStatusCode() == HttpStatus.SC_GONE
									|| response.getStatusCode() == HttpStatus.SC_NOT_FOUND)
								return null;
							else {
								HttpUtilities
										.processUnexpectedStatusCode(response);
								return null;
							}

						}
					});
		} catch (StorageException e) {
			throw HttpUtilities.translateWebException(e);
		}
		return result;
	}

	private ContainerProperties containerPropertiesFromResponse(
			HttpWebResponse response) {
		ContainerProperties prop = new ContainerProperties(getContainerName());
		prop.setLastModifiedTime(response.getLastModified());
		prop.setETag(response.getHeader(HeaderNames.ETag));
		prop.setUri(getContainerUri());
		prop.setMetadata(metadataFromHeaders(response.getHeaders()));
		return prop;
	}

	private NameValueCollection metadataFromHeaders(NameValueCollection headers) {
		int prefixLength = HeaderNames.PrefixForMetadata.length();

		NameValueCollection metadataEntries = new NameValueCollection();
		for (Object key : headers.keySet()) {
			String headerName = (String) key;
			if (headerName.toLowerCase().startsWith(
					HeaderNames.PrefixForMetadata)) {
				// strip out the metadata prefix
				metadataEntries.putAll(headerName.substring(prefixLength),
						headers.getCollection(headerName));
			}
		}
		return metadataEntries;
	}

	// / Get the access control permissions associated with the container.
	@Override
	public void setContainerAccessControl(final ContainerAccessControl acl)
			throws StorageException {
		try {
			getRetryPolicy().execute(new Callable<Boolean>() {
				public Boolean call() throws Exception {
					NameValueCollection queryParams = new NameValueCollection();
					queryParams.put(QueryParams.QueryRestType,
							CompConstants.Container);
					queryParams.put(QueryParams.QueryParamComp,
							CompConstants.Acl);
					ResourceUriComponents uriComponents = new ResourceUriComponents(
							getAccountName(), getContainerName(), null);
					URI uri = HttpUtilities.createRequestUri(getBaseUri(),
							isUsePathStyleUris(), getAccountName(),
							getContainerName(), null, getTimeout(),
							queryParams, uriComponents);
					HttpRequest request = HttpUtilities
							.createHttpRequestWithCommonHeaders(uri,
									HttpMethod.Put, getTimeout());
					request.addHeader(HeaderNames.PublicAccess, String
							.valueOf(acl.isPublic()));
					addVerisonHeader(request);

					attachBody(acl, request);

					credentials.signRequest(request, uriComponents);
					HttpWebResponse response = HttpUtilities
							.getResponse(request);
					if (response.getStatusCode() != HttpStatus.SC_OK) {
						HttpUtilities.processUnexpectedStatusCode(response);
					}
					return true;
				}

				private void attachBody(final ContainerAccessControl acl,
						HttpRequest request) {
					String atom = AtomUtil.convertACLToXml(acl);
					((HttpEntityEnclosingRequest) request)
							.setEntity(new ByteArrayEntity(atom.getBytes()));
				}

			});
		} catch (StorageException e) {
			throw HttpUtilities.translateWebException(e);

		}
	}

	private void addVerisonHeader(HttpRequest request) {
		request
				.addHeader(HeaderNames.ApiVersion,
						XmsVersion.VERSION_2009_07_17);
	}

	public ContainerAccessControl getContainerAccessControl()
			throws StorageException {
		ContainerAccessControl accessControl = ContainerAccessControl.Private;
		try {
			accessControl = (ContainerAccessControl) getRetryPolicy().execute(
					new Callable<ContainerAccessControl>() {

						public ContainerAccessControl call() throws Exception {
							NameValueCollection queryParams = new NameValueCollection();
							queryParams.put(QueryParams.QueryParamComp,
									CompConstants.Acl);
							// New version container ACL
							queryParams.put(QueryParams.QueryRestType,
									CompConstants.Container);

							ResourceUriComponents uriComponents = new ResourceUriComponents(
									getAccountName(), getContainerName(), null);
							URI uri = HttpUtilities.createRequestUri(
									getBaseUri(), isUsePathStyleUris(),
									getAccountName(), getContainerName(), null,
									getTimeout(), queryParams, uriComponents);
							HttpRequest request = HttpUtilities
									.createHttpRequestWithCommonHeaders(uri,
											HttpMethod.Get, getTimeout());
							request.addHeader(HeaderNames.ApiVersion,
									XmsVersion.VERSION_2009_07_17);

							credentials.signRequest(request, uriComponents);
							HttpWebResponse response = HttpUtilities
									.getResponse(request);
							if (response.getStatusCode() == HttpStatus.SC_OK) {
								String acl = response
										.getHeader(HeaderNames.PublicAccess);
								boolean publicAcl = false;
								if (acl != null) {
									publicAcl = Boolean.parseBoolean(acl);
									List<SignedIdentifier> identifiers = getSignedIdentifiersFromResponse(response);
									ContainerAccessControl aclEntity = null;
									if (identifiers != null
											&& identifiers.size() > 0) {
										aclEntity = new ContainerAccessControl(
												publicAcl);
										aclEntity
												.setSigendIdentifiers(identifiers);
									} else {
										aclEntity = publicAcl ? ContainerAccessControl.Public
												: ContainerAccessControl.Private;
									}
									return aclEntity;
								} else {
									throw new StorageServerException(
											StorageErrorCode.ServiceBadResponse,
											"The server did not respond with expected container access control header",
											response.getStatusCode(), null);
								}
							} else {
								HttpUtilities
										.processUnexpectedStatusCode(response);
								return null;
							}
						}

					});
		} catch (Exception e) {
			throw HttpUtilities.translateWebException(e);

		}
		return accessControl;
	}

	@SuppressWarnings("unchecked")
	private List<SignedIdentifier> getSignedIdentifiersFromResponse(
			HttpWebResponse response) {
		InputStream stream = response.getStream();
		if (stream == null)
			return Collections.EMPTY_LIST;
		try {
			Document doc = XmlUtil.load(stream,
					"Container access control parsed error.");
			List selectNodes = doc
					.selectNodes(XPathQueryHelper.SignedIdentifierListQuery);
			List<SignedIdentifier> result = new ArrayList<SignedIdentifier>();
			if (selectNodes.size() > 0) {
				for (Iterator iter = selectNodes.iterator(); iter.hasNext();) {
					Element element = (Element) iter.next();
					SignedIdentifier identifier = new SignedIdentifier();
					identifier
							.setId(XPathQueryHelper
									.loadSingleChildStringValue(
											element,
											XmlElementNames.ContainerSignedIdentifierId,
											true));
					AccessPolicy policy = new AccessPolicy();
					Element accesPlocy = (Element) element
							.selectSingleNode(XmlElementNames.ContainerAccessPolicyName);
					if (accesPlocy != null && accesPlocy.hasContent()) {
						policy
								.setStart(new DateTime(
										XPathQueryHelper
												.loadSingleChildStringValue(
														accesPlocy,
														XmlElementNames.ContainerAccessPolicyStart,
														true)));
						policy
								.setExpiry(new DateTime(
										XPathQueryHelper
												.loadSingleChildStringValue(
														accesPlocy,
														XmlElementNames.ContainerAccessPolicyExpiry,
														true)));
						policy
								.setPermission(Permissions
										.valueOf(XPathQueryHelper
												.loadSingleChildStringValue(
														accesPlocy,
														XmlElementNames.ContainerAccessPolicyPermission,
														true)));
						identifier.setPolicy(policy);
					}

					result.add(identifier);
				}
			}
			return result;
		} catch (Exception e) {
			// For dev local storage, Container access control may have no
			// detail.
			Logger.error("Parse container accesss control error", e);
			return Collections.EMPTY_LIST;
		}
	}

	@Override
	public boolean deleteContainer() throws StorageException {
		final boolean result = (Boolean) getRetryPolicy().execute(
				new Callable<Boolean>() {
					public Boolean call() throws Exception {
						ResourceUriComponents uriComponents = new ResourceUriComponents(
								getAccountName(), getContainerName(), null);
						URI uri = HttpUtilities.createRequestUri(getBaseUri(),
								isUsePathStyleUris(), getAccountName(),
								getContainerName(), null, getTimeout(),
								new NameValueCollection(), uriComponents);
						HttpRequest request = HttpUtilities
								.createHttpRequestWithCommonHeaders(uri,
										HttpMethod.Delete, getTimeout());
						credentials.signRequest(request, uriComponents);
						try {
							HttpWebResponse response = HttpUtilities
									.getResponse(request);
							if (response.getStatusCode() == HttpStatus.SC_OK
									|| response.getStatusCode() == HttpStatus.SC_ACCEPTED)
								return true;
							else if (response.getStatusCode() == HttpStatus.SC_NOT_FOUND)
								return false;
							else
								HttpUtilities
										.processUnexpectedStatusCode(response);
						} catch (StorageException we) {
							throw HttpUtilities.translateWebException(we);
						}
						return false;
					}
				});
		return result;
	}

	@Override
	public boolean deleteBlob(final String name) throws StorageException {
		return deleteBlobImpl(name, null, new OutParameter<Boolean>(false));
	}

	@Override
	public boolean deleteBlobIfNotModified(BlobProperties blob)
			throws StorageException {
		OutParameter<Boolean> modified = new OutParameter<Boolean>(false);
		boolean result = deleteBlobImpl(blob.getName(), blob.getETag(),
				modified);
		if (modified.getValue()) {
			throw new StorageException(
					"The blob was not deleted because it was modified.");
		} else {
			return result;
		}
	}

	private boolean deleteBlobImpl(final String name, final String eTag,
			final OutParameter<Boolean> unused) throws StorageException {
		if (Utilities.isNullOrEmpty(name))
			throw new IllegalArgumentException(
					"Blob name cannot be null or empty!");

		final OutParameter<Boolean> retval = new OutParameter<Boolean>(false);
		final OutParameter<Boolean> localModified = new OutParameter<Boolean>(
				false);

		getRetryPolicy().execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccountName(), getContainerName(), name);
				URI blobUri = HttpUtilities.createRequestUri(getBaseUri(),
						isUsePathStyleUris(), getAccountName(),
						getContainerName(), name, getTimeout(),
						new NameValueCollection(), uriComponents);
				HttpRequest request = HttpUtilities
						.createHttpRequestWithCommonHeaders(blobUri,
								HttpMethod.Delete, getTimeout());

				if (!Utilities.isNullOrEmpty(eTag))
					request.addHeader(HeaderNames.IfMatch, eTag);
				credentials.signRequest(request, uriComponents);
				try {
					HttpWebResponse response = HttpUtilities
							.getResponse(request);
					int status = response.getStatusCode();
					if (status == HttpStatus.SC_OK
							|| status == HttpStatus.SC_ACCEPTED) {
						retval.setValue(true);
					} else if (status == HttpStatus.SC_NOT_FOUND
							|| status == HttpStatus.SC_GONE) {
						localModified.setValue(true);
						HttpUtilities.processUnexpectedStatusCode(response);
					} else if (status == HttpStatus.SC_PRECONDITION_FAILED
							|| status == HttpStatus.SC_NOT_MODIFIED) {
						localModified.setValue(true);
						HttpUtilities.processUnexpectedStatusCode(response);
					} else {
						HttpUtilities.processUnexpectedStatusCode(response);
					}
				} catch (StorageException ioe) {
					HttpUtilities.translateWebException(ioe);
				}
				return null;
			}
		});
		unused.setValue(localModified.getValue());
		return retval.getValue();
	}

	public BlobProperties getBlob(String name, BlobContents blobContents,
			boolean transferAsChunks) throws StorageException {
		Boolean notModified = false;
		try {
			return getBlobImpl(name, blobContents.getStream(), null,
					transferAsChunks, new OutParameter<Boolean>(notModified));
		} catch (Exception e) {
			throw HttpUtilities.translateWebException(e);
		}
	}

	private BlobProperties getBlobImpl(final String blobName,
			final Stream stream, final String oldETag,
			boolean transferAsChunks, OutParameter<Boolean> modified)
			throws Exception {
		if (Utilities.isNullOrEmpty(blobName))
			throw new IllegalArgumentException(
					"Blob name cannot be null or empty!");

		final BlobProperties[] blobProperties = new BlobProperties[1];
		final OutParameter<Boolean> localModified = new OutParameter<Boolean>(
				true);
		// Reset the stop flag
		stopFetchProgress(Boolean.FALSE);
		// If we are interested only in the blob properties (stream ==null) or
		// we are performing
		// a chunked download we first obtain just the blob properties
		if (stream == null || transferAsChunks) {
			getRetryPolicy().execute(new Callable<Object>() {

				public Object call() throws Exception {
					// Set the position to rewind in case of a retry.
					BlobProperties blob = downloadData(blobName, null, oldETag,
							null, 0, 0, new NameValueCollection(),
							localModified);
					blobProperties[0] = blob;
					return blob;
				}
			});

			modified.setValue(localModified.getValue());
			if (stream == null) {
				return blobProperties[0];
			}

		}

		RetryPolicy rp = stream.canSeek() ? getRetryPolicy() : RetryPolicies
				.noRetry();
		final long originalPosition = stream.canSeek() ? stream.getPosition()
				: 0;
		if (transferAsChunks && blobProperties != null
				&& blobProperties[0].getContentLength() > 0) {
			// Chunked download. Obtain ranges of the blobs in 'BlockSize'
			// chunks
			// Ensure that the If-Match <Etag>header is used on each request so
			// that we are assured that all data belongs to the single blob we
			// started downloading.
			final long[] location = new long[] { 0 };

			while (location[0] < blobProperties[0].getContentLength()) {
				if (isStopped())
					throw new IOException(
							"Download blob progress is terminated.");

				final long nBytes = Math.min(blobProperties[0]
						.getContentLength()
						- location[0], getBlockSize());

				rp.execute(new Callable<Object>() {

					public Object call() throws Exception {
						// Set the position to rewind in case of a retry.
						if (stream.canSeek()) {
							stream.setPosition(originalPosition + location[0]);
						}
						downloadData(blobName, stream, oldETag,
								blobProperties[0].getETag(), location[0],
								nBytes, new NameValueCollection(),
								localModified);
						return null;
					}
				});
				location[0] += nBytes;
			}
		}

		else {
			rp.execute(new Callable<Object>() {
				public Object call() throws Exception {
					// Set the position to rewind in case of a retry.
					if (stream.canSeek()) {
						stream.setPosition(originalPosition);
					}
					BlobProperties blob = downloadData(blobName, stream,
							oldETag, null, 0, 0, new NameValueCollection(),
							localModified);
					blobProperties[0] = blob;
					return blob;
				}
			});
		}
		modified.setValue(localModified.getValue());
		return blobProperties[0];
	}

	private BlobProperties downloadData(String blobName, Stream stream,
			String eTagIfNoneMatch, String eTagIfMatch, long offset,
			long length, NameValueCollection nvc,
			OutParameter<Boolean> localModified) throws StorageException {
		ResourceUriComponents uriComponents = new ResourceUriComponents(
				getAccountName(), getContainerName(), blobName);
		URI blobUri = HttpUtilities.createRequestUri(getBaseUri(),
				isUsePathStyleUris(), getAccountName(), getContainerName(),
				blobName, getTimeout(), nvc, uriComponents);
		String httpMethod = (stream == null ? HttpMethod.Head : HttpMethod.Get);
		HttpRequest request = createHttpRequestForGetBlob(blobUri, httpMethod,
				eTagIfNoneMatch, eTagIfMatch);

		if (offset != 0 || length != 0) {
			// Use the blob storage custom header for range since the standard
			// HttpWebRequest.
			// AddRange accepts only 32 bit integers and so does not work for
			// large blobs.
			String rangeHeaderValue = MessageFormat
					.format(HeaderValues.RangeHeaderFormat, offset, offset
							+ length - 1);
			request.addHeader(HeaderNames.StorageRange, rangeHeaderValue);
		}

		credentials.signRequest(request, uriComponents);
		BlobProperties blobProperties;

		try {
			HttpWebResponse response = HttpUtilities.getResponse(request);
			if (response.getStatusCode() == HttpStatus.SC_OK
					|| response.getStatusCode() == HttpStatus.SC_PARTIAL_CONTENT) {

				blobProperties = blobPropertiesFromResponse(blobName, blobUri,
						response);
				if (stream != null) {
					InputStream responseStream = response.getStream();
					long byteCopied = Utilities.copyStream(responseStream,
							stream);
					if (blobProperties.getContentLength() > 0
							&& byteCopied < blobProperties.getContentLength()) {
						throw new StorageServerException(
								StorageErrorCode.ServiceTimeout,
								"Unable to read complete data from server",
								HttpStatus.SC_REQUEST_TIMEOUT, null);
					}
				}
			} else {
	
				HttpUtilities.processUnexpectedStatusCode(response);
				return null;
			}
			return blobProperties;
		} catch (Exception we) {
			throw HttpUtilities.translateWebException(we);
		}
	}

	private BlobProperties blobPropertiesFromResponse(String blobName,
			URI blobUri, HttpWebResponse response) throws URISyntaxException{

		BlobProperties blobProperties = new BlobProperties(blobName);
		blobProperties.setUri(constructBlobUri(blobName));//removeQueryParams(blobUri));
		blobProperties.setContentEncoding(response
				.getHeader(HeaderNames.ContentEncoding));
		blobProperties.setLastModifiedTime(response.getLastModified());
		blobProperties.setETag(response.getHeader(HeaderNames.ETag));
		blobProperties.setContentLanguage(response
				.getHeader(HeaderNames.ContentLanguage));
		blobProperties.setContentLength(response.getContentLength());
		blobProperties.setContentType(response.getContentType());

		NameValueCollection metadataEntries = metadataFromHeaders(response
				.getHeaders());

		if (metadataEntries.size() > 0) {
			blobProperties.setMetadata(metadataEntries);
		}
		return blobProperties;
	}

	private URI constructBlobUri(String blobName) {
		ResourceUriComponents uriComponents = new ResourceUriComponents(
				getAccountName(), getContainerName(), blobName);
		return HttpUtilities.createRequestUri(getBaseUri(),
				isUsePathStyleUris(), getAccountName(), getContainerName(),
				blobName, null, new NameValueCollection(), uriComponents);		
	}

	private URI removeQueryParams(URI blobUri) throws URISyntaxException {
		String uri = blobUri.toString();
		int pos = uri.indexOf('?');
		if(pos < 0)
			return blobUri;
		else
			return new URI( uri.substring(0, pos));	
	}

	private HttpRequest createHttpRequestForGetBlob(URI blobUri,
			String httpMethod, String tagIfNoneMatch, String tagIfMatch) {
		HttpRequest request = HttpUtilities.createHttpRequestWithCommonHeaders(
				blobUri, httpMethod, getTimeout());
		if (tagIfNoneMatch != null) {
			request.addHeader(HeaderNames.IfNoneMatch, tagIfNoneMatch);
		}
		if (tagIfMatch != null) {
			request.addHeader(HeaderNames.IfMatch, tagIfMatch);
		}
		return request;
	}

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
	public Collection<BlobProperties> listBlobs(String prefix,
			boolean combineCommonPrefixes, int maxResults)
			throws StorageException {
		if (maxResults <= 0)
			throw new IllegalArgumentException(
					"maxResults should be positive value.");

		ListBlobsResult all = new ListBlobsResult(
				new ArrayList<BlobProperties>(), new ArrayList<String>(), "");
		String marker = "";

		String delimiter = combineCommonPrefixes ? ConstChars.Slash : Utilities
				.emptyString();
		do {
			ListBlobsResult partResult = listBlobsImpl(prefix, marker,
					delimiter, maxResults);
			marker = partResult.getNextMarker();
			all.getBlobs().addAll(partResult.getBlobs());
			all.getCommonPrefixs().addAll(partResult.getCommonPrefixs());
		} while (marker != null);

		return all.getBlobs();
	}

	/**
	 * Enumerates all blobs with a given prefix.
	 * 
	 * @param prefix
	 * @param combineCommonPrefixes
	 *            If true common prefixes with "/" as separator
	 * @return The list of blob properties and common prefixes
	 * @throws StorageException
	 */
	@Override
	public Collection<BlobProperties> listBlobs(String prefix,
			boolean combineCommonPrefixes) throws StorageException {
		final int maxResults = ListingConstants.MaxBlobListResults;
		try {
			return listBlobs(prefix, combineCommonPrefixes, maxResults);
		} catch (StorageException se) {
			throw HttpUtilities.translateWebException(se);
		}
	}

	private ListBlobsResult listBlobsImpl(final String prefix,
			final String fromMarker, final String delimiter,
			final int maxResults) throws StorageException {
		final OutParameter<ListBlobsResult> result = new OutParameter<ListBlobsResult>();
		getRetryPolicy().execute(new Callable<Object>() {
			public Object call() throws Exception {
				NameValueCollection queryParameters = createRequestUriForListing(
						prefix, fromMarker, delimiter, maxResults);
				ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccountName(), getContainerName(), null);
				queryParameters.put(QueryParams.QueryRestType,
						CompConstants.Container);
				URI uri = HttpUtilities.createRequestUri(getBaseUri(),
						isUsePathStyleUris(), getAccountName(),
						getContainerName(), null, getTimeout(),
						queryParameters, uriComponents);
				HttpRequest request = HttpUtilities
						.createHttpRequestWithCommonHeaders(uri,
								HttpMethod.Get, getTimeout());
				credentials.signRequest(request, uriComponents);

				HttpWebResponse response = HttpUtilities.getResponse(request);
				if (response.getStatusCode() == HttpStatus.SC_OK) {
					result.setValue(listBlobsResultFromResponse(response
							.getStream()));
				} else {
					XmlUtil.load(response.getStream());
					HttpUtilities.processUnexpectedStatusCode(response);
				}
				return null;
			}
		});
		return result.getValue();
	}

	/**
	 * Retrieve the blob meta-data from the response body in XML format.
	 * 
	 * @param stream
	 *            HTTP response body
	 * @return an ListBlobsResult
	 * @throws StorageServerException
	 */
	@SuppressWarnings("unchecked")
	private ListBlobsResult listBlobsResultFromResponse(InputStream stream)
			throws StorageServerException {
		List<BlobProperties> blobs = new ArrayList<BlobProperties>();
		List<String> commonPrefixes = new ArrayList<String>();
		String nextMarker = null;
		Document document = XmlUtil.load(stream);
		// Get the commonPrefixes
		List xmlNodes = document
				.selectNodes(XPathQueryHelper.CommonPrefixQuery);
		for (Iterator iterator = xmlNodes.iterator(); iterator.hasNext();) {
			Element element = (Element) iterator.next();
			String blobPrefix = XPathQueryHelper.loadSingleChildStringValue(
					element, XmlElementNames.BlobPrefixName, false);
			commonPrefixes.add(blobPrefix);
		}

		// Get all the blobs returned as the listing results
		xmlNodes = document.selectNodes(XPathQueryHelper.BlobQuery);
		for (Iterator iterator = xmlNodes.iterator(); iterator.hasNext();) {
			/*
			 * Parse the Blob meta-data from response XML content.
			 */
			Element blobNode = (Element) iterator.next();
			Timestamp lastModified = XPathQueryHelper
					.loadSingleChildDateTimeValue(blobNode,
							XmlElementNames.LastModified, false);
			String eTag = XPathQueryHelper.loadSingleChildStringValue(blobNode,
					XmlElementNames.Etag, false);

			String contentType = XPathQueryHelper.loadSingleChildStringValue(
					blobNode, XmlElementNames.ContentType, false);
			String contentEncoding = XPathQueryHelper
					.loadSingleChildStringValue(blobNode,
							XmlElementNames.ContentEncoding, false);
			String contentLanguage = XPathQueryHelper
					.loadSingleChildStringValue(blobNode,
							XmlElementNames.ContentLanguage, false);
			Long blobSize = XPathQueryHelper.loadSingleChildLongValue(blobNode,
					XmlElementNames.Size, false);
			String blobUrl = XPathQueryHelper.loadSingleChildStringValue(
					blobNode, XmlElementNames.Url, true);
			String blobName = XPathQueryHelper.loadSingleChildStringValue(
					blobNode, XmlElementNames.BlobName, true);

			BlobProperties properties = new BlobProperties(blobName);
			if (lastModified != null)
				properties.setLastModifiedTime(lastModified);
			properties.setContentType(contentType);
			properties.setContentEncoding(contentEncoding);
			properties.setContentLanguage(contentLanguage);
			properties.setETag(eTag);
			properties.setContentLength(blobSize);
			//FIXME: blank character in url
			blobUrl = blobUrl.replaceAll(" ", "%20");			
			properties.setUri(URI.create(blobUrl));
			blobs.add(properties);
		}

		// Get the nextMarker
		Element nextMarkerNode = (Element) document
				.selectSingleNode(XPathQueryHelper.NextMarkerQuery);
		if (nextMarkerNode != null && nextMarkerNode.hasContent()) {
			nextMarker = nextMarkerNode.getStringValue();
		}

		return new ListBlobsResult(blobs, commonPrefixes, nextMarker);
	}

	private NameValueCollection createRequestUriForListing(String prefix,
			String fromMarker, String delimiter, int maxResults) {
		NameValueCollection queryParams = new NameValueCollection();
		queryParams.put(QueryParams.QueryParamComp, CompConstants.List);

		if (!Utilities.isNullOrEmpty(prefix))
			queryParams.put(QueryParams.QueryParamPrefix, prefix);

		if (!Utilities.isNullOrEmpty(fromMarker))
			queryParams.put(QueryParams.QueryParamMarker, fromMarker);

		if (!Utilities.isNullOrEmpty(delimiter))
			queryParams.put(QueryParams.QueryParamDelimiter, delimiter);

		queryParams.put(QueryParams.QueryParamMaxResults, Integer
				.toString(maxResults));
		return queryParams;
	}

	@Override
	public boolean getBlobIfModified(BlobProperties blobProperties,
			BlobContents blobContents, boolean transferAsChunks)
			throws StorageException {
		try {
			OutParameter<Boolean> modified = new OutParameter<Boolean>(true);
			BlobProperties newBlob = getBlobImpl(blobProperties.getName(),
					blobContents.getStream(), blobProperties.getETag(),
					transferAsChunks, modified);

			if (modified.getValue()) {
				blobProperties.assign(newBlob);
			}
			return modified.getValue();
		} catch (Exception e) {
			throw HttpUtilities.translateWebException(e);
		}
	}

	@Override
	public BlobProperties getBlobProperties(String name)
			throws StorageException {
		try {
			boolean modified = false;
			return getBlobImpl(name, null, null, false,
					new OutParameter<Boolean>(modified));
		} catch (Exception e) {
			throw HttpUtilities.translateWebException(e);
		}
	}

	@Override
	public void updateBlobMetadata(BlobProperties blobProperties)
			throws StorageException {
		setBlobMetadataImpl(blobProperties, null);
	}

	private boolean setBlobMetadataImpl(final BlobProperties blobProperties,
			final String eTag) {
		if (blobProperties == null)
			throw new IllegalArgumentException(
					"Blob properties cannot be null or empty!");

		final OutParameter<Boolean> retval = new OutParameter<Boolean>(false);
		getRetryPolicy().execute(new Callable<Boolean>() {

			public Boolean call() throws Exception {
				NameValueCollection queryParams = new NameValueCollection();
				queryParams.put(QueryParams.QueryParamComp,
						CompConstants.Metadata);
				final ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccountName(), getContainerName(), blobProperties
								.getName());
				URI uri = HttpUtilities.createRequestUri(getBaseUri(),
						isUsePathStyleUris(), getAccountName(),
						getContainerName(), blobProperties.getName(),
						getTimeout(), queryParams, uriComponents);
				HttpRequest request = HttpUtilities
						.createHttpRequestWithCommonHeaders(uri,
								HttpMethod.Put, getTimeout());
				if (blobProperties.getMetadata() != null) {
					HttpUtilities.addMetadataHeaders(request, blobProperties
							.getMetadata());
				}
				if (!Utilities.isNullOrEmpty(eTag)) {
					request.addHeader(HeaderNames.IfMatch, eTag);
				}
				credentials.signRequest(request, uriComponents);
				HttpWebResponse response = HttpUtilities.getResponse(request);
				int statusCode = response.getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					retval.setValue(true);
					blobProperties.setLastModifiedTime(response
							.getLastModified());
					blobProperties
							.setETag(response.getHeader(HeaderNames.ETag));
				} else if (statusCode == HttpStatus.SC_PRECONDITION_FAILED
						|| statusCode == HttpStatus.SC_NOT_MODIFIED) {
					retval.setValue(false);
				} else {
					retval.setValue(false);
					HttpUtilities.processUnexpectedStatusCode(response);
				}
				return null;
			}
		});
		return retval.getValue();
	}

	@Override
	public boolean copyBlob(String destContainer, String destBlobName,
			String sourceBlobName) throws StorageException {
		return copyBlobImpl(destContainer, destBlobName, sourceBlobName, null,
				null);
	}

	@Override
	public boolean copyBlob(String destContainer, String destBlobName,
			String sourceBlobName, NameValueCollection metadata,
			BlobConstraints constraints) throws StorageException {
		return copyBlobImpl(destContainer, destBlobName, sourceBlobName,
				metadata, constraints);
	}

	private boolean copyBlobImpl(String destContainer, String destBlobName,
			final String sourceBlobName, final NameValueCollection metadata,
			final BlobConstraints constraints) throws StorageException {

		if (Utilities.isNullOrEmpty(sourceBlobName)) {
			throw new IllegalArgumentException(
					"Source blob name cannot be null or empty!");
		}

		final String container = Utilities.isNullOrEmpty(destContainer) ? getContainerName()
				: destContainer;
		final String blob = Utilities.isNullOrEmpty(destBlobName) ? sourceBlobName
				: destBlobName;

		if (container.equals(getContainerName()) && blob.equals(sourceBlobName)) {
			throw new IllegalArgumentException(
					"Destnation blob and source blob could not be the same.");
		}

		final OutParameter<Boolean> retval = new OutParameter<Boolean>(false);
		getRetryPolicy().execute(new Callable<Object>() {

			public Object call() throws Exception {
				final ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccountName(), container, blob);
				URI uri = HttpUtilities.createRequestUri(getBaseUri(),
						isUsePathStyleUris(), getAccountName(), container,
						blob, getTimeout(), null, uriComponents);
				HttpRequest request = HttpUtilities
						.createHttpRequestWithCommonHeaders(uri,
								HttpMethod.Put, getTimeout());

				request.addHeader(HeaderNames.ApiVersion,
						XmsVersion.VERSION_2009_07_17);
				request.addHeader(HeaderNames.CopySource,
						craeteCopySourceHeaderValue(sourceBlobName));
				// add constraints
				addMoreConstraints(constraints, request);

				if (metadata != null) {
					HttpUtilities.addMetadataHeaders(request, metadata);
				}

				credentials.signRequest(request, uriComponents);
				HttpWebResponse response = HttpUtilities.getResponse(request);
				int statusCode = response.getStatusCode();
				if (statusCode == HttpStatus.SC_CREATED) {
					retval.setValue(true);
				} else {
					retval.setValue(false);
					HttpUtilities.processUnexpectedStatusCode(response);
				}
				return retval;
			}

		});
		return retval.getValue();
	}

	private void addMoreConstraints(final BlobConstraints constraints,
			HttpRequest request) {
		if (constraints != null) {
			List<BasicHeader> headers = constraints.getConstraints();
			if (headers != null && !headers.isEmpty()) {
				for (BasicHeader header : headers) {
					request.addHeader(header);
				}
			}
		}
	}

	private String craeteCopySourceHeaderValue(final String sourceBlobName) {
		return String.format("/%s/%s/%s", getAccountName(), getContainerName(),
				sourceBlobName);
	}

	@Override
	public boolean updateBlobMetadataIfNotModified(BlobProperties blobProperties)
			throws StorageException {
		return setBlobMetadataImpl(blobProperties, blobProperties.getETag());
	}

	@Override
	public void clearSharedAccessUrl() {
		this.shareAccessUrl = null;
		if (credentials instanceof SharedKeyCredentialsWrapper) {
			SharedKeyCredentialsWrapper warpper = (SharedKeyCredentialsWrapper) credentials;
			credentials = warpper.getCredentials();
		}
	}

	@Override
	public void useSharedAccessUrl(ShareAccessUrl url) {
		if (url == null)
			throw new IllegalArgumentException("Share access url invalid");
		this.shareAccessUrl = url;
		credentials = new SharedKeyCredentialsWrapper(credentials,
				shareAccessUrl, this);

	}

	/**
	 * @return the shareAccessUrl
	 */
	public ShareAccessUrl getShareAccessUrl() {
		return shareAccessUrl;
	}
}
