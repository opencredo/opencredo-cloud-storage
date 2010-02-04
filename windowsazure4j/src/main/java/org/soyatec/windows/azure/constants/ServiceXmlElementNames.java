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
package org.soyatec.windows.azure.constants;

public final class ServiceXmlElementNames {

	public static final String HostedServices = "HostedServices";

	public static final String HostedService = "HostedService";

	public static final String HostedService_Url = "Url";

	public static final String HostedService_Name = "ServiceName";

	public static final String StorageServices = "StorageServices";

	public static final String StorageService = "StorageService";

	public static final String StorageService_Url = "Url";

	public static final String StorageService_Name = "ServiceName";

	public static final String StorageServiceKeys = "StorageServiceKeys";
	
	public static final String HostedServiceProperties = "HostedServiceProperties";

	public static final String Key_Primary = "Primary";

	public static final String Key_Secondary = "Secondary";

	public static final String StorageServiceProperties = "StorageServiceProperties";

	public static final String Label = "Label";
	
	public static final String Description = "Description";
	/**
	 * Constants for affinity group
	 */
	public static final String AffinityGroups = "AffinityGroups";
	public static final String AffinityGroup = "AffinityGroup";
	public static final String AffinityGroupName = "Name";
	public static final String AffinityGroupDescription = "Description";
	public static final String AffinityGroupLocation = "Location";

	/*
	 * Constatns for deployment
	 */
	public static final String Deployment = "Deployment";
	public static final String DeploymentName = "Name";
	public static final String DeploymentSlot = "DeploymentSlot";
	public static final String DeploymentPrivateID = "PrivateID";
	public static final String DeploymentLabel = "Label";
	public static final String DeploymentUrl = "Url";
	public static final String DeploymentConfiguration = "Configuration";
	public static final String DeploymentStatus = "Status";
	public static final String DeploymentUpgradeStatus = "UpgradeStatus";
	public static final String DeploymentUpgradeStatusUpgradeType = "UpgradeType";
	public static final String DeploymentUpgradeStatusCurrentUpgradeDomainState = "CurrentUpgradeDomainState";
	public static final String DeploymentUpgradeStatusCurrentUpgradeDomain = "CurrentUpgradeDomain";
	public static final String DeploymentRoleInstanceList = "RoleInstanceList";
	public static final String DeploymentRoleInstance = "RoleInstance";
	public static final String DeploymentRoleInstanceRoleName = "RoleName";
	public static final String DeploymentRoleInstanceInstanceName = "InstanceName";
	public static final String DeploymentRoleInstanceInstanceState = "InstanceStatus";

	/*
	 * Operation status constants
	 */
	public static final String OperationStatus = "OperationStatus";
	public static final String OperationStatusName = "Operation";
	public static final String OperationStatusId = "ID";
	public static final String OperationStatusStatus = "Status";
	public static final String OperationStatusHTTPCode = "HTTPCode";
	public static final String OperationStatusHTTPError = "Error";
	public static final String OperationStatusHTTPErrorCode = "Code";
	public static final String OperationStatusHTTPErrorMessage = "Message";
}
