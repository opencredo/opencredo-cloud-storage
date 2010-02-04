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
package org.soyatec.windows.azure.management;

public class StorageServiceKeys {
	private String primaryKey;
	private String secondaryKey;

	public StorageServiceKeys(String primaryKey, String secondaryKey) {

		this.primaryKey = primaryKey;
		this.secondaryKey = secondaryKey;
	}

	public StorageServiceKeys() {

	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getSecondaryKey() {
		return secondaryKey;
	}

	public void setSecondaryKey(String secondaryKey) {
		this.secondaryKey = secondaryKey;
	}

	public String getKey(String type) {
		if (type.equals(KeyType.Primary))
			return this.primaryKey;
		else if (type.equals(KeyType.Secondary))
			return this.secondaryKey;
		return "";
	}
}
