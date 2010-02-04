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

import java.net.URI;

import java.util.List;

import org.soyatec.windows.azure.blob.RetryPolicy;
import org.soyatec.windows.azure.error.StorageException;
import org.soyatec.windows.azure.util.TimeSpan;

/**
 * API entry point for using structured storage.</p>
 * 
 * The underlying usage pattern is designed to be similar to the one used in
 * blob and queue services in this library.</p>
 * 
 * <h5>Batch operations</h5>
 * 
 * The {@link AzureTable} implements the {@link BatchStorage}. Some TRANSCATION
 * supported operations can be enlist a single batch operation. Example:
 * 
 * <pre>
 *      SampleEntity sampleEntity1 = createSampleEntity();
 * 		SampleEntity sampleEntity2 = createSampleEntity();
 * 		try {
 * 			table.startBatch();
 * 			table.insertEntity(sampleEntity1);
 * 			table.insertEntity(sampleEntity2);
 * 			table.executeBatch();
 * 	    catch(Exception e){
 * 	    	//
 * 	    }
 * </pre>
 * 
 * For more detail about <strong>Batch operation</strong>, see
 * {@link BatchStorage}.
 * 
 */
public abstract class AzureTable implements BatchStorage {

	/**
	 * The base uri of the table service
	 */

	private URI baseUri;
	/**
	 * The name of storage account
	 */
	private String accountName;

	/**
	 * TThe name of the specified table
	 */
	protected String tableName;

	/**
	 * Indicates whether to use/generate path-style or host-style URIs
	 */
	private boolean usePathStyleUris;

	/**
	 * The time out for each request to the storage service.
	 */
	private TimeSpan timeout;

	/**
	 * The retry policy used for retrying requests
	 */
	private RetryPolicy retryPolicy;

	/**
	 * Default, azure table entities are retrieved as the instances of class
	 * {@link SimpleTableStorageEntity}. A list of
	 * {@link SimpleTableStorageEntity} is return when retrieve entities from
	 * table service.</p>
	 * 
	 * <strong>Model</strong> class can be changed to specified table service to
	 * represent more <strong>Model</strong> details. </p>
	 * 
	 * <strong>Model</strong> class must be subclass of
	 * {@link TableStorageEntity}.
	 */
	private Class<? extends TableStorageEntity> modelClass;

	/**
	 * Create a new azure table.
	 * 
	 * It't not make a call to service. Subclass extends in RESTful style call
	 * this to create a new azure table instance.
	 * 
	 * @param baseUri
	 *            The base uri of the table service.
	 * 
	 * @param usePathStyleUris
	 *            Indicates whether to use/generate path-style or host-style
	 *            URIs
	 * @param accountName
	 *            The name of storage account.
	 * @param tableName
	 *            The name of the specified table.
	 * 
	 * @param base64Key
	 *            Authentication key used for signing requests.
	 * @param timeout
	 *            The time out for each request to the storage service.
	 * @param retryPolicy
	 *            The retry policy used for retrying requests.
	 */
	protected AzureTable(URI baseUri, boolean usePathStyleUris,
			String accountName, String tableName, String base64Key,
			TimeSpan timeout, RetryPolicy retryPolicy) {
		this.baseUri = baseUri;
		this.usePathStyleUris = usePathStyleUris;
		this.accountName = accountName;
		this.tableName = tableName;
		this.timeout = timeout;
		this.retryPolicy = retryPolicy;
	}

	/**
	 * Creates a new table in the service
	 * 
	 */
	public abstract boolean createTable();

	/**
	 * Checks whether a table with the same name already exists.
	 * 
	 * @return True if the table already exists.
	 */
	public abstract boolean doesTableExist();

	/**
	 * Deletes a table from the service. </p>
	 * 
	 * When a table is successfully deleted, it is immediately marked for
	 * deletion and is no longer accessible to clients. The table is later
	 * removed from the Table service during <strong>garbage
	 * collection.</strong></p>
	 * 
	 * Note that deleting a table is likely to take at least 40 seconds to
	 * complete. If an operation is attempted against the table while it was
	 * being deleted, the service returns status code 409 (Conflict), with
	 * additional error information indicating that the table is being
	 * deleted.</p>
	 * 
	 * @param tableName
	 *            The name of the table to be deleted
	 */
	public abstract boolean deleteTable();

	/**
	 * Inserts a new entity into a table. </p>
	 * 
	 * When inserting an entity into a table, you must specify values for the
	 * <strong>PartitionKey</strong> and <strong>RowKey</strong> system
	 * properties. Together, these properties form the primary key and must be
	 * unique within the table. </p>
	 * 
	 * Both the <strong>PartitionKey</strong> and <strong>RowKey</strong> values
	 * must be string values; each key value may be up to 64 KB in size. If you
	 * are using an integer value for the key value, you should convert the
	 * integer to a fixed-width string, because they are canonically sorted. For
	 * example, you should convert the value 1 to 0000001 to ensure proper
	 * sorting.</p>
	 * 
	 * Reference <a
	 * href="http://msdn.microsoft.com/en-us/library/dd179433.aspx"> Insert
	 * entity </a>
	 * 
	 * @param obj
	 *            The object to be inserted. The entity shoule be instance of
	 *            {@link TableStorageEntity} or subclass of
	 *            {@link TableStorageEntity}
	 * @throws StorageException
	 */
	public abstract void insertEntity(TableStorageEntity obj)
			throws StorageException;

	/**
	 * Updates an existing entity within a table by replacing it.
	 * 
	 * When updating an entity, you must specify the
	 * <strong>PartitionKey</strong> and <strong>RowKey</strong> system
	 * properties as part of the update operation. </p>
	 * 
	 * An entity's ETag provides default optimistic concurrency for update
	 * operations. The ETag value is opaque and should not be read or relied
	 * upon. Before an update operation occurs, the Table service verifies that
	 * the entity's current ETag value is identical to the ETag value included
	 * with the update request. If the values are identical, the Table service
	 * determines that the entity has not been modified since it was retrieved,
	 * and the update operation proceeds. </p>
	 * 
	 * If the entity's ETag differs from that specified with the update request,
	 * the update operation fails with status code 412 (Precondition Failed).
	 * This error indicates that the entity has been changed on the server since
	 * it was retrieved. To resolve this error, retrieve the entity again and
	 * reissue the request. </p>
	 * 
	 * To force an unconditional update operation, set the value of the If-Match
	 * header to the wildcard character (*) on the request. Passing this value
	 * to the operation will override the default optimistic concurrency and
	 * ignore any mismatch in ETag values. </p>
	 * 
	 * If the If-Match header is missing from the request, the service returns
	 * status code 400 (Bad Request). A request malformed in other ways may also
	 * return 400; see Table Service Error Codes for more information. </p>
	 * 
	 * If the request specifies a property with a null value, that property is
	 * ignored, the update proceeds, and the existing entity is replaced. </p>
	 * 
	 * Reference <a
	 * href="http://msdn.microsoft.com/en-us/library/dd179427.aspx"> Update
	 * entity </a>
	 * 
	 * @param obj
	 *            the object to be updated
	 * @throws StorageException
	 * 
	 */
	public abstract void updateEntity(TableStorageEntity obj)
			throws StorageException;

	/**
	 * Updates table entity if the entity is not modified after it is loaded
	 * from azure table storage. TableStorageEntity should have etag value which
	 * is retrieved from table storage.
	 * 
	 * 
	 * @param obj
	 *            the object to be updated
	 * @throws StorageException
	 * 
	 * @see {@link #updateEntity(TableStorageEntity)}
	 */
	public abstract void updateEntityIfNotModified(TableStorageEntity obj)
			throws StorageException;

	/**
	 * Merges table entity </p>
	 * 
	 * Updates an existing entity within a table by merging new property values
	 * into the entity.
	 * 
	 * <h3>Remark</h3>
	 * 
	 * Any properties with null values are ignored by the <strong>Merge
	 * Entity</strong> operation. All other properties will be updated. </p>
	 * 
	 * A property cannot be removed with a Merge Entity operation. To remove a
	 * property from an entity, replace the entity by calling the Update Entity
	 * operation.</p>
	 * 
	 * When merging an entity, you must specify the PartitionKey and RowKey
	 * system properties as part of the merge operation.</p>
	 * 
	 * An entity's ETag provides default optimistic concurrency for merge
	 * operations. The ETag value is opaque and should not be read or relied
	 * upon. Before a merge operation occurs, the Table service verifies that
	 * the entity's current ETag value is identical to the ETag value included
	 * with the request. If the values are identical, the Table service
	 * determines that the entity has not been modified since it was retrieved,
	 * and the merge operation proceeds.</p>
	 * 
	 * If the entity's ETag differs from that specified with the merge request,
	 * the merge operation fails with status code 412 (Precondition Failed).
	 * This error indicates that the entity has been changed on the server since
	 * it was retrieved. To resolve this error, retrieve the entity again and
	 * reissue the request.</p>
	 * 
	 * To force an unconditional merge operation, set the value of the If-Match
	 * header to the wildcard character (*) on the request. Passing this value
	 * to the operation will override the default optimistic concurrency and
	 * ignore any mismatch in ETag values.</p>
	 * 
	 * Reference <a
	 * href="http://msdn.microsoft.com/en-us/library/dd179392.aspx">Merge entity
	 * </a>
	 * 
	 * @param obj
	 *            the object to be merged
	 * @throws StorageException
	 */
	public abstract void mergeEntity(TableStorageEntity obj)
			throws StorageException;

	/**
	 * Deletes an entity within a table </p>
	 * 
	 * When an entity is successfully deleted, the entity is immediately marked
	 * for deletion and is no longer accessible to clients. The entity is later
	 * removed from the Table service during garbage collection.
	 * 
	 * @param obj
	 *            the object to be deleted
	 * @throws StorageException
	 *             If entity is not exists, an exception is also thrown.
	 */
	public abstract void deleteEntity(TableStorageEntity obj)
			throws StorageException;

	/**
	 * Deletes a table entity if the entity is not modified after it is loaded
	 * from azure table storage. TableStorageEntity should have etag value which
	 * is retrieved from table storage.
	 * 
	 * @param obj
	 *            the object to be deleted
	 * @throws StorageException
	 * @see {@link #deleteEntity(TableStorageEntity)}
	 */
	public abstract void deleteEntityIfNotModified(TableStorageEntity obj)
			throws StorageException;

	/**
	 * Load the entity within table by the entity identifier(PartitionKey and
	 * RowKey).
	 * 
	 * @param <T>
	 *            {@link TableStorageEntity} instance of subclass of
	 *            {@link TableStorageEntity} instance
	 * @param entity
	 *            Specified Entity
	 * 
	 * @return Entity within table
	 * @throws StorageException
	 */
	public abstract <T extends TableStorageEntity> T loadEntity(T entity)
			throws StorageException;

	/**
	 * This operation queries entities in a table. A query against a table
	 * returns a list of entities conforming to the criteria specified in the
	 * query.
	 * 
	 * 
	 * @param queryExpression
	 *            If queryExpression is not given, all rows are return.
	 * @return A list of TableStorageEntity
	 * @throws StorageException
	 */
	public abstract List<TableStorageEntity> retrieveEntities(
			String queryExpression) throws StorageException;

	public abstract List<TableStorageEntity> retrieveEntities(final Query query)
			throws StorageException;

	/**
	 * This operation queries entities in a table. A query against a table
	 * returns a list of entities conforming to the criteria specified in the
	 * query.
	 * 
	 * @return A list of TableStorageEntity
	 * @throws StorageException
	 */
	public abstract List<TableStorageEntity> retrieveEntities()
			throws StorageException;

	/**
	 * 
	 * @param partitionKey
	 * @param rowKey
	 * @return A list of entities who's partition key and row key are all euqals
	 *         with the given.
	 * @throws StorageException
	 */
	public abstract List<TableStorageEntity> retrieveEntitiesByKey(
			String partitionKey, String rowKey) throws StorageException;

	/**
	 * Get the base uri of the table service
	 * 
	 * @return
	 */
	public URI getBaseUri() {
		return baseUri;
	}

	/**
	 * Get the name of the storage account
	 * 
	 * @return
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * Get the name of specified table.
	 * 
	 * @return
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Whether use/generate path-style or host-style URIs
	 * 
	 * @return
	 */
	public boolean isUsePathStyleUris() {
		return usePathStyleUris;
	}

	/**
	 * Get the timeout per requeet
	 * 
	 * @return
	 */
	public TimeSpan getTimeout() {
		return timeout;
	}

	/**
	 * Get the retry policy used for retrying requests
	 * 
	 * @return
	 */
	public RetryPolicy getRetryPolicy() {
		return retryPolicy;
	}

	/**
	 * Set timeout per request
	 * 
	 * @param timeout
	 */
	public void setTimeout(TimeSpan timeout) {
		this.timeout = timeout;
	}

	/**
	 * Set the {@link RetryPolicy} userd for retrying requests
	 * 
	 * @param retryPolicy
	 */
	public void setRetryPolicy(RetryPolicy retryPolicy) {
		this.retryPolicy = retryPolicy;
	}

	/**
	 * Get the model class for this table.
	 * 
	 * @return {@link #modelClass}
	 */
	public Class<? extends TableStorageEntity> getModelClass() {
		return modelClass;
	}

	/**
	 * Set model class for this table.
	 * 
	 * @param modelClass
	 * @see {@link #modelClass}
	 */
	public void setModelClass(Class<? extends TableStorageEntity> modelClass) {
		this.modelClass = modelClass;
	}

}
