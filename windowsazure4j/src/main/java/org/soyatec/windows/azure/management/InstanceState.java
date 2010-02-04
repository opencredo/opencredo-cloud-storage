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

public enum InstanceState {
	Unknown("Unknown"), Initialized("Initialized"), Created("Created"), Started(
			"Started"), Stopping("Stopping"), Stopped("Stopped"), Aborted(
			"Aborted"), Unresponsive("Unresponsive"), Unhealthy("Unhealthy"), Suspended(
			"Suspended"), Teardown("Teardown"), Destroyed("Destroyed"), Busy(
			"Busy"), Alerted("Alerted");

	private final String literal;

	InstanceState(String value) {
		this.literal = value;
	}

	/**
	 * @return the literal
	 */
	public String getLiteral() {
		return literal;
	}
}
