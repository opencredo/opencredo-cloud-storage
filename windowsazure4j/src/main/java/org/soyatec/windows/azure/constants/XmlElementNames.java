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

public final class XmlElementNames {
	public static final String BlockList = "BlockList";
	public static final String Block = "Block";
	public static final String EnumerationResults = "EnumerationResults";
	public static final String Prefix = "Prefix";
	public static final String Marker = "Marker";
	public static final String MaxResults = "MaxResults";
	public static final String Delimiter = "Delimiter";
	public static final String NextMarker = "NextMarker";
	public static final String Containers = "Containers";
	public static final String Container = "Container";
	public static final String ContainerName = "Name";
	public static final String ContainerNameAttribute = "ContainerName";
	public static final String AccountNameAttribute = "AccountName";
	public static final String LastModified = "LastModified";
	public static final String Etag = "Etag";
	public static final String Url = "Url";
	public static final String CommonPrefixes = "CommonPrefixes";
	public static final String ContentType = "ContentType";
	public static final String ContentEncoding = "ContentEncoding";
	public static final String ContentLanguage = "ContentLanguage";
	public static final String Size = "Size";
	public static final String Blobs = "Blobs";
	public static final String Blob = "Blob";
	public static final String BlobName = "Name";
	public static final String BlobPrefix = "BlobPrefix";
	public static final String BlobPrefixName = "Name";

	// Signed Identifired
	public static final String ContainerSignedIdentifierName = "SignedIdentifier";
	public static final String ContainerSignedIdentifierId = "Id";
	public static final String ContainerAccessPolicyName = "AccessPolicy";
	public static final String ContainerAccessPolicyStart = "Start";
	public static final String ContainerAccessPolicyExpiry = "Expiry";
	public static final String ContainerAccessPolicyPermission = "Permission";

	public static final String Name = "Name";
	public static final String Queues = "Queues";
	public static final String Queue = "Queue";
	public static final String QueueName = "QueueName";
	public static final String QueueMessagesList = "QueueMessagesList";
	public static final String QueueMessage = "QueueMessage";
	public static final String MessageId = "MessageId";
	public static final String PopReceipt = "PopReceipt";
	public static final String InsertionTime = "InsertionTime";
	public static final String ExpirationTime = "ExpirationTime";
	public static final String TimeNextVisible = "TimeNextVisible";
	public static final String MessageText = "MessageText";

	// Error specific constants
	public static final String ErrorRootElement = "Error";
	public static final String ErrorCode = "Code";
	public static final String ErrorMessage = "Message";
	public static final String ErrorException = "ExceptionDetails";
	public static final String ErrorExceptionMessage = "ExceptionMessage";
	public static final String ErrorExceptionStackTrace = "StackTrace";
	public static final String AuthenticationErrorDetail = "AuthenticationErrorDetail";

	// The following are for table error messages
	public static final String TableFeed = "feed";
	public static final String TableEntry = "entry";
	public static final String TableEntryContent = "content";
	public static final String TableEntryProperties = "properties";
	public static final String TableEntryTableName = "TableName";
	public static final String DataWebMetadataNamespace = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata";
	public static final String TableErrorCodeElement = "code";
	public static final String TableErrorMessageElement = "message";

	public static final String TableEntryPropertyPartitionKey = "PartitionKey";
	public static final String TableEntryPropertyRowKey = "RowKey";
	public static final String TableEntryPropertyETag = "etag";
	public static final String TableEntryPropertyTimestamp = "Timestamp";
	public static final String TableEntryPropertyType = "type";
}
