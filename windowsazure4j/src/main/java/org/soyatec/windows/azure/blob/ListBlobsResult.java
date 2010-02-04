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
package org.soyatec.windows.azure.blob;

import java.util.List;

/**
 * @internal
 */
final class ListBlobsResult {
	private final List<BlobProperties> blobs;
	private final List<String> commonPrefixs;
	private final String nextMarker;

	public ListBlobsResult(final List<BlobProperties> blobs, final List<String> commonPrefixs, final String nextMarker) {
		this.blobs = blobs;
		this.commonPrefixs = commonPrefixs;
		this.nextMarker = nextMarker;
	}

	public List<BlobProperties> getBlobs() {
		return blobs;
	}

	public List<String> getCommonPrefixs() {
		return commonPrefixs;
	}

	public String getNextMarker() {
		return nextMarker;
	}

}
