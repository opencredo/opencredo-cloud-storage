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
 */
public class DatabaseTable {

	private Database database;

	private String name;

	private List<TableColumn> tableColumns = new ArrayList<TableColumn>();

	public Database getDatabase() {
		return database;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TableColumn> getTableColumns() {
		return tableColumns;
	}

	public void addColumn(final TableColumn column) {
		if (column == null || tableColumns.contains(column)) {
			return;
		}
		tableColumns.add(column);
	}

	public void removeColumn(final TableColumn column) {
		if (column == null) {
			return;
		}
		tableColumns.remove(column);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("table: " + name).append("\n");

		for (TableColumn column : getTableColumns()) {
			sb.append(column.toString()).append("\n");
		}
		return sb.toString();
	}

}
