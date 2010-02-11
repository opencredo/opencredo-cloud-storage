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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.jets3t.service.S3ServiceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opencredo.cloud.storage.BlobObject;
import org.opencredo.cloud.storage.StorageCommunicationException;
import org.opencredo.cloud.storage.StorageOperations;
import org.opencredo.cloud.storage.s3.AwsCredentials;
import org.opencredo.cloud.storage.s3.S3Template;
import org.opencredo.cloud.storage.test.TestPropertiesAccessor;

/**
 * 
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
@Ignore
public class S3TemplateIntegrationTest {

    private AwsCredentials credentials = new AwsCredentials(TestPropertiesAccessor.getDefaultTestAwsKey(),
            TestPropertiesAccessor.getDefaultTestAwsSecretKey());

    private static String BUCKET_NAME = "template-test-" + UUID.randomUUID().toString();
    private static String KEY = "key1";
    private static String TEST_FILE_NAME = "test-s3.txt";

    private static File TEST_FILE;

    static {
        URL url = S3TemplateIntegrationTest.class.getResource(TEST_FILE_NAME);
        TEST_FILE = new File(url.getFile());
    }

    private StorageOperations template = null;

    @Before
    public void before() {
        template = new S3Template(credentials, TestPropertiesAccessor.getS3DefaultBucketName());
        template.createContainer(BUCKET_NAME);
    }

    @After
    public void after() {
        try {

            List<BlobObject> objects = template.listContainerObjects(BUCKET_NAME);
            for (BlobObject blobObject : objects) {
                template.deleteObject(BUCKET_NAME, blobObject.getName());
            }

            template.deleteContainer(BUCKET_NAME);
        } catch (StorageCommunicationException e) {
            System.err.println("After failed: " + e.getMessage());
        }
    }

    @Test
    public void testRealFileUpload() throws S3ServiceException, StorageCommunicationException, IOException {
        template.send(BUCKET_NAME, KEY, TEST_FILE);

        File f = File.createTempFile(getClass().getSimpleName(), ".txt");
        FileUtils.forceDeleteOnExit(f);
        template.receiveAndSaveToFile(BUCKET_NAME, KEY, f);

        String receivedFileContent = FileUtils.readFileToString(f);
        System.out.println("Received file content: " + receivedFileContent);

        String orgFileContent = FileUtils.readFileToString(TEST_FILE);
        assertEquals("File conetent does not match", orgFileContent, receivedFileContent);
    }
}