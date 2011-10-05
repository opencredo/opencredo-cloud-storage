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
package org.opencredo.cloud.storage.s3;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.StorageCommunicationException;
import org.opencredo.cloud.storage.StorageOperations;
import org.opencredo.cloud.storage.test.TestPropertiesAccessor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JCloudS3TemplateIntegrationTest {

    private AwsCredentials credentials = new AwsCredentials(TestPropertiesAccessor.getDefaultTestAwsKey(),
            TestPropertiesAccessor.getDefaultTestAwsSecretKey());

    private static String BUCKET_NAME = "template-test-" + UUID.randomUUID().toString();
    private static String KEY = "key1";
    private static String TEST_FILE_NAME = "test-s3.txt";

    private static File TEST_FILE;

    static {
        URL url = JCloudS3TemplateIntegrationTest.class.getResource(TEST_FILE_NAME);
        TEST_FILE = new File(url.getFile());
    }

    private StorageOperations template = null;

    @Before
    public void before() {
        template = new JCloudS3Template(credentials, TestPropertiesAccessor.getDefaultContainerName());
        template.createContainer(BUCKET_NAME);
    }

    @After
    public void after() {
        try {

            List<BlobDetails> objects = template.listContainerObjectDetails(BUCKET_NAME);
            for (BlobDetails blobObject : objects) {
                template.deleteObject(BUCKET_NAME, blobObject.getName());
            }

            template.deleteContainer(BUCKET_NAME);
        } catch (StorageCommunicationException e) {
            System.err.println("After failed: " + e.getMessage());
        }
    }

    @Test
    public void testRealFileUpload() throws IOException {
        template.send(BUCKET_NAME, KEY, TEST_FILE);

        File f = File.createTempFile(getClass().getSimpleName(), ".txt");
        FileUtils.forceDeleteOnExit(f);
        template.receiveAndSaveToFile(BUCKET_NAME, KEY, f);

        String receivedFileContent = FileUtils.readFileToString(f);
        System.out.println("Received file content: " + receivedFileContent);

        String orgFileContent = FileUtils.readFileToString(TEST_FILE);
        assertEquals("File content does not match", orgFileContent, receivedFileContent);
    }

    @Test
    public void testCreateTimeExpiredUrl() throws IOException {
        template.send(BUCKET_NAME, KEY, TEST_FILE);

        File f = File.createTempFile(getClass().getSimpleName(), ".txt");
        FileUtils.forceDeleteOnExit(f);
        template.receiveAndSaveToFile(BUCKET_NAME, KEY, f);

        String receivedFileContent = FileUtils.readFileToString(f);
        System.out.println("Received file content: " + receivedFileContent);

        String orgFileContent = FileUtils.readFileToString(TEST_FILE);
        assertEquals("File content does not match", orgFileContent, receivedFileContent);

        // Determine what the time will be in 5 minutes.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 5);
        Date expiryDate = cal.getTime();

        try {
            template.createdSignedUrl(BUCKET_NAME, KEY, expiryDate);
            assertFalse("Exception should have been thrown", true);
        } catch (StorageCommunicationException e) {
            assertTrue("Exception was thrown correctly", true);
        }

    }
}
