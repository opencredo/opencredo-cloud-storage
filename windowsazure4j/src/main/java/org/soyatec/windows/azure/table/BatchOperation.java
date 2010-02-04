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

import org.apache.http.Header;

/**
 * Represents all informations of an single table service operation temporary.
 * Include
 * <ul>
 * <li>Request url information</li>
 * <li>Request headers</li>
 * <li>Request bodya</li>
 * </ul>
 * 
 * These informations are used to assamble a whole batch operation.
 * 
 */
final class BatchOperation {

	/**
	 * operation path
	 */
	private String path;

	/**
	 * operation query string
	 */
	private String queryString;

	/**
	 * Http verb
	 */
	private String method;

	/**
	 * Request headers
	 */
	private Header[] headers;

	/**
	 * Operation body
	 */
	private String requestBody;

	/**
	 * Create a new batch opeation
	 */
	public BatchOperation() {
	}

	/**
	 * Create a new batch opeation
	 * 
	 * @param path
	 * @param queryString
	 * @param method
	 * @param headers
	 * @param requestBody
	 */
	public BatchOperation(String path, String queryString, String method,
			Header[] headers, String requestBody) {
		super();
		this.path = path;
		this.queryString = queryString;
		this.method = method;
		this.headers = headers;
		this.requestBody = requestBody;
	}

	/**
	 * Get the batch path
	 * 
	 * @return
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Set the batch path
	 * 
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Get the query string
	 * 
	 * @return
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * Set the query string
	 * 
	 * @param queryString
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * Get http method verb.
	 * 
	 * @return
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Set http method verb.
	 * 
	 * @param method
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * Get http headers
	 * 
	 * @return
	 */
	public Header[] getHeaders() {
		return headers;
	}

	/**
	 * Set http headers
	 * 
	 * @param headers
	 */
	public void setHeaders(Header[] headers) {
		this.headers = headers;
	}

	/**
	 * Get the request body
	 * 
	 * @return
	 */
	public String getRequestBody() {
		return requestBody;
	}

	/**
	 * Set the request body
	 * 
	 * @param requestBody
	 */
	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

}
