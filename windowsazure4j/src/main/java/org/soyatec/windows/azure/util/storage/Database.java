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
package org.soyatec.windows.azure.util.storage;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author xiaowei.ye (xiaowei.ye@soyatec.com)
 * 
 */
public class Database {

	private String name;
	private boolean forceCreate;
	private List<DatabaseTable> tables = new ArrayList<DatabaseTable>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isForceCreate() {
		return forceCreate;
	}

	public void setForceCreate(boolean forceCreate) {
		this.forceCreate = forceCreate;
	}

	public List<DatabaseTable> getTables() {
		return tables;
	}

	public void addTable(final DatabaseTable table) {
		if (table == null || tables.contains(table)) {
			return;
		}
		tables.add(table);
	}

	public void removeTable(final DatabaseTable table) {
		if (table == null) {
			return;
		}
		tables.remove(table);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("database: " + name).append("\n");

		for (DatabaseTable table : getTables()) {
			sb.append(table.toString()).append("\n");
		}
		return sb.toString();
	}
}
