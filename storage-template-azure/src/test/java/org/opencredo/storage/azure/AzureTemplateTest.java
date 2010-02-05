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
package org.opencredo.storage.azure;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opencredo.aws.s3.TestPropertiesAccessor;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
@Ignore
public class AzureTemplateTest {

    private AzureCredentials credentials = new AzureCredentials(TestPropertiesAccessor.getAzureDefaultAccountName(),
            TestPropertiesAccessor.getAzureDefaultSecretKey());
    private AzureTemplate template;

    @Before
    public void setUp() {
        template = new AzureTemplate(credentials);
    }

    @Test
    public void testAll() throws Exception {
        String containerName = "template-test-1";
        String objectName = "string-1";
        
        template.createContainer(containerName);
        System.out.println("---------------------------------------------");
        template.listContainerObjects(containerName);
        System.out.println("---------------------------------------------");
        template.send(containerName, objectName, "testString");
        System.out.println("---------------------------------------------");
        template.receiveAsString(containerName, objectName);
        System.out.println("---------------------------------------------");
        template.listContainerObjects(containerName);
        System.out.println("---------------------------------------------");
        template.deleteObject(containerName, objectName);
        System.out.println("---------------------------------------------");
        template.listContainerObjects(containerName);
        System.out.println("---------------------------------------------");
        template.deleteContainer(containerName);
    }
    
    @Test
    public void testListContainers() throws Exception {
        template.listContainers();
    }
}
