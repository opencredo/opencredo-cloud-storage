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

/**
 * This type represents the different constituent parts that make up a resource Uri in the context of cloud services.
 * 
 */
public class ResourceUriComponents {

	/**
	 * The account name in the URI.
	 */
	private String accountName;

	/**
	 * This is really the first component (delimited by '/') after the account name. </p> Since it happens to be a container name in the context of all our storage services (containers in blob storage,</p> queues in the queue service and table names in table storage), it's named as ContainerName to make it more </p> readable at the cost of slightly being incorrectly named.
	 */
	private String containerName;

	/**
	 * The remaining string in the URI.
	 */
	private String remainingPart;

	/**
	 * Construct a ResourceUriComponents object.
	 */
	public ResourceUriComponents() {

	}

	/**
	 * Construct a ResourceUriComponents object.
	 * 
	 * @param accountName
	 *            The account name that should become part of the URI.
	 */
	public ResourceUriComponents(String accountName) {
		this(accountName, null, null);
	}

	/**
	 * Construct a ResourceUriComponents object.
	 * 
	 * @param accountName
	 *            The account name that should become part of the URI.
	 * @param containerName
	 *            The container name (container, queue or table name) that should become part of the URI.
	 */
	public ResourceUriComponents(String accountName, String containerName) {
		this(accountName, containerName, null);
	}

	/**
	 * Construct a ResourceUriComponents object.
	 * 
	 * @param accountName
	 *            The account name that should become part of the URI.
	 * @param containerName
	 *            The container name (container, queue or table name) that should become part of the URI.
	 * @param remainingPart
	 *            Remaining part of the URI.
	 */
	public ResourceUriComponents(String accountName, String containerName, String remainingPart) {
		this.accountName = accountName;
		this.containerName = containerName;
		this.remainingPart = remainingPart;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getRemainingPart() {
		return remainingPart;
	}

	public void setRemainingPart(String remainingPart) {
		this.remainingPart = remainingPart;
	}

}
