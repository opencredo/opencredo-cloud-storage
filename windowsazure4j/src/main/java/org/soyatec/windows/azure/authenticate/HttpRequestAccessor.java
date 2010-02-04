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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpHost;
import org.soyatec.windows.azure.constants.ConstChars;
import org.soyatec.windows.azure.util.Logger;
import org.soyatec.windows.azure.util.NameValueCollection;
import org.soyatec.windows.azure.util.Utilities;

/**
 * 
 * Use this class to extract various header values from HTTP requests.
 * 
 */
public class HttpRequestAccessor {

	/**
	 * URI scheme delimiter
	 */
	private static final String SCHEME_DELIMITER = "://";

	/**
	 * A helper function for extracting HTTP header values from a
	 * NameValueCollection object.
	 * 
	 * @param header
	 *            A MultiValueMap object that should contain HTTP header
	 *            name-values pairs.
	 * @param headerName
	 *            Name of the header that we want to get values of.
	 * @return A array list of values for the header. The values are in the same
	 *         order as they are exist in the MultiValueMap object.
	 */
	public static List<String> getHeaderValues(NameValueCollection header,
			String headerName) {
		List<String> arrayOfValues = new ArrayList<String>();
		Collection collection = header.getCollection(headerName);
		if (collection != null) {
			for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
				String object = (String) iterator.next();
				arrayOfValues.add(object);
			}
		}
		return arrayOfValues;
	}

	/**
	 * Constructs an URI given all its constituents
	 * 
	 * @param endPoint
	 *            This is the service endpoint in case of path-style URIs and a
	 *            host suffix in case of host-style URIs IMPORTANT: This does
	 *            NOT include the service name or account name
	 * @param uriComponents
	 *            Uri constituents
	 * @param pathStyleUri
	 *            Indicates whether to construct a path-style Uri (true) or
	 *            host-style URI (false)
	 * @return Full URI
	 */
	public static URI constructResourceUri(URI endPoint,
			ResourceUriComponents uriComponents, boolean pathStyleUri) {
		return pathStyleUri ? constructPathStyleResourceUri(endPoint,
				uriComponents) : constructHostStyleResourceUri(endPoint,
				uriComponents);
	}

	/**
	 * Constructs a host-style resource URI given all its constituents
	 * 
	 * @param hostSuffix
	 * @param uriComponents
	 * @return Full URI
	 */
	private static URI constructHostStyleResourceUri(URI hostSuffix,
			ResourceUriComponents uriComponents) {
		if (uriComponents.getAccountName() == null) {
			// When there is no account name, full URI is same as hostSuffix
			return hostSuffix;
		}
		// accountUri will be something like "http://accountname.hostSuffix/"
		// and then we append
		// container name and remaining part if they are present.
		URI accountUri = constructHostStyleAccountUri(hostSuffix, uriComponents
				.getAccountName());
		StringBuilder path = new StringBuilder();
		if (uriComponents.getContainerName() != null) {
			path.append(uriComponents.getContainerName());

			if (uriComponents.getRemainingPart() != null) {
				path.append(ConstChars.Slash);
				path.append(uriComponents.getRemainingPart());
			}
		}

		return constructUriFromUriAndString(accountUri, path.toString());
	}

	/**
	 * Constructs a path-style resource URI given all its constituents
	 * 
	 * @param endPoint
	 *            This is the service endpoint in case of path-style URIs
	 * @param uriComponents
	 *            Uri constituents
	 * @return Full URI
	 */
	private static URI constructPathStyleResourceUri(URI endPoint,
			ResourceUriComponents uriComponents) {
		StringBuilder path = new StringBuilder();

		if (uriComponents.getAccountName() != null) {
			path.append(uriComponents.getAccountName());

			if (uriComponents.getContainerName() != null) {
				path.append(ConstChars.Slash);
				path.append(uriComponents.getContainerName());

				if (uriComponents.getRemainingPart() != null) {
					path.append(ConstChars.Slash);
					path.append(uriComponents.getRemainingPart());
				}
			}
		}

		return constructUriFromUriAndString(endPoint, path.toString());
	}

	/**
	 * Given the host suffix part, service name and account name, this method
	 * constructs the account Uri
	 * 
	 * @param hostSuffix
	 * @param accountName
	 * @return
	 */
	private static URI constructHostStyleAccountUri(URI hostSuffix,
			String accountName) {
		URI serviceUri = hostSuffix;
		String hostString = hostSuffix.toString();
		if (!hostString.startsWith(HttpHost.DEFAULT_SCHEME_NAME)) {
			hostString = HttpHost.DEFAULT_SCHEME_NAME + "://" + hostString;
			serviceUri = URI.create(hostString);
		}
		// Example:
		// Input: serviceEndpoint="http://blob.windows.net/",
		// accountName="youraccount"
		// Output: accountUri="http://youraccount.blob.windows.net/"
		// serviceUri in our example would be "http://blob.windows.net/"
		String accountUriString = null;
		if (serviceUri.getPort() == -1) {
			accountUriString = MessageFormat
					.format(
							"{0}{1}{2}.{3}/",
							Utilities.isNullOrEmpty(serviceUri.getScheme()) ? HttpHost.DEFAULT_SCHEME_NAME
									: serviceUri.getScheme(), SCHEME_DELIMITER,
							accountName, serviceUri.getHost());
		} else {
			accountUriString = MessageFormat
					.format(
							"{0}{1}{2}.{3}:{4}/",
							Utilities.isNullOrEmpty(serviceUri.getScheme()) ? HttpHost.DEFAULT_SCHEME_NAME
									: serviceUri.getScheme(), SCHEME_DELIMITER,
							accountName, serviceUri.getHost(), serviceUri
									.getPort());
		}

		return URI.create(accountUriString);
	}

	private static URI constructUriFromUriAndString(URI endPoint, String path) {
		// This is where we encode the url path to be valid
		String encodedPath = Utilities.encode(path);
		if (!Utilities.isNullOrEmpty(encodedPath)
				&& encodedPath.charAt(0) != ConstChars.Slash.charAt(0)) {
			encodedPath = ConstChars.Slash + encodedPath;
		}
		try {
			return new URI(endPoint.getScheme(), null, endPoint.getHost(),
					endPoint.getPort(), encodedPath, endPoint.getQuery(),
					endPoint.getFragment());
			// return new URI(endPoint.getScheme(), endPoint.getHost(),
			// encodedPath, endPoint.getFragment());
		} catch (URISyntaxException e) {
			Logger.error("Can not new URI", e);
			return null;
		}
	}
}
