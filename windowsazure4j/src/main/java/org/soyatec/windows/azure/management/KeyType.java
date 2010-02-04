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

/**
 * The Regenerate Keys operation regenerates the primary or secondary access key
 * for the specified storage account.
 * 
 * @author xiaowei.ye@soyatec.com
 * 
 */
public enum KeyType {
	
	Primary("Primary"), Secondary("Secondary"), All("Primary|Secondary");

	private String literal;

	KeyType(String value) {
		this.literal = value;
	}

	/**
	 * @return the literal
	 */
	public String getLiteral() {
		return literal;
	}

}
