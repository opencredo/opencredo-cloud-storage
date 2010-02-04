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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * @author xiaowei.ye (xiaowei.ye@soyatec.com)
 * 
 */
public class ConnectionFactory {

	public static final String DATABASE_MASTER = "master";

	/**
	 * Every time get new connection instance.
	 * 
	 * @param info
	 * @return
	 * @throws SQLException
	 */
	public static Connection connect(DbAccountInfo info) throws SQLException {
		if (info == null)
			throw new IllegalArgumentException("Connect info");

		// Establish the connection.
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return DriverManager.getConnection(info.getConnectString());
	}
}
