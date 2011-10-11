/* Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opencredo.cloud.storage.azure;

import org.springframework.util.Assert;

/**
 * Credentials used to interact with Azure cloud storage.
 *
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class AzureCredentials {

    private final String accountName;
    private final String secretKey;

    /**
     * @param accountName
     * @param secretKey
     */
    public AzureCredentials(String accountName, String secretKey) {
        super();
        Assert.hasText("Azure account name must be specified", accountName);
        Assert.hasText("Azure secret key must be specified", secretKey);
        this.accountName = accountName;
        this.secretKey = secretKey;
    }

    /**
     * @return the accountName
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * @return the secretKey
     */
    public String getSecretKey() {
        return secretKey;
    }

}
