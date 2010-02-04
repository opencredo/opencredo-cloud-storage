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

import java.util.EventObject;

/**
 * The argument class for the MessageReceived event.
 * 
 */
public class MessageReceivedEvent extends EventObject {

	private static final long serialVersionUID = 1731749203394194396L;

	/**
	 * The message itself.
	 */
	private Message message;

	/**
	 * Constructor for creating a message received argument.
	 * 
	 * @param source
	 *            Event source
	 * @param message
	 */
	public MessageReceivedEvent(Object source, Message message) {
		super(source);
		checkMessage(message);
		this.message = message;
	}

	/**
	 * 
	 * Helper method to ensure that message is not null.
	 */
	private void checkMessage(Message message) {
		if (message == null) {
			throw new IllegalArgumentException("Message cannot be null!");
		}
	}

	/**
	 * Returns the new message which is put into the queue.
	 * 
	 * @return The new message
	 */
	public Message getMessage() {
		return message;
	}
	
}
