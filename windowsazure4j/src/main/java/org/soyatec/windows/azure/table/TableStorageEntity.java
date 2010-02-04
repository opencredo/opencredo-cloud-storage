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

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * This class represents an entity (row) in a table in table storage. </p>
 * 
 * Two entieis are considered to same when they have same PartitionKey and same
 * RowKey in azure table service. This feature is used when update/merge/load
 * entity from table service.
 * 
 */
public abstract class TableStorageEntity {

	protected Timestamp timestamp;

	/**
	 * The partition key of a table entity. The concatenation of the partition
	 * key and row key must be unique per table.
	 */
	protected String partitionKey;

	/**
	 * The row key of a table entity.
	 */
	protected String rowKey;

	/**
	 * Etag property</p>
	 * 
	 * A property generaged by table service when insert/modify the entity
	 * within table service.
	 * 
	 * It should not be setting by users.
	 * 
	 */
	protected String eTag;

	protected transient List<AzureTableColumn> values;

	public TableStorageEntity(String partitionKey, String rowKey) {
		this.timestamp = new Timestamp(new Date().getTime());
		this.partitionKey = partitionKey;
		this.rowKey = rowKey;
	}

	public List<AzureTableColumn> getValues() {
		return values;
	}

	public void setValues(List<AzureTableColumn> values) {
		this.values = values;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getPartitionKey() {
		return partitionKey;
	}

	public void setPartitionKey(String partitionKey) {
		this.partitionKey = partitionKey;
	}

	public String getRowKey() {
		return rowKey;
	}

	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}

	public String getETag() {
		return eTag;
	}

	void setETag(String tag) {
		eTag = tag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eTag == null) ? 0 : eTag.hashCode());
		result = prime * result
				+ ((partitionKey == null) ? 0 : partitionKey.hashCode());
		result = prime * result + ((rowKey == null) ? 0 : rowKey.hashCode());
		result = prime * result
				+ ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableStorageEntity other = (TableStorageEntity) obj;
		if (eTag == null) {
			if (other.eTag != null)
				return false;
		} else if (!eTag.equals(other.eTag))
			return false;
		if (partitionKey == null) {
			if (other.partitionKey != null)
				return false;
		} else if (!partitionKey.equals(other.partitionKey))
			return false;
		if (rowKey == null) {
			if (other.rowKey != null)
				return false;
		} else if (!rowKey.equals(other.rowKey))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	/**
	 * Different with equals(). Two entieis are considered to same when they
	 * have same PartitionKey and same RowKey in azure table service. This
	 * feature is used when update/merge/load entity from table service.
	 * 
	 * @param entity
	 * @return True only if the entity's PartitionKey and RowKey are all same
	 *         with given entity.
	 */
	public boolean sameEntity(final TableStorageEntity entity) {
		if (entity == null) {
			return false;
		}

		if (this.equals(entity)) {
			return true;
		}

		if (getPartitionKey() == null || getRowKey() == null)
			return false;
		if (entity.getPartitionKey() == null || entity.getRowKey() == null)
			return false;

		return getPartitionKey().equals(entity.getPartitionKey())
				&& getRowKey().equals(entity.getRowKey());
	}

}
