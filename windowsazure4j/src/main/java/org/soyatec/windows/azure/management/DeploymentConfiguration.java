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

import org.soyatec.windows.azure.authenticate.Base64;

/**
 * 
 * @author xiaowei.ye@soyatec.com
 * 
 */
public class DeploymentConfiguration extends Configuration {

	public DeploymentConfiguration() {

	}

	public DeploymentConfiguration(String name, String pkgUrl,
			String configurefileUrl, String label) {
		this.name = name;
		this.packageBlobUrl = pkgUrl;
		this.configurationFileUrl = configurefileUrl;
		this.label = label;
	}

	private String name;
	
	private String label;

	public void validate() {
		if (isEmpty(name)) {
			throw new IllegalStateException("Name is required!");
		}

		if (isEmpty(packageBlobUrl)) {
			throw new IllegalStateException("Package blob url is required!");
		}

		if (isEmpty(configurationFileUrl)) {
			throw new IllegalStateException("Configuration file is required!");
		}

		if (isEmpty(label)) {
			throw new IllegalStateException("Label is required!");
		}

		readConfigurationContent();
	}
	

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * @return the base64Label
	 */
	public String getBase64Label() {
		return Base64.encode(getLabel().getBytes());
	}

}
