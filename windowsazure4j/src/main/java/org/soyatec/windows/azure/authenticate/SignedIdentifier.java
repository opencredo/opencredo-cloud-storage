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
 * 
 * Represents a sing identifier for shared access url.
 * 
 * @author xiaowei.ye@soyatec.com
 * 
 */
public class SignedIdentifier {

	private String id;

	private AccessPolicy policy;

	public SignedIdentifier() {

	}

	/**
	 * 
	 * @param id
	 *            Id for the signed identifier
	 * @param permission
	 *            Signed permissions - read (r), write (w), delete (d) and list
	 *            (l)
	 * @param start
	 *            The time at which the Shared Access Signature becomes valid.
	 * @param expiry
	 *            The time at which the Shared Access Signature becomes invalid.
	 */
	public SignedIdentifier(final String id, final int permission,
			final DateTime start, final DateTime expiry) {
		this.id = id;
		this.policy = new AccessPolicy(start, expiry, permission);
	}

	/**
	 * @return the policy
	 */
	public AccessPolicy getPolicy() {
		return policy;
	}

	/**
	 * @param policy
	 *            the policy to set
	 */
	public void setPolicy(AccessPolicy policy) {
		this.policy = policy;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

}
