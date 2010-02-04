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

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

public class KeyTools {
    
    public static final String PASSWORD = "changeit";
    public static final String ALIAS = "cert";
    public static final String DNAME = "CN=soyatec, OU=soyatec.group, O=soyatec.inc, L=shenzhen, ST=guangdong, C=CN";
    public static final String CERTIFICATE_NAME = "azure.cer";
    public static final String KEYSTORE = ".keystore";
    public static final String TRUSTSTORE = ".trustcacerts";
    

	public static KeyPair generateKeys() throws NoSuchAlgorithmException,
			NoSuchProviderException {
		return generateKeys(2048);
	}

	public static KeyPair generateKeys(int keysize)
			throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA", "BC");
		keygen.initialize(keysize);

		KeyPair rsaKeys = keygen.generateKeyPair();
		return rsaKeys;
	}

	public static void generateSelfCertificate(String keystore,
			String password, String alias, String dname, int validDays)
			throws IOException, InterruptedException {
		prepareFolder(keystore);
		List<String> commands = new ArrayList<String>();
		commands.add("keytool");
		commands.add("-selfcert");
		commands.add("-genkey");
		commands.add("-keyalg");
		commands.add("RSA");
		commands.add("-keysize");
		commands.add("2048");
		commands.add("-alias");
		commands.add(alias);
		commands.add("-validity");
		commands.add("" + validDays);
		commands.add("-dname");
		commands.add("\"" + dname + "\"");

		commands.add("-keystore");
		commands.add(keystore);
		commands.add("-keypass");
		commands.add(password);
		commands.add("-storepass");
		commands.add(password);

		execCommand(commands);
	}

	private static void prepareFolder(String keystore) {
		File file = new File(keystore);
		if(file.exists())
			return;
		try {
			File dir = file.getParentFile();
			dir.mkdirs();
		} catch (Exception e) {			
			
		}
	}

	/**
	 * @param commands
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void execCommand(List<String> commands) throws IOException,
			InterruptedException {
		String[] command = commands.toArray(new String[commands.size()]);
		Process exec = Runtime.getRuntime().exec(command);

		exec.waitFor();
	}

	public static void exportCertificate(String keystore, String password,
			String alias, String certFileName) throws IOException,
			InterruptedException {		
		prepareFolder(certFileName);
		List<String> commands = new ArrayList<String>();
		commands.add("keytool");
		commands.add("-export");
		commands.add("-rfc");
		commands.add("-keystore");
		commands.add(keystore);
		commands.add("-storepass");
		commands.add(password);
		commands.add("-alias");
		commands.add(alias);
		commands.add("-file");
		commands.add(certFileName);
		execCommand(commands);
	}

	public static void importCertificateToTrustStore(String keystore,
			String password, String alias, String certFileName)
			throws IOException, InterruptedException {
		List<String> commands = new ArrayList<String>();
		commands.add("keytool");
		commands.add("-import");
		commands.add("-trustcacerts");
		commands.add("-noprompt");
		commands.add("-keystore");
		commands.add(keystore);
		commands.add("-storepass");
		commands.add(password);
		commands.add("-alias");
		commands.add(alias);
		commands.add("-file");
		commands.add(certFileName);
		execCommand(commands);
	}

	public static void main(String[] args) {
		try {
			generateSelfCertificate(
					"F:/Azure/keys/.keystore",
					"changeit",
					"cert",
					"CN=soyatec, OU=vg.group, O=soyatec.inc, L=shenzhen, ST=guangdong, C=CN",
					7200);
			exportCertificate("F:/Azure/keys/.keystore", "changeit", "cert",
					"F:/Azure/keys/test.cer");
			importCertificateToTrustStore("F:/Azure/keys/.trustcacerts",
					"changeit", "cert", "F:/Azure/keys/test.cer");
			// Thread.sleep(5000);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
