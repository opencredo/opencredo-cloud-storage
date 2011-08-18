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

import org.apache.commons.io.IOUtils;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.ContainerStatus;
import org.opencredo.cloud.storage.StorageCommunicationException;
import org.opencredo.cloud.storage.StorageException;
import org.opencredo.cloud.storage.StorageOperations;
import org.opencredo.cloud.storage.test.TestPropertiesAccessor;
import org.springframework.beans.DirectFieldAccessor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class JetS3TemplateTest {

    private AwsCredentials credentials = new AwsCredentials(TestPropertiesAccessor.getDefaultTestAwsKey(),
            TestPropertiesAccessor.getDefaultTestAwsSecretKey());

    private static String BUCKET_NAME = "bucket1";
    private static String KEY = "key1";
    private static String TEST_FILE_NAME = "test-s3.txt";
    private static final S3BucketNameMatcher S3_BUCKET_NAME_MATCHER = new S3BucketNameMatcher();
    private static final S3ObjectMatcher S3_OBJECT_MATCHER = new S3ObjectMatcher(KEY);

    private static File TEST_FILE;
    private static InputStream TEST_INPUT_STREAM;

    static {
        URL url = JetS3TemplateTest.class.getResource(TEST_FILE_NAME);
        TEST_FILE = new File(url.getFile());

        TEST_INPUT_STREAM = JetS3TemplateTest.class.getResourceAsStream(TEST_FILE_NAME);
    }

    private S3Service s3Service;
    private StorageOperations template = null;

    @Before
    public void before() {
        template = new JetS3Template(credentials, TestPropertiesAccessor.getDefaultContainerName());

        s3Service = mock(S3Service.class);

        DirectFieldAccessor accessor = new DirectFieldAccessor(template);
        accessor.setPropertyValue("s3Service", s3Service);
    }

    @Test(expected = StorageException.class)
    public void testConstructorWithWrongCredentials() {
        AwsCredentials invalidCredentials = new AwsCredentials("bla", "blaBla");
        new JetS3Template(invalidCredentials, TestPropertiesAccessor.getDefaultContainerName());
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#createContainer(java.lang.String)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test(expected = StorageCommunicationException.class)
    public void testCreateBucketCauseS3CommunicationException() throws S3ServiceException {

        when(s3Service.createBucket(argThat(S3_BUCKET_NAME_MATCHER))).thenThrow(new S3ServiceException());
        template.createContainer(BUCKET_NAME);
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#createContainer(java.lang.String)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test
    public void testCreateBucket() throws S3ServiceException {
        when(s3Service.createBucket(argThat(S3_BUCKET_NAME_MATCHER))).thenReturn(null);
        template.createContainer(BUCKET_NAME);
        verify(s3Service).createBucket(argThat(S3_BUCKET_NAME_MATCHER));
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#deleteContainer(java.lang.String)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test(expected = StorageCommunicationException.class)
    public void testDeleteBucketCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).deleteBucket(argThat(S3_BUCKET_NAME_MATCHER));
        template.deleteContainer(BUCKET_NAME);
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#deleteContainer(java.lang.String)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test
    public void testDeleteBucket() throws S3ServiceException {
        doNothing().when(s3Service).deleteBucket(argThat(S3_BUCKET_NAME_MATCHER));
        template.deleteContainer(BUCKET_NAME);
        verify(s3Service).deleteBucket(argThat(S3_BUCKET_NAME_MATCHER));
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#deleteObject(java.lang.String, java.lang.String)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test(expected = StorageCommunicationException.class)
    public void testDeleteObjectCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).deleteObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));
        template.deleteObject(BUCKET_NAME, KEY);
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#deleteObject(java.lang.String, java.lang.String)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test
    public void testDeleteObject() throws S3ServiceException {
        doNothing().when(s3Service).deleteObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));
        template.deleteObject(BUCKET_NAME, KEY);
        verify(s3Service).deleteObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#listContainerNames()}.
     *
     * @throws S3ServiceException
     */
    @Test(expected = StorageCommunicationException.class)
    public void testListBucketsCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).listAllBuckets();
        template.listContainerNames();
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#listContainerNames()}.
     *
     * @throws S3ServiceException
     */
    @Test
    public void testListBuckets() throws S3ServiceException {
        S3Bucket[] objs = new S3Bucket[]{new S3Bucket("name1"), new S3Bucket("name2")};
        doReturn(objs).when(s3Service).listAllBuckets();
        List<String> listBuckets = template.listContainerNames();
        verify(s3Service).listAllBuckets();

        assertEquals(2, listBuckets.size());
        assertEquals("name1", listBuckets.get(0));
        assertEquals("name2", listBuckets.get(1));
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#checkContainerStatus(java.lang.String)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test(expected = StorageCommunicationException.class)
    public void testGetBucketStatusCauseStorageCommunicationException() throws ServiceException {
        doThrow(new ServiceException()).when(s3Service).checkBucketStatus(eq(BUCKET_NAME));
        template.checkContainerStatus(BUCKET_NAME);
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#checkContainerStatus(java.lang.String)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test(expected = StorageException.class)
    public void testGetBucketStatusCauseStorageException() throws ServiceException {
        doReturn(-1).when(s3Service).checkBucketStatus(eq(BUCKET_NAME));
        template.checkContainerStatus(BUCKET_NAME);
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#checkContainerStatus(java.lang.String)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test
    public void testGetBucketStatus() throws ServiceException {
        ContainerStatus bucketStatus;

        doReturn(S3Service.BUCKET_STATUS__MY_BUCKET).when(s3Service).checkBucketStatus(eq(BUCKET_NAME));
        bucketStatus = template.checkContainerStatus(BUCKET_NAME);
        assertEquals(ContainerStatus.MINE, bucketStatus);

        doReturn(S3Service.BUCKET_STATUS__DOES_NOT_EXIST).when(s3Service).checkBucketStatus(eq(BUCKET_NAME));
        bucketStatus = template.checkContainerStatus(BUCKET_NAME);
        assertEquals(ContainerStatus.DOES_NOT_EXIST, bucketStatus);

        doReturn(S3Service.BUCKET_STATUS__ALREADY_CLAIMED).when(s3Service).checkBucketStatus(eq(BUCKET_NAME));
        bucketStatus = template.checkContainerStatus(BUCKET_NAME);
        assertEquals(ContainerStatus.ALREADY_CLAIMED, bucketStatus);

        verify(s3Service, times(3)).checkBucketStatus(eq(BUCKET_NAME));
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#listContainerObjectDetails(java.lang.String)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test(expected = StorageCommunicationException.class)
    public void testListBucketObjectsCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).listObjects(argThat(S3_BUCKET_NAME_MATCHER));
        template.listContainerObjectDetails(BUCKET_NAME);
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#listContainerObjectDetails(java.lang.String)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test
    public void testListBucketObjects() throws S3ServiceException {
        long currentMills = System.currentTimeMillis();
        S3Object[] objs = new S3Object[]{new S3Object(), new S3Object(), new S3Object()};
        for (int i = 0; i < objs.length; i++) {
            objs[i].setKey("key" + i);
            objs[i].setETag("eTag" + i);
            objs[i].setLastModifiedDate(new Date(currentMills - (24 * 60 * 60 * 1000 * i)));
        }

        doReturn(objs).when(s3Service).listObjects(argThat(S3_BUCKET_NAME_MATCHER));
        List<BlobDetails> bucketObjects = template.listContainerObjectDetails(BUCKET_NAME);
        verify(s3Service).listObjects(argThat(S3_BUCKET_NAME_MATCHER));
        assertEquals(objs.length, bucketObjects.size());

        for (int i = 0; i < objs.length; i++) {
            assertEquals(objs[i].getKey(), bucketObjects.get(i).getName());
            assertEquals(objs[i].getETag(), bucketObjects.get(i).getETag());
            assertEquals(objs[i].getLastModifiedDate(), bucketObjects.get(i).getLastModified());
        }
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#send(java.lang.String, java.lang.String)}
     *
     * @throws S3ServiceException
     */
    @Test(expected = StorageCommunicationException.class)
    public void testSendStringCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).putObject(argThat(S3_BUCKET_NAME_MATCHER),
                argThat(S3_OBJECT_MATCHER));
        template.send(BUCKET_NAME, KEY, "test-payload");
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#send(java.lang.String, java.lang.String)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test
    public void testSendString() throws S3ServiceException {
        doReturn(null).when(s3Service).putObject(argThat(S3_BUCKET_NAME_MATCHER), argThat(S3_OBJECT_MATCHER));
        template.send(BUCKET_NAME, KEY, "test-payload");
        verify(s3Service).putObject(argThat(S3_BUCKET_NAME_MATCHER), argThat(S3_OBJECT_MATCHER));
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#send(java.lang.String, java.lang.String, java.io.File)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test(expected = StorageCommunicationException.class)
    public void testSendFileCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).putObject(argThat(S3_BUCKET_NAME_MATCHER),
                argThat(new S3ObjectMatcher(TEST_FILE_NAME)));
        template.send(BUCKET_NAME, TEST_FILE);
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#send(java.lang.String, java.lang.String, java.io.File)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test
    public void testSendFile() throws S3ServiceException {
        doReturn(null).when(s3Service).putObject(argThat(S3_BUCKET_NAME_MATCHER), argThat(S3_OBJECT_MATCHER));
        template.send(BUCKET_NAME, KEY, TEST_FILE);
        verify(s3Service).putObject(argThat(S3_BUCKET_NAME_MATCHER), argThat(S3_OBJECT_MATCHER));
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#send(java.lang.String, java.lang.String, java.io.InputStream)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test(expected = StorageCommunicationException.class)
    public void testSendInputStreamCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).putObject(argThat(S3_BUCKET_NAME_MATCHER),
                argThat(S3_OBJECT_MATCHER));
        template.send(BUCKET_NAME, KEY, TEST_INPUT_STREAM);
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#send(java.lang.String, java.lang.String, java.io.InputStream)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test
    public void testSendInputStream() throws S3ServiceException {
        doReturn(null).when(s3Service).putObject(argThat(S3_BUCKET_NAME_MATCHER), argThat(S3_OBJECT_MATCHER));
        template.send(BUCKET_NAME, KEY, TEST_FILE);
        verify(s3Service).putObject(argThat(S3_BUCKET_NAME_MATCHER), argThat(S3_OBJECT_MATCHER));
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#receiveAsString(java.lang.String)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test(expected = StorageCommunicationException.class)
    public void testReceiveAsStringCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).getObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));
        template.receiveAsString(BUCKET_NAME, KEY);
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#receiveAsString(java.lang.String)}
     * .
     *
     * @throws S3ServiceException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void testReceiveAsString() throws S3ServiceException, NoSuchAlgorithmException, IOException {
        String data = "test data\ntest data2";
        doReturn(new S3Object(KEY, data)).when(s3Service).getObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));
        String received = template.receiveAsString(BUCKET_NAME, KEY);
        verify(s3Service).getObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));

        assertEquals(data, received);
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#receiveAndSaveToFile(java.lang.String, File)}
     * .
     *
     * @throws S3ServiceException
     * @throws IOException
     * @throws StorageCommunicationException
     */
    @Test(expected = StorageCommunicationException.class)
    public void testReceiveAsFileCauseS3CommunicationException() throws S3ServiceException,
            StorageCommunicationException, IOException {
        doThrow(new S3ServiceException()).when(s3Service).getObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));
        template.receiveAndSaveToFile(BUCKET_NAME, KEY, File.createTempFile(getClass().getSimpleName(), ".txt"));
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#receiveAndSaveToFile(java.lang.String, File)}
     * .
     *
     * @throws S3ServiceException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void testReceiveAsFile() throws S3ServiceException, NoSuchAlgorithmException, IOException {
        doReturn(new S3Object(TEST_FILE)).when(s3Service).getObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));

        template.receiveAndSaveToFile(BUCKET_NAME, KEY, File.createTempFile(getClass().getSimpleName(), ".txt"));

        verify(s3Service).getObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#receiveAsInputStream(java.lang.String)}
     * .
     *
     * @throws S3ServiceException
     */
    @Test(expected = StorageCommunicationException.class)
    public void testReceiveAsInputStreamStringCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).getObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));
        template.receiveAsInputStream(BUCKET_NAME, KEY);
    }

    /**
     * Test method for
     * {@link org.opencredo.cloud.storage.s3.S3Template#receiveAsInputStream(java.lang.String)}
     * .
     *
     * @throws S3ServiceException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void testReceiveAsInputStream() throws S3ServiceException, NoSuchAlgorithmException, IOException {
        String data = "test data\ntest data2";
        doReturn(new S3Object(KEY, data)).when(s3Service).getObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));
        InputStream received = template.receiveAsInputStream(BUCKET_NAME, KEY);
        verify(s3Service).getObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));

        String receivedStr = IOUtils.toString(received);
        System.out.println("String from input stream: " + receivedStr);
        assertEquals(data, receivedStr);
    }

    /**
     * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
     */
    static class S3BucketNameMatcher extends ArgumentMatcher<S3Bucket> {
        public boolean matches(Object obj) {
            return ((S3Bucket) obj).getName() == BUCKET_NAME;
        }
    }

    /**
     * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
     */
    static class S3ObjectMatcher extends ArgumentMatcher<S3Object> {
        private String key;

        /**
         * @param key
         */
        public S3ObjectMatcher(String key) {
            super();
            this.key = key;
        }

        public boolean matches(Object obj) {
            S3Object s3o = (S3Object) obj;
            return key.equals(s3o.getKey());
        }
    }
}
