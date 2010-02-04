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

public enum DeploymentStatus {
	
	Deleting("Deleting"), Deploying("Deploying"), Running("Running"), RunningTransitioning(
			"RunningTransitioning"), Starting("Starting"), Suspended(
			"Suspended"), SuspendedTransitioning("SuspendedTransitioning"), Suspending(
			"Suspending");
	
	private final String literal;

	DeploymentStatus(String value) {
		this.literal = value;
	}

	/**
	 * @return the literal
	 */
	public String getLiteral() {
		return literal;
	}

	// Fields
	// public static final String Deleting = "Deleting";
	// public static final String Deploying = "Deploying";
	// public static final String Running = "Running";
	// public static final String RunningTransitioning = "RunningTransitioning";
	// public static final String Starting = "Starting";
	// public static final String Suspended = "Suspended";
	// public static final String SuspendedTransitioning =
	// "SuspendedTransitioning";
	// public static final String Suspending = "Suspending";

}
