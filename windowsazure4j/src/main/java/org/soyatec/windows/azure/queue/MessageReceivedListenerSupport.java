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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Internal class for managing a collection listeners who will be notified when a new message is put into the queue.
 * 
 */
class MessageReceivedListenerSupport {

	/**
	 * Empty constructor.
	 */
	public MessageReceivedListenerSupport() {

	}

	/**
	 * 
	 * @param source
	 */
	public MessageReceivedListenerSupport(Object source) {
		this.source = source;
	}

	private List<IMessageReceivedListener> handlerList = new ArrayList<IMessageReceivedListener>();

	private Object source;

	/**
	 * Adds the listener to the collection of listeners who will be notified when a new message is received.
	 * 
	 * @param listener
	 *            the listener which should be notified
	 */
	public synchronized void addListener(IMessageReceivedListener handler) {
		if (handler == null)
			return;
		if (!handlerList.contains(handler)) {
			handlerList.add(handler);
		}
	}

	/**
	 * Removes the listener from the collection of listeners who will when a new message is received.
	 * 
	 * @param listener
	 *            the listener which should no longer be notified
	 */
	public synchronized void removeListener(IMessageReceivedListener handler) {
		if (handler == null) {
			return;
		}
		if (handlerList.contains(handler)) {
			handlerList.remove(handler);
		}
	}

	/**
	 * Notify all listeners that a new message is received.
	 * 
	 * @param event
	 *            The event holding the new message.
	 */
	public void notifyAll(final MessageReceivedEvent event) {
		if (event == null) {
			return;
		}

		if (handlerList.isEmpty()) {
			return;
		}
		synchronized (handlerList) {
			for (Iterator<IMessageReceivedListener> iterator = handlerList.iterator(); iterator.hasNext();) {
				IMessageReceivedListener type = (IMessageReceivedListener) iterator.next();
				if (type != null) {
					if (event.getSource() == null && getSource() != null) {
						type.handle(new MessageReceivedEvent(getSource(), event.getMessage()));
					} else {
						type.handle(event);
					}
				}
			}
		}
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

}
