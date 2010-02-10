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

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opencredo.aws.s3.TestPropertiesAccessor;
import org.opencredo.storage.BlobObject;
import org.opencredo.storage.azure.rest.AzureRestServiceException;
import org.opencredo.storage.azure.rest.AzureRestServiceUtil;

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
    public void setUp() throws InterruptedException {
        template = new AzureTemplate(credentials);
    }

    @Test
    public void testGetContainers() {
        String[] containers = template.listContainers();
        assertTrue("Unexpected amount of containers", containers.length == 0);

        String containerName = "template-test-" + UUID.randomUUID().toString();
        String objectName = "string-1";

        // Create container
        template.createContainer(containerName);
        {
            containers = template.listContainers();
            assertTrue("Unexpected amount of containers", containers.length == 1);
            System.out.println("Existing container in Azure: " + containers[0]);
            assertEquals("Container name should match", containerName, containers[0]);
        }
        
        // Get empty list of container blobs
        List<BlobObject> containerObjects = template.listContainerObjects(containerName);
        assertNotNull("Container object list should be created", containerObjects);
        assertTrue("Container object list should be empty", containerObjects.isEmpty());
        
        // Add blob
        String stringToSend = "Test message: " + AzureRestServiceUtil.currentTimeStringInRFC1123();
        template.send(containerName, objectName, stringToSend);

        containerObjects = template.listContainerObjects(containerName);
        assertNotNull("Container object list should be created", containerObjects);
        assertEquals("Incorrect container object list size", 1, containerObjects.size());
        assertEquals("Incorrect blob name", objectName, containerObjects.get(0).getName());
        
        // Receive blob
        String receiveAsString = template.receiveAsString(containerName, objectName);
        System.out.println("Received string: " + receiveAsString);
        assertEquals("Send and received strings should match", stringToSend, receiveAsString);

        // Delete blob
        template.deleteObject(containerName, objectName);

        // Delete container
        template.deleteContainer(containerName);
        containers = template.listContainers();
        assertTrue("Unexpected amount of containers", containers.length == 0);
    }

    @Ignore
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

    @Ignore
    @Test
    public void testListContainers() {
        template.listContainers();
    }

    @Ignore
    @Test(expected = AzureRestServiceException.class)
    public void testDeleteNonExistingContainers() {
        template.deleteContainer("abcefg");
    }

    @Ignore
    @Test(expected = AzureRestServiceException.class)
    public void testDeleteNonExistingBlob() {
        template.deleteObject("abcefg", "hijklmnop");
    }
}
