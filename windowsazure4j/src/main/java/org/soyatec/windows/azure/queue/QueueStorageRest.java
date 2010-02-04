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
package org.soyatec.windows.azure.queue;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.dom4j.Document;
import org.dom4j.Element;
import org.soyatec.windows.azure.authenticate.Base64;
import org.soyatec.windows.azure.authenticate.ResourceUriComponents;
import org.soyatec.windows.azure.authenticate.SharedKeyCredentials;
import org.soyatec.windows.azure.constants.CompConstants;
import org.soyatec.windows.azure.constants.HttpMethod;
import org.soyatec.windows.azure.constants.ListingConstants;
import org.soyatec.windows.azure.constants.QueryParams;
import org.soyatec.windows.azure.constants.XmlElementNames;
import org.soyatec.windows.azure.error.StorageException;
import org.soyatec.windows.azure.internal.HttpWebResponse;
import org.soyatec.windows.azure.internal.OutParameter;
import org.soyatec.windows.azure.util.HttpUtilities;
import org.soyatec.windows.azure.util.NameValueCollection;
import org.soyatec.windows.azure.util.Utilities;
import org.soyatec.windows.azure.util.xml.XPathQueryHelper;
import org.soyatec.windows.azure.util.xml.XmlUtil;

/**
 * Rest implementation of QueueStorage.
 * 
 */
public class QueueStorageRest extends QueueStorage {

	/**
	 * Constructor for rest implementation of QueueStorage.
	 * 
	 * @param baseUri
	 *            The base URI of the blob storage service
	 * @param usePathStyleUris
	 *            Indicates whether to use/generate path-style or host-style URIs
	 * @param accountName
	 *            The name of the storage account
	 * @param base64Key
	 *            Authentication key used for signing requests
	 */
	public QueueStorageRest(URI baseUri, boolean usePathStyleUris, String accountName, String base64Key) {
		super(baseUri, usePathStyleUris, accountName, base64Key);
		byte[] key = null;
		this.base64Key = base64Key;
		if (base64Key != null)
			key = Base64.decode(getBase64Key());
		setCredentials(new SharedKeyCredentials(accountName, key));
	}

	/**
	 * Lists the queues within the account that start with the given prefix.
	 * 
	 * @param prefix
	 *            If prefix is null returns all queues.
	 * @return A list of queues.
	 */
	public List<String> listQueues(String prefix) {
		ListQueueResult all = new ListQueueResult(new ArrayList<String>(), new ArrayList<String>(), Utilities.emptyString());
		String marker = Utilities.emptyString();
		final int maxResults = ListingConstants.MaxQueueListResults;
		do {
			ListQueueResult partResult = listQueueImpl(prefix, marker, maxResults);
			marker = partResult.getNextMarker();
			all.getQueueNames().addAll(partResult.getQueueNames());
			all.getUrls().addAll(partResult.getUrls());
			all.setNextMarker(marker);
		} while (marker != null);
		return all.getQueueNames();
	}

	/**
	 * Internal rest implementation for listing the queues within the account.
	 * 
	 */
	private ListQueueResult listQueueImpl(final String prefix, final String marker, final int maxResults) throws StorageException {
		final OutParameter<ListQueueResult> result = new OutParameter<ListQueueResult>();
		getRetryPolicy().execute(new Callable<Object>() {
			public Object call() throws Exception {
				NameValueCollection col = new NameValueCollection();
				col.put(QueryParams.QueryParamComp, CompConstants.List);
				if (!Utilities.isNullOrEmpty(prefix)) {
					col.put(QueryParams.QueryParamPrefix, prefix);
				}
				if (!Utilities.isNullOrEmpty(marker)) {
					col.put(QueryParams.QueryParamMarker, marker);
				}
				ResourceUriComponents uriComponents = new ResourceUriComponents(getAccountName(), null, null);
				URI uri = HttpUtilities.createRequestUri(getBaseUri(), isUsePathStyleUris(), getAccountName(), null, null, getTimeout(), col, uriComponents);
				HttpRequest request = HttpUtilities.createHttpRequestWithCommonHeaders(uri, HttpMethod.Get, getTimeout());
				getCredentials().signRequest(request, uriComponents);
				HttpWebResponse response = HttpUtilities.getResponse(request);
				if (response.getStatusCode() == HttpStatus.SC_OK) {
					result.setValue(getQueuesResultFromResponse(response.getStream()));
				} else {
					HttpUtilities.processUnexpectedStatusCode(response);
				}
				return null;
			}
		});
		return result.getValue();
	}

	/**
	 * Internal method for creating ListQueueResult from input stream.
	 */
	@SuppressWarnings("unchecked")
	private ListQueueResult getQueuesResultFromResponse(InputStream stream) throws StorageException {
		List<String> names = new ArrayList<String>();
		List<String> urls = new ArrayList<String>();
		String nextMarker = null;

		Document document = XmlUtil.load(stream, "The result of a ListQueue operation could not be parsed");
		// get queue names and urls
		List xmlNodes = document.selectNodes(XPathQueryHelper.QueueListQuery);
		for (Iterator iterator = xmlNodes.iterator(); iterator.hasNext();) {
			Element queueNameNode = (Element) iterator.next();
			String queueName = XPathQueryHelper.loadSingleChildStringValue(queueNameNode, XmlElementNames.QueueName, true);
			names.add(queueName);
			String url = XPathQueryHelper.loadSingleChildStringValue(queueNameNode, XmlElementNames.Url, true);
			urls.add(url);
		}
		// Get the nextMarker
		Element nextMarkerNode = (Element) document.selectSingleNode(XPathQueryHelper.NextMarkerQuery);
		if (nextMarkerNode != null && nextMarkerNode.hasContent()) {
			nextMarker = nextMarkerNode.getStringValue();
		}
		assert names.size() == urls.size(); // Make this useful, you shoule open
		// the -enableassertions when run.
		return new ListQueueResult(names, urls, nextMarker);
	}

	/**
	 * Get a reference to a Queue object with a specified name. This method does not make a call to the queue service.
	 * 
	 * @param queueName
	 *            The name of the queue
	 * @return A newly created queue object
	 */
	public MessageQueue getQueue(String queueName) throws StorageException {
		if (Utilities.isNullOrEmpty(queueName)) {
			throw new IllegalArgumentException("Queue name cannot be null or empty!");
		}

		return new QueueRest(queueName, getAccountInfo(), getTimeout(), getRetryPolicy());

	}
}
