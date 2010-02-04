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

public final class StandardPortalEndpoints {

	public static final String HttpProtocolPrefix = "http://";

	public static final String BlobStorage = "blob";
	public static final String QueueStorage = "queue";
	public static final String TableStorage = "table";

	public static final String StorageHostSuffix = ".core.windows.net";

	public static final String BlobStorageEndpoint = BlobStorage
			+ StorageHostSuffix;
	public static final String QueueStorageEndpoint = QueueStorage
			+ StorageHostSuffix;
	public static final String TableStorageEndpoint = TableStorage
			+ StorageHostSuffix;

	public static final String DevBlobEndpoint = "127.0.0.1:10000";

	public static final String DevQueueEndpoint = "127.0.0.1:10001";

	public static final String DevTableEndpoint = "127.0.0.1:10002";
}
