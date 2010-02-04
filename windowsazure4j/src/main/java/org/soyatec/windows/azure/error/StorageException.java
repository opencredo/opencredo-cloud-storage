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

/**
 * The base class for storage service exceptions
 * 
 */
public class StorageException extends RuntimeException {

	private static final long serialVersionUID = 5889964601658022935L;

	/**
	 * The Http status code returned by the storage service
	 */
	private int statusCode;

	/**
	 * The specific error code returned by the storage service
	 */
	private StorageErrorCode errorCode;

	/**
	 * 
	 */
	private StorageExtendedErrorInformation extendedErrorInformation;

	protected StorageException() {
	}

	public StorageException(Exception innerException) {
		super(innerException.getMessage(), innerException);
	}

	public StorageException(String msg) {
		super(msg);
	}

	protected StorageException(StorageErrorCode errorcode1, String message,
			int statusCode, Exception innerException) {
		super(message, innerException);
		this.errorCode = errorcode1;
		this.statusCode = statusCode;
	}

	protected StorageException(StorageErrorCode errorCode, String message,
			int statusCode, StorageExtendedErrorInformation extendedErrorInfo,
			Exception innerException) {
		super(message, innerException);
		this.errorCode = errorCode;
		this.statusCode = statusCode;
		this.extendedErrorInformation = extendedErrorInfo;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public StorageErrorCode getErrorCode() {
		return errorCode;
	}

	public StorageExtendedErrorInformation getExtendedErrorInformation() {
		return extendedErrorInformation;
	}

	// TODO toString() need to override.

}
