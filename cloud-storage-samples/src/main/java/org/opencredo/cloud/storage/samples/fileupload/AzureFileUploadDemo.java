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
package org.opencredo.cloud.storage.samples.fileupload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.ContainerStatus;
import org.opencredo.cloud.storage.StorageOperations;
import org.opencredo.cloud.storage.azure.AzureCredentials;
import org.opencredo.cloud.storage.azure.AzureTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: Requirements to run this sample.
 * 
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class AzureFileUploadDemo {
    private final static Logger LOG = LoggerFactory.getLogger(AzureFileUploadDemo.class);

    // Properties file. To run this example you have to specify required properties.
    private static final String PROPERTIES_FILE = "fileUploadSample/fileUpload.properties";

    // Properties expected in properties file.
    private static final String AZURE_ACCOUNT = "azureAccount";
    private static final String AZURE_SECRET_KEY = "azureSecretKey";
    private static final String DEFAULT_CONTAINER_NAME = "defaultContainerName";

    // File to upload.
    private static final String FILE_TO_UPLOAD = "fileUploadSample/cloud.jpg";

    // File to save received blob.
    private static final String BLOB_JPG = "blob.jpg";

    public static void main(String[] args) throws IOException, URISyntaxException {
        // Properties containing credentials to connect to cloud storage.
        Properties p = loadAndValidate();

        // Credentials
        AzureCredentials ac = new AzureCredentials(p.getProperty(AZURE_ACCOUNT), p.getProperty(AZURE_SECRET_KEY));

        String containerName = p.getProperty(DEFAULT_CONTAINER_NAME);

        // Template to access cloud storage
        StorageOperations template = new AzureTemplate(ac, containerName);

        // Create container if it does not exist
        if (template.checkContainerStatus(containerName) == ContainerStatus.DOES_NOT_EXIST) {
            template.createContainer(containerName);
        }

        File fileToUpload = getFileToUpload();
        LOG.info("File to upload: {}", fileToUpload.getAbsolutePath());

        // Upload file to cloud storage. With blob name as file name.
        template.send(fileToUpload);

        List<BlobDetails> blobsInStorage = template.listContainerObjectDetails();
        LOG.info("There is {} blob(s) in cloud storage", blobsInStorage.size());
        for (BlobDetails blobDetails : blobsInStorage) {
            LOG.info("Blob in cloud storage: {}", blobDetails);
        }

        // Download the file from cloud storage
        File toFile = new File(BLOB_JPG);
        template.receiveAndSaveToFile(fileToUpload.getName(), toFile);

        LOG.info("Downloaded blob saved to file: {}", toFile.getAbsolutePath());

        // Remove blob from cloud storage
        template.deleteObject(fileToUpload.getName());

        blobsInStorage = template.listContainerObjectDetails();
        LOG.info("There is {} blob(s) in cloud storage", blobsInStorage.size());
    }

    private static Properties loadAndValidate() throws IOException {
        Properties props = new Properties();
        URL url = ClassLoader.getSystemResource(PROPERTIES_FILE);
        props.load(url.openStream());

        validateProperty(props, AZURE_ACCOUNT);
        validateProperty(props, AZURE_SECRET_KEY);
        validateProperty(props, DEFAULT_CONTAINER_NAME);

        LOG.info("Properties loaded from: {}", url);

        return props;
    }

    private static void validateProperty(Properties props, String propertyName) {
        String property = props.getProperty(propertyName);
        if (property == null || property.trim().length() == 0) {
            System.out.println("Property '" + propertyName + "' is not defained.");
            System.out.println("Please update '" + PROPERTIES_FILE + "' with your details!");
            System.exit(1);
        }
    }

    public static File getFileToUpload() throws URISyntaxException, FileNotFoundException {
        URL fileUrl = ClassLoader.getSystemResource(FILE_TO_UPLOAD);
        URI fileUri = fileUrl.toURI();

        File f = new File(fileUri);
        if (!f.exists()) {
            throw new FileNotFoundException("File '" + f.getAbsolutePath() + "' does not exist");
        }

        return f;
    }
}
