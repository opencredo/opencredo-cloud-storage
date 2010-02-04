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

import org.soyatec.windows.azure.util.NameValueCollection;

public class StorageExtendedErrorInformation {
	private String errorCode;
	private String errorMessage;
	private NameValueCollection additionalDetails;

	/**
	 * Error body content return by http response
	 */
	private String errorBody;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public NameValueCollection getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(NameValueCollection additionalDetails) {
		this.additionalDetails = additionalDetails;
	}

	/**
	 * @return the errorBody
	 */
	public String getErrorBody() {
		return errorBody;
	}

	/**
	 * @param errorBody
	 *            the errorBody to set
	 */
	public void setErrorBody(String errorBody) {
		this.errorBody = errorBody;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StorageExtendedErrorInformation [additionalDetails="
				+ additionalDetails + ", errorBody=" + errorBody
				+ ", errorCode=" + errorCode + ", errorMessage=" + errorMessage
				+ "]";
	}

}
