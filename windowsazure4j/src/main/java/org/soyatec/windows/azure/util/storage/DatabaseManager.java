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

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

import org.soyatec.windows.azure.table.ETableColumnType;
import org.soyatec.windows.azure.util.Utilities;

public class DatabaseManager {

	private final static String SQL_QUOTE = "\"";

	private final DbAccountInfo info;

	public DatabaseManager(DbAccountInfo info) {
		if (info == null) {
			throw new IllegalArgumentException("Database account");
		}
		this.info = info;
	}

	private static final String CREATE_DATABASE_SQL = "DECLARE @device_directory NVARCHAR(520) SELECT @device_directory = SUBSTRING(filename, 1, CHARINDEX(N'MASTER.MDF', LOWER(filename)) - 1) FROM master.dbo.sysaltfiles WHERE dbid = 1 AND fileid = 1  EXEC (N'CREATE DATABASE [?] ON PRIMARY (NAME = N''?'', FILENAME = N''' + @device_directory + N'?.mdf'') LOG ON (NAME = N''dna_log'',  FILENAME = N''' + @device_directory + N'dna_log.ldf'') COLLATE Latin1_General_BIN')";

	public void createDatabase(Database database) throws SQLException {
		if (database == null)
			throw new IllegalArgumentException("database");
		createDatabaseImpl(database);
		// create tables
		if (database.getTables().isEmpty())
			return;
		createTablesInDatabase(database);
	}

	private void createDatabaseImpl(Database database) throws SQLException {
		Connection connection = ConnectionFactory.connect(info);
		boolean databaseExist = doesDatabaseExist(connection, database
				.getName());
		if (databaseExist) {
			if (database.isForceCreate()) {
				dropDatabase(connection, database.getName());
				doCreateDatabase(connection, database.getName());
			}
		} else {
			doCreateDatabase(connection, database.getName());
		}
		connection.close();
	}

	private void doCreateDatabase(final Connection connection,
			final String databaseName) throws SQLException {
		Statement statement = connection.createStatement();
		statement.executeUpdate(CREATE_DATABASE_SQL.replaceAll("\\?",
				databaseName));
		statement.close();
	}

	private boolean doesDatabaseExist(final Connection connection,
			String databaseName) throws SQLException {
		PreparedStatement prepareStatement = connection
				.prepareStatement("select name as Database_Name from sys.databases where name = ?");
		prepareStatement.setString(1, databaseName);
		ResultSet resultSet = prepareStatement.executeQuery();
		boolean result = resultSet != null && resultSet.next();
		resultSet.close();
		prepareStatement.close();
		return result;
	}

	private void dropDatabase(final Connection connection, String databaseName)
			throws SQLException {
		String sql = "IF  EXISTS (SELECT NAME FROM SYS.DATABASES WHERE NAME = N'?')DROP DATABASE [?]";
		String test = "SELECT filename = SUBSTRING(filename, 1, CHARINDEX(N'MASTER.MDF', LOWER(filename)) - 1)FROM master.dbo.sysaltfiles WHERE DBID = 1 AND FILEID = 1 ";
		PreparedStatement prepareStatement = connection.prepareStatement(sql
				.replaceAll("\\?", databaseName));
		prepareStatement.executeUpdate();
		prepareStatement.close();
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(test);
		if (rs.next()) {
			String databaseFilePath = rs.getString(1);
			if (!Utilities.isNullOrEmpty(databaseFilePath)) {
				deleteFile(databaseFilePath + databaseName + ".mdf");
				deleteFile(databaseFilePath + databaseName + "_log.ldf");
			}
		}
		rs.close();
		statement.close();
	}

	private void deleteFile(String mdfPath) {
		File file = new File(mdfPath);
		if (file.exists() && file.isFile())
			file.delete();
	}

	private void createTablesInDatabase(Database database) throws SQLException {
		info.setDbName(database.getName());
		// get connection
		Connection connection = ConnectionFactory.connect(info);
		Statement statement = connection.createStatement();
		List<DatabaseTable> tableList = Collections.unmodifiableList(database
				.getTables());
		for (DatabaseTable table : tableList) {
			StringBuffer createTableSQL = new StringBuffer();
			createTableSQL.append("CREATE TABLE ");
			createTableSQL.append(SQL_QUOTE + table.getName() + SQL_QUOTE);
			createTableSQL.append("(");
			// create column
			for (TableColumn column : table.getTableColumns()) {
				createTableSQL.append(column.getName()).append(" ").append(
						getSQLTypeFrom(column.getType()));
				if (column.getName().equals("PartitionKey")
						|| column.getName().equals("RowKey")) {
					createTableSQL.append(" NOT NULL");
				}
				createTableSQL.append(",");
			}
			String primaryKeyString = "CONSTRAINT [PK_"
					+ table.getName()
					+ "] PRIMARY KEY CLUSTERED ([PartitionKey] ASC,[RowKey] ASC )) ON [PRIMARY]";
			createTableSQL.append(primaryKeyString);
			// createTableSQL.append(")");
			System.out.println("---------SQL---------------");
			System.out.println(createTableSQL.toString());
			statement.executeUpdate(createTableSQL.toString());
		}
		statement.close();
		connection.close();
	}

	private String getSQLTypeFrom(ETableColumnType tableColumnType) {
		return tableColumnType.getSqlType();
	}
}
