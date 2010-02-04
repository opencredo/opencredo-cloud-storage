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

import java.io.InputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.dom4j.Document;
import org.dom4j.Element;
import org.soyatec.windows.azure.authenticate.Base64;
import org.soyatec.windows.azure.authenticate.HttpRequestAccessor;
import org.soyatec.windows.azure.authenticate.ResourceUriComponents;
import org.soyatec.windows.azure.authenticate.SharedKeyCredentials;
import org.soyatec.windows.azure.authenticate.StorageAccountInfo;
import org.soyatec.windows.azure.blob.RetryPolicy;
import org.soyatec.windows.azure.blob.io.MemoryStream;
import org.soyatec.windows.azure.blob.io.Stream;
import org.soyatec.windows.azure.constants.CompConstants;
import org.soyatec.windows.azure.constants.HeaderNames;
import org.soyatec.windows.azure.constants.HttpMethod;
import org.soyatec.windows.azure.constants.QueryParams;
import org.soyatec.windows.azure.constants.RequestParams;
import org.soyatec.windows.azure.constants.XmlElementNames;
import org.soyatec.windows.azure.error.StorageException;
import org.soyatec.windows.azure.internal.HttpWebResponse;
import org.soyatec.windows.azure.internal.OutParameter;
import org.soyatec.windows.azure.util.HttpUtilities;
import org.soyatec.windows.azure.util.NameValueCollection;
import org.soyatec.windows.azure.util.TimeSpan;
import org.soyatec.windows.azure.util.Utilities;
import org.soyatec.windows.azure.util.xml.XPathQueryHelper;
import org.soyatec.windows.azure.util.xml.XmlUtil;

/**
 * Rest implementation of MessageQueue.
 * 
 */
public class QueueRest extends MessageQueue {

	private SharedKeyCredentials credentials;

	/**
	 * The poll interval in milliseconds. If not explicitly set, this defaults
	 * to the default poll interval.
	 */
	private int pollInterval = DEFAULT_POLL_INTERVAL;

	/**
	 * Constructor for rest implementation of MessageQueue..
	 * 
	 * @param name
	 *            The name of the queue.
	 * @param account
	 *            The user account this queue lives in.
	 * @param timeout
	 *            The timeout of requests.
	 * @param retryPolicy
	 *            The retry policy used for retrying requests
	 */
	QueueRest(String name, StorageAccountInfo account, TimeSpan timeout,
			RetryPolicy retryPolicy) {
		super(name, account);
		byte[] key = null;
		if (getAccount().getBase64Key() != null) {
			key = Base64.decode(getAccount().getBase64Key());
		}
		ResourceUriComponents uriComponents = new ResourceUriComponents(
				getAccount().getAccountName(), name, null);
		credentials = new SharedKeyCredentials(getAccount().getAccountName(),
				key);
		setQueueUri(HttpRequestAccessor
				.constructResourceUri(getAccount().getBaseUri(), uriComponents,
						getAccount().isUsePathStyleUris()));
		setTimeout(timeout);
		setRetryPolicy(retryPolicy);

	}

	/**
	 * Return the poll interval of checking new messages in milliseconds.
	 * 
	 * @return The poll interval in milliseconds.
	 */
	public int getPollInterval() {
		return pollInterval;
	}

	/**
	 * Specify the poll interval of checking new messages in milliseconds.
	 * 
	 * @param pollInterval
	 *            The poll interval in milliseconds.
	 */
	public void setPollInterval(int pollInterval) {
		if (pollInterval <= 0)
			throw new IllegalArgumentException(
					"poll interval must be positive!");
		this.pollInterval = pollInterval;
	}

	/**
	 * Retrieves the approximate number of messages in a queue.
	 * 
	 * @return The approximate number of messages in this queue.
	 * @throws StorageException
	 */
	@Override
	public int approximateCount() throws StorageException {
		QueueProperties props = getProperties();
		return props.getApproximateMessageCount();
	}

	/**
	 * Delete all messages in a queue.
	 * 
	 * @return true if all messages were deleted successfully.
	 * @throws StorageException
	 */
	@Override
	public boolean clear() throws StorageException {
		boolean result = false;
		result = (Boolean) getRetryPolicy().execute(new Callable<Object>() {
			public Object call() throws Exception {

				ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccount().getAccountName(), getName(),
						RequestParams.Messages);

				URI uri = createRequestUri(RequestParams.Messages,
						new NameValueCollection(), false, uriComponents);

				HttpRequest request = HttpUtilities
						.createHttpRequestWithCommonHeaders(uri,
								HttpMethod.Delete, getTimeout());
				credentials.signRequest(request, uriComponents);
				try {
					HttpWebResponse response = HttpUtilities
							.getResponse(request);
					if (response.getStatusCode() == HttpStatus.SC_NO_CONTENT) {
						return true;
					} else {
						HttpUtilities.processUnexpectedStatusCode(response);
						return false; // Can't return
					}
				} catch (StorageException we) {
					throw HttpUtilities.translateWebException(we);
				}
			}
		});
		return result;
	}

	/**
	 * Creates a queue in the specified storage account.
	 * 
	 * @return true if the queue was successfully created.
	 * @throws StorageException
	 *             If queue is exist, a StorageException is throwed.
	 */
	@Override
	public boolean createQueue() throws StorageException {
		boolean result = false;
		final boolean[] exists = new boolean[] { false };
		result = (Boolean) getRetryPolicy().execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccount().getAccountName(), getName(), null);
				URI uri = createRequestUri(null, new NameValueCollection(),
						false, uriComponents);
				HttpRequest request = HttpUtilities
						.createHttpRequestWithCommonHeaders(uri,
								HttpMethod.Put, getTimeout());
				credentials.signRequest(request, uriComponents);
				try {
					HttpWebResponse response = HttpUtilities
							.getResponse(request);
					if (response.getStatusCode() == HttpStatus.SC_CREATED) {
						// as an addition we could parse the result and retrieve
						// queue properties at this point
						exists[0] = false;
						return true;
					} else if (response.getStatusCode() == HttpStatus.SC_NO_CONTENT
							|| response.getStatusCode() == HttpStatus.SC_CONFLICT) {
						exists[0] = true;
						return false;
					} else {
						HttpUtilities.processUnexpectedStatusCode(response);
						return false; // Can't return
					}
				} catch (StorageException we) {
					throw HttpUtilities.translateWebException(we);
				}
			}
		});
		return result;
	}

	/**
	 * Deletes a message from the queue.
	 * 
	 * @param msg
	 *            The message to retrieve with a valid popreceipt.
	 * @return true if the operation was successful.
	 * @throws StorageException
	 */
	@Override
	public boolean deleteMessage(final Message msg) throws StorageException {
		if (msg == null)
			throw new IllegalArgumentException("Message cannot be null!");
		if (msg.getPopReceipt() == null) {
			throw new IllegalArgumentException(
					"No PopReceipt for the given message!");
		}

		boolean result = false;
		result = (Boolean) getRetryPolicy().execute(new Callable<Object>() {
			public Object call() throws Exception {
				NameValueCollection col = new NameValueCollection();
				col.put(RequestParams.PopReceipt, msg.getPopReceipt()
						.toString());

				ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccount().getAccountName(), getName(),
						RequestParams.Messages + "/" + msg.getId());
				URI uri = createRequestUri(RequestParams.Messages + "/"
						+ msg.getId(), col, false, uriComponents);

				HttpRequest request = HttpUtilities
						.createHttpRequestWithCommonHeaders(uri,
								HttpMethod.Delete, getTimeout());
				credentials.signRequest(request, uriComponents);
				try {
					HttpWebResponse response = HttpUtilities
							.getResponse(request);
					if (response.getStatusCode() == HttpStatus.SC_NO_CONTENT) {
						return true;
					} else {
						HttpUtilities.processUnexpectedStatusCode(response);
						return false; // Can't return
					}
				} catch (StorageException we) {
					throw HttpUtilities.translateWebException(we);
				}
			}
		});
		return result;
	}

	/**
	 * Deletes the queue. The queue will be deleted regardless of whether there
	 * are messages in the queue or not.
	 * 
	 * @return true if the queue was successfully deleted.
	 * @exception StorageException
	 */
	@Override
	public boolean deleteQueue() throws StorageException {

		boolean result = false;
		result = (Boolean) getRetryPolicy().execute(new Callable<Object>() {
			public Object call() throws Exception {
				ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccount().getAccountName(), getName(), null);
				URI uri = createRequestUri(null, new NameValueCollection(),
						false, uriComponents);
				HttpRequest request = HttpUtilities
						.createHttpRequestWithCommonHeaders(uri,
								HttpMethod.Delete, getTimeout());
				credentials.signRequest(request, uriComponents);
				try {
					HttpWebResponse response = HttpUtilities
							.getResponse(request);
					if (response.getStatusCode() == HttpStatus.SC_NO_CONTENT) {
						return true;
					} else if (response.getStatusCode() == HttpStatus.SC_NOT_FOUND
							|| response.getStatusCode() == HttpStatus.SC_PRECONDITION_FAILED
							|| response.getStatusCode() == HttpStatus.SC_CONFLICT) {
						return false;
					} else {
						HttpUtilities.processUnexpectedStatusCode(response);
						return false; // Can't return
					}
				} catch (StorageException we) {
					throw HttpUtilities.translateWebException(we);
				}
			}
		});
		return result;
	}

	/**
	 * Retrieves a message from the queue.
	 * 
	 * @return The message retrieved or null if the queue is empty.
	 * @throws StorageException
	 */
	@Override
	public Message getMessage() throws StorageException {
		List<Message> result = getMessages(1);
		if (result == null || result.size() == 0) {
			return null;
		}
		return result.get(0);
	}

	/**
	 * Retrieves a message and sets its visibility timeout to the specified
	 * number of seconds.
	 * 
	 * @param visibilityTimeoutInSeconds
	 *            Visibility timeout of the message retrieved in seconds.
	 * @return
	 * @throws StorageException
	 */
	@Override
	public Message getMessage(int visibilityTimeoutInSeconds)
			throws StorageException {
		List<Message> result = getMessages(1, visibilityTimeoutInSeconds);
		if (result == null || result.size() == 0) {
			return null;
		}
		return result.get(0);
	}

	/**
	 * Tries to retrieve the given number of messages.
	 * 
	 * @param numberOfMessages
	 *            Maximum number of messages to retrieve.
	 * @return The list of messages retrieved.
	 * @throws StorageException
	 */
	@Override
	public List<Message> getMessages(int numberOfMessages)
			throws StorageException {
		return getMessages(numberOfMessages, -1);
	}

	/**
	 * Tries to retrieve the given number of messages.
	 * 
	 * @param numberOfMessages
	 *            Maximum number of messages to retrieve.
	 * @param visibilityTimeoutInSeconds
	 *            The visibility timeout of the retrieved messages in seconds.
	 * @return The list of messages retrieved.
	 * @throws StorageException
	 */
	@Override
	public List<Message> getMessages(int numberOfMessages,
			int visibilityTimeoutInSeconds) throws StorageException {
		return internalGet(numberOfMessages, visibilityTimeoutInSeconds, false);
	}

	/**
	 * 
	 * Internal rest implementation of getting messages.
	 */
	@SuppressWarnings("unchecked")
	private List<Message> internalGet(final int numberOfMessages,
			final int visibilityTimeout, final boolean peekOnly) {
		if (peekOnly && visibilityTimeout != -1) {
			throw new IllegalArgumentException(
					"A peek operation does not change the visibility of messages");
		}
		if (numberOfMessages < 1) {
			throw new IllegalArgumentException(
					"numberOfMessages must be a positive integer");
		}
		if (visibilityTimeout < -1) {
			throw new IllegalArgumentException(
					"Visibility Timeout must be 0 or a positive integer");
		}
		List<Message> result = (List<Message>) getRetryPolicy().execute(
				new Callable<Object>() {
					public Object call() throws Exception {
						NameValueCollection col = new NameValueCollection();
						col.put(RequestParams.NumOfMessages, String
								.valueOf(numberOfMessages));

						if (visibilityTimeout != -1) {
							col.put(RequestParams.VisibilityTimeout, String
									.valueOf(visibilityTimeout));
						}
						if (peekOnly) {
							col.put(RequestParams.PeekOnly, String
									.valueOf(peekOnly));
						}
						ResourceUriComponents uriComponents = new ResourceUriComponents(
								getAccount().getAccountName(), getName(),
								RequestParams.Messages);
						// /eeee/messages?peekonly=true&numofmessages=3
						URI uri = createRequestUri(RequestParams.Messages, col,
								uriComponents);
						HttpRequest request = HttpUtilities
								.createHttpRequestWithCommonHeaders(uri,
										HttpMethod.Get, getTimeout());
						credentials.signRequest(request, uriComponents);

						try {
							HttpWebResponse response = HttpUtilities
									.getResponse(request);
							if (response.getStatusCode() == HttpStatus.SC_OK) {
								InputStream stream = response.getStream();
								List<Message> result = getMessagesFromResponse(stream);
								stream.close();
								return result;
							} else {
								HttpUtilities
										.processUnexpectedStatusCode(response);
								return null;
							}
						} catch (Exception e) {
							throw HttpUtilities.translateWebException(e);
						}
					}

				});

		return result;
	}

	/**
	 * Retrieves the queue's properties.
	 * 
	 * @return The queue's properties.
	 * @throws StorageException
	 */
	@Override
	public QueueProperties getProperties() throws StorageException {
		QueueProperties result = null;
		result = (QueueProperties) getRetryPolicy().execute(
				new Callable<QueueProperties>() {
					public QueueProperties call() throws Exception {
						NameValueCollection col = new NameValueCollection();
						col.put(QueryParams.QueryParamComp,
								CompConstants.Metadata);

						ResourceUriComponents uriComponents = new ResourceUriComponents(
								getAccount().getAccountName(), getName(), null);
						URI uri = createRequestUri(null, col, false,
								uriComponents);
						HttpRequest request = HttpUtilities
								.createHttpRequestWithCommonHeaders(uri,
										HttpMethod.Get, getTimeout());
						credentials.signRequest(request, uriComponents);
						try {
							HttpWebResponse response = HttpUtilities
									.getResponse(request);
							if (response.getStatusCode() == HttpStatus.SC_OK) {
								return getPropertiesFromHeaders(response);

							} else {
								HttpUtilities
										.processUnexpectedStatusCode(response);
								return null; // Can't return
							}
						} catch (StorageException we) {
							throw HttpUtilities.translateWebException(we);
						}
					}
				});
		return result;
	}

	/**
	 * Internal method for getting queue properties from web response.
	 */
	private QueueProperties getPropertiesFromHeaders(HttpWebResponse response) {
		QueueProperties properties = new QueueProperties();
		properties.setMetadata(new NameValueCollection());
		int prefixLength = HeaderNames.PrefixForMetadata.length();
		for (Object o : response.getHeaders().keySet()) {
			String key = (String) o;
			if (key.equalsIgnoreCase(HeaderNames.ApproximateMessagesCount)) {
				properties.setApproximateMessageCount(Integer.parseInt(response
						.getHeader(key)));
			} else if (key.length() > prefixLength
					&& key.substring(0, prefixLength).equalsIgnoreCase(
							HeaderNames.PrefixForMetadata)) {
				// if (properties.getMetadata() == null) {
				// properties.setMetadata(new NameValueCollection());
				// }
				properties.getMetadata().put(key.substring(prefixLength),
						response.getHeader(key));
			}
		}
		return properties;
	}

	/**
	 * Get a message from the queue but do not actually dequeue it. The message
	 * will remain visible for other parties requesting messages.
	 * 
	 * @return The message retrieved or null if there are no messages in the
	 *         queue.
	 * @throws StorageException
	 */
	@Override
	public Message peekMessage() throws StorageException {
		List<Message> result = peekMessages(1);
		if (result == null || result.size() == 0) {
			return null;
		}
		return result.get(0);
	}

	/**
	 * Tries to get a copy of messages in the queue without actually dequeuing
	 * the messages. The messages will remain visible in the queue.
	 * 
	 * @param numberOfMessages
	 *            Maximum number of message to retrieve.
	 * @return The list of messages retrieved.
	 * @throws StorageException
	 */
	@Override
	public List<Message> peekMessages(int numberOfMessages)
			throws StorageException {
		return internalGet(numberOfMessages, -1, true);
	}

	/**
	 * Puts a message in the queue.
	 * 
	 * @param msg
	 *            The message to store in the queue.
	 * @return true if the message has been successfully enqueued.
	 * @throws StorageException
	 */
	@Override
	public boolean putMessage(Message msg) throws StorageException {
		return putMessage(msg, -1);
	}

	/**
	 * Puts a message in the queue.
	 * 
	 * @param msg
	 *            The message to store in the queue.
	 * @param timeToLiveInSeconds
	 *            The time to live for the message in seconds.
	 * @return true if the message has been successfully enqueued.
	 * @throws StorageException
	 */
	@Override
	public boolean putMessage(final Message msg, final int timeToLiveInSeconds)
			throws StorageException {
		if (timeToLiveInSeconds < -1) {
			throw new IllegalArgumentException(
					"ttl parameter must be equal or larger than 0.");
		} else if (timeToLiveInSeconds > Message.MaxTimeToLive) {
			throw new IllegalArgumentException(
					MessageFormat
							.format(
									"timeToLiveHours parameter must be smaller or equal than {0}, which is 7 days in hours.",
									Message.MaxTimeToLive));
		}

		if (msg == null || msg.getContentAsBytes() == null) {
			throw new IllegalArgumentException("Message cannot be null!");
		}

		if (Base64.encode(msg.getContentAsBytes()).length() > Message.MaxMessageSize) {
			throw new IllegalArgumentException(MessageFormat.format(
					"Messages cannot be larger than {0} bytes.",
					Message.MaxMessageSize));
		}

		final OutParameter<Boolean> result = new OutParameter<Boolean>(false);
		getRetryPolicy().execute(new Callable<Boolean>() {

			public Boolean call() throws Exception {
				NameValueCollection col = new NameValueCollection();
				if (timeToLiveInSeconds != -1) {
					col.put(RequestParams.MessageTtl, Integer
							.toString(timeToLiveInSeconds));
				}
				ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccount().getAccountName(), getName(),
						RequestParams.Messages);
				URI uri = createRequestUri(RequestParams.Messages, col,
						uriComponents);

				HttpRequest request = HttpUtilities
						.createHttpRequestWithCommonHeaders(uri,
								HttpMethod.Post, getTimeout());

				int len = 0;
				byte[] body = msg.GetContentXMLRepresentation();
				len = body.length;

				/*
				 * Apache Http Client will add the content-length header accord
				 * to the request body automaticlly. So please don't add
				 * content-length header for put/post request.
				 */
				// request.addHeader(HeaderNames.ContentLength, Integer
				// .toString(len));
				credentials.signRequest(request, uriComponents);
				Stream requestStream = new MemoryStream();
				Utilities.copyStream(new MemoryStream(body), requestStream,
						(int) len);
				((HttpEntityEnclosingRequest) request)
						.setEntity(new ByteArrayEntity(requestStream.getBytes()));
				try {
					HttpWebResponse response = HttpUtilities
							.getResponse(request);
					if (response.getStatusCode() == HttpStatus.SC_CREATED) {
						result.setValue(true);
					} else {
						HttpUtilities.processUnexpectedStatusCode(response);
						result.setValue(false);
					}
				} catch (Exception e) {
					throw HttpUtilities.translateWebException(e);
				}
				return null;
			}
		});
		return result.getValue();
	}

	/**
	 * Sets the properties of a queue.
	 * 
	 * @param properties
	 *            The queue's properties to set.
	 * @return true if the properties were successfully written to the queue.
	 * @throws StorageException
	 */
	@Override
	public boolean setProperties(final QueueProperties properties)
			throws StorageException {
		if (properties == null) {
			throw new IllegalArgumentException("Properties cannot be null!");

		}
		if (properties.getMetadata() == null) {
			throw new IllegalArgumentException("Metadata cannot be null!");

		}
		boolean result = false;

		result = (Boolean) getRetryPolicy().execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				NameValueCollection col = new NameValueCollection();
				col.put(QueryParams.QueryParamComp, CompConstants.Metadata);

				ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccount().getAccountName(), getName(), null);
				URI uri = createRequestUri(null, col, false, uriComponents);
				HttpRequest request = HttpUtilities
						.createHttpRequestWithCommonHeaders(uri,
								HttpMethod.Put, getTimeout());
				HttpUtilities.addMetadataHeaders(request, properties
						.getMetadata());
				credentials.signRequest(request, uriComponents);
				try {
					HttpWebResponse response = HttpUtilities
							.getResponse(request);
					if (response.getStatusCode() == HttpStatus.SC_NO_CONTENT) {
						return true;

					} else {
						HttpUtilities.processUnexpectedStatusCode(response);
						return false; // Can't return
					}
				} catch (StorageException we) {
					throw HttpUtilities.translateWebException(we);
				}
			}
		});
		return result;
	}

	/**
	 * Internal method for creating request uri.
	 */
	private URI createRequestUri(String uriSuffix,
			NameValueCollection queryParameters, boolean accountOperation,
			ResourceUriComponents uriComponents) {
		return HttpUtilities.createRequestUri(getAccount().getBaseUri(),
				getAccount().isUsePathStyleUris(), getAccount()
						.getAccountName(), accountOperation ? null : this
						.getName(), uriSuffix, this.getTimeout(),
				queryParameters, uriComponents);
	}

	/**
	 * Internal method for creating request uri.
	 */
	private URI createRequestUri(String uriSuffix,
			NameValueCollection queryParameters,
			ResourceUriComponents uriComponents) {
		return createRequestUri(uriSuffix, queryParameters, false,
				uriComponents);
	}

	/**
	 * Starts the automatic receiving messages.
	 * 
	 * @return true if the operation was successful.
	 */
	@Override
	public boolean startReceiving() {
		synchronized (this) {
			if (_run) {
				return true;
			}
			_run = true;
		}

		if (_evStarted == null) {
			_evStarted = lock.newCondition();
		}
		if (_evQuit == null) {
			_evQuit = lock.newCondition();
		}
		if (_evStopped == null) {
			_evStopped = lock.newCondition();
		}

		_receiveThread = new Thread(new PeriodicReceive());
		_receiveThread.start();

		try {
			lock.lock();
			if (!_evStarted.await(10000, TimeUnit.MILLISECONDS)) {
				return terminateReceiveThread();
			}
		} catch (InterruptedException e) {
			return terminateReceiveThread();
		} finally {
			lock.unlock();
		}
		return true;
	}

	/**
	 * Internal method to stop automatic receiving messages thread.
	 */
	private boolean terminateReceiveThread() {
		forceTerminateReceiveThread();
		clearConditions();
		_run = false;
		return false;
	}

	/**
	 * Stop the automatic receiving messages.
	 * 
	 */
	@Override
	public void stopReceiving() {
		lock.lock();
		_evQuit.signal();
		try {
			if (timeoutForTerminating()) {
				forceTerminateReceiveThread();
			}
		} catch (InterruptedException e) {
			forceTerminateReceiveThread();
		} finally {
			lock.unlock();
		}
		clearConditions();
		_run = false;
	}

	private void forceTerminateReceiveThread() {
		_receiveThread.interrupt();
	}

	private boolean timeoutForTerminating() throws InterruptedException {
		return !_evStopped.await(10000, TimeUnit.MILLISECONDS);
	}

	private void clearConditions() {
		_evStarted = null;
		_evQuit = null;
		_evStopped = null;
	}

	private boolean _run = Boolean.FALSE;
	private Thread _receiveThread;
	private int _internalPollInterval;

	private final Lock lock = new ReentrantLock();
	private Condition _evStarted = null;
	private Condition _evStopped = null;
	private Condition _evQuit = null;

	/**
	 * Internal class for automatic receiving messages repeatedly.
	 * 
	 */
	private class PeriodicReceive implements Runnable {
		public void run() {
			Message msg;
			lock.lock();
			try {
				_evStarted.signal();
				_internalPollInterval = getPollInterval();
				while (timeoutForReceiving()) {
					// time is up, so we get the message and continue
					msg = getMessage();
					if (msg != null) {
						receiveMessage(this, new MessageReceivedEvent(
								QueueRest.this, msg));
						// continue receiving fast until we get no message
						_internalPollInterval = 10;
					} else {
						// we got no message, so we can fall back to the normal
						// speed
						_internalPollInterval = getPollInterval();
					}
				}

				_evStopped.signal();

			} catch (InterruptedException ie) {

			} finally {
				lock.unlock();
			}
		}

		private boolean timeoutForReceiving() throws InterruptedException {
			return !_evQuit.await(_internalPollInterval, TimeUnit.MILLISECONDS);
		}

		private synchronized void receiveMessage(
				PeriodicReceive periodicReceive,
				MessageReceivedEvent messageReceivedEvent) {
			QueueRest.this.changeSupport.notifyAll(messageReceivedEvent);
		}
	}

	/**
	 * Internal method for getting queue messages from input stream.
	 */
	@SuppressWarnings("unchecked")
	private List<Message> getMessagesFromResponse(InputStream stream) {
		if (stream == null) {
			return Collections.EMPTY_LIST;
		}
		Document doc = XmlUtil.load(stream,
				"The result of a get message opertation could not be parsed");
		List messagesNodes = doc
				.selectNodes(XPathQueryHelper.MessagesListQuery);

		List<Message> result = null;
		Message msg = null;
		if (messagesNodes.size() > 0) {
			result = new ArrayList<Message>();
		}
		for (Iterator iterator = messagesNodes.iterator(); iterator.hasNext();) {
			Element messageNode = (Element) iterator.next();
			msg = new Message();
			msg.setId(messageNode.selectSingleNode(XmlElementNames.MessageId)
					.getStringValue().trim());
			assert (msg.getId() != null);
			if (messageNode.selectSingleNode(XmlElementNames.PopReceipt) != null) {
				msg.setPopReceipt(messageNode.selectSingleNode(
						XmlElementNames.PopReceipt).getStringValue().trim());
				assert (msg.getPopReceipt() != null);
			}
			msg.setInsertTime(XPathQueryHelper.loadSingleChildDateTimeValue(
					messageNode, XmlElementNames.InsertionTime, false));

			msg.setExpirationTime(XPathQueryHelper
					.loadSingleChildDateTimeValue(messageNode,
							XmlElementNames.ExpirationTime, false));
			if (XPathQueryHelper.loadSingleChildDateTimeValue(messageNode,
					XmlElementNames.TimeNextVisible, false) != null) {
				msg.setTimeNextVisible(XPathQueryHelper
						.loadSingleChildDateTimeValue(messageNode,
								XmlElementNames.TimeNextVisible, false));
			}

			msg.setContentFromBase64String(XPathQueryHelper
					.loadSingleChildStringValue(messageNode,
							XmlElementNames.MessageText, false));

			result.add(msg);
		}

		return result;
	}
}
