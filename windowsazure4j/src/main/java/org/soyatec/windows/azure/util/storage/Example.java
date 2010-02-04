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

import java.sql.SQLException;

import org.soyatec.windows.azure.table.ETableColumnType;

public class Example {

	public static void main(String[] args) {
		DbAccountInfo info = DbAccountInfo.createWindowAuthAccount("localhost",
				"SQLEXPRESS", ConnectionFactory.DATABASE_MASTER);
		DatabaseManager client = new DatabaseManager(info);
		Database db = new Database();
		db.setName("dba");
		db.setForceCreate(true);

		DatabaseTable table = new DatabaseTable();
		table.setName("xye");
		TableColumn column = new TableColumn();
		column.setName("PartitionKey");
		column.setType(ETableColumnType.TYPE_STRING);
		table.addColumn(column);

		db.addTable(table);
		try {
			client.createDatabase(db);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
