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

import org.soyatec.windows.azure.authenticate.Base64;
import org.soyatec.windows.azure.util.Utilities;

public abstract class Configuration {
	
	protected String packageBlobUrl;
	
	protected String configurationFileUrl;
	
	private String base64ConfigurationFile;
	
	protected boolean isEmpty(String str) {
		return str == null || str.trim().equals("");
	}
	
	/**
	 * @return the packageBlobUrl
	 */
	public String getPackageBlobUrl() {
		return packageBlobUrl;
	}

	/**
	 * @param packageBlobUrl
	 *            the packageBlobUrl to set
	 */
	public void setPackageBlobUrl(String packageBlobUrl) {
		this.packageBlobUrl = packageBlobUrl;
	}

	/**
	 * @return the configurationFileUrl
	 */
	public String getConfigurationFileUrl() {
		return configurationFileUrl;
	}

	/**
	 * @param configurationFileUrl
	 *            the configurationFileUrl to set
	 */
	public void setConfigurationFileUrl(String configurationFileUrl) {
		this.configurationFileUrl = configurationFileUrl;
	}

	/**
	 * @return the base64ConfigurationFile
	 */
	public String getBase64ConfigurationFile() {
		if (base64ConfigurationFile == null) {
			readConfigurationContent();
		}
		return base64ConfigurationFile;
	}

	protected void readConfigurationContent() {
		File file = new File(configurationFileUrl);
		if (file.exists() && file.isFile() && file.canRead()) {
			try {
				byte[] bytes = Utilities.getBytesFromFile(file);
				if (base64ConfigurationFile == null)
					this.base64ConfigurationFile = Base64.encode(bytes);
			} catch (IOException e) {
				throw new IllegalStateException("Configuration file is invalid");
			}
		} else {
			throw new IllegalStateException("Configuration file is invalid");
		}
	}

}
