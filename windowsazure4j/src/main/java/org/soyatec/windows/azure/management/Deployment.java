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

import java.util.ArrayList;
import java.util.List;

public class Deployment {

	private String name;
	private String privateId;
	private String deploymentSlot;

	private String status;
	private String label;
	private String url;
	private String configuration;
	private List<RoleInstance> roleInstances;
	private UpgradeStatus upgradeStatus;

	public String getDeploymentSlot() {
		return deploymentSlot;
	}

	public void setDeploymentSlot(String deploymentSlot) {
		this.deploymentSlot = deploymentSlot;
	}

	public UpgradeStatus getUpgradeStatus() {
		return upgradeStatus;
	}

	public void setUpgradeStatus(UpgradeStatus upgradeStatus) {
		this.upgradeStatus = upgradeStatus;
	}

	public void addRoleInstance(RoleInstance role) {
		if (roleInstances == null)
			roleInstances = new ArrayList<RoleInstance>();
		roleInstances.add(role);
	}

	public List<RoleInstance> getRoleInstances() {
		return roleInstances;
	}

	public void setRoleInstances(List<RoleInstance> roleInstances) {
		this.roleInstances = roleInstances;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrivateId() {
		return privateId;
	}

	public void setPrivateId(String privateId) {
		this.privateId = privateId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

}
