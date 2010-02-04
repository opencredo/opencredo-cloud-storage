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

import java.util.List;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.soyatec.windows.azure.util.ssl.SslUtil;

public abstract class ServiceManagement {

	private final String subscriptionId;
	private final SSLSocketFactory sslSocketFactory;
	
	private boolean blocking = false;

	public ServiceManagement(String subscriptionId, String keyStoreFile,
			String keyStorePassword, String trustStoreFile,
			String trustStorePassword, String certificateAlias)
			throws Exception {
		this.subscriptionId = subscriptionId;
		this.sslSocketFactory = SslUtil.createSSLSocketFactory(keyStoreFile,
				keyStorePassword, trustStoreFile, trustStorePassword,
				certificateAlias);
	}


	public boolean isBlocking() {
		return blocking;
	}

	public void setBlocking(boolean blocking) {
		this.blocking = blocking;
	}

	/**
	 * @return the subscriptionId
	 */
	public String getSubscriptionId() {
		return subscriptionId;
	}

	/**
	 * @return the sslSocketFactory
	 */
	SSLSocketFactory getSslSocketFactory() {
		return sslSocketFactory;
	}

	String getBaseUrl() {
		return "https://management.core.windows.net:443/" + subscriptionId;
	}

	public abstract List<HostService> listHostedServices();

	/**
	 * The Create Deployment operation uploads a new service package and creates
	 * a new deployment on staging or production.
	 * 
	 * </p>
	 * 
	 * Note that it is possible to call Create Deployment only for a hosted
	 * service that has previously been created via the Windows Azure Developer
	 * Portal. You cannot upload a new hosted service via the Service Management
	 * API.
	 * 
	 * </p>
	 * 
	 * The Create Deployment operation is an asynchronous operation. To
	 * determine whether the management service has finished processing the
	 * request, call Get Operation Status.
	 * 
	 * @param serviceName
	 * @param deploySlotName
	 * @param define
	 * @return
	 */
	public abstract String createDeployment(String serviceName,
			DeploymentSlotType deploySlotName, DeploymentConfiguration define,
			AsyncResultCallback callback);

	public abstract Deployment getDeployment(String serviceName,
			DeploymentSlotType type);

	public abstract Deployment getDeployment(String serviceName,
			String deploymentName);

	public abstract String deleteDeployment(String serviceName,
			DeploymentSlotType type, AsyncResultCallback callback);

	public abstract String deleteDeployment(String serviceName,
			String deploymentName, AsyncResultCallback callback);

	public abstract HostedServiceProperties getHostedServiceProperties(
			String serviceName, boolean embedDetail);

	public abstract OperationStatus getOperationStatus(String requestId);

	/**
	 * The List Storage Accounts operation lists the storage accounts available
	 * under the current subscription.
	 * 
	 * @return
	 */
	public abstract List<StorageService> listStorageAccounts();

	public abstract List<AffinityGroup> listAffinityGroups();

	public abstract StorageServiceKeys getStorageAccountKeys(String serviceName);

	/**
	 * The Regenerate Keys operation regenerates the primary or secondary access
	 * key for the specified storage account.
	 * 
	 * @param serviceName
	 * @param type
	 * @return
	 */
	public abstract StorageServiceKeys regenerateKeys(String serviceName,
			KeyType type);

	public abstract StorageServiceProperties getStorageAccountProperties(
			String serviceName);

	public abstract AffinityGroupProperties getAffinityGroupProperties(
			String groupName);

	public abstract String updateDeplymentStatus(String serviceName,
			DeploymentSlotType type, UpdateStatus status,
			AsyncResultCallback callback);

	public abstract String updateDeplymentStatus(String serviceName,
			String deploymentName, UpdateStatus status,
			AsyncResultCallback callback);

	public abstract String upgradeDeployment(String serviceName,
			DeploymentSlotType type, UpgradeConfiguration configuration,
			AsyncResultCallback callback);

	public abstract String upgradeDeployment(String serviceName,
			String deploymentName, UpgradeConfiguration configuration,
			AsyncResultCallback callback);

	public abstract String walkUpgradeDomain(String serviceName,
			String deploymentName, String domainId, AsyncResultCallback callback);

	public abstract String walkUpgradeDomain(String serviceName,
			DeploymentSlotType type, String domainId,
			AsyncResultCallback callback);

	public abstract String changeDeploymentConfiguration(String serviceName,
			DeploymentSlotType type, String configurationFileUrl,
			AsyncResultCallback callback);

	public abstract String changeDeploymentConfiguration(String serviceName,
			String deploymentName, String configurationFileUrl,
			AsyncResultCallback callback);

	public abstract String swapDeployment(String serviceName,
			String productName, String sourceName, AsyncResultCallback callback);
}
