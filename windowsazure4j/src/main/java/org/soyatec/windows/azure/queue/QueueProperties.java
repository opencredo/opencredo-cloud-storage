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

import org.soyatec.windows.azure.util.NameValueCollection;

/**
 * The properties of a queue.
 * 
 */
public class QueueProperties {

	// The approximated amount of messages in the queue.
	private int approximateMessageCount;

	// Metadata for the queue in the form of name-value pairs.
	private NameValueCollection metadata;

	/**
	 * Returns the approximated amount of messages in the queue.
	 * 
	 * @return The approximated amount of messages in the queue.
	 */
	public int getApproximateMessageCount() {
		return approximateMessageCount;
	}

	/**
	 * Specify the approximated amount of messages in the queue.
	 * 
	 * @param approximateMessageCount
	 *            The approximated amount of messages in the queue.
	 */
	void setApproximateMessageCount(int approximateMessageCount) {
		this.approximateMessageCount = approximateMessageCount;
	}

	/**
	 * Returns metadata for the queue in the form of name-value pairs.
	 * 
	 * @return Metadata for the queue in the form of name-value pairs.
	 */
	public NameValueCollection getMetadata() {
		return metadata;
	}

	/**
	 * Specify metadata for the queue in the form of name-value pairs.
	 * 
	 * @param metadata
	 *            Metadata for the queue in the form of name-value pairs.
	 */
	public void setMetadata(NameValueCollection metadata) {
		this.metadata = metadata;
	}

}
