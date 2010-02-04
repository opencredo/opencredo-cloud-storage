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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.soyatec.windows.azure.table.ETableColumnType;
import org.soyatec.windows.azure.table.TableStorageEntity;
import org.soyatec.windows.azure.util.xml.AtomUtil;

@SuppressWarnings("unchecked")
public class DevelopmentStorageUtil {

	public static Database createTableSchema(String databaseName, Class clazz) {
		if (!TableStorageEntity.class.isAssignableFrom(clazz)) {
			throw new RuntimeException(
					"input class must be a subclass of the TableStorageEntity");
		}
		DatabaseTable table = new DatabaseTable();
		table.setName(databaseName);

		createSchemaForSingle(clazz, table);

		Database db = new Database();
		db.addTable(table);
		return db;
	}

	public static DatabaseTable createSchema(String tableName, Class klass) {
		DatabaseTable table = new DatabaseTable();
		table.setName(tableName);

		Class superClass = klass.getSuperclass();
		createSchemaForSingle(klass, table);
		while (superClass != null) {
			createSchemaForSingle(superClass, table);
			superClass = superClass.getSuperclass();
		}
		return table;
	}

	private static void createSchemaForSingle(Class klass, DatabaseTable table) {
		Field[] fields = klass.getDeclaredFields();
		for (Field f : fields) {
			if (Modifier.isTransient(f.getModifiers()) || f.isSynthetic()) {
				continue;
			}
			TableColumn column = new TableColumn();
			String filedName = f.getName();
			String name = decorateStorageTableColumnName(filedName);
			column.setName(name);
			ETableColumnType type = AtomUtil.getFieldType(f);
			column.setType(type);
			table.addColumn(column);
		}
	}

	/**
	 * This is must. Or the table can't be recognized as the azure table.
	 * 
	 * @param initName
	 * @return
	 */
	private static String decorateStorageTableColumnName(String initName) {
		String name = initName;
		if (initName.equalsIgnoreCase("PartitionKey")) {
			name = "PartitionKey";
		}

		if (initName.equalsIgnoreCase("RowKey")) {
			name = "RowKey";
		}

		if (initName.equalsIgnoreCase("timestamp")) {
			name = "Timestamp";
		}
		return name;
	}

	public static Database createTableSchema(Class clazz) {
		return createTableSchema(clazz.getSimpleName(), clazz);
	}
}
