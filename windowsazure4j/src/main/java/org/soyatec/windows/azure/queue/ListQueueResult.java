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
package org.soyatec.windows.azure.queue;

import java.util.List;

/**
 * Internal class for holding intermediate result of list queue operation.
 * 
 * @author xye xiaowei.ye@soyatec.com
 * 
 */
final class ListQueueResult {

	private List<String> queueNames;
	private List<String> urls;
	private String nextMarker;

	/**
	 * Constructor for ListQueueResult.
	 * 
	 * @param queueNames
	 *            A collection of queue names in this portion of list result.
	 * @param urls
	 *            A collection of urls in this portion of list result.
	 * @param nextMarker
	 *            Identifies the portion of the list to be returned with the next list operation.
	 */
	ListQueueResult(List<String> queueNames, List<String> urls, final String nextMarker) {
		this.queueNames = queueNames;
		this.urls = urls;
		this.nextMarker = nextMarker;
	}

	/**
	 * Returns A collection of queue names.
	 * 
	 * @return A collection of queue names.
	 */
	List<String> getQueueNames() {
		return queueNames;
	}

	/**
	 * Specify a collection of queue names.
	 * 
	 * @param queueNames
	 *            A collection of queue names.
	 */
	void setQueueNames(List<String> queueNames) {
		this.queueNames = queueNames;
	}

	/**
	 * Returns marker that identifies the portion of the list to be returned with the next list operation.
	 * 
	 * @return Marker that identifies the portion of the list to be returned with the next list operation.
	 */
	String getNextMarker() {
		return nextMarker;
	}

	/**
	 * Specify the marker that identifies the portion of the list to be returned with the next list operation.
	 * 
	 * @param nextMarker
	 *            The marker that identifies the portion of the list to be returned with the next list operation.
	 */
	void setNextMarker(String nextMarker) {
		this.nextMarker = nextMarker;
	}

	/**
	 * Returns a collection of urls.
	 * 
	 * @return A collection of urls.
	 */
	List<String> getUrls() {
		return urls;
	}

	/**
	 * Specify a collection of urls.
	 * 
	 * @param urls
	 *            A collection of urls.
	 */
	void setUrls(List<String> urls) {
		this.urls = urls;
	}

}
