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
package org.soyatec.windows.azure.blob;

public class ShareAccessUrl {

	private String accountName;

	private String blobName;

	private String containerName;

	private String signedResource;

	private String signedStart;

	private String signedExpiry;

	private String signedPermissions;

	private String signedIdentifier;

	private String signature;

	private String restUrl;

	private String signedString;

	public static ShareAccessUrl parse(String url) {
		ShareAccessUrl share = new ShareAccessUrl();
		String[] parts = url.split("\\?");
		share.restUrl = parts[0];
		share.signedString = parts[1];
		int pos = url.indexOf(".blob.core.windows.net");
		if (pos > -1) {
			share.accountName = url.substring("http://".length(), pos);
			url = url.substring(pos + ".blob.core.windows.net/".length(), url
					.indexOf('?'));
		} else {
			pos = url.indexOf("blob.core.windows.net/")
					+ "blob.core.windows.net/".length();
			share.accountName = url.substring(pos, url.indexOf('/', pos));
			url = url.substring(url.indexOf('/', pos) + 1, url
					.indexOf('?', pos));
		}
		parts = url.split("/");
		share.containerName = parts[0];
		if (parts.length > 1)
			share.blobName = parts[1];

		String[] params = share.signedString.split("&");
		for (String param : params) {
			if (param.indexOf("st=") > -1)
				share.signedStart = param.substring(3);
			else if (param.indexOf("sr=") > -1)
				share.signedResource = param.substring(3);
			else if (param.indexOf("se=") > -1)
				share.signedExpiry = param.substring(3);
			else if (param.indexOf("sp=") > -1)
				share.signedPermissions = param.substring(3);
			else if (param.indexOf("si=") > -1)
				share.signedIdentifier = param.substring(3);
			else if (param.indexOf("sig=") > -1)
				share.signature = param.substring(4);
		}

		return share;
	}

	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * @param accountName
	 *            the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	/**
	 * @return the blobName
	 */
	public String getBlobName() {
		return blobName;
	}

	/**
	 * @param blobName
	 *            the blobName to set
	 */
	public void setBlobName(String blobName) {
		this.blobName = blobName;
	}

	/**
	 * @return the containerName
	 */
	public String getContainerName() {
		return containerName;
	}

	/**
	 * @param containerName
	 *            the containerName to set
	 */
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	/**
	 * @return the signedResource
	 */
	public String getSignedResource() {
		return signedResource;
	}

	/**
	 * @param signedResource
	 *            the signedResource to set
	 */
	public void setSignedResource(String signedResource) {
		this.signedResource = signedResource;
	}

	/**
	 * @return the signedStart
	 */
	public String getSignedStart() {
		return signedStart;
	}

	/**
	 * @param signedStart
	 *            the signedStart to set
	 */
	public void setSignedStart(String signedStart) {
		this.signedStart = signedStart;
	}

	/**
	 * @return the signedExpiry
	 */
	public String getSignedExpiry() {
		return signedExpiry;
	}

	/**
	 * @param signedExpiry
	 *            the signedExpiry to set
	 */
	public void setSignedExpiry(String signedExpiry) {
		this.signedExpiry = signedExpiry;
	}

	/**
	 * @return the signedPermissions
	 */
	public String getSignedPermissions() {
		return signedPermissions;
	}

	/**
	 * @param signedPermissions
	 *            the signedPermissions to set
	 */
	public void setSignedPermissions(String signedPermissions) {
		this.signedPermissions = signedPermissions;
	}

	/**
	 * @return the signedIdentifier
	 */
	public String getSignedIdentifier() {
		return signedIdentifier;
	}

	/**
	 * @param signedIdentifier
	 *            the signedIdentifier to set
	 */
	public void setSignedIdentifier(String signedIdentifier) {
		this.signedIdentifier = signedIdentifier;
	}

	/**
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * @param signature
	 *            the signature to set
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * @return the restUrl
	 */
	public String getRestUrl() {
		return restUrl;
	}

	/**
	 * @param restUrl
	 *            the restUrl to set
	 */
	public void setRestUrl(String restUrl) {
		this.restUrl = restUrl;
	}

	/**
	 * @return the signedString
	 */
	public String getSignedString() {
		return signedString;
	}

	/**
	 * @param signedString
	 *            the signedString to set
	 */
	public void setSignedString(String signedString) {
		this.signedString = signedString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ShareAccessUrl [accountName=" + accountName + ", blobName="
				+ blobName + ", containerName=" + containerName + ", restUrl="
				+ restUrl + ", signature=" + signature + ", signedExpiry="
				+ signedExpiry + ", signedIdentifier=" + signedIdentifier
				+ ", signedPermissions=" + signedPermissions
				+ ", signedResource=" + signedResource + ", signedStart="
				+ signedStart + ", signedString=" + signedString + "]";
	}

}
