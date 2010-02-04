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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class HttpsX509TrustManager implements X509TrustManager
{
    private X509TrustManager defaultTrustManager = null; 

    /** Log object for this class. */
    private static final Log LOG = LogFactory.getLog(HttpsX509TrustManager.class); 

    /**
     * Constructor for HttpsX509TrustManager.
     */
    public HttpsX509TrustManager(final X509TrustManager defaultTrustManager) {
        super();
        if (defaultTrustManager == null) {
            throw new IllegalArgumentException("Trust manager may not be null");
        }
        this.defaultTrustManager = defaultTrustManager;
    } 
  

	/**
     */
    public boolean isClientTrusted(X509Certificate[] certificates) {
       return true;
    } 

    /**
     */
    public boolean isServerTrusted(X509Certificate[] certificates) {
        return true;
    } 

    /**
     */
    public X509Certificate[] getAcceptedIssuers() {
        return this.defaultTrustManager.getAcceptedIssuers();
    }

	public void checkClientTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
			
	}

	public void checkServerTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
		
	}
} 