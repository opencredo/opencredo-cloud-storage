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
package org.soyatec.windows.azure.util.ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;

public class HttpsX509KeyManager implements X509KeyManager  {

	private final X509ExtendedKeyManager manager;
	private final String certAlias;
	private final PrivateKey privateKey;
	private final X509Certificate certificate;

	public HttpsX509KeyManager(X509ExtendedKeyManager x509ExtendedKeyManager, String certAlias, PrivateKey privateKey, X509Certificate cert) {
		this.manager = x509ExtendedKeyManager;
		this.certAlias = certAlias;
		this.privateKey = privateKey;
		this.certificate = cert;
	}

	public String chooseClientAlias(String[] keyType, Principal[] issuers,
			Socket socket) {		
		return certAlias;
	}

	public String chooseEngineClientAlias(String[] arg0, Principal[] arg1,
			SSLEngine arg2) {
		return certAlias;
	}

	public String chooseEngineServerAlias(String arg0, Principal[] arg1,
			SSLEngine arg2) {
		return manager.chooseEngineServerAlias(arg0, arg1, arg2);
	}

	public String chooseServerAlias(String keyType, Principal[] issuers,
			Socket socket) {
		return manager.chooseServerAlias(keyType, issuers, socket);
	}

	public X509Certificate[] getCertificateChain(String alias) {

		return new X509Certificate[]{certificate};		
	}

	public String[] getClientAliases(String keyType, Principal[] issuers) {
		return manager.getClientAliases(keyType, issuers);
	}

	public PrivateKey getPrivateKey(String alias) {
		if(alias.equals(this.certAlias))
			return privateKey;
		else
			return manager.getPrivateKey(alias);
	}

	public String[] getServerAliases(String keyType, Principal[] issuers) {
		return manager.getServerAliases(keyType, issuers);
	}

	
}
