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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.dom4j.Document;
import org.dom4j.Element;
import org.soyatec.windows.azure.authenticate.Base64;
import org.soyatec.windows.azure.authenticate.HttpRequestAccessor;
import org.soyatec.windows.azure.authenticate.ResourceUriComponents;
import org.soyatec.windows.azure.authenticate.SharedKeyCredentials;
import org.soyatec.windows.azure.constants.CompConstants;
import org.soyatec.windows.azure.constants.HttpMethod;
import org.soyatec.windows.azure.constants.HttpStatusConstant;
import org.soyatec.windows.azure.constants.ListingConstants;
import org.soyatec.windows.azure.constants.QueryParams;
import org.soyatec.windows.azure.constants.XmlElementNames;
import org.soyatec.windows.azure.error.StorageErrorCode;
import org.soyatec.windows.azure.error.StorageException;
import org.soyatec.windows.azure.error.StorageServerException;
import org.soyatec.windows.azure.internal.HttpWebResponse;
import org.soyatec.windows.azure.internal.OutParameter;
import org.soyatec.windows.azure.util.HttpUtilities;
import org.soyatec.windows.azure.util.NameValueCollection;
import org.soyatec.windows.azure.util.Utilities;
import org.soyatec.windows.azure.util.xml.XPathQueryHelper;
import org.soyatec.windows.azure.util.xml.XmlUtil;

public class BlobStorageRest extends BlobStorage {

	private final SharedKeyCredentials credentials;

	public BlobStorageRest(URI baseUri, boolean usePathStyleUris, String accountName, String base64Key) {
		super(baseUri, usePathStyleUris, accountName, base64Key);
		byte[] key = null;
		setBase64Key(base64Key);
		if (base64Key != null)
			key = Base64.decode(getBase64Key());
		this.credentials = new SharedKeyCredentials(accountName, key);
	}

	/**
	 * Lists the containers within the account.
	 * 
	 * @return A list of containers
	 * @throws Exception
	 */
	@Override
	public List<BlobContainer> listBlobContainers() throws StorageServerException {
		String marker = "", prefix = null;
		final int maxResults = ListingConstants.MaxContainerListResults;
		List<BlobContainer> all = new ArrayList<BlobContainer>();
		do {
			ListContainersResult partResult = listContainersImpl(prefix, marker, maxResults);
			marker = partResult.getNextMarker();
			for (ContainerProperties container : partResult.getContains()) {
				all.add(new BlobContainerRest(getBaseUri(), isUsePathStyleUris(), getAccountName(), container.getName(), getBase64Key(), container.getLastModifiedTime(), getTimeout(), getRetryPolicy()));
			}
		} while (marker != null);
		return all;
	}

	private ListContainersResult listContainersImpl(String prefix, String marker, int maxResults) throws StorageServerException {
		final OutParameter<ListContainersResult> result = new OutParameter<ListContainersResult>();
		ResourceUriComponents uriComponents = new ResourceUriComponents(getAccountName(), null, null);
		URI listContainerssUri = createRequestUriForListContainers(prefix, marker, null, maxResults, uriComponents);
		final HttpRequest request = HttpUtilities.createHttpRequestWithCommonHeaders(listContainerssUri, HttpMethod.Get, getTimeout());
		credentials.signRequest(request, uriComponents);

		getRetryPolicy().execute(new Callable<Object>() {

			public Object call() throws Exception {
				try {
					HttpWebResponse response = HttpUtilities.getResponse(request);
					if (response.getStatusCode() == HttpStatus.SC_OK) {
						result.setValue(listContainersResultFromResponse(response.getStream()));
					} else {
						HttpUtilities.processUnexpectedStatusCode(response);
					}
				} catch (Exception ioe) {
					throw new StorageServerException(StorageErrorCode.TransportError, "The connection may be lost", HttpStatusConstant.DEFAULT_STATUS, null, ioe);
				}
				return result;
			}

		});
		return result.getValue();
	}

	@SuppressWarnings("unchecked")
	private ListContainersResult listContainersResultFromResponse(InputStream stream) {
		List<ContainerProperties> props = new ArrayList<ContainerProperties>();
		String nextMarker = null;
		Document document = XmlUtil.load(stream);
		// Get all the containers returned as the listing results
		List xmlNodes = document.selectNodes(XPathQueryHelper.ContainerQuery);
		for (Iterator iterator = xmlNodes.iterator(); iterator.hasNext();) {
			/*
			 * Parse the container meta-data from response XML content.
			 */
			Element containerNode = (Element) iterator.next();
			Timestamp lastModified = XPathQueryHelper.loadSingleChildDateTimeValue(containerNode, XmlElementNames.LastModified, false);
			String eTag = XPathQueryHelper.loadSingleChildStringValue(containerNode, XmlElementNames.Etag, false);
			String containerUri = XPathQueryHelper.loadSingleChildStringValue(containerNode, XmlElementNames.Url, true);

			String containerName = XPathQueryHelper.loadSingleChildStringValue(containerNode, XmlElementNames.Name, true);

			ContainerProperties properties = new ContainerProperties(containerName);
			if (lastModified != null)
				properties.setLastModifiedTime(lastModified);
			properties.setETag(eTag);
			if (!Utilities.isNullOrEmpty(containerUri))
				properties.setUri(URI.create(containerUri));
			props.add(properties);
		}

		// Get the nextMarker
		Element nextMarkerNode = (Element) document.selectSingleNode(XPathQueryHelper.NextMarkerQuery);
		if (nextMarkerNode != null && nextMarkerNode.hasContent()) {
			nextMarker = nextMarkerNode.getStringValue();
		}

		ListContainersResult result = new ListContainersResult();
		result.setContains(props);
		result.setNextMarker(nextMarker);
		return result;
	}

	private URI createRequestUriForListContainers(String prefix, String marker, String delimiter, int maxResults, ResourceUriComponents uriComponents) {
		NameValueCollection queryParams = BlobStorageRest.createRequestUriForListing(prefix, marker, delimiter, maxResults);
		return HttpUtilities.createRequestUri(getBaseUri(), isUsePathStyleUris(), getAccountName(), null, null, getTimeout(), queryParams, uriComponents);
	}

	private static NameValueCollection createRequestUriForListing(String prefix, String marker, String delimiter, int maxResults) {
		NameValueCollection queryParams = new NameValueCollection();
		queryParams.put(QueryParams.QueryParamComp, CompConstants.List);

		if (!Utilities.isNullOrEmpty(prefix))
			queryParams.put(QueryParams.QueryParamPrefix, prefix);

		if (!Utilities.isNullOrEmpty(marker))
			queryParams.put(QueryParams.QueryParamMarker, marker);

		if (!Utilities.isNullOrEmpty(delimiter))
			queryParams.put(QueryParams.QueryParamDelimiter, delimiter);

		queryParams.put(QueryParams.QueryParamMaxResults, Integer.toString(maxResults));

		return queryParams;
	}

	/**
	 * Get a reference to a BlobContainer object with the given name. This method does not make any calls to the storage service.
	 * 
	 * @param containerName
	 *            The name of the container
	 * @return A reference to a newly created BlobContainer object
	 */
	@Override
	public BlobContainer getBlobContainer(String containerName) {
		if (Utilities.isNullOrEmpty(containerName)) {
			throw new IllegalArgumentException("Container name cannot be null or empty!");
		}
		return new BlobContainerRest(getBaseUri(), isUsePathStyleUris(), getAccountName(), containerName, getBase64Key(), Utilities.minTime(), getTimeout(), getRetryPolicy());
	}

	public SharedKeyCredentials getCredentials() {
		return credentials;
	}

	
	public ShareAccessUrl createSharedAccessUrl(String containerName, String blobName, EResourceType resource, int permissions, DateTime start, DateTime expiry, String identifier) throws StorageServerException {
		if (Utilities.isNullOrEmpty(containerName)) {
			throw new IllegalArgumentException("Container name cannot be null or empty!");
		}
		if (!Utilities.isValidContainerOrQueueName(containerName)) {
			throw new IllegalArgumentException(
					MessageFormat
							.format(
									"The specified container name \"{0}\" is not valid!"
											+ "Please choose a name that conforms to the naming conventions for containers!",
									containerName));
		}
		if(identifier == null && start != null && expiry != null){
			Calendar calendar = start.toCalendar();
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			if(calendar.before(expiry.toCalendar()) )
				throw new IllegalArgumentException("Access without signed identifier cannot have time window more than 1 hour");				
		}
		
		ShareAccessUrl result = new ShareAccessUrl();
		result.setAccountName(getAccountName());
		String resourceName = containerName;		
		if(blobName != null)
			resourceName += "/" + blobName;
		
		StringBuilder buf = new StringBuilder();
		try {
			if (start != null)
				buf.append("st=").append(urlencode(start.toString())).append("&");
			buf.append("se=").append( urlencode(expiry.toString())).append("&");
			buf.append("sr=").append(  resource.toString()).append("&");
			if(permissions != Permissions.NONE)
				buf.append("sp=").append( Permissions.toString(permissions)).append("&");
			if(identifier != null)
				buf.append("si=").append( identifier).append("&");	
			
			String canonicalizedResource = "/" + getAccountName();
			if(isUsePathStyleUris())
				canonicalizedResource += "/" + getAccountName();			
			canonicalizedResource += "/" + resourceName;
			
			String signature = credentials.signSharedAccessUrl(Permissions.toString(permissions ), start == null ? null: start.toString(), expiry == null ? null: expiry.toString(), canonicalizedResource, identifier);
			buf.append("sig=").append(urlencode(signature));
		} catch (UnsupportedEncodingException e) {		
			e.printStackTrace();
		}
		ResourceUriComponents uriComponents = new ResourceUriComponents(getAccountName(), "", null);
		URI containerUri = HttpRequestAccessor.constructResourceUri(getBaseUri(), uriComponents, isUsePathStyleUris());
		
		result.setRestUrl(containerUri.toString() + "/" + resourceName  );
		result.setSignedString( buf.toString());
		//return containerUri.toString() + "/" + resourceName +  "?" + buf.toString();
		return result;
		
	}

	private String urlencode(String str) throws UnsupportedEncodingException {		
		return java.net.URLEncoder.encode(str, "UTF-8");
	}

	@Override
	public boolean doesContainerExist(String containerName)
			throws StorageException {
		BlobContainer container = getBlobContainer(containerName);
		return container.doesContainerExist();
	}
}
