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
package org.soyatec.windows.azure.error;

import org.soyatec.windows.azure.constants.WebExceptionStatus;
import org.soyatec.windows.azure.internal.HttpWebResponse;


public class WebException extends Exception {
	private HttpWebResponse response;
	private WebExceptionStatus status;

	public WebException(HttpWebResponse response) {
		this.response = response;
	}

	public HttpWebResponse getResponse() {
		return response;
	}

	public void setResponse(HttpWebResponse response) {
		this.response = response;
	}

	public WebExceptionStatus getStatus() {
		return status;
	}

	public void setStatus(WebExceptionStatus status) {
		this.status = status;
	}

}
