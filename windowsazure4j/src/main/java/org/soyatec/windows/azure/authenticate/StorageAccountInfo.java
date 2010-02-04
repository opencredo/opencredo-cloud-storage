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

import java.net.URI;

/**
 * Objects of this class encapsulate information about a storage account and
 * endpoint configuration. Associated with a storage account is the account
 * name, the base URI of the account and a shared key.
 * 
 */
public class StorageAccountInfo {
	
	public static final String DEVSTORE_ACCOUNT = "devstoreaccount1";

	public static final String DEVSTORE_KEY = "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==";

	private boolean usePathStyleUris;

	private URI baseUri;

	private String accountName;

	private String base64Key;

	/**
	 * Constructor for creating account info objects.
	 * 
	 * @param baseUri
	 *            The account's base URI.
	 * @param usePathStyleUris
	 *            If true, path-style URIs
	 *            (http://baseuri/accountname/containername/objectname) are
	 *            used,If false host-style URIs
	 *            (http://accountname.baseuri/containername/objectname) are
	 *            used, where baseuri is the URI of the service..
	 * @param accountName
	 *            The account name.
	 * @param base64Key
	 *            The account's shared key.
	 */
	public StorageAccountInfo(URI baseUri, boolean usePathStyleUris,
			String accountName, String base64Key) {
		this.usePathStyleUris = usePathStyleUris;
		this.baseUri = baseUri;
		this.accountName = accountName;
		this.base64Key = base64Key;

	}

	public boolean isUsePathStyleUris() {
		return usePathStyleUris;
	}

	public URI getBaseUri() {
		return baseUri;
	}

	public String getAccountName() {
		return accountName;
	}

	public String getBase64Key() {
		return base64Key;
	}

	public void setUsePathStyleUris(boolean usePathStyleUris) {
		this.usePathStyleUris = usePathStyleUris;
	}

	public void setBaseUri(URI baseUri) {
		this.baseUri = baseUri;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public void setBase64Key(String base64Key) {
		this.base64Key = base64Key;
	}

}
