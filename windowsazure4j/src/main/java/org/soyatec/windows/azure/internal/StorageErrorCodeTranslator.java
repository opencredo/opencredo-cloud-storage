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

import org.soyatec.windows.azure.error.BlobErrorCodeStrings;
import org.soyatec.windows.azure.error.StorageErrorCode;
import org.soyatec.windows.azure.error.StorageErrorCodeStrings;

public class StorageErrorCodeTranslator {

	public static StorageErrorCode translateStorageErrorCodeString(
			String erorCodeString) {
		if (erorCodeString.equals(StorageErrorCodeStrings.UnsupportedHttpVerb)
				|| erorCodeString
						.equals(StorageErrorCodeStrings.MissingContentLengthHeader)
				|| erorCodeString
						.equals(StorageErrorCodeStrings.MissingRequiredHeader)
				|| erorCodeString
						.equals(StorageErrorCodeStrings.UnsupportedHeader)
				|| erorCodeString
						.equals(StorageErrorCodeStrings.InvalidHeaderValue)
				|| erorCodeString
						.equals(StorageErrorCodeStrings.MissingRequiredQueryParameter)
				|| erorCodeString
						.equals(StorageErrorCodeStrings.UnsupportedQueryParameter)
				|| erorCodeString
						.equals(StorageErrorCodeStrings.InvalidQueryParameterValue)
				|| erorCodeString
						.equals(StorageErrorCodeStrings.OutOfRangeQueryParameterValue)
				|| erorCodeString.equals(StorageErrorCodeStrings.InvalidUri)
				|| erorCodeString
						.equals(StorageErrorCodeStrings.InvalidHttpVerb)
				|| erorCodeString
						.equals(StorageErrorCodeStrings.EmptyMetadataKey)
				|| erorCodeString
						.equals(StorageErrorCodeStrings.RequestBodyTooLarge)
				|| erorCodeString
						.equals(StorageErrorCodeStrings.InvalidXmlDocument)
				|| erorCodeString
						.equals(StorageErrorCodeStrings.InvalidXmlNodeValue)
				|| erorCodeString
						.equals(StorageErrorCodeStrings.MissingRequiredXmlNode)
				|| erorCodeString.equals(StorageErrorCodeStrings.InvalidMd5)
				|| erorCodeString
						.equals(StorageErrorCodeStrings.OutOfRangeInput)
				|| erorCodeString.equals(StorageErrorCodeStrings.InvalidInput)
				|| erorCodeString
						.equals(StorageErrorCodeStrings.InvalidMetadata)
				|| erorCodeString
						.equals(StorageErrorCodeStrings.MetadataTooLarge)
				|| erorCodeString.equals(StorageErrorCodeStrings.InvalidRange)) {
			return StorageErrorCode.BadRequest;
		}
		if (erorCodeString.equals(StorageErrorCodeStrings.AuthenticationFailed)) {
			return StorageErrorCode.AuthenticationFailure;
		}

		if (erorCodeString.equals(StorageErrorCodeStrings.ResourceNotFound)) {
			return StorageErrorCode.ResourceNotFound;
		}
		if (erorCodeString.equals(StorageErrorCodeStrings.ConditionNotMet)) {
			return StorageErrorCode.ConditionFailed;
		}
		if (erorCodeString
				.equals(StorageErrorCodeStrings.ContainerAlreadyExists)) {
			return StorageErrorCode.ContainerAlreadyExists;
		}
		if (erorCodeString.equals(StorageErrorCodeStrings.ContainerNotFound)) {
			return StorageErrorCode.ContainerNotFound;
		}
		if (erorCodeString.equals(BlobErrorCodeStrings.BlobNotFound)) {
			return StorageErrorCode.BlobNotFound;
		}
		if (erorCodeString.equals(BlobErrorCodeStrings.BlobAlreadyExists)) {
			return StorageErrorCode.BlobAlreadyExists;
		}

		if (erorCodeString.equals(StorageErrorCodeStrings.InternalError)
				|| erorCodeString.equals(StorageErrorCodeStrings.ServerBusy)) {
			return StorageErrorCode.ServiceInternalError;
		}

		if (erorCodeString.equals(StorageErrorCodeStrings.Md5Mismatch)) {
			return StorageErrorCode.ServiceIntegrityCheckFailed;
		}
		if (erorCodeString.equals(StorageErrorCodeStrings.OperationTimedOut)) {
			return StorageErrorCode.ServiceTimeout;
		}

		return StorageErrorCode.None;
	}
}
