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
package org.soyatec.windows.azure.authenticate;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIUtils;
import org.soyatec.windows.azure.blob.BlobContainer;
import org.soyatec.windows.azure.blob.ShareAccessUrl;
import org.soyatec.windows.azure.constants.CompConstants;
import org.soyatec.windows.azure.constants.HeaderNames;
import org.soyatec.windows.azure.constants.HeaderValues;
import org.soyatec.windows.azure.constants.HttpMethod;
import org.soyatec.windows.azure.constants.QueryParams;
import org.soyatec.windows.azure.constants.XmsVersion;
import org.soyatec.windows.azure.util.HttpUtilities;
import org.soyatec.windows.azure.util.Logger;
import org.soyatec.windows.azure.util.Utilities;

public class SharedKeyCredentialsWrapper extends SharedKeyCredentials {

	private final SharedKeyCredentials credentials;
	private final ShareAccessUrl shareAccessUrl;
	private final BlobContainer container;

	public SharedKeyCredentialsWrapper(SharedKeyCredentials credentials,
			ShareAccessUrl url, BlobContainer container) {
		this.credentials = credentials;
		this.shareAccessUrl = url;
		this.container = container;
	}

	/**
	 * @param request
	 * @param uriComponents
	 * @see org.soyatec.windows.azure.authenticate.SharedKeyCredentials#signRequest(org.apache.http.HttpRequest,
	 *      org.soyatec.windows.azure.authenticate.ResourceUriComponents)
	 */
	public void signRequest(HttpRequest request,
			ResourceUriComponents uriComponents) {
		if (request instanceof HttpRequestBase) {
			HttpRequestBase hrb = ((HttpRequestBase) request);
			URI uri = hrb.getURI();
			// replace the container name
			// replace the blob name
			// replace the account name
			uri = replaceAccountName(uri, shareAccessUrl.getAccountName());
			uri = replaceContainerName(uri, shareAccessUrl.getContainerName());
			uri = appendSignString(uri, shareAccessUrl.getSignedString());
			((HttpRequestBase) request).setURI(uri);
		}

		addVerisonHeader(request);
	}

	private URI appendSignString(URI uri, String signedString) {
		try {
			return URIUtils.createURI(uri.getScheme(), uri.getHost(), uri
					.getPort(), HttpUtilities.normalizePath(uri),
					(uri.getQuery() == null ? Utilities.emptyString() : uri
							.getQuery())
							+ "&" + signedString, uri.getFragment());
		} catch (URISyntaxException e) {
			Logger.error("", e);
		}
		return uri;
	}

	private void addVerisonHeader(HttpRequest request) {
		request.addHeader(HeaderNames.ApiVersion,
				XmsVersion.VERSION_2009_07_17);
	}

	private URI replaceAccountName(URI uri, String accountName) {
		try {
			String host = uri.getHost();
			String[] temp = host.split("\\.");
			temp[0] = accountName;
			return URIUtils.createURI(uri.getScheme(), join(".", temp), uri
					.getPort(), HttpUtilities.normalizePath(uri),
					(uri.getQuery() == null ? Utilities.emptyString() : uri
							.getQuery()), uri.getFragment());
		} catch (URISyntaxException e) {
			Logger.error("", e);
		}
		return uri;
	}

	private URI replaceContainerName(URI uri, String containerName) {
		if (containerName == null) {
			return uri;
		}
		try {
			String host = uri.getPath();
			String[] temp = host.split("/");
			temp[0] = containerName;
			return URIUtils.createURI(uri.getScheme(), uri.getHost(), uri
					.getPort(), join("/", temp),
					(uri.getQuery() == null ? Utilities.emptyString() : uri
							.getQuery()), uri.getFragment());
		} catch (URISyntaxException e) {
			Logger.error("", e);
		}
		return uri;
	}

	public static final String join(String se, String[] sources) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0, n = sources.length; i < n; i++) {
			sb.append(sources[i]);
			if (i < n - 1) {
				sb.append(se);
			}
		}
		return sb.toString();
	}

	/**
	 * @param obj
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return credentials.equals(obj);
	}

	/**
	 * @return
	 * @see org.soyatec.windows.azure.authenticate.SharedKeyCredentials#getAccountName()
	 */
	public String getAccountName() {
		return credentials.getAccountName();
	}

	/**
	 * @return
	 * @see org.soyatec.windows.azure.authenticate.SharedKeyCredentials#getKey()
	 */
	public byte[] getKey() {
		return credentials.getKey();
	}

	/**
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return credentials.hashCode();
	}

	/**
	 * @param request
	 * @param uriComponents
	 * @see org.soyatec.windows.azure.authenticate.SharedKeyCredentials#signRequestForSharedKeyLite(org.apache.http.HttpRequest,
	 *      org.soyatec.windows.azure.authenticate.ResourceUriComponents)
	 */
	public void signRequestForSharedKeyLite(HttpRequest request,
			ResourceUriComponents uriComponents) {
		credentials.signRequestForSharedKeyLite(request, uriComponents);
	}

	/**
	 * @param permissions
	 * @param start
	 * @param expiry
	 * @param canonicalizedResource
	 * @param identifier
	 * @return
	 * @see org.soyatec.windows.azure.authenticate.SharedKeyCredentials#signSharedAccessUrl(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	public String signSharedAccessUrl(String permissions, String start,
			String expiry, String canonicalizedResource, String identifier) {
		return credentials.signSharedAccessUrl(permissions, start, expiry,
				canonicalizedResource, identifier);
	}

	/**
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return credentials.toString();
	}

	/**
	 * @return the shareAccessUrl
	 */
	public ShareAccessUrl getShareAccessUrl() {
		return shareAccessUrl;
	}

	/**
	 * @return the credentials
	 */
	public SharedKeyCredentials getCredentials() {
		return credentials;
	}

}
