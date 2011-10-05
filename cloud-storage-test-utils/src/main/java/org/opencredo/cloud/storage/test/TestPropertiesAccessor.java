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
package org.opencredo.cloud.storage.test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class TestPropertiesAccessor {
    public static final String AWS_KEY_NAME = "awsKey";

    public static final String AWS_SECRET_KEY_NAME = "awsSecretKey";

    public static final String DEFAULT_CONTAINER_NAME = "defaultContainerName";

    public static final String AZURE_ACCOUNT_NAME = "azureAccountName";

    public static final String AZURE_SECRET_KEY = "azureSecretKey";

    private static Properties TEST_PROPERTIES;

    static {
        try {
            TEST_PROPERTIES = new Properties();
            TEST_PROPERTIES.load((new ClassPathResource("test.properties").getInputStream()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test.properties", e);
        }

    }

    public static String getDefaultTestAwsKey() {
        return get(AWS_KEY_NAME);
    }

    public static String getDefaultTestAwsSecretKey() {
        return get(AWS_SECRET_KEY_NAME);
    }

    public static String getDefaultContainerName() {
        return get(DEFAULT_CONTAINER_NAME);
    }

    /**
     * @return the azureAccountName from properties
     */
    public static String getAzureDefaultAccountName() {
        return get(AZURE_ACCOUNT_NAME);
    }

    /**
     * @return the azureSharedKey from properties
     */
    public static String getAzureDefaultSecretKey() {
        return get(AZURE_SECRET_KEY);
    }

    protected static String get(String propertyName) {
        String value = TEST_PROPERTIES.getProperty(propertyName);
        checkPropertySet(propertyName, value);
        return value;
    }

    protected static void checkPropertySet(String propertyName, String valueRetreived) {
        if (!StringUtils.hasText(valueRetreived) || valueRetreived.equals("${" + propertyName + "}")) {
            throw new RuntimeException("No value for key " + propertyName
                    + " found you may want to add this to your maven settings.xml");
        }
    }
}
