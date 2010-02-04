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
package org.soyatec.windows.azure.table;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.dom4j.Document;
import org.dom4j.Element;
import org.soyatec.windows.azure.authenticate.Base64;
import org.soyatec.windows.azure.authenticate.ResourceUriComponents;
import org.soyatec.windows.azure.authenticate.SharedKeyCredentials;
import org.soyatec.windows.azure.blob.RetryPolicy;
import org.soyatec.windows.azure.constants.ConstChars;
import org.soyatec.windows.azure.constants.HeaderNames;
import org.soyatec.windows.azure.constants.HeaderValues;
import org.soyatec.windows.azure.constants.HttpMethod;
import org.soyatec.windows.azure.constants.QueryParams;
import org.soyatec.windows.azure.constants.XmsVersion;
import org.soyatec.windows.azure.constants.XmlElementNames;
import org.soyatec.windows.azure.error.StorageClientException;
import org.soyatec.windows.azure.error.StorageErrorCode;
import org.soyatec.windows.azure.error.StorageException;
import org.soyatec.windows.azure.error.StorageServerException;
import org.soyatec.windows.azure.internal.HttpMerge;
import org.soyatec.windows.azure.internal.HttpWebResponse;
import org.soyatec.windows.azure.util.HttpUtilities;
import org.soyatec.windows.azure.util.Logger;
import org.soyatec.windows.azure.util.NameValueCollection;
import org.soyatec.windows.azure.util.TimeSpan;
import org.soyatec.windows.azure.util.Utilities;
import org.soyatec.windows.azure.util.xml.AtomUtil;
import org.soyatec.windows.azure.util.xml.XPathQueryHelper;
import org.soyatec.windows.azure.util.xml.XmlUtil;

/**
 * Implement {@link AzureTable} by RESTful service APIs.
 * 
 */
public class AzureTableRest extends AzureTable {

	/**
	 * Credentials instance
	 */
	private SharedKeyCredentials credentials;

	/**
	 * Batch operation list
	 */
	protected List<BatchOperation> batch = null;

	protected AzureTableRest(URI baseUri, boolean usePathStyleUris,
			String accountName, String tableName, String base64Key,
			TimeSpan timeout, RetryPolicy retryPolicy) {
		super(baseUri, usePathStyleUris, accountName, tableName, base64Key,
				timeout, retryPolicy);
		byte[] key = null;
		if (base64Key != null)
			key = Base64.decode(base64Key);
		credentials = new SharedKeyCredentials(accountName, key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.windows.azure.table.AzureTable#createTable()
	 */
	@Override
	public boolean createTable() {
		boolean result = (Boolean) getRetryPolicy().execute(
				new Callable<Boolean>() {
					public Boolean call() throws Exception {

						ResourceUriComponents uriComponents = new ResourceUriComponents(
								getAccountName(),
								TableStorageConstants.TablesName, null);

						URI uri = HttpUtilities.createRequestUri(getBaseUri(),
								isUsePathStyleUris(), getAccountName(),
								TableStorageConstants.TablesName, null, null,
								new NameValueCollection(), uriComponents);
						HttpRequest request = HttpUtilities.createHttpRequest(
								uri, HttpMethod.Post);
						request.addHeader(HeaderNames.ContentType,
								TableStorageConstants.AtomXml);
						request.addHeader(HeaderNames.Date, Utilities
								.getUTCTime());

						String atom = AtomUtil.createTableXml(tableName);
						((HttpEntityEnclosingRequest) request)
								.setEntity(new ByteArrayEntity(atom.getBytes()));
						credentials.signRequestForSharedKeyLite(request,
								uriComponents);
						HttpWebResponse response = HttpUtilities
								.getResponse(request);
						if (response.getStatusCode() == HttpStatus.SC_CREATED) {
							return true;
						} else if (response.getStatusCode() == HttpStatus.SC_CONFLICT) {
							return false;
						} else {
							HttpUtilities.processUnexpectedStatusCode(response);
						}
						return false;
					}
				});
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.windows.azure.table.AzureTable#deleteTable()
	 */
	@Override
	public boolean deleteTable() {
		boolean result = (Boolean) getRetryPolicy().execute(new Callable<Object>() {
			public Object call() throws Exception {
				ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccountName(), getTableNameURI(tableName), null);

				URI uri = HttpUtilities.createRequestUri(getBaseUri(),
						isUsePathStyleUris(), getAccountName(),
						TableStorageConstants.TablesName, null, null,
						new NameValueCollection(), uriComponents);

				HttpRequest request = HttpUtilities.createHttpRequest(uri,
						HttpMethod.Delete);
				request.addHeader(HeaderNames.ContentType,
						TableStorageConstants.AtomXml);
				request.addHeader(HeaderNames.Date, Utilities.getUTCTime());

				credentials.signRequestForSharedKeyLite(request, uriComponents);
				HttpWebResponse response = HttpUtilities.getResponse(request);
				if (response.getStatusCode() == HttpStatus.SC_NO_CONTENT)
					return true;
				else if(response.getStatusCode() == HttpStatus.SC_NOT_FOUND)
					return false;
				else{
					HttpUtilities.processUnexpectedStatusCode(response);
				}
				return true;
			}
		});
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.windows.azure.table.AzureTable#doesTableExist()
	 */
	@Override
	public boolean doesTableExist() {
		boolean result = false;
		result = (Boolean) getRetryPolicy().execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccountName(), getTableNameURI(tableName), null);
				URI uri = HttpUtilities.createRequestUri(getBaseUri(),
						isUsePathStyleUris(), getAccountName(), tableName,
						null, getTimeout(), new NameValueCollection(),
						uriComponents);
				HttpRequest request = HttpUtilities.createHttpRequest(uri,
						HttpMethod.Get);
				request.addHeader(HeaderNames.ContentType,
						TableStorageConstants.AtomXml);
				request.addHeader(HeaderNames.Date, Utilities.getUTCTime());

				credentials.signRequestForSharedKeyLite(request, uriComponents);

				try {
					HttpWebResponse response = HttpUtilities
							.getResponse(request);
					if (response.getStatusCode() == HttpStatus.SC_OK)
						return true;
					else if (response.getStatusCode() == HttpStatus.SC_GONE
							|| response.getStatusCode() == HttpStatus.SC_NOT_FOUND)
						return false;
					else {
						HttpUtilities.processUnexpectedStatusCode(response);
						return false;
					}
				} catch (StorageException we) {
					throw HttpUtilities.translateWebException(we);
				}
			}
		});
		return result;
	}

	String getTableNameURI(String tableName) {
		return TableStorageConstants.TablesName + ConstChars.LeftBrackets
				+ ConstChars.SingleQuotes + tableName + ConstChars.SingleQuotes
				+ ConstChars.RightBrackets;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.windows.azure.table.AzureTable#insertEntity(org.soyatec
	 * .microsoft.azure.table.TableStorageEntity)
	 */
	@Override
	public void insertEntity(final TableStorageEntity obj)
			throws StorageException {
		checkEntityKey(obj);
		getRetryPolicy().execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {

				ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccountName(), tableName, null);

				URI uri = HttpUtilities.createRequestUri(getBaseUri(),
						isUsePathStyleUris(), getAccountName(), tableName,
						null, null, new NameValueCollection(), uriComponents);

				HttpRequest request = HttpUtilities.createHttpRequest(uri,
						HttpMethod.Post);
				request.addHeader(HeaderNames.ContentType,
						TableStorageConstants.AtomXml);

				String atom = AtomUtil.tableEntityXml(tableName, obj);
				((HttpEntityEnclosingRequest) request)
						.setEntity(new ByteArrayEntity(atom.getBytes()));

				if (isInBatch()) {
					batch.add(new BatchOperation(tableName, null,
							HttpMethod.Post, request.getAllHeaders(), atom));
					return true;
				} else {
					credentials.signRequestForSharedKeyLite(request,
							uriComponents);
					HttpWebResponse response = HttpUtilities
							.getResponse(request);
					if (response.getStatusCode() == HttpStatus.SC_CREATED) {
						return true;
					} else {
						HttpUtilities.processUnexpectedStatusCode(response);
					}
					return false;
				}
			}
		});
	}

	/*
	 * Call changeEntity(...) to work actually.
	 * 
	 * @see org.soyatec.windows.azure.table.AzureTable#updateEntity(org.soyatec
	 * .microsoft.azure.table.TableStorageEntity)
	 */
	public void updateEntity(final TableStorageEntity obj)
			throws StorageException {
		changeEntity(obj, HeaderValues.MatchAny, HttpMethod.Put); // getEtag(obj)
		// );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.windows.azure.table.AzureTable#updateEntityIfNotModified
	 * (org.soyatec.windows.azure.table.TableStorageEntity)
	 */
	@Override
	public void updateEntityIfNotModified(TableStorageEntity obj)
			throws StorageException {
		changeEntity(obj, obj.getETag(), HttpMethod.Put); // getEtag(obj));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.windows.azure.table.AzureTable#mergeEntity(org.soyatec.
	 * microsoft.azure.table.TableStorageEntity)
	 */
	public void mergeEntity(TableStorageEntity obj) {
		changeEntity(obj, HeaderValues.MatchAny, HttpMerge.METHOD_NAME);
	}

	/**
	 * Change the specified entity. If the etag is not given, an
	 * {@link IllegalArgumentException} is thown.
	 * 
	 * @param obj
	 *            Entity
	 * @param etag
	 *            <strong>MUST</strong>! A property generaged by table service
	 *            when insert/modify the entity within table service.
	 * @param method
	 *            Http request send by. see {@link HttpMethod}
	 * @throws StorageException
	 */
	private void changeEntity(final TableStorageEntity obj, final String etag,
			final String method) throws StorageException {

		checkEntityKey(obj);
		if (etag == null) {
			throw new IllegalArgumentException(
					"Etag is required to change a table entity.");
		}

		getRetryPolicy().execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				String id = getEntityId(obj);
				ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccountName(), id, null);

				URI uri = HttpUtilities.createRequestUri(getBaseUri(),
						isUsePathStyleUris(), getAccountName(), id, null, null,
						new NameValueCollection(), uriComponents);

				HttpRequest request = HttpUtilities.createHttpRequest(uri,
						method);
				request.addHeader(HeaderNames.ContentType,
						TableStorageConstants.AtomXml);
				request.addHeader(HeaderNames.IfMatch, etag);

				String atom = AtomUtil.tableEntityXml(tableName, obj);
				((HttpEntityEnclosingRequest) request)
						.setEntity(new ByteArrayEntity(atom.getBytes()));
				if (isInBatch()) {
					batch.add(new BatchOperation(id, (String) null,
							HttpMethod.Put, request.getAllHeaders(), atom));
					return true;
				} else {
					credentials.signRequestForSharedKeyLite(request,
							uriComponents);
					HttpWebResponse response = HttpUtilities
							.getResponse(request);
					if (response.getStatusCode() == HttpStatus.SC_NO_CONTENT) {
						return true;
					} else {
						HttpUtilities.processUnexpectedStatusCode(response);
					}
					return false;
				}
			}
		});
	}

	private void checkEntityKey(TableStorageEntity obj) {
		if (obj == null)
			throw new IllegalArgumentException("Table entity cannot be null.");
		if (obj.getPartitionKey() == null) {
			throw new IllegalArgumentException("PartitionKey cannot be null.");
		}
		if (obj.getRowKey() == null) {
			throw new IllegalArgumentException("RowKey cannot be null.");
		}
	}

	private String getEntityId(TableStorageEntity obj) {
		return MessageFormat.format("{0}(PartitionKey=''{1}'',RowKey=''{2}'')",
				tableName, obj.getPartitionKey(), obj.getRowKey());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.windows.azure.table.AzureTable#deleteEntity(org.soyatec
	 * .microsoft.azure.table.TableStorageEntity)
	 */
	@Override
	public void deleteEntity(final TableStorageEntity obj)
			throws StorageException {
		deleteEntity(obj, HeaderValues.MatchAny);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.windows.azure.table.AzureTable#deleteEntityIfNotModified
	 * (org.soyatec.windows.azure.table.TableStorageEntity)
	 */
	@Override
	public void deleteEntityIfNotModified(TableStorageEntity obj)
			throws StorageException {
		deleteEntity(obj, obj.getETag());
	}

	private void deleteEntity(final TableStorageEntity obj, final String etag)
			throws StorageException {
		checkEntityKey(obj);
		if (etag == null) {
			throw new IllegalArgumentException(
					"Etag is required to delete a table entity.");
		}
		getRetryPolicy().execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				String id = getEntityId(obj);
				ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccountName(), id, null);

				URI uri = HttpUtilities.createRequestUri(getBaseUri(),
						isUsePathStyleUris(), getAccountName(), id, null, null,
						new NameValueCollection(), uriComponents);
				HttpRequest request = HttpUtilities.createHttpRequest(uri,
						HttpMethod.Delete);
				request.addHeader(HeaderNames.IfMatch, etag);
				if (isInBatch()) {
					batch.add(new BatchOperation(id, null, HttpMethod.Delete,
							request.getAllHeaders(), null)); // AtomUtil.
					// tableEntityXml
					// (tableName,
					// obj)
					return true;
				} else {
					credentials.signRequestForSharedKeyLite(request,
							uriComponents);
					HttpWebResponse response = HttpUtilities
							.getResponse(request);
					if (response.getStatusCode() == HttpStatus.SC_NO_CONTENT) {
						return true;
					} else {
						HttpUtilities.processUnexpectedStatusCode(response);
					}
					return false;
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.soyatec.windows.azure.table.AzureTable#loadEntity(org.soyatec.microsoft
	 * .azure.table.TableStorageEntity)
	 */
	@SuppressWarnings("unchecked")
	public <T extends TableStorageEntity> T loadEntity(final T obj)
			throws StorageException {
		checkEntityKey(obj);
		TableStorageEntity result = (TableStorageEntity) getRetryPolicy()
				.execute(new Callable<TableStorageEntity>() {
					public TableStorageEntity call() throws Exception {
						String id = getEntityId(obj);
						ResourceUriComponents uriComponents = new ResourceUriComponents(
								getAccountName(), id, null);
						URI uri = HttpUtilities.createRequestUri(getBaseUri(),
								isUsePathStyleUris(), getAccountName(), id,
								null, null, new NameValueCollection(),
								uriComponents);
						HttpRequest request = HttpUtilities.createHttpRequest(
								uri, HttpMethod.Get);
						credentials.signRequestForSharedKeyLite(request,
								uriComponents);
						HttpWebResponse response = HttpUtilities
								.getResponse(request);
						if (response.getStatusCode() == HttpStatus.SC_OK) {
							setModelClass(obj.getClass());
							List<TableStorageEntity> entities = getTableEntitiesFromResponse(response
									.getStream());
							if (entities != null && entities.size() > 0) {
								return entities.get(0);
							}
						} else {
							HttpUtilities.processUnexpectedStatusCode(response);
						}
						return null;
					}
				});

		return (T) result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.soyatec.windows.azure.table.AzureTable#retrieveEntities(java.lang
	 * .String)
	 */
	@Override
	public List<TableStorageEntity> retrieveEntities(
			final String queryExpression) throws StorageException {
		final List<TableStorageEntity> result = new ArrayList<TableStorageEntity>();
		getRetryPolicy().execute(new Callable<Object>() {
			public Object call() throws Exception {
				ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccountName(), getTableNameToQuery(tableName), null);
				NameValueCollection queryFliter = new NameValueCollection();
				URI uri = HttpUtilities.createRequestUri(getBaseUri(),
						isUsePathStyleUris(), getAccountName(), tableName,
						null, getTimeout(), queryFliter, uriComponents,
						queryExpression);
				HttpRequest request = HttpUtilities.createHttpRequest(uri,
						HttpMethod.Get);
				credentials.signRequestForSharedKeyLite(request, uriComponents);
				try {
					HttpWebResponse response = HttpUtilities
							.getResponse(request);
					Logger.log("Request URI:" + uri.toString());
					if (response.getStatusCode() == HttpStatus.SC_OK) {
						result.addAll(getTableEntitiesFromResponse(response
								.getStream()));
						return null;
					} else {
						HttpUtilities.processUnexpectedStatusCode(response);
						return null;
					}
				} catch (StorageException we) {
					throw HttpUtilities.translateWebException(we);
				}
			}
		});
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.soyatec.windows.azure.table.AzureTable#retrieveEntities(org.soyatec
	 * .windows.azure.table.Query)
	 */
	@Override
	public List<TableStorageEntity> retrieveEntities(final Query query)
			throws StorageException {
		if (query == null)
			throw new IllegalArgumentException("Query is empty");
		return retrieveEntities(query.toAzureQuery());
	}

	/**
	 * Part of request uri used in table "Query" operation.
	 * 
	 * @param tableName
	 * @return
	 */
	private String getTableNameToQuery(String tableName) {
		return tableName + ConstChars.LeftBrackets + ConstChars.RightBrackets;
	}

	/**
	 * All response for table request is represents in xml format. Parse xml and
	 * compose entities with the content in xml.
	 * 
	 * @param stream
	 * @return A list of {@link TableStorageEntity}
	 */
	@SuppressWarnings("unchecked")
	private List<TableStorageEntity> getTableEntitiesFromResponse(
			InputStream stream) {
		final List<TableStorageEntity> result = new ArrayList<TableStorageEntity>();
		Document document = XmlUtil
				.load(stream,
						"The result of a ListTableEntities operation could not be parsed");
		// get queue names and urls
		List xmlNodes = XPathQueryHelper.parseEntryFromFeed(document);
		for (Iterator iterator = xmlNodes.iterator(); iterator.hasNext();) {
			Element tableEntryNode = (Element) iterator.next();
			String partitionKey = XPathQueryHelper.loadTableEntryPropertyValue(
					tableEntryNode,
					XmlElementNames.TableEntryPropertyPartitionKey);
			String rowKey = XPathQueryHelper.loadTableEntryPropertyValue(
					tableEntryNode, XmlElementNames.TableEntryPropertyRowKey);
			// Retrieve ETag value from the entry attribute [m:etag]
			String eTag = XPathQueryHelper.loadTableEntryValueFromAttribute(
					tableEntryNode, XmlElementNames.TableEntryPropertyETag);
			String timestamp = XPathQueryHelper
					.loadTableEntryPropertyValue(tableEntryNode,
							XmlElementNames.TableEntryPropertyTimestamp);
			Element element = XPathQueryHelper
					.loadTableEntryProperties(tableEntryNode);

			List<AzureTableColumn> values = new ArrayList<AzureTableColumn>();
			for (Iterator iter = element.elementIterator(); iter.hasNext();) {
				Element next = (Element) iter.next();
				String xmlNodeName = next.getName();
				String xmlType = next
						.attributeValue(XmlElementNames.TableEntryPropertyType);
				String stringValue = next.getStringValue().trim();
				AzureTableColumn column = new AzureTableColumn();
				column.setName(xmlNodeName);
				column.setValue(stringValue);
				column.setType(ETableColumnType.getTypebyLiteral(xmlType));
				values.add(column);
			}
			if (getModelClass() != null) {
				try {
					TableStorageEntity targetObject = createObjectByModelClass(
							partitionKey, rowKey, eTag, timestamp, values);
					if (targetObject != null)
						result.add(targetObject);
				} catch (Exception e) {
					Logger.error("Compose table entity error!", e);
				}

			} else {
				// If have no targerObject given, use InternalTableStorageEntity
				TableStorageEntity entity = null;
				try {
					entity = new SimpleTableStorageEntity(partitionKey, rowKey,
							Utilities.tryGetDateTimeFromTableEntry(timestamp));
					entity.setValues(values);
					result.add(entity);
				} catch (ParseException e) {
					Logger.error("Parse timestamp error!", e);
				}
			}
		}
		return result;

	}

	/**
	 * Deserial the xml to object accord to the give model class.
	 * 
	 * @param partitionKey
	 * @param rowKey
	 * @param eTag
	 * @param timestamp
	 * @param values
	 * @return Instance of the given model class.
	 */
	private TableStorageEntity createObjectByModelClass(String partitionKey,
			String rowKey, String eTag, String timestamp,
			List<AzureTableColumn> values) {
		TableStorageEntity newInstance = instanceModel(partitionKey, rowKey,
				eTag, timestamp, values);
		if(newInstance == null){
			return null;
		}
		// Copy Field
		if (values != null && !values.isEmpty()) {
			for (AzureTableColumn column : values) {
				Field field = null;
				try {
					field = newInstance.getClass().getDeclaredField(
							column.getName());
				} catch (NoSuchFieldException e) {
					continue;
				}

				if (field == null) {
					continue;
				}
				int modifier = field.getModifiers();
				if (Modifier.isPrivate(modifier) && Modifier.isFinal(modifier)
						&& Modifier.isStatic(modifier)) {
					if (getModelClass() != null)
						Logger
								.debug(MessageFormat
										.format(
												"{0} class {1} is a static final field. Can not set data to it.",
												getModelClass().getClass(),
												field.getName()));
					continue;
				}

				boolean accessible = field.isAccessible();
				if (!accessible)
					field.setAccessible(true);
				ETableColumnType type = column.getType();
				String value = column.getValue();
				if (value == null) {
					continue;
				} else {
					try {
						if (type == null) {
							setStringOrObjectField(newInstance, column, field,
									value);
						} else if (type.equals(ETableColumnType.TYPE_BINARY)) {
							field.set(newInstance, Base64.decode(value));
						} else if (type.equals(ETableColumnType.TYPE_BOOL)) {
							field.setBoolean(newInstance, Boolean
									.parseBoolean(value));
						} else if (type.equals(ETableColumnType.TYPE_DATE_TIME)) {
							field.set(newInstance, Utilities
									.tryGetDateTimeFromTableEntry(value));
						} else if (type.equals(ETableColumnType.TYPE_DOUBLE)) {
							field.setDouble(newInstance, Double
									.parseDouble(value));
						} else if (type.equals(ETableColumnType.TYPE_GUID)) {
							Guid guid = new Guid();
							try {
								Field valueField = guid.getClass()
										.getDeclaredField("value");
								boolean accessiable = valueField.isAccessible();
								if (!accessible) {
									valueField.setAccessible(true);
								}
								valueField.set(guid, value);
								valueField.setAccessible(accessiable);
								field.set(newInstance, guid);
							} catch (NoSuchFieldException e) {
								Logger.error(e.getMessage(), e);
							}
						} else if (type.equals(ETableColumnType.TYPE_INT)) {
							try {
								field.setInt(newInstance, Integer
										.parseInt(value));
							} catch (Exception e) {
								field.setByte(newInstance, Byte
										.parseByte(value));
							}
						} else if (type.equals(ETableColumnType.TYPE_LONG)) {
							field.setLong(newInstance, Long.parseLong(value));
						} else if (type.equals(ETableColumnType.TYPE_STRING)) {
							setStringOrObjectField(newInstance, column, field,
									value);
						}
					} catch (Exception e) {
						Logger.error(MessageFormat.format(
								"{0} class filed {1} set failed.",
								getModelClass(), value), e);
					}
				}
				// revert aaccessible
				field.setAccessible(accessible);
			}
		}
		return newInstance;
	}

	/**
	 * Try to set the field as String type, if failed, set it as an Object
	 * field.
	 * 
	 * @param newInstance
	 * @param column
	 * @param field
	 * @param value
	 */
	private void setStringOrObjectField(TableStorageEntity newInstance,
			AzureTableColumn column, Field field, String value) {
		// Maybe Object type
		Object object = null;
		try {
			field.set(newInstance, value);
		} catch (Exception e) {
			try {
				object = Utilities.convertStringToObject(value);
				field.set(newInstance, object);
			} catch (Exception e1) {
				Logger.error(MessageFormat.format(
						"Fail to set {0} field value", column), e);
			}
		}
	}

	/**
	 * Create new table model entity and fill all attributes.
	 * 
	 * @param partitionKey
	 *            Partition key
	 * @param rowKey
	 *            Row key
	 * @param eTag
	 *            Etag
	 * @param timestamp
	 *            Last-modified
	 * @param values
	 *            Dynamic values.
	 * @return instance of {@link SimpleTableStorageEntity} or instance for
	 *         specified model class.
	 */
	private TableStorageEntity instanceModel(String partitionKey,
			String rowKey, String eTag, String timestamp,
			List<AzureTableColumn> values) {
		TableStorageEntity newInstance = null;
		try {
			newInstance = getModelClass().getDeclaredConstructor(String.class,
					String.class).newInstance(partitionKey, rowKey);
			newInstance.setETag(eTag);
			try {
				newInstance.setTimestamp(Utilities
						.tryGetDateTimeFromTableEntry(timestamp));
			} catch (ParseException e) {
				Logger
						.error(
								MessageFormat
										.format(
												"{0} class Timestamp field retrieve time from {2} failed.",
												getModelClass(), timestamp), e);
			}
			newInstance.setValues(values);
//		} catch (IllegalArgumentException e) {
//			Logger.error(MessageFormat.format("Retrieve object {0} fail.",
//					getModelClass()), e);
//		} catch (SecurityException e) {
//			Logger.error(MessageFormat.format("Retrieve object {0} fail.",
//					getModelClass()), e);
//		} catch (InstantiationException e) {
//			Logger.error(MessageFormat.format("Can't instance for class {0}",
//					getModelClass()), e);
//		} catch (IllegalAccessException e) {
//			Logger.error(MessageFormat.format("Retrieve object {0} fail.",
//					getModelClass()), e);
//		} catch (InvocationTargetException e) {
//			Logger.error(MessageFormat.format("Can't instance for class {0}",
//					getModelClass()), e);
//		} catch (NoSuchMethodException e) {
		} catch(Exception e){
			Logger.error(MessageFormat.format("Can't instance for class {0}",
					getModelClass()), e);
			throw new StorageException(
					MessageFormat
							.format(
									"Can't instance model class. Please check your class {0}. Make sure it has a public constructor method with partitionKey and rowKey as parameters!",
									getModelClass().toString()));
		}
		return newInstance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.windows.azure.table.AzureTable#retrieveEntities()
	 */
	@Override
	public List<TableStorageEntity> retrieveEntities() throws StorageException {
		return retrieveEntities("");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.soyatec.windows.azure.table.AzureTable#retrieveEntitiesByKey(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public List<TableStorageEntity> retrieveEntitiesByKey(String partitionKey,
			String rowKey) throws StorageException {
		StringBuffer buffer = new StringBuffer();
		if (partitionKey != null && rowKey != null) {
			if (isInBatch() && batch.size() == 0) {
				String id = MessageFormat.format(
						"{0}(PartitionKey=''{1}'',RowKey=''{2}'')", tableName,
						partitionKey, rowKey);
				batch.add(new BatchOperation(id, (String) null, HttpMethod.Get,
						null, null));
				return null;
			}
			buffer.append("PartitionKey").append(" ").append("eq").append(" ")
					.append(ConstChars.SingleQuotes).append(partitionKey)
					.append(ConstChars.SingleQuotes);
			buffer.append(" ").append("and").append(" ");
			buffer.append("RowKey").append(" ").append("eq").append(" ")
					.append(ConstChars.SingleQuotes).append(rowKey).append(
							ConstChars.SingleQuotes);
		} else if (partitionKey != null) {
			buffer.append("PartitionKey").append(" ").append("eq").append(" ")
					.append(ConstChars.SingleQuotes).append(partitionKey)
					.append(ConstChars.SingleQuotes);
		} else if (rowKey != null) {
			buffer.append("RowKey").append(" ").append("eq").append(" ")
					.append(ConstChars.SingleQuotes).append(rowKey).append(
							ConstChars.SingleQuotes);
		} else {
			throw new IllegalArgumentException("Query partitionKey or rowKey");
		}
		return retrieveEntities(QueryParams.QueryParamTableFilterPrefix + "="
				+ buffer.toString());
	}

	/**
	 * Make call to table service to send the batch operations.
	 */
	private void performBatch() {
		getRetryPolicy().execute(new Callable<Object>() {
			public Object call() throws Exception {
				String path = BatchStorage.BATCH_PATH;
				String queryString = "";
				ResourceUriComponents uriComponents = new ResourceUriComponents(
						getAccountName(), path, queryString);

				String batchBoundary = BatchStorage.BATCH_BOUNDARY_PREFIX
						+ Utilities.MD5(String.valueOf(new Date().getTime()));
				String changesetBoundary = BatchStorage.CHANGESET_BOUNDARY_PREFIX
						+ Utilities.MD5(String.valueOf(new Date().getTime()));
				URI uri = HttpUtilities.createRequestUri(getBaseUri(),
						isUsePathStyleUris(), getAccountName(), path, null,
						getTimeout(), null, uriComponents);

				HttpRequest request = HttpUtilities.createHttpRequest(uri,
						HttpMethod.Post);

				request.addHeader(HeaderNames.ApiVersion,
						XmsVersion.VERSION_2009_07_17);
				request.addHeader(HeaderNames.ContentType,
						"multipart/mixed; boundary=" + batchBoundary);

				String body = assambleBatchOperationBody(batch, batchBoundary,
						changesetBoundary);
				((HttpEntityEnclosingRequest) request)
						.setEntity(new ByteArrayEntity(body.getBytes()));

				credentials.signRequestForSharedKeyLite(request, uriComponents);
				try {
					HttpWebResponse response = HttpUtilities
							.getResponse(request);
					if (response.getStatusCode() == HttpStatus.SC_ACCEPTED) {
						StringBuilder buf = new StringBuilder(1024);
						Scanner in = new Scanner(response.getStream());
						while (in.hasNext())
							buf.append(in.nextLine()).append("\n");
						String errorMessage = Utilities
								.retrieveErrorMessages(buf.toString());
						if (errorMessage != null) {
							throw new StorageServerException(
									StorageErrorCode.BatchOperationError,
									errorMessage, response.getStatusCode(),
									null);
						} else {
							Logger.log(buf.toString());
						}
					} else {
						HttpUtilities.processUnexpectedStatusCode(response);
					}
				} catch (StorageException we) {
					throw HttpUtilities.translateWebException(we);
				}
				return null;
			}
		});
	}

	/**
	 * Help to assamble all single batch operations to a whole request body.
	 * 
	 * @param operations
	 *            All single batch operations
	 * @param batchBoundary
	 *            Boundary for each batch operation
	 * @param changesetBoundary
	 *            Boundary for each changeset
	 * @return A literal request body for a batch commit request. Always be a
	 *         atom xml.
	 */
	private String assambleBatchOperationBody(
			final List<BatchOperation> operations, final String batchBoundary,
			final String changesetBoundary) {

		StringBuffer rawData = new StringBuffer();
		rawData.append("--").append(batchBoundary).append("\n");

		if (operations.size() == 1
				&& operations.get(0).getMethod().equals(HttpMethod.Get)) {
			// A batch may include a single query operation that retrieves a
			// single entity.
			// This approach may be used to retrieve an entity when the size of
			// the PartitionKey and RowKey values exceed 256 characters and the
			// entity can therefore not be retrieved via a GET operation.
			// Note that a query operation is not permitted within a batch that
			// contains insert, update, or delete operations; it must be
			// submitted singly in the batch.

			rawData.append("Content-Type: application/http").append("\n");
			rawData.append("Content-Transfer-Encoding: binary").append("\n\n");
			rawData.append(getBatchOperationRequest(operations.get(0), 0));
			rawData.append("\n");

		} else {

			// Only support one changeset per one batch
			// The change set can include multiple insert, update, and delete
			// operations.

			rawData.append("Content-Type: multipart/mixed; boundary="
					+ changesetBoundary + "\n\n");

			// add all operations
			int index = 0;
			for (BatchOperation operation : operations) {
				rawData.append("--").append(changesetBoundary).append("\n");
				rawData.append("Content-Type: application/http").append("\n");
				rawData.append("Content-Transfer-Encoding: binary").append(
						"\n\n");
				rawData.append(getBatchOperationRequest(operation, index));
				rawData.append("\n");
				index++;
			}

			rawData.append("--").append(changesetBoundary).append("--").append(
					"\n");
		}

		rawData.append("--").append(batchBoundary).append("--");

		return rawData.toString();
	}

	/*
	 * Set the current batch null.
	 * 
	 * @see org.soyatec.windows.azure.table.BatchStorage#clearBatch()
	 */
	public void clearBatch() {
		batch = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.windows.azure.table.BatchStorage#executeBatch()
	 */
	public void executeBatch() {
		if (batch == null) {
			throw new IllegalStateException("No batch is started.");
		} else if (batch.isEmpty()) {
			throw new IllegalStateException(
					"Batch does not have any operation.");
		}
		try {
			performBatch();
		} finally {
			clearBatch();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.windows.azure.table.BatchStorage#startBatch()
	 */
	public void startBatch() {
		if (isInBatch()) {
			throw new IllegalArgumentException(
					"A batch is already start. Only one batch can be active at a time.");
		}
		batch = new ArrayList<BatchOperation>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.soyatec.windows.azure.table.BatchStorage#isInBatch()
	 */
	public boolean isInBatch() {
		return batch != null;
	}

	/**
	 * Help to deal with operation to a batch operation
	 * 
	 * @param op
	 * @param index
	 * @return
	 */
	private String getBatchOperationRequest(BatchOperation op, int index) {
		String path = op.getPath();
		// Clean path
		if (path.charAt(0) != '/')
			path = "/" + path;

		Header[] headers = op.getHeaders();
		if (headers == null)
			headers = new Header[0];

		// Generate URL
		String requestUrl = getBaseUri() + urlEncode(path)
				+ urlEncode(op.getQueryString());

		String requestBody = op.getRequestBody();
		if (requestBody == null)
			requestBody = "";

		StringBuffer buf = new StringBuffer();
		buf.append(op.getMethod() + " " + requestUrl + " HTTP/1.1\n");

		for (Header header : headers) {
			if (header.getName().equals(HeaderNames.ContentType)
					|| header.getName().equals(HeaderNames.ContentLength))
				continue;
			buf.append(header.getName() + ":" + header.getValue() + "\n");
		}
		index++;
		buf.append(HeaderNames.ContentID + ":" + index + "\n");

		if (!op.getMethod().equals(HttpMethod.Delete)
				&& !op.getMethod().equals(HttpMethod.Get)) {
			buf.append(HeaderNames.ContentType
					+ ":application/atom+xml;type=entry\n");
			buf.append(HeaderNames.ContentLength + ":" + requestBody.length()
					+ "\n");
		}

		buf.append("\n");
		buf.append(requestBody);
		return buf.toString();
	}

	/**
	 * Encode the url.
	 * 
	 * @param url
	 * @return
	 */
	private String urlEncode(String url) {
		if (url == null)
			return "";
		else
			return url.replaceAll(" ", "%20");
	}

}
