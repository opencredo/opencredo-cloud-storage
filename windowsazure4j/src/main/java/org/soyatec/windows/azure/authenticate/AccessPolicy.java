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
package org.soyatec.windows.azure.authenticate;

import org.soyatec.windows.azure.blob.DateTime;

/**
 * @author xiaowei.ye@soyatec.com
 * 
 */
public class AccessPolicy {

	public AccessPolicy() {

	}

	public AccessPolicy(DateTime start, DateTime expiry, int permission) {
		this.start = start;
		this.expiry = expiry;
		this.permission = permission;
	}

	private int permission;
	private DateTime start;
	private DateTime expiry;

	/**
	 * @return the permission
	 */
	public int getPermission() {
		return permission;
	}

	/**
	 * @param permission
	 *            the permission to set
	 */
	public void setPermission(int permission) {
		this.permission = permission;
	}

	/**
	 * @return the start
	 */
	public DateTime getStart() {
		return start;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(DateTime start) {
		this.start = start;
	}

	/**
	 * @return the expiry
	 */
	public DateTime getExpiry() {
		return expiry;
	}

	/**
	 * @param expiry
	 *            the expiry to set
	 */
	public void setExpiry(DateTime expiry) {
		this.expiry = expiry;
	}
}
