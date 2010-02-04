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

public class DbAccountInfo {

	private String userName;
	private String password;
	private String host;
	private String dbName;
	private String port;
	private String instanceName;
	private boolean authByWindowLogin = false;

	public static DbAccountInfo createWindowAuthAccount(final String host,
			String instanceName, String dbName) {
		DbAccountInfo info = new DbAccountInfo();
		info.setAuthByWindowLogin(true);
		info.setHost(host);
		info.setInstanceName(instanceName);
		info.setDbName(dbName);
		return info;
	}

	public static DbAccountInfo createWindowsLocalAccount() {
		return DbAccountInfo.createWindowAuthAccount("localhost", "SQLEXPRESS",
				ConnectionFactory.DATABASE_MASTER);
	}

	public DbAccountInfo(final String userName, final String password,
			String host) {
		this.userName = userName;
		this.password = password;
		this.host = host;
	}

	public DbAccountInfo() {

	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public boolean isAuthByWindowLogin() {
		return authByWindowLogin;
	}

	public void setAuthByWindowLogin(boolean authInWindows) {
		this.authByWindowLogin = authInWindows;
	}

	public String getConnectString() {
		if (isAuthByWindowLogin()) {
			return String
					.format(
							"jdbc:sqlserver://%s%s\\%s;integratedSecurity=true;databaseName=%s;",
							getHost(),
							getPort() == null ? "" : ":" + getPort(),
							getInstanceName(), getDbName());
		} else {
			return String
					.format(
							"jdbc:sqlserver://%s%s;user=%s;password=%s;databaseName=%s;",
							getHost(),
							getPort() == null ? "" : ":" + getPort(),
							getUserName(), getPassword(), getDbName());
		}
	}

}
