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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.soyatec.windows.azure.constants.ConstChars;
import org.soyatec.windows.azure.constants.HeaderNames;
import org.soyatec.windows.azure.constants.QueryParams;
import org.soyatec.windows.azure.util.HttpUtilities;
import org.soyatec.windows.azure.util.NameValueCollection;
import org.soyatec.windows.azure.util.Utilities;

/**
 * Canonicale message
 * 
 */
public class MessageCanonicalizer {

	public static String canonicalizeHttpRequest(HttpRequest request,
			ResourceUriComponents uriComponents) {
		if (!(request instanceof HttpUriRequest)) {
			throw new IllegalArgumentException(
					"Request should be a URI http request");
		}
		HttpUriRequest rq = (HttpUriRequest) request;
		return canonicalizeHttpRequest(rq.getURI(), uriComponents, rq
				.getMethod(), HttpUtilities.parseRequestContentType(rq),
				Utilities.emptyString(), HttpUtilities.parseHttpHeaders(rq));
	}

	public static String canonicalizeHttpRequestForSharedKeyLite(
			HttpRequest request, ResourceUriComponents uriComponents,
			String date) {
		if (!(request instanceof HttpUriRequest)) {
			throw new IllegalArgumentException(
					"Request should be a URI http request");
		}
		StringBuilder canonicalizedString = new StringBuilder(date);
		MessageCanonicalizer.appendStringToCanonicalizedString(
				canonicalizedString, MessageCanonicalizer
						.getCanonicalizedResource(((HttpUriRequest) request)
								.getURI(), uriComponents));
		return canonicalizedString.toString();
	}

	private static String appendStringToCanonicalizedString(
			StringBuilder canonicalizedString, String stringToAppend) {
		canonicalizedString.append(ConstChars.Linefeed);
		canonicalizedString.append(stringToAppend);
		return canonicalizedString.toString();
	}

	/**
	 * Create a canonicalized string out of HTTP request header contents for
	 * signing blob/queue requests with the Shared Authentication scheme.
	 * 
	 * @param address
	 *            The uri address of the HTTP request.
	 * @param uriComponents
	 *            Components of the Uri extracted out of the request.
	 * @param method
	 *            The method of the HTTP request (GET/PUT, etc.).
	 * @param contentType
	 *            The content type of the HTTP request.
	 * @param date
	 *            The date of the HTTP request.
	 * @param headers
	 *            Should contain other headers of the HTTP request.
	 * @return A canonicalized string of the HTTP request.
	 */
	public static String canonicalizeHttpRequest(URI address,
			ResourceUriComponents uriComponents, String method,
			String contentType, String date, NameValueCollection headers) {
		// The first element should be the Method of the request.
		// I.e. GET, POST, PUT, or HEAD.
		CanonicalizedString canonicalizedString = new CanonicalizedString(
				method);

		// The second element should be the MD5 value.
		// This is optional and may be empty.
		String httpContentMD5Value = Utilities.emptyString();

		// First extract all the content MD5 values from the header.
		List<String> httpContentMD5Values = HttpRequestAccessor
				.getHeaderValues(headers, HeaderNames.ContentMD5);

		// If we only have one, then set it to the value we want to append to
		// the canonicalized string.
		if (httpContentMD5Values != null && httpContentMD5Values.size() == 1) {
			httpContentMD5Value = (String) httpContentMD5Values.get(0);
		}
		canonicalizedString.appendCanonicalizedElement(httpContentMD5Value);

		// The third element should be the content type.
		canonicalizedString.appendCanonicalizedElement(contentType);

		// The fourth element should be the request date.
		// See if there's an storage date header.
		// If there's one, then don't use the date header.
		List<String> httpStorageDateValues = HttpRequestAccessor
				.getHeaderValues(headers, HeaderNames.StorageDateTime);
		if (httpStorageDateValues != null && httpStorageDateValues.size() > 0) {
			date = "";
		}
		if (date != null) {
			canonicalizedString.appendCanonicalizedElement(date);
		}

		// Look for header names that start with
		// StorageHttpConstants.HeaderNames.PrefixForStorageHeader
		// Then sort them in case-insensitive manner.
		ArrayList<String> httpStorageHeaderNameArray = new ArrayList<String>();
		for (Object keyObj : headers.keySet()) {
			String key = (String) keyObj;
			if (key.toLowerCase()
					.startsWith(HeaderNames.PrefixForStorageHeader)) {
				httpStorageHeaderNameArray.add(key.toLowerCase());
			}
		}

		Collections.sort(httpStorageHeaderNameArray, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}
		});

		for (String key : httpStorageHeaderNameArray) {
			StringBuilder canonicalizedElement = new StringBuilder(key);
			String delimiter = ConstChars.Colon;
			List<String> values = HttpRequestAccessor.getHeaderValues(headers,
					key);
			for (String headerValue : values) {
				// Unfolding is simply removal of CRLF.
				String unfoldedValue = headerValue.replaceAll(
						ConstChars.CarriageReturnLinefeed, Utilities
								.emptyString());
				// Append it to the canonicalized element string.
				canonicalizedElement.append(delimiter);
				canonicalizedElement.append(unfoldedValue);
				delimiter = ConstChars.Comma;
			}
			// Now, add this canonicalized element to the canonicalized header
			// string.
			canonicalizedString.appendCanonicalizedElement(canonicalizedElement
					.toString());
		}

		// Now we append the canonicalized resource element.
		String canonicalizedResource = getCanonicalizedResource(address,
				uriComponents);
		canonicalizedString.appendCanonicalizedElement(canonicalizedResource);

		return canonicalizedString.getValue();
	}

	private static String getCanonicalizedResource(URI address,
			ResourceUriComponents uriComponents) {
		// Algorithem is as follows
		// 1. Start with the empty string ("")
		// 2. Append the account name owning the resource preceded by a /. This
		// is not
		// the name of the account making the request but the account that owns
		// the
		// resource being accessed.
		// 3. Append the path part of the un-decoded HTTP Request-URI, up-to but
		// not
		// including the query string.
		// 4. If the request addresses a particular component of a resource,
		// like?comp=
		// metadata then append the sub-resource including question mark (like
		// ?comp=
		// metadata)
		StringBuilder canonicalizedResource = new StringBuilder(
				ConstChars.Slash);
		canonicalizedResource.append(uriComponents.getAccountName());

		// Note that AbsolutePath starts with a '/'.
		//FIXME: blank character in url
		String path = address.getRawPath();
//		path = path.replaceAll(" ", "%20");
//		path = java.net.URLEncoder.encode(path);
		canonicalizedResource.append(path);

		NameValueCollection queryVariables = HttpUtilities
				.parseQueryString(address.getQuery());

		// TODO noticed
		String compQueryParameterValue = queryVariables
				.getSingleValue(QueryParams.QueryParamComp);
		if (compQueryParameterValue != null) {
			canonicalizedResource.append(ConstChars.QuestionMark);
			canonicalizedResource.append(QueryParams.QueryParamComp);
			canonicalizedResource
					.append(QueryParams.SeparatorForParameterAndValue);
			canonicalizedResource.append(compQueryParameterValue);
		}

		return canonicalizedResource.toString();
	}

	/**
	 * An internal class that stores the canonicalized string version of an HTTP
	 * request.
	 * 
	 */
	private static class CanonicalizedString {

		private StringBuilder canonicalizedString = new StringBuilder();

		/**
		 * Append additional canonicalized element to the string.
		 * 
		 * @return
		 */
		public String getValue() {
			return canonicalizedString.toString();
		}

		/**
		 * Constructor for the class.
		 * 
		 * @param initialElement
		 *            The first canonicalized element to start the string with.
		 */
		public CanonicalizedString(String initialElement) {
			canonicalizedString.append(initialElement);
		}

		/**
		 * Append additional canonicalized element to the string.
		 * 
		 * @param element
		 *            An additional canonicalized element to append to the
		 *            string.
		 */
		public void appendCanonicalizedElement(String element) {
			canonicalizedString.append(ConstChars.Linefeed);
			canonicalizedString.append(element);
		}
	}

}
