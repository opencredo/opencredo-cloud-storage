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
package org.soyatec.windows.azure.constants;

public final class HeaderNames {
	public static final String PrefixForStorageProperties = "x-ms-prop-";
	public static final String PrefixForMetadata = "x-ms-meta-";
	public static final String PrefixForStorageHeader = "x-ms-";
	public static final String PrefixForTableContinuation = "x-ms-continuation-";
	public static final String ApiVersion = "x-ms-version";
	public static final String CopySource = "x-ms-copy-source";

	public static final String IfSourceModifiedSince = "x-ms-source-if-modified-since";
	public static final String IfSourceUnmodifiedSince = "x-ms-source-if-unmodified-since";

	public static final String IfSourceMatch = "x-ms-source-if-match";
	public static final String IfSourceNoneMatch = "x-ms-source-if-none-match";
	public static final String ContentID = "Content-ID";
	//
	// Standard headers...
	//
	public static final String ContentLanguage = "Content-Language";
	public static final String ContentLength = "Content-Length";
	public static final String ContentType = "Content-Type";
	public static final String ContentEncoding = "Content-Encoding";
	public static final String ContentMD5 = "Content-MD5";
	public static final String ContentRange = "Content-Range";
	public static final String Sotimeout = "So-Timeout";
	public static final String LastModifiedTime = "Last-Modified";
	public static final String Server = "Server";
	public static final String Allow = "Allow";
	public static final String ETag = "ETag";
	public static final String Range = "Range";
	public static final String Date = "Date";
	public static final String Authorization = "Authorization";
	public static final String IfModifiedSince = "If-Modified-Since";
	public static final String IfUnmodifiedSince = "If-Unmodified-Since";
	public static final String IfMatch = "If-Match";
	public static final String IfNoneMatch = "If-None-Match";
	public static final String IfRange = "If-Range";
	public static final String NextPartitionKey = "NextPartitionKey";
	public static final String NextRowKey = "NextRowKey";
	public static final String NextTableName = "NextTableName";

	//
	// Storage specific custom headers...
	//
	public static final String StorageDateTime = PrefixForStorageHeader
			+ "date";
	public static final String PublicAccess = PrefixForStorageProperties
			+ "publicaccess";
	public static final String StorageRange = PrefixForStorageHeader + "range";

	public static final String CreationTime = PrefixForStorageProperties
			+ "creation-time";
	public static final String ForceUpdate = PrefixForStorageHeader
			+ "force-update";
	public static final String ApproximateMessagesCount = PrefixForStorageHeader
			+ "approximate-messages-count";

	public static final String TableStorageNextTableName = PrefixForStorageHeader
			+ "continuation-NextTableName";
	public static final String TableStorageNextTablePartitionKey = PrefixForStorageHeader
			+ "continuation-NextPartitionKey";
	public static final String TableStorageNextTableRowKey = PrefixForStorageHeader
			+ "continuation-NextRowKey ";

	/*
	 * management
	 */
	public static final String ManagementRequestId = PrefixForStorageHeader
			+ "request-id";
}
