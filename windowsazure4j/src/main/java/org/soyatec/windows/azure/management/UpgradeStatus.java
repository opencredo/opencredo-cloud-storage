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

public class UpgradeStatus {
	private UpgradeType upgradeType;
	private CurrentUpgradeDomainState currentUpgradeDomainState;
	private String currentUpgradeDomain;

	public UpgradeType getUpgradeType() {
		return upgradeType;
	}

	public void setUpgradeType(UpgradeType upgradeType) {
		this.upgradeType = upgradeType;
	}

	public CurrentUpgradeDomainState getCurrentUpgradeDomainState() {
		return currentUpgradeDomainState;
	}

	public void setCurrentUpgradeDomainState(
			CurrentUpgradeDomainState currentUpgradeDomainState) {
		this.currentUpgradeDomainState = currentUpgradeDomainState;
	}

	public String getCurrentUpgradeDomain() {
		return currentUpgradeDomain;
	}

	public void setCurrentUpgradeDomain(String currentUpgradeDomain) {
		this.currentUpgradeDomain = currentUpgradeDomain;
	}

}
