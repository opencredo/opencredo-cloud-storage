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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.ContainerStatus;
import org.opencredo.cloud.storage.StorageCommunicationException;
import org.opencredo.cloud.storage.azure.rest.AzureRestServiceUtil;
import org.opencredo.cloud.storage.test.TestPropertiesAccessor;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
@Ignore
public class AzureTemplateTest {

    private static final String TEST_CONATINER_PREFIX = "template-test-";

    private static String TEST_FILE_NAME = "test-azure.txt";

    private static File TEST_FILE;

    static {
        URL url = AzureTemplateTest.class.getResource(TEST_FILE_NAME);
        TEST_FILE = new File(url.getFile());
    }

    private AzureCredentials credentials = new AzureCredentials(TestPropertiesAccessor.getAzureDefaultAccountName(),
            TestPropertiesAccessor.getAzureDefaultSecretKey());
    private AzureTemplate template;

    @Before
    public void setUp() throws InterruptedException {
        template = new AzureTemplate(credentials);
    }

    @After
    public void tearDown() {
        try {
            List<String> containerNames = template.listContainerNames();
            for (String containerName : containerNames) {
                if (containerName.startsWith(TEST_CONATINER_PREFIX)) {
                    template.deleteContainer(containerName);
                }
            }

        } catch (StorageCommunicationException e) {
            System.err.println("Cleaning failed: " + e.getMessage());
        }
    }

    /**
     * Test list/create/delete container and also list/create/delete blob in
     * container.
     */
    @Test
    public void testMajor() {
        String containerName = TEST_CONATINER_PREFIX + UUID.randomUUID().toString();

        List<String> containers = template.listContainerNames();
        assertTrue("Unexpected amount of containers", containers.isEmpty());

        String objectName = "string-1";

        // Create container
        template.createContainer(containerName);
        {
            containers = template.listContainerNames();
            assertTrue("Unexpected amount of containers", containers.size() == 1);
            System.out.println("Existing container in Azure: " + containers.get(0));
            assertEquals("Container name should match", containerName, containers.get(0));
        }

        // Get empty list of container blobs
        List<BlobDetails> containerObjects = template.listContainerObjectDetails(containerName);
        assertNotNull("Container object list should be created", containerObjects);
        assertTrue("Container object list should be empty", containerObjects.isEmpty());

        // Add blob
        String stringToSend = "Test message: " + AzureRestServiceUtil.currentTimeStringInRFC1123();
        template.send(containerName, objectName, stringToSend);

        containerObjects = template.listContainerObjectDetails(containerName);
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
        containers = template.listContainerNames();
        assertTrue("Unexpected amount of containers", containers.isEmpty());
    }

    @Test
    public void testReceiveAndSaveToFile() throws IOException {
        String containerName = TEST_CONATINER_PREFIX + UUID.randomUUID().toString();

        template.createContainer(containerName);

        String objectName = "string-1";

        template.send(containerName, objectName, TEST_FILE);

        File f = File.createTempFile(getClass().getSimpleName(), ".txt");
        FileUtils.forceDeleteOnExit(f);
        template.receiveAndSaveToFile(containerName, objectName, f);

        String receivedFileContent = FileUtils.readFileToString(f);
        System.out.println("Received file content: " + receivedFileContent);

        String orgFileContent = FileUtils.readFileToString(TEST_FILE);
        assertEquals("File conetent does not match", orgFileContent, receivedFileContent);
    }

    @Test
    public void testContainerStatus() {
        ContainerStatus containerStatus = template.checkContainerStatus("abcefg-" + UUID.randomUUID().toString());
        assertEquals("Invalid container status", ContainerStatus.DOES_NOT_EXIST, containerStatus);

        String containerName = TEST_CONATINER_PREFIX + UUID.randomUUID().toString();
        template.createContainer(containerName);

        containerStatus = template.checkContainerStatus(containerName);
        assertEquals("Invalid container status", ContainerStatus.MINE, containerStatus);
    }
    
    @Test(expected = StorageCommunicationException.class)
    public void testDeleteNonExistingContainers() {
        template.deleteContainer("abcefg-" + UUID.randomUUID().toString());
    }

    @Test(expected = StorageCommunicationException.class)
    public void testDeleteNonExistingBlob() {
        template.deleteObject("abcefg-" + UUID.randomUUID().toString(), "hijklmnop");
    }
}
