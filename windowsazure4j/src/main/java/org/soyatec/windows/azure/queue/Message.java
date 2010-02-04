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

import java.sql.Timestamp;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.soyatec.windows.azure.authenticate.Base64;
import org.soyatec.windows.azure.constants.XmlElementNames;
import org.soyatec.windows.azure.util.Utilities;

/**
 * Objects of this class represent a single message in the queue.
 */
public class Message {

	/**
	 * The maximum message size in bytes.
	 */
	public static final int MaxMessageSize = 8 * 1024;

	/**
	 * The maximum amount of time a message is kept in the queue. Max value is 7 days. Value is given in seconds.
	 */
	public static final int MaxTimeToLive = 7 * 24 * 60 * 60;

	// A unique ID of the message as returned from queue operations.
	private String id;

	// When a message is retrieved from a queue, a PopReceipt is returned. The
	// PopReceipt is used when
	// deleting a message from the queue.
	private String popReceipt;

	// The point in time when the message was put into the queue.
	private Timestamp insertTime;

	// A message's expiration time.
	private Timestamp expirationTime;

	// The point in time when a message becomes visible again after a Get()
	// operation was called
	// that returned the message.
	private Timestamp timeNextVisible;

	private byte[] content;

	/**
	 * Empty constructor.
	 */
	public Message() {
	}

	/**
	 * Creates a message and initializes the content of the message to be the specified string.
	 * 
	 * @param content
	 *            A string representing the contents of the message.
	 */
	public Message(String content) {
		if (content == null) {
			throw new IllegalArgumentException("Content cannot be null!");
		}
		this.content = content.getBytes();
	}

	/**
	 * Creates a message and given the specified byte contents. In this implementation, regardless of whether an XML or binary data is passed into this function, message contents are converted to base64 before passing the data to the queue service. When calculating the size of the message, the size of the base64 encoding is thus the important parameter.
	 * 
	 * @param content
	 */
	public Message(byte[] content) {
		if (content == null) {
			throw new IllegalArgumentException("Content cannot be null!");
		}
		if (Base64.encode(content).length() > MaxMessageSize) {
			throw new IllegalArgumentException("Message body is too big!");
		}
		this.content = content;
	}

	/**
	 * Returns the the contents of the message as a string.
	 * 
	 * @return
	 */
	public String getContentAsString() {
		return new String(content);
	}

	/**
	 * Returns the content of the message as a byte array
	 */
	public byte[] getContentAsBytes() {
		return content;
	}

	/**
	 * When calling the Get() operation on a queue, the content of messages returned in the REST protocol are represented as Base64-encoded strings. This internal function transforms the Base64 representation into a byte array.
	 * 
	 * @param str
	 *            The Base64-encoded string.
	 */
	void setContentFromBase64String(String str) {
		if (Utilities.isNullOrEmpty(str)) {
			// we got a message with an empty <MessageText> element
			this.content = Utilities.emptyString().getBytes();
		} else {
			this.content = Base64.decode(str);
		}
	}

	/**
	 * Internal method used for creating the XML that becomes part of a REST request
	 */
	byte[] GetContentXMLRepresentation() {
		byte[] ret = null;
		Document doc = DocumentHelper.createDocument();
		Element message = doc.addElement(XmlElementNames.QueueMessage);
		message.addElement(XmlElementNames.MessageText).setText(Base64.encode(content));
		ret = doc.asXML().getBytes();
		return ret;
	}

	/**
	 * Returns the unique ID of the message.
	 * 
	 * @return The unique ID of the message
	 */
	public String getId() {
		return id;
	}

	/**
	 * Return popReceipt which is used when deleting a message from the queue.
	 * 
	 * @return PopReceipt
	 */
	public String getPopReceipt() {
		return popReceipt;
	}

	/**
	 * Returns the point in time when the message was put into the queue.
	 * 
	 * @return The point in time when the message was put into the queue.
	 */
	public Timestamp getInsertTime() {
		return insertTime;
	}

	/**
	 * Returns the message's expiration time.
	 * 
	 * @return The message's expiration time.
	 */
	public Timestamp getExpirationTime() {
		return expirationTime;
	}

	/**
	 * Return the point in time when a message becomes visible again after a Get() operation was called that returned the message.
	 * 
	 * @return The point in time when a message becomes visible again after a Get() operation was called that returned the message.
	 */
	public Timestamp getTimeNextVisible() {
		return timeNextVisible;
	}

	/**
	 * Specify the unique ID of the message.
	 * 
	 * @param id
	 *            The unique ID of the message
	 */
	void setId(String id) {
		this.id = id;
	}

	/**
	 * Specify the popReceipt for this message which is used when deleting a message from the queue.
	 * 
	 * @param popReceipt
	 *            The popReceipt for this message which is used when deleting a message from the queue.
	 */
	void setPopReceipt(String popReceipt) {
		this.popReceipt = popReceipt;
	}

	/**
	 * Specify the point in time when the message was put into the queue.
	 * 
	 * @param insertTime
	 *            The point in time when the message was put into the queue.
	 */
	void setInsertTime(Timestamp insertTime) {
		this.insertTime = insertTime;
	}

	/**
	 * Specify the message's expiration time.
	 * 
	 * @param expirationTime
	 *            The message's expiration time.
	 */
	void setExpirationTime(Timestamp expirationTime) {
		this.expirationTime = expirationTime;
	}

	/**
	 * Specify the point in time when a message becomes visible again after a Get() operation was called that returned the message.
	 * 
	 * @param timeNextVisible
	 *            The point in time when a message becomes visible again after a Get() operation was called that returned the message.
	 */
	void setTimeNextVisible(Timestamp timeNextVisible) {
		this.timeNextVisible = timeNextVisible;
	}

}
