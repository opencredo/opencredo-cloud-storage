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

/**
 * <a href="http://msdn.microsoft.com/en-us/library/dd894038.aspx">Batch
 * operation on azure table service</a> </p>
 * 
 * The 2009-04-14 version of the Table service supports batch transactions on
 * entities that are in the same table and belong to the same partition group.
 * Multiple Insert Entity, Update Entity, Merge Entity, and Delete Entity
 * operations are supported within a single transaction. </p>
 * 
 * All entities subject to operations as part of the batch must have the same
 * PartitionKey value. An entity can appear only once in the transaction, and
 * only one operation may be performed against it. The transaction can include
 * at most 100 entities, and its total payload may be no more than 4 MB in size.
 * 
 */
public interface BatchStorage {

	/**
	 * batch atom xml boundary prefix
	 */
	public static final String BATCH_BOUNDARY_PREFIX = "batch_";

	/**
	 * batch atom xml changeset boundary prefix
	 */
	public static final String CHANGESET_BOUNDARY_PREFIX = "changeset_";

	public static final String BATCH_PATH = "$batch";

	/**
	 * Starts a new batch operation set. Don't make call to service.
	 */
	void startBatch();

	/**
	 * Cleanup current batch
	 */
	void clearBatch();

	/**
	 * Commit current batch. Make call to table service. Whether the batch
	 * operation is success or fail, current batch will be cleaned.
	 */
	void executeBatch();

	/**
	 * Is there a current batch?
	 * 
	 * @return
	 */
	boolean isInBatch();
}
