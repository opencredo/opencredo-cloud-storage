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

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

public class SslUtil {
	public static final String CERT_ALIAS = "soyatec";
	public static final String KEYSTORE_PASS = "soyatec";
	public static final String KEYSTORE = ".keystore";

	public static String importCertificate(String x509Cert) throws Exception {
		// CREATE A KEYSTORE OF TYPE "Java Key Store"
		KeyStore ks = KeyStore.getInstance("JKS");
		/*
		 * LOAD THE STORE The first time you're doing this (i.e. the keystore
		 * does not yet exist - you're creating it), you HAVE to load the
		 * keystore from a null source with null password. Before any methods
		 * can be called on your keystore you HAVE to load it first. Loading it
		 * from a null source and null password simply creates an empty
		 * keystore. At a later time, when you want to verify the keystore or
		 * get certificates (or whatever) you can load it from the file with
		 * your password.
		 */
		ks.load(null, null);
		// GET THE FILE CONTAINING YOUR CERTIFICATE
		File x509 = new File(x509Cert);
		FileInputStream fis = new FileInputStream(x509);
		BufferedInputStream bis = new BufferedInputStream(fis);
		// I USE x.509 BECAUSE THAT'S WHAT keytool CREATES
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		// NOTE: THIS IS java.security.cert.Certificate NOT
		// java.security.Certificate
		X509Certificate cert = (X509Certificate) cf.generateCertificate(bis);

		ks.setCertificateEntry(CERT_ALIAS, cert);
		// SAVE THE KEYSTORE TO A FILE
		/*
		 * After this is saved, I believe you can just do setCertificateEntry to
		 * add entries and then not call store. I believe it will update the
		 * existing store you load it from and not just in memory.
		 */
		File storeFile = new File(x509.getParentFile().getAbsolutePath(),
				KEYSTORE);
		ks.store(new FileOutputStream(storeFile), KEYSTORE_PASS.toCharArray());

		return storeFile.getAbsolutePath();
	}

	public static X509Certificate getCertificateFromFile(String cert)
			throws Exception {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		FileInputStream in = new FileInputStream(cert);
		X509Certificate c = (X509Certificate) cf.generateCertificate(in);
		return c;
	}

	@SuppressWarnings("deprecation")
	public static X509Certificate getCertificateFromStore(String store,
			String alias, String password) throws Exception {
		KeyStore ks = getKeyStore(new File(store).toURL(), password);
		X509Certificate c = (X509Certificate) ks.getCertificate(alias);
		return c;
	}

	@SuppressWarnings("deprecation")
	public static PrivateKey getPrivateKey(String storePath, String password,
			String alias) throws Exception {
		KeyStore store = getKeyStore(new File(storePath).toURL(), password);
		return (PrivateKey) store.getKey(password, password.toCharArray());
	}

	public static KeyStore getKeyStore(final URL url, final String password)
			throws Exception {
		if (url == null) {
			throw new IllegalArgumentException("Keystore url may not be null");
		}
		KeyStore keystore = KeyStore.getInstance("jks");
		InputStream is = null;
		try {
			is = url.openStream();
			keystore.load(is, password != null ? password.toCharArray() : null);
		} finally {
			if (is != null)
				is.close();
		}
		return keystore;
	}

	private static KeyManager[] createKeyManagers(final KeyStore keystore,
			final String password) throws Exception {
		if (keystore == null) {
			throw new IllegalArgumentException("Keystore may not be null");
		}
		KeyManagerFactory kmfactory = KeyManagerFactory
				.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmfactory.init(keystore, password != null ? password.toCharArray()
				: null);
		return kmfactory.getKeyManagers();
	}

	private static TrustManager[] createTrustManagers(final KeyStore keystore)
			throws KeyStoreException, NoSuchAlgorithmException {
		if (keystore == null) {
			throw new IllegalArgumentException("Keystore may not be null");
		}
		TrustManagerFactory tmfactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmfactory.init(keystore);
		TrustManager[] trustmanagers = tmfactory.getTrustManagers();
		return trustmanagers;
	}

	@SuppressWarnings("deprecation")
	public static SSLContext createSSLContext(String keyStoreUrl,
			String keyStorePassword, String trustStoreUrl,
			String trustStorePassword, String certAlias) throws Exception {

		KeyManager[] keymanagers = null;
		TrustManager[] trustmanagers = null;

		KeyStore keystore = getKeyStore(new File(keyStoreUrl).toURL(),
				keyStorePassword);

		PrivateKey privateKey = (PrivateKey) keystore.getKey(certAlias,
				keyStorePassword.toCharArray());
		X509Certificate cert = (X509Certificate) keystore
				.getCertificate(certAlias);

		keymanagers = createKeyManagers(keystore, keyStorePassword);
		for (int i = 0; i < keymanagers.length; i++) {

			if (keymanagers[i] instanceof X509ExtendedKeyManager) {
				keymanagers[i] = new HttpsX509KeyManager(
						(X509ExtendedKeyManager) keymanagers[i], certAlias,
						privateKey, cert);
			}

		}
		SSLContext sslcontext = SSLContext.getInstance("TLS");
		KeyStore trustStore = getKeyStore(new File(trustStoreUrl).toURL(),
				trustStorePassword);
		trustmanagers = createTrustManagers(trustStore);
		for (int i = 0; i < trustmanagers.length; i++) {
			if (trustmanagers[i] instanceof X509TrustManager) {
				trustmanagers[i] = new HttpsX509TrustManager(
						(X509TrustManager) trustmanagers[i]);
			}
		}
		sslcontext.init(keymanagers, trustmanagers, null);

		return sslcontext;
	}

	@SuppressWarnings("unused")
	private static void loadWindowsCert() throws Exception {
		KeyStore ks = KeyStore.getInstance("Windows-MY");// "Windows-ROOT"
		ks.load(null, null);
		Enumeration<String> en = ks.aliases();
		while (en.hasMoreElements()) {
			String key = en.nextElement();
			Certificate[] certs = ks.getCertificateChain(key);
			X509Certificate cert = (X509Certificate) certs[0];
			System.out.println(key + ":" + cert.getType());
		}
	}

	public static SSLSocketFactory getSSLSocketFactory(SSLContext context) {
		SSLSocketFactory factory = new SSLSocketFactory(context);
		factory
				.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		return factory;
	}

	public static SSLSocketFactory createSSLSocketFactory(String keyStoreUrl,
			String keyStorePassword, String trustStoreUrl,
			String trustStorePassword, String certAlias) throws Exception {

		SSLContext context = createSSLContext(keyStoreUrl, keyStorePassword,
				trustStoreUrl, trustStorePassword, certAlias);
		return getSSLSocketFactory(context);
	}
}
