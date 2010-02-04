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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.List;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.soyatec.windows.azure.authenticate.Base64;
import org.soyatec.windows.azure.constants.ConstChars;
import org.soyatec.windows.azure.constants.HeaderNames;
import org.soyatec.windows.azure.constants.HttpMethod;
import org.soyatec.windows.azure.error.StorageException;
import org.soyatec.windows.azure.internal.HttpWebResponse;
import org.soyatec.windows.azure.util.HttpUtilities;
import org.soyatec.windows.azure.util.Utilities;
import org.soyatec.windows.azure.util.xml.XPathQueryHelper;

/**
 * See http://msdn.microsoft.com/en-us/library/ee460799.aspx for description.
 * 
 */
public class ServiceManagementRest extends ServiceManagement {

	private static final String APPLICATION_XML = "application/xml";

	private static final String MEIDA_TYPE_TEXT_XML = "text/xml";

	private static final String KEYS_ACTION_REGENERATE = "/keys?action=regenerate";
	private static final String SERVICE_KEYS = "/keys";
	private static final String SERVICES_AFFINITYGROUPS = "/affinitygroups";
	private static final String SERVICES_HOSTEDSERVICES = "/services/hostedservices";
	private static final String SERVICES_STORAGESERVICE = "/services/storageservices";
	private static final String DEPLOYMENT_SLOTS = "/deploymentslots";
	private static final String DEPLOYMENTS = "/deployments";

	private static final String OPERATIONS = "/operations";

	private static final String CREATE_DEPLOYMENT_BODY = "<?xml version=\"1.0\"?><CreateDeployment xmlns=\"http://schemas.microsoft.com/windowsazure\"><Name>{0}</Name><PackageUrl>{1}</PackageUrl><Label>{3}</Label><Configuration>{2}</Configuration></CreateDeployment>";

	public ServiceManagementRest(String subscriptionId, String keyStoreFile,
			String keyStorePassword, String trustStoreFile,
			String trustStorePassword, String certificateAlias)
			throws Exception {
		super(subscriptionId, keyStoreFile, keyStorePassword, trustStoreFile,
				trustStorePassword, certificateAlias);
	}

	/**
	 * The List Hosted Services operation lists the hosted services available
	 * under the current subscription.
	 */
	@Override
	public List<HostService> listHostedServices() {
		HttpRequest request = HttpUtilities
				.createServiceHttpRequest(URI.create(getBaseUrl()
						+ SERVICES_HOSTEDSERVICES), HttpMethod.Get);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper.parseHostServiceResponse(response
						.getStream());
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * The Get Deployment operation returns configuration information, status,
	 * and system properties for the specified deployment.
	 */
	public Deployment getDeployment(String serviceName, DeploymentSlotType type) {
		HttpRequest request = createHttpRequest(HttpMethod.Get, serviceName,
				type, "");
		return getDeployment(request);
	}

	public Deployment getDeployment(String serviceName, String deploymentName) {
		HttpRequest request = createHttpRequest(HttpMethod.Get, serviceName,
				deploymentName, "");
		return getDeployment(request);
	}

	private Deployment getDeployment(HttpRequest request) {
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());

			if (isRequestAccepted(response)) {
				return XPathQueryHelper.parseDeploymentResponse(response
						.getStream());
			}else if (response.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
				return null;
			}  else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * The Update Deployment Status operation initiates a change in deployment
	 * status. The Update Deployment Status operation is an asynchronous
	 * operation.
	 */
	public String updateDeplymentStatus(String serviceName,
			DeploymentSlotType type, UpdateStatus status,
			AsyncResultCallback callback) {
		HttpRequest request = createHttpRequest(HttpMethod.Post, serviceName,
				type, "/?comp=status");
		return updateDeplymentStatus(request, status, callback);
	}

	private HttpRequest createHttpRequest(String method, String serviceName,
			DeploymentSlotType type, String params) {
		HttpRequest request = HttpUtilities.createServiceHttpRequest(URI
				.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName + DEPLOYMENT_SLOTS
						+ ConstChars.Slash + type.getLiteral().toLowerCase()
						+ params), method);
		return request;
	}

	private HttpRequest createHttpRequest(String method, String serviceName,
			String deploymentName, String params) {
		HttpRequest request = HttpUtilities.createServiceHttpRequest(URI
				.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName + DEPLOYMENTS
						+ ConstChars.Slash + deploymentName + params), method);
		return request;
	}

	public String updateDeplymentStatus(String serviceName,
			String deploymentName, UpdateStatus status,
			AsyncResultCallback callback) {
		HttpRequest request = createHttpRequest(HttpMethod.Post, serviceName,
				deploymentName, "/?comp=status");
		return updateDeplymentStatus(request, status, callback);
	}

	private String changeDeploymentConfiguration(HttpRequest request,
			String configurationFileUrl, AsyncResultCallback callback) {
		String template = "<ChangeConfiguration xmlns=\"http://schemas.microsoft.com/windowsazure\"><Configuration>{0}</Configuration></ChangeConfiguration>";
		String configurationFile = readBase64(configurationFileUrl);
		System.out.println(Utilities.encode(configurationFile));
		request.setHeader(HeaderNames.ContentType, APPLICATION_XML);// MEIDA_TYPE_TEXT_XML);
		return sendAsyncPostRequest(request, callback, template, Utilities
				.encode(configurationFile));
		// "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTE2Ij8+DQo8U2VydmljZUNvbmZpZ3VyYXRpb24geG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSIgeG1sbnM6eHNkPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYSIgc2VydmljZU5hbWU9IiIgeG1sbnM9Imh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vU2VydmljZUhvc3RpbmcvMjAwOC8xMC9TZXJ2aWNlQ29uZmlndXJhdGlvbiI+DQogIDxSb2xlIG5hbWU9IldlYlJvbGUiPg0KICAgIDxDb25maWd1cmF0aW9uU2V0dGluZ3MgLz4NCiAgICA8SW5zdGFuY2VzIGNvdW50PSIxIiAvPg0KICAgIDxDZXJ0aWZpY2F0ZXMgLz4NCiAgPC9Sb2xlPg0KPC9TZXJ2aWNlQ29uZmlndXJhdGlvbj4=");
	}

	private String updateDeplymentStatus(HttpRequest request,
			UpdateStatus status, AsyncResultCallback callback) {
		String template = "<UpdateDeploymentStatus xmlns=\"http://schemas.microsoft.com/windowsazure\"><Status>{0}</Status></UpdateDeploymentStatus>";
		request.setHeader(HeaderNames.ContentType, APPLICATION_XML);
		return sendAsyncPostRequest(request, callback, template, status
				.getLiteral());
	}

	/**
	 * he Swap Deployment operation initiates a virtual IP swap between the
	 * staging and production deployment slots for a service. If the service is
	 * currently running in the staging environment, it will be swapped to the
	 * production environment. If it is running in the production environment,
	 * it will be swapped to staging.
	 * 
	 * @param serviceName
	 * @param productName
	 * @param sourceName
	 * @param callback
	 * @return
	 */
	public String swapDeployment(String serviceName, String productName,
			String sourceName, AsyncResultCallback callback) {
		HttpRequest request = HttpUtilities.createServiceHttpRequest(URI
				.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName), HttpMethod.Post);
		String template = "<Swap xmlns=\"http://schemas.microsoft.com/windowsazure\"><Production>{0}</Production><SourceDeployment>{1}</SourceDeployment></Swap>";
		request.setHeader(HeaderNames.ContentType, APPLICATION_XML);
		return sendAsyncPostRequest(request, callback, template, productName,
				sourceName);
	}

	/**
	 * @param request
	 * @param status
	 * @param callback
	 * @param template
	 * @return
	 */
	private String sendAsyncPostRequest(HttpRequest request,
			AsyncResultCallback callback, String template, Object... arugments) {
		String body = MessageFormat.format(template, arugments);
		((HttpEntityEnclosingRequest) request).setEntity(new ByteArrayEntity(
				body.getBytes()));
		return sendAsynchronousRequest(request, callback);
	}

	/**
	 * 
	 * The Walk Upgrade Domain operation specifies the next upgrade domain to be
	 * walked during an in-place upgrade. For more information on in-place
	 * upgrades.
	 * 
	 * @param serviceName
	 * @param type
	 * @param domainId
	 * @param callback
	 * @return
	 */
	public String walkUpgradeDomain(String serviceName,
			DeploymentSlotType type, String domainId,
			AsyncResultCallback callback) {
		HttpRequest request = createHttpRequest(HttpMethod.Post, serviceName,
				type, "/action=walkupgradedomain");
		return walkUpgradeDomain(request, domainId, callback);
	}

	public String walkUpgradeDomain(String serviceName, String deploymentName,
			String domainId, AsyncResultCallback callback) {
		HttpRequest request = createHttpRequest(HttpMethod.Post, serviceName,
				deploymentName, "/action=walkupgradedomain");
		return walkUpgradeDomain(request, domainId, callback);
	}

	private String walkUpgradeDomain(HttpRequest request, String domainId,
			AsyncResultCallback callback) {
		// <WalkUpgradeDomain xmlns="http://schemas.microsoft.com/windowsazure">
		// <UpgradeDomain>upgrade-domain-id</UpgradeDomain>
		// </WalkUpgradeDomain>
		String template = "<WalkUpgradeDomain xmlns=\"http://schemas.microsoft.com/windowsazure\"><UpgradeDomain>{0}</UpgradeDomain></WalkUpgradeDomain>";
		request.setHeader(HeaderNames.ContentType, APPLICATION_XML);
		return sendAsyncPostRequest(request, callback, template, domainId);
	}

	/**
	 * The Change Deployment Configuration operation initiates a change to the
	 * deployment configuration.
	 * 
	 * @param serviceName
	 * @param type
	 * @param configurationFileUrl
	 * @param callback
	 * @return
	 */
	public String changeDeploymentConfiguration(String serviceName,
			DeploymentSlotType type, String configurationFileUrl,
			AsyncResultCallback callback) {
		HttpRequest request = createHttpRequest(HttpMethod.Post, serviceName,
				type, "/?comp=config");
		return changeDeploymentConfiguration(request, configurationFileUrl,
				callback);
	}

	public String changeDeploymentConfiguration(String serviceName,
			String deploymentName, String configurationFileUrl,
			AsyncResultCallback callback) {
		HttpRequest request = createHttpRequest(HttpMethod.Post, serviceName,
				deploymentName, "/?comp=config");
		return changeDeploymentConfiguration(request, configurationFileUrl,
				callback);
	}

	private String readBase64(String configurationFileUrl) {
		File file = new File(configurationFileUrl);
		if (file.exists() && file.isFile() && file.canRead()) {
			try {
				byte[] bytes = Utilities.getBytesFromFile(file);

				String content = new String(bytes);
				content = content.replaceAll("\r\n", "");

				return Base64.encode(content.getBytes("utf-8"));
			} catch (IOException e) {
				throw new IllegalStateException("Configuration file is invalid");
			}
		} else {
			throw new IllegalStateException("Configuration file is invalid");
		}
	}

	/**
	 * The Upgrade Deployment operation initiates an upgrade. The Upgrade
	 * Deployment operation is an asynchronous operation. To determine whether
	 * the Management service has finished processing the request, call Get
	 * Operation Status.
	 * 
	 * @return
	 */
	public String upgradeDeployment(String serviceName,
			DeploymentSlotType type, UpgradeConfiguration configuration,
			AsyncResultCallback callback) {
		HttpRequest request = createHttpRequest(HttpMethod.Post, serviceName,
				type, "/action=upgrade");
		return upgradeDeployment(request, configuration, callback);
	}

	public String upgradeDeployment(String serviceName, String deploymentName,
			UpgradeConfiguration configuration, AsyncResultCallback callback) {
		HttpRequest request = createHttpRequest(HttpMethod.Post, serviceName,
				deploymentName, "/action=upgrade");
		return upgradeDeployment(request, configuration, callback);
	}

	private String upgradeDeployment(HttpRequest request,
			UpgradeConfiguration configuration, AsyncResultCallback callback) {
		configuration.validate();
		StringBuilder buf = new StringBuilder();
		buf
				.append("<UpgradeDeployment xmlns=\"http://schemas.microsoft.com/windowsazure\">");
		buf.append("<Mode>" + configuration.getMode().getLiteral() + "</Mode>");
		buf.append("<PackageUrl>" + configuration.getPackageBlobUrl()
				+ "</PackageUrl>");
		buf.append("<RoleToUpgrade>" + configuration.getUpgradRole()
				+ "</RoleToUpgrade>");
		buf.append("<Configuration>"
				+ readBase64(configuration.getConfigurationFileUrl())
				+ "</Configuration>");
		buf.append("<DeploymentLabel>" + configuration.getBase64Label()
				+ "</DeploymentLabel>");
		buf.append("</UpgradeDeployment>");
		request.setHeader(HeaderNames.ContentType, APPLICATION_XML);
		((HttpEntityEnclosingRequest) request).setEntity(new ByteArrayEntity(
				buf.toString().getBytes()));
		return sendAsynchronousRequest(request, callback);
	}

	public String deleteDeployment(String serviceName, DeploymentSlotType type,
			AsyncResultCallback callback) {
		HttpRequest request = createHttpRequest(HttpMethod.Delete, serviceName,
				type, "");
		return sendAsynchronousRequest(request, callback);
	}

	public String deleteDeployment(String serviceName, String deploymentName,
			AsyncResultCallback callback) {
		HttpRequest request = createHttpRequest(HttpMethod.Delete, serviceName,
				deploymentName, "");
		return sendAsynchronousRequest(request, callback);
	}

	/**
	 * Request-id is returned
	 * 
	 * @param request
	 * @param callback
	 * @return
	 */
	private String sendAsynchronousRequest(HttpRequest request,
			AsyncResultCallback callback) {
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				String requestId = getHeaderValueFromResponse(response,
						HeaderNames.ManagementRequestId);
				if (callback != null)
					observeOperationStatus(requestId, callback, this.isBlocking());
				return requestId;
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	private String getHeaderValueFromResponse(HttpWebResponse response,
			String headerName) {
		return response.getHeader(headerName);
	}

	/**
	 * The Get Hosted Service Properties operation retrieves system properties
	 * for the specified hosted service. These properties include the service
	 * name and service type; the name of the affinity group to which the
	 * service belongs, or its location if it is not part of an affinity group;
	 * and optionally, information on the service's deployments. When the
	 * request sets the embed-detail parameter to true, the response body
	 * includes additional details on the service's deployments:
	 */
	public HostedServiceProperties getHostedServiceProperties(
			String serviceName, boolean embedDetail) {
		HttpRequest request = HttpUtilities.createServiceHttpRequest(URI
				.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName + "?embed-detail="
						+ embedDetail), HttpMethod.Get);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper.parseHostedPropertiesResponse(response
						.getStream());
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * For more detail, see: <a
	 * href="http://msdn.microsoft.com/en-us/library/ee460791.aspx">Service
	 * status</a>
	 * 
	 * </br>
	 * 
	 * If the request was successful and the asynchronous operation is being
	 * processed, the service returns status code 202 (Accept). Note that this
	 * status code does not indicate whether the operation itself has been
	 * processed successfully, but only that the request has been received by
	 * the service. If the return status code is not 202 (Accept), then the
	 * request must be retried.
	 * 
	 * @param response
	 * @return
	 */
	protected boolean isRequestAccepted(HttpWebResponse response) {
		return response.getStatusCode() == HttpStatus.SC_OK
				|| response.getStatusCode() == HttpStatus.SC_ACCEPTED;
	}

	/**
	 * The List Affinity Groups operation lists the affinity groups associated
	 * with the specified subscription.
	 */
	@Override
	public List<AffinityGroup> listAffinityGroups() {
		HttpRequest request = HttpUtilities
				.createServiceHttpRequest(URI.create(getBaseUrl()
						+ SERVICES_AFFINITYGROUPS), HttpMethod.Get);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper.parseAffinityGroupResponse(response
						.getStream());
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * The Get Storage Keys operation returns the primary and secondary access
	 * keys for the specified storage account.
	 */
	public StorageServiceKeys getStorageAccountKeys(String serviceName) {
		HttpRequest request = HttpUtilities.createServiceHttpRequest(URI
				.create(getBaseUrl() + SERVICES_STORAGESERVICE
						+ ConstChars.Slash + serviceName + SERVICE_KEYS),
				HttpMethod.Get);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return convertServiceToKeys(response);
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * Help method
	 * 
	 * @param response
	 * @return
	 */
	private StorageServiceKeys convertServiceToKeys(HttpWebResponse response) {
		StorageService service = XPathQueryHelper
				.parseStorageServiceKeysResponse(response.getStream());
		if (service != null) {
			StorageServiceKeys keys = new StorageServiceKeys();
			keys.setPrimaryKey(service.getPrimaryKey());
			keys.setSecondaryKey(service.getSecondaryKey());
			return keys;
		}
		return null;
	}

	/**
	 * The Regenerate Keys operation regenerates the primary or secondary access
	 * key for the specified storage account.
	 */
	public StorageServiceKeys regenerateKeys(String serviceName, KeyType type) {
		if (type == null) {
			throw new IllegalArgumentException("Key type null");
		}
		HttpRequest request = HttpUtilities.createServiceHttpRequest(URI
				.create(getBaseUrl() + SERVICES_STORAGESERVICE
						+ ConstChars.Slash + serviceName
						+ KEYS_ACTION_REGENERATE), HttpMethod.Post);
		request.addHeader(HeaderNames.ContentType, MEIDA_TYPE_TEXT_XML);
		try {
			String template = "<RegenerateKeys xmlns=\"http://schemas.microsoft.com/windowsazure\"><KeyType>{0}</KeyType></RegenerateKeys>";
			String body = MessageFormat.format(template, type.getLiteral());
			((HttpEntityEnclosingRequest) request)
					.setEntity(new ByteArrayEntity(body.getBytes()));
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return convertServiceToKeys(response);
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * The Get Storage Account Properties operation returns the system
	 * properties for the specified storage account. These properties include:
	 * the address, description, and label of the storage account; and the name
	 * of the affinity group to which the service belongs, or its geo-location
	 * if it is not part of an affinity group.
	 */
	public StorageServiceProperties getStorageAccountProperties(
			String serviceName) {
		HttpRequest request = HttpUtilities.createServiceHttpRequest(URI
				.create(getBaseUrl() + SERVICES_STORAGESERVICE
						+ ConstChars.Slash + serviceName), HttpMethod.Get);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper
						.parseStorageServicePropertiesResponse(response
								.getStream());
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * The List Storage Accounts operation lists the storage accounts available
	 * under the current subscription.
	 */
	@Override
	public List<StorageService> listStorageAccounts() {
		HttpRequest request = HttpUtilities
				.createServiceHttpRequest(URI.create(getBaseUrl()
						+ SERVICES_STORAGESERVICE), HttpMethod.Get);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper.parseStorageServiceResponse(response
						.getStream());
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;

	}

	/**
	 * The Get Affinity Group Properties operation returns the system properties
	 * associated with the specified affinity group.
	 */
	public AffinityGroupProperties getAffinityGroupProperties(String groupName) {
		HttpRequest request = HttpUtilities.createServiceHttpRequest(URI
				.create(getBaseUrl() + SERVICES_AFFINITYGROUPS
						+ ConstChars.Slash + groupName), HttpMethod.Get);
		try {
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper
						.parseAffinityGroupPropertiesResponse(response
								.getStream());
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	protected void observeOperationStatus(final String requestId,
			final AsyncResultCallback callback, boolean blocked) {
		Runnable runnable = new Runnable() {

			public void run() {
				while (true) {
					OperationStatus status = getOperationStatus(requestId);
					if (status.getStatus() == OperationState.Failed) {
						callback.onError(status);
						return;
					} else if (status.getStatus() == OperationState.Succeeded) {
						callback.onSuccess(status);
						return;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		};

		Thread thread = new Thread(runnable);
		thread.start();
		if (blocked)
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	/**
	 * The Get Operation Status operation returns the status of the specified
	 * operation. After calling an asynchronous operation, you can call Get
	 * Operation Status to determine whether the operation has succeed, failed,
	 * or is still in progress.
	 */
	public OperationStatus getOperationStatus(String requestId) {
		HttpRequest request = HttpUtilities.createServiceHttpRequest(URI
				.create(getBaseUrl() + OPERATIONS + ConstChars.Slash
						+ requestId), HttpMethod.Get);
		try {
			attachHeaderForRequestId(requestId, request);
			HttpWebResponse response = HttpUtilities.getSSLReponse(request,
					getSslSocketFactory());
			if (isRequestAccepted(response)) {
				return XPathQueryHelper.parseOperationStatusResponse(response
						.getStream());
			} else {
				HttpUtilities.processUnexpectedStatusCode(response);
			}
		} catch (StorageException we) {
			throw HttpUtilities.translateWebException(we);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * A value that uniquely identifies a request made against the management
	 * service.
	 * 
	 * @param requestId
	 * @param request
	 */
	private void attachHeaderForRequestId(String requestId, HttpRequest request) {
		request.addHeader(HeaderNames.ManagementRequestId, requestId);
	}

	public String createDeployment(String serviceName,
			DeploymentSlotType deploySlotName,
			DeploymentConfiguration configuration, AsyncResultCallback callback) {
		if (serviceName == null)
			throw new IllegalArgumentException("Service name is required!");

		if (deploySlotName == null)
			throw new IllegalArgumentException("Deploy Slot is required!");

		if (configuration == null)
			throw new IllegalArgumentException("Deployment define is required!");

		configuration.validate();

		HttpRequest request = HttpUtilities.createServiceHttpRequest(URI
				.create(getBaseUrl() + SERVICES_HOSTEDSERVICES
						+ ConstChars.Slash + serviceName + DEPLOYMENT_SLOTS
						+ ConstChars.Slash
						+ deploySlotName.getLiteral().toLowerCase()),
				HttpMethod.Post);
		request.addHeader(HeaderNames.ContentType, APPLICATION_XML);
		String content = readBase64(configuration.getConfigurationFileUrl());
		String label = configuration.getBase64Label();
		String body = MessageFormat.format(CREATE_DEPLOYMENT_BODY,
				configuration.getName(), configuration.getPackageBlobUrl(),
				content, label); // configuration.getBase64ConfigurationFile()
		// body =
		// "<?xml version=\"1.0\"?><CreateDeployment xmlns=\"http://schemas.microsoft.com/windowsazure\"><Name>testdep</Name><PackageUrl>http://soyatecdemo.blob.core.windows.net/manageusage/simpletest</PackageUrl><Label>c2ltcGxldGVzdA==</Label><Configuration>PD94bWwgdmVyc2lvbj0iMS4wIj8+PFNlcnZpY2VDb25maWd1cmF0aW9uIHNlcnZpY2VOYW1lPSJzaW1wbGV0ZXN0IiB4bWxucz0iaHR0cDovL3NjaGVtYXMubWljcm9zb2Z0LmNvbS9TZXJ2aWNlSG9zdGluZy8yMDA4LzEwL1NlcnZpY2VDb25maWd1cmF0aW9uIj4gIDxSb2xlIG5hbWU9IldlYlJvbGUiPiAgICA8SW5zdGFuY2VzIGNvdW50PSIxIi8+ICAgIDxDb25maWd1cmF0aW9uU2V0dGluZ3M+ICAgIDwvQ29uZmlndXJhdGlvblNldHRpbmdzPiAgPC9Sb2xlPjwvU2VydmljZUNvbmZpZ3VyYXRpb24+</Configuration></CreateDeployment>";

		((HttpEntityEnclosingRequest) request).setEntity(new ByteArrayEntity(
				body.getBytes()));
		return sendAsynchronousRequest(request, callback);
	}

}
