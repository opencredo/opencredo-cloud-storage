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
package org.soyatec.windows.azure.internal;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.soyatec.windows.azure.constants.HeaderNames;
import org.soyatec.windows.azure.util.Logger;
import org.soyatec.windows.azure.util.NameValueCollection;
import org.soyatec.windows.azure.util.Utilities;

public class HttpWebResponse {

	private HttpResponse response;

	private NameValueCollection headers;

	private int statusCode;

	private Timestamp lastModified;

	private String contentType;

	private long contentLength;

	private InputStream inputStream;

	public HttpWebResponse(HttpResponse response) {
		this.response = response;
		parseResponse();
	}

	private void parseResponse() {
		this.statusCode = response.getStatusLine().getStatusCode();
		headers = new NameValueCollection();
		for (Header header : response.getAllHeaders()) {
			headers.put(header.getName(), header.getValue());
		}

		String time = headers.getSingleValue(HeaderNames.LastModifiedTime);
		if (time != null) {
			lastModified = Utilities.convertTime(time);
		}

		contentType = headers.getSingleValue(HeaderNames.ContentType);

		contentLength = 0;
		String length = headers.getSingleValue(HeaderNames.ContentLength);
		if (length != null) {
			contentLength = Long.parseLong(length);
		}

	}

	public NameValueCollection getHeaders() {
		return headers;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusDescription() {
		return response.getStatusLine().getReasonPhrase();
	}

	public String getHeader(String name) {
		return headers.getSingleValue(name);
	}

	public Timestamp getLastModified() {
		return lastModified;
	}

	public String getContentType() {
		return contentType;
	}

	public long getContentLength() {
		return contentLength;
	}

	public InputStream getStream() {
		if (inputStream != null) {
			return inputStream;
		} else {
			try {
				HttpEntity entity = response.getEntity();
				if(entity != null){
					inputStream = entity.getContent();
					return inputStream;
				}
			} catch (IllegalStateException e) {
				Logger.error("IllegalStateException", e);
			} catch (IOException e) {
				Logger.error("IOException", e);
			}
			return null;
		}

	}

	public void close() {
		// TODO How to response.close?
	}

}
