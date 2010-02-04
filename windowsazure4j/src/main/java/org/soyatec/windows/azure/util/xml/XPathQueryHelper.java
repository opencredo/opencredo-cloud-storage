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
package org.soyatec.windows.azure.util.xml;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.soyatec.windows.azure.authenticate.Base64;
import org.soyatec.windows.azure.constants.ConstChars;
import org.soyatec.windows.azure.constants.HttpStatusConstant;
import org.soyatec.windows.azure.constants.ServiceXmlElementNames;
import org.soyatec.windows.azure.constants.XmlElementNames;
import org.soyatec.windows.azure.error.StorageErrorCode;
import org.soyatec.windows.azure.error.StorageServerException;
import org.soyatec.windows.azure.management.AffinityGroup;
import org.soyatec.windows.azure.management.AffinityGroupProperties;
import org.soyatec.windows.azure.management.CurrentUpgradeDomainState;
import org.soyatec.windows.azure.management.Deployment;
import org.soyatec.windows.azure.management.HostService;
import org.soyatec.windows.azure.management.HostedServiceProperties;
import org.soyatec.windows.azure.management.OperationState;
import org.soyatec.windows.azure.management.OperationStatus;
import org.soyatec.windows.azure.management.RoleInstance;
import org.soyatec.windows.azure.management.ServiceManagementConstants;
import org.soyatec.windows.azure.management.StorageService;
import org.soyatec.windows.azure.management.StorageServiceProperties;
import org.soyatec.windows.azure.management.UpgradeStatus;
import org.soyatec.windows.azure.management.UpgradeType;
import org.soyatec.windows.azure.table.TableStorageConstants;
import org.soyatec.windows.azure.util.Utilities;

/**
 * Helper class for loading values from an XML segment
 * 
 */
public class XPathQueryHelper {

	private static final String XMLNS = "xmlns";

	private static final String ATOM_ENTRY_PATH = "//atom:entry";

	public final static String NextMarkerQuery = join(ConstChars.Slash,
			new String[] { "", "", XmlElementNames.EnumerationResults,
					XmlElementNames.NextMarker });

	public final static String ContainerQuery = join(ConstChars.Slash,
			new String[] { "", "", XmlElementNames.EnumerationResults,
					XmlElementNames.Containers, XmlElementNames.Container });

	public final static String BlobQuery = join(ConstChars.Slash, new String[] {
			"", "", XmlElementNames.EnumerationResults, XmlElementNames.Blobs,
			XmlElementNames.Blob });

	public final static String BlockQuery = join(ConstChars.Slash,
			new String[] { "", "", XmlElementNames.BlockList,
					XmlElementNames.Block });

	public final static String QueueListQuery = join(ConstChars.Slash,
			new String[] { "", "", XmlElementNames.EnumerationResults,
					XmlElementNames.Queues, XmlElementNames.Queue });

	public final static String MessagesListQuery = join(ConstChars.Slash,
			new String[] { "", "", XmlElementNames.QueueMessagesList,
					XmlElementNames.QueueMessage });

	public final static String CommonPrefixQuery = join(ConstChars.Slash,
			new String[] { "", "", XmlElementNames.EnumerationResults,
					XmlElementNames.Blob, XmlElementNames.BlobPrefix });

	public final static String SignedIdentifierListQuery = join(
			ConstChars.Slash, new String[] { "", "",
					XmlElementNames.ContainerSignedIdentifierName });

	// some constants for ServiceManagement
	public final static String HostServiceListQuery = globalQueryPath(ServiceXmlElementNames.HostedService);
	public final static String StorageServiceListQuery = globalQueryPath(ServiceXmlElementNames.StorageService);
	public final static String AffinifyGroupListQuery = globalQueryPath(ServiceXmlElementNames.AffinityGroup);
	public final static String RoleInstanceQuery = globalQueryPath(ServiceXmlElementNames.DeploymentRoleInstance);
	public final static String OperationQuery = globalQueryPath(ServiceXmlElementNames.OperationStatusName);
	public final static String DeploymentQuery = globalQueryPath(ServiceXmlElementNames.Deployment);

	/**
	 * Help to generate the global query xpath as //xmlns:element-name
	 * 
	 * @return
	 */
	private static String globalQueryPath(String elementName) {
		return join(ConstChars.Slash, new String[] { "", "",
				XPathQueryHelper.addXmlnsNameSpace(elementName) });
	}

	public final static String addXmlnsNameSpace(String query) {
		return join("", new String[] { XMLNS, ConstChars.Colon, query });
	}

	private static String join(String delimiter, String[] source) {
		StringBuffer buffer = new StringBuffer();
		int i = 0;
		while (i < source.length) {
			buffer.append(source[i]);
			if (i < source.length - 1)
				buffer.append(delimiter);
			i++;
		}
		return buffer.toString();
	}

	public static String loadSingleChildStringValue(Element node,
			String childName, boolean throwIfNotFound) {
		Element childNode = (Element) node.selectSingleNode(childName);
		if (childNode != null && childNode.hasContent()) {
			return childNode.node(0).getText();
		} else if (!throwIfNotFound) {
			return null;
		} else {
			// unnecessary since Fail will throw, but keeps the compiler happy
			return null;
		}
	}

	public static Timestamp loadSingleChildDateTimeValue(Element blobNode,
			String childName, boolean throwIfNotFound) {
		Element childNode = (Element) blobNode.selectSingleNode(childName);
		if (childNode != null && childNode.hasContent()) {
			Timestamp date;
			try {
				date = Utilities.tryGetDateTimeFromHttpString(childNode
						.getStringValue());
				return date;
			} catch (ParseException e) {
				throw new StorageServerException(
						StorageErrorCode.ServiceBadResponse,
						"Date time value returned from server "
								+ childNode.getStringValue()
								+ " can't be parsed.",
						HttpStatusConstant.DEFAULT_STATUS, null);
			}
		} else if (!throwIfNotFound) {
			return null;
		} else {
			return null;
		}
	}

	public static Long loadSingleChildLongValue(Element blobNode,
			String childName, boolean throwIfNotFound)
			throws StorageServerException {
		Element childNode = (Element) blobNode.selectSingleNode(childName);
		if (childNode != null && childNode.hasContent()) {
			try {
				return Long.parseLong(childNode.getStringValue());
			} catch (Exception e) {
				throw new StorageServerException(
						StorageErrorCode.ServiceBadResponse,
						"Reponse size field is not a valid long number."
								+ childNode.getStringValue()
								+ " can't be parsed.",
						HttpStatusConstant.DEFAULT_STATUS, null);
			}
		} else if (!throwIfNotFound) {
			return null;
		} else {
			return null;
		}
	}

	public static String loadTableNameFromTableEntry(Element element) {
		return element.element(XmlElementNames.TableEntryContent).element(
				XmlElementNames.TableEntryProperties).elementText(
				XmlElementNames.TableEntryTableName);
	}

	public static String loadTableEntryPropertyValue(Element element,
			String propertyName) {
		if (Utilities.isNullOrEmpty(propertyName)) {
			throw new IllegalArgumentException("property name");
		}
		return loadTableEntryProperties(element).elementText(propertyName);
	}

	public static String loadTableEntryValueFromAttribute(Element element,
			String propertyName) {
		if (Utilities.isNullOrEmpty(propertyName)) {
			throw new IllegalArgumentException("property name");
		}
		return element.attribute(propertyName).getValue();
	}

	public static Element loadTableEntryProperties(Element element) {
		return element.element(XmlElementNames.TableEntryContent).element(
				XmlElementNames.TableEntryProperties);
	}

	@SuppressWarnings("unchecked")
	public static List parseEntryFromFeed(final Document doc) {
		Map xmlMap = new HashMap();
		xmlMap.put("atom", TableStorageConstants.AtomNamespace);
		XPath x = doc.createXPath(ATOM_ENTRY_PATH);
		x.setNamespaceURIs(xmlMap);
		return x.selectNodes(doc);
	}

	@SuppressWarnings("unchecked")
	public static List serviceXmlSelected(final Node doc, String path) {
		Map xmlMap = new HashMap();
		xmlMap.put(XMLNS, ServiceManagementConstants.ServiceManagementNS);
		XPath x = doc.createXPath(path);
		x.setNamespaceURIs(xmlMap);
		return x.selectNodes(doc);
	}

	@SuppressWarnings("unchecked")
	public static Node serviceXmlSelectSingle(final Node doc, String path) {
		Map xmlMap = new HashMap();
		xmlMap.put(XMLNS, ServiceManagementConstants.ServiceManagementNS);
		XPath x = doc.createXPath(path);
		x.setNamespaceURIs(xmlMap);
		Node selectSingleNode = x.selectSingleNode(doc);
		return selectSingleNode;
	}

	@SuppressWarnings("unchecked")
	public static List<HostService> parseHostServiceResponse(InputStream stream) {
		Document load = XmlUtil.load(stream);
		List result = XPathQueryHelper.serviceXmlSelected(load,
				XPathQueryHelper.HostServiceListQuery);
		if (result == null || result.isEmpty()) {
			return Collections.EMPTY_LIST;
		} else {
			List<HostService> hss = new ArrayList<HostService>();
			for (int i = 0, n = result.size(); i < n; i++) {
				Element element = (Element) result.get(i);
				String url = getStringValue(element,
						ServiceXmlElementNames.HostedService_Url);
				String name = getStringValue(element,
						ServiceXmlElementNames.HostedService_Name);
				HostService service = new HostService(name);
				service.setUrl(url);
				hss.add(service);
			}
			return hss;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<StorageService> parseStorageServiceResponse(
			InputStream stream) {
		Document load = XmlUtil.load(stream);
		List result = XPathQueryHelper.serviceXmlSelected(load,
				XPathQueryHelper.StorageServiceListQuery);
		if (result == null || result.isEmpty()) {
			return Collections.EMPTY_LIST;
		} else {
			List<StorageService> storageServiceList = new ArrayList<StorageService>();
			for (int i = 0, n = result.size(); i < n; i++) {
				Element element = (Element) result.get(i);
				String url = getStringValue(element,
						ServiceXmlElementNames.StorageService_Url);
				String name = getStringValue(element,
						ServiceXmlElementNames.StorageService_Name);
				StorageService service = new StorageService(name);
				service.setUrl(url);
				storageServiceList.add(service);
			}
			return storageServiceList;
		}
	}

	public static StorageServiceProperties parseStorageServicePropertiesResponse(
			InputStream stream) {
		Document load = XmlUtil.load(stream);
		Element storageServiceElement = load.getRootElement();
		if (storageServiceElement == null) {
			return null;
		}
		Element element = (Element) serviceXmlSelectSingle(
				storageServiceElement,
				addXmlnsNameSpace((ServiceXmlElementNames.StorageServiceProperties)));
		String description = getStringValue(element,
				ServiceXmlElementNames.AffinityGroupDescription);
		String label = getStringValue(element, ServiceXmlElementNames.Label);
		String location = getStringValue(element,
				ServiceXmlElementNames.AffinityGroupLocation);

		Node groupElement = serviceXmlSelectSingle(element,
				addXmlnsNameSpace(ServiceXmlElementNames.AffinityGroup));
		String group = groupElement == null ? null : groupElement
				.getStringValue();
		return new StorageServiceProperties(description, group, location, label);
	}

	public static StorageService parseStorageServiceKeysResponse(
			InputStream stream) {
		Document load = XmlUtil.load(stream);
		Element storageServiceElement = load.getRootElement();
		if (storageServiceElement == null) {
			return null;
		} else {
			String url = getStringValue(storageServiceElement,
					ServiceXmlElementNames.StorageService_Url);
			Element keyElement = (Element) serviceXmlSelectSingle(
					storageServiceElement,
					addXmlnsNameSpace((ServiceXmlElementNames.StorageServiceKeys)));
			String primaryKey = getStringValue(keyElement,
					ServiceXmlElementNames.Key_Primary);
			String secondaryKey = getStringValue(keyElement,
					ServiceXmlElementNames.Key_Secondary);
			StorageService service = new StorageService();

			service.setUrl(url);
			service.setPrimaryKey(primaryKey);
			service.setSecondaryKey(secondaryKey);

			return service;
		}
	}

	@SuppressWarnings("unchecked")
	public static HostedServiceProperties parseHostedPropertiesResponse(
			InputStream stream) {
		Document load = XmlUtil.load(stream);
		Element root = load.getRootElement();
		HostedServiceProperties result = new HostedServiceProperties();
		result.setUrl(getStringValue(root,
				ServiceXmlElementNames.HostedService_Url));
		Element element = (Element) serviceXmlSelectSingle(
				root,
				addXmlnsNameSpace((ServiceXmlElementNames.HostedServiceProperties)));
		result.setDescription(getStringValue(element,
				ServiceXmlElementNames.Description));
		result.setAffinityGroup(getStringValue(element,
				ServiceXmlElementNames.AffinityGroup));
		result.setLocation(getStringValue(element,
				ServiceXmlElementNames.AffinityGroupLocation));
		result.setLabel(getStringValue(element, ServiceXmlElementNames.Label,
				true));
		List deployments = serviceXmlSelected(element, DeploymentQuery);
		if (deployments != null && !deployments.isEmpty()) {
			for (int i = 0; i < deployments.size(); i++) {
				Element d = (Element) deployments.get(i);
				result.addDeployment(parseDeployment(d));
			}
		}

		return result;
	}

	private static String getStringValue(Node node, String query,
			boolean decodeBase64) {
		Node child = serviceXmlSelectSingle(node, addXmlnsNameSpace(query));
		if (child == null)
			return "";
		else {
			String value = child.getStringValue();
			if (decodeBase64)
				return new String(Base64.decode(value));
			else
				return value;
		}

	}

	private static String getStringValue(Node node, String query) {
		return getStringValue(node, query, false);
	}

	/**
	 * Construct a Deployment from the response content
	 * 
	 * @param stream
	 * @return
	 */
	public static Deployment parseDeploymentResponse(InputStream stream) {
		Document load = XmlUtil.load(stream);
		Element deploymentElement = load.getRootElement();
		if (deploymentElement == null) {
			return null;
		} else {
			return parseDeployment(deploymentElement);
		}
	}

	/**
	 * @param deploymentElement
	 * @return
	 */
	private static Deployment parseDeployment(Element deploymentElement) {
		Deployment deployment = new Deployment();
		parseDeploymentAttributes(deploymentElement, deployment);
		parseDeploymentStatus(deploymentElement, deployment);
		parseRoleInstanceList(deploymentElement, deployment);
		return deployment;
	}

	@SuppressWarnings("unchecked")
	private static void parseRoleInstanceList(Element storageServiceElement,
			Deployment deployment) {
		List roles = serviceXmlSelected(storageServiceElement,
				RoleInstanceQuery);
		if (roles != null && !roles.isEmpty()) {

			for (Iterator iter = roles.iterator(); iter.hasNext();) {
				Element roleInstanceElement = (Element) iter.next();
				String roleName = getStringValue(roleInstanceElement,
						ServiceXmlElementNames.DeploymentRoleInstanceRoleName);
				String instanceName = getStringValue(
						roleInstanceElement,
						ServiceXmlElementNames.DeploymentRoleInstanceInstanceName);
				String instanceState = getStringValue(
						roleInstanceElement,
						ServiceXmlElementNames.DeploymentRoleInstanceInstanceState);

				RoleInstance roleInstance = new RoleInstance(roleName,
						instanceName, instanceState);

				deployment.addRoleInstance(roleInstance);
			}
		}
	}

	private static void parseDeploymentStatus(Element storageServiceElement,
			Deployment deployment) {
		Element upgradeStatusElement = (Element) serviceXmlSelectSingle(
				storageServiceElement,
				addXmlnsNameSpace((ServiceXmlElementNames.DeploymentUpgradeStatus)));
		if (upgradeStatusElement == null) {
			return;
		}

		String upgradeType = getStringValue(upgradeStatusElement,
				ServiceXmlElementNames.DeploymentUpgradeStatusUpgradeType);
		String currentUpgradeDomainState = getStringValue(
				upgradeStatusElement,
				ServiceXmlElementNames.DeploymentUpgradeStatusCurrentUpgradeDomainState);

		String currentUpgradeDomain = getStringValue(
				upgradeStatusElement,
				ServiceXmlElementNames.DeploymentUpgradeStatusCurrentUpgradeDomain);

		UpgradeStatus upgradeStatus = new UpgradeStatus();
		upgradeStatus.setUpgradeType(UpgradeType.get(upgradeType));
		upgradeStatus.setCurrentUpgradeDomain(currentUpgradeDomain);
		upgradeStatus.setCurrentUpgradeDomainState(CurrentUpgradeDomainState
				.get(currentUpgradeDomainState));
		deployment.setUpgradeStatus(upgradeStatus);
	}

	private static void parseDeploymentAttributes(
			Element storageServiceElement, Deployment deployment) {
		String name = getStringValue(storageServiceElement,
				ServiceXmlElementNames.DeploymentName);
		deployment.setName(name);

		String label = getStringValue(storageServiceElement,
				ServiceXmlElementNames.DeploymentLabel);
		if (label != null)
			deployment.setLabel(new String(Base64.decode(label)));

		String slot = getStringValue(storageServiceElement,
				ServiceXmlElementNames.DeploymentSlot);
		deployment.setDeploymentSlot(slot);

		String url = getStringValue(storageServiceElement,
				ServiceXmlElementNames.DeploymentUrl);
		deployment.setUrl(url);

		String privateId = getStringValue(storageServiceElement,
				ServiceXmlElementNames.DeploymentPrivateID);
		deployment.setPrivateId(privateId);

		String configuration = getStringValue(storageServiceElement,
				ServiceXmlElementNames.DeploymentConfiguration);
		System.out.println(configuration);
		if (configuration != null) {
			deployment
					.setConfiguration(new String(Base64.decode(configuration)));
		}

		String status = getStringValue(storageServiceElement,
				ServiceXmlElementNames.DeploymentStatus);
		deployment.setStatus(status);
	}

	/**
	 * Parse Affinity group list
	 * 
	 * @param stream
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<AffinityGroup> parseAffinityGroupResponse(
			InputStream stream) {
		Document load = XmlUtil.load(stream);
		List result = XPathQueryHelper.serviceXmlSelected(load,
				XPathQueryHelper.AffinifyGroupListQuery);
		if (result == null || result.isEmpty()) {
			return Collections.EMPTY_LIST;
		} else {
			List<AffinityGroup> groupList = new ArrayList<AffinityGroup>();
			for (int i = 0, n = result.size(); i < n; i++) {

				Element element = (Element) result.get(i);

				String name = getStringValue(element,
						ServiceXmlElementNames.AffinityGroupName);
				String description = getStringValue(element,
						ServiceXmlElementNames.AffinityGroupDescription);
				String location = getStringValue(element,
						ServiceXmlElementNames.AffinityGroupLocation);

				AffinityGroup group = new AffinityGroup();

				group.setName(name);
				group.setDescription(description);
				group.setLocation(location);
				groupList.add(group);
			}
			return groupList;
		}
	}

	/**
	 * The Get Operation Status operation returns the status of the specified
	 * operation. After calling an asynchronous operation, you can call Get
	 * Operation Status to determine whether the operation has succeed, failed,
	 * or is still in progress.
	 * 
	 * @param stream
	 * @return
	 */
	public static OperationStatus parseOperationStatusResponse(
			InputStream stream) {
		Document load = XmlUtil.load(stream);
		Element root = load.getRootElement();
		if (root == null)
			return null;

		Element element = (Element) serviceXmlSelectSingle(root, OperationQuery);
		OperationStatus result = new OperationStatus();
		String id = getStringValue(element,
				ServiceXmlElementNames.OperationStatusId);
		String status = getStringValue(element,
				ServiceXmlElementNames.OperationStatusStatus);

		result.setRequestId(id);
		result.setStatus(OperationState.valueOf(status));

		// Response includes HTTP status code only if the operation succeeded or
		// failed
		try {
			String httpCode = getStringValue(element,
					ServiceXmlElementNames.OperationStatusHTTPCode);
			result.setHttpCode(httpCode);
		} catch (Exception e) {
			// pass
		}

		// Response includes additional error information only if the operation
		// failed
		try {
			Node errorElement = serviceXmlSelectSingle(
					element,
					addXmlnsNameSpace(ServiceXmlElementNames.OperationStatusHTTPError));
			if (errorElement != null) {
				String httpErrorCode = getStringValue(errorElement,
						ServiceXmlElementNames.OperationStatusHTTPErrorCode);
				String httpErrorMessage = getStringValue(errorElement,
						ServiceXmlElementNames.OperationStatusHTTPErrorMessage);
				result.setErrorCode(httpErrorCode);
				result.setErrorMessage(httpErrorMessage);
			}
		} catch (Exception e) {
			// pass
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static AffinityGroupProperties parseAffinityGroupPropertiesResponse(
			InputStream stream) {
		Document load = XmlUtil.load(stream);
		Element element = load.getRootElement();
		if (element == null)
			return null;
		// Element element = (Element) serviceXmlSelectSingle(root,
		// addXmlnsNameSpace((ServiceXmlElementNames.AffinityGroup)));
		AffinityGroupProperties result = new AffinityGroupProperties();
		String description = getStringValue(element,
				ServiceXmlElementNames.AffinityGroupDescription);
		result.setDescription(description);
		String location = getStringValue(element,
				ServiceXmlElementNames.AffinityGroupLocation);
		result.setLocation(location);
		List hosts = XPathQueryHelper.serviceXmlSelected(load,
				XPathQueryHelper.HostServiceListQuery);
		if (hosts != null && !hosts.isEmpty())
			for (Object e : hosts)
				result.addHostedService(getStringValue((Node) e,
						XmlElementNames.Url));

		List services = XPathQueryHelper.serviceXmlSelected(load,
				XPathQueryHelper.StorageServiceListQuery);
		if (services != null && !services.isEmpty())
			for (Object e : services)
				result.addHostedService(getStringValue((Node) e,
						XmlElementNames.Url));

		return result;
	}
}
