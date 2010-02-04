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

public class UpgradeConfiguration extends Configuration {
	private UpgradeType mode;
	private String upgradRole;
	private String deploymentLabel;
	
	public UpgradeConfiguration() {

	}

	public UpgradeConfiguration( String pkgUrl,
			String configurefileUrl, UpgradeType mode, String upgradRole, String label) {
		this.packageBlobUrl = pkgUrl;
		this.configurationFileUrl = configurefileUrl;
		this.mode = mode;
		this.upgradRole = upgradRole;
		this.deploymentLabel = label;
	}
	
	public void validate() {
		if (isEmpty(deploymentLabel)) {
			throw new IllegalStateException("DeploymentLabel is required!");
		}

		if (isEmpty(packageBlobUrl)) {
			throw new IllegalStateException("Package blob url is required!");
		}

		if (isEmpty(configurationFileUrl)) {
			throw new IllegalStateException("Configuration file is required!");
		}

		if (isEmpty(upgradRole)) {
			throw new IllegalStateException("UpgradeRole is required!");
		}

		readConfigurationContent();
	}
	
	
	/**
	 * @return the label
	 */
	public String getDeploymentLabel() {
		return deploymentLabel;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setDeploymentLabel(String label) {
		this.deploymentLabel = label;
	}
	
	/**
	 * @return the base64Label
	 */
	public String getBase64Label() {
		return Base64.encode(getDeploymentLabel().getBytes());
	}
	public UpgradeType getMode() {
		return mode;
	}

	public void setMode(UpgradeType mode) {
		this.mode = mode;
	}

	public String getUpgradRole() {
		return upgradRole;
	}

	public void setUpgradRole(String upgradRole) {
		this.upgradRole = upgradRole;
	}

}
