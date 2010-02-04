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

/**
 * A direct children for TableStorageEntity. It does not add any new properties.
 * </p>
 * 
 * When query entities from table serivce, A list of InternalTableStorageEntity
 * instances is returned if no model class is specified.
 * 
 * 
 */
public final class SimpleTableStorageEntity extends TableStorageEntity {

	/**
	 * Create a new instance with specified partitionKey and rowKey and the
	 * timestamp
	 * 
	 * @param partitionKey
	 * @param rowKey
	 * @param timestamp
	 */
	SimpleTableStorageEntity(String partitionKey, String rowKey,
			Timestamp timestamp) {
		super(partitionKey, rowKey);
		this.timestamp = timestamp;
	}

	/**
	 * Create a new instance with specified partitionKey and rowKey
	 * 
	 * @param partitionKey
	 * @param rowKey
	 */
	SimpleTableStorageEntity(String partitionKey, String rowKey) {
		super(partitionKey, rowKey);
	}
}
