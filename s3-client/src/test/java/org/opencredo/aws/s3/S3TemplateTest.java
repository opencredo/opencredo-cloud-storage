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
package org.opencredo.aws.s3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.opencredo.aws.AwsCommunicationException;
import org.opencredo.aws.AwsCredentials;
import org.opencredo.aws.AwsException;
import org.opencredo.aws.AwsOperations;
import org.opencredo.aws.BlobObject;
import org.springframework.beans.DirectFieldAccessor;

/**
 * 
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class S3TemplateTest {

    private AwsCredentials credentials = new AwsCredentials(TestPropertiesAccessor.getDefaultTestAwsKey(),
            TestPropertiesAccessor.getDefaultTestAwsSecretKey());

    private static String BUCKET_NAME = "bucket1";
    private static String KEY = "key1";
    private static String TEST_FILE_NAME = "test.txt";
    private static final S3BucketNameMatcher S3_BUCKET_NAME_MATCHER = new S3BucketNameMatcher();
    private static final S3ObjectMatcher S3_OBJECT_MATCHER = new S3ObjectMatcher(KEY);

    private static File TEST_FILE;
    private static InputStream TEST_INPUT_STREAM;

    static {
        URL url = S3TemplateTest.class.getResource(TEST_FILE_NAME);
        TEST_FILE = new File(url.getFile());

        TEST_INPUT_STREAM = S3TemplateTest.class.getResourceAsStream(TEST_FILE_NAME);
    }

    private S3Service s3Service;
    private AwsOperations template = null;


    @Before
    public void before() {
        template = new S3Template(credentials, TestPropertiesAccessor.getS3DefaultBucketName());

        s3Service = mock(S3Service.class);

        DirectFieldAccessor accessor = new DirectFieldAccessor(template);
        accessor.setPropertyValue("s3Service", s3Service);
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#createBucket(java.lang.String)}.
     * 
     * @throws S3ServiceException
     */
    @Test(expected = AwsCommunicationException.class)
    public void testCreateBucketCauseS3CommunicationException() throws S3ServiceException {

        when(s3Service.createBucket(argThat(S3_BUCKET_NAME_MATCHER))).thenThrow(new S3ServiceException());
        template.createBucket(BUCKET_NAME);
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#createBucket(java.lang.String)}.
     * 
     * @throws S3ServiceException
     */
    @Test
    public void testCreateBucket() throws S3ServiceException {
        when(s3Service.createBucket(argThat(S3_BUCKET_NAME_MATCHER))).thenReturn(null);
        template.createBucket(BUCKET_NAME);
        verify(s3Service).createBucket(argThat(S3_BUCKET_NAME_MATCHER));
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#deleteBucket(java.lang.String)}.
     * 
     * @throws S3ServiceException
     */
    @Test(expected = AwsCommunicationException.class)
    public void testDeleteBucketCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).deleteBucket(argThat(S3_BUCKET_NAME_MATCHER));
        template.deleteBucket(BUCKET_NAME);
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#deleteBucket(java.lang.String)}.
     * 
     * @throws S3ServiceException
     */
    @Test
    public void testDeleteBucket() throws S3ServiceException {
        doNothing().when(s3Service).deleteBucket(argThat(S3_BUCKET_NAME_MATCHER));
        template.deleteBucket(BUCKET_NAME);
        verify(s3Service).deleteBucket(argThat(S3_BUCKET_NAME_MATCHER));
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#deleteObject(java.lang.String, java.lang.String)}
     * .
     * 
     * @throws S3ServiceException
     */
    @Test(expected = AwsCommunicationException.class)
    public void testDeleteObjectCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).deleteObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));
        template.deleteObject(BUCKET_NAME, KEY);
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#deleteObject(java.lang.String, java.lang.String)}
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
     * Test method for {@link org.opencredo.aws.s3.S3Template#listBuckets()}.
     * 
     * @throws S3ServiceException
     */
    @Test(expected = AwsCommunicationException.class)
    public void testListBucketsCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).listAllBuckets();
        template.listBuckets();
    }

    /**
     * Test method for {@link org.opencredo.aws.s3.S3Template#listBuckets()}.
     * 
     * @throws S3ServiceException
     */
    @Test
    public void testListBuckets() throws S3ServiceException {
        S3Bucket[] objs = new S3Bucket[] { new S3Bucket("name1"), new S3Bucket("name2") };
        doReturn(objs).when(s3Service).listAllBuckets();
        String[] listBuckets = template.listBuckets();
        verify(s3Service).listAllBuckets();

        assertEquals(2, listBuckets.length);
        assertEquals("name1", listBuckets[0]);
        assertEquals("name2", listBuckets[1]);
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#getBucketStatus(java.lang.String)}
     * .
     * 
     * @throws S3ServiceException
     */
    @Test(expected = AwsCommunicationException.class)
    public void testGetBucketStatusCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).checkBucketStatus(eq(BUCKET_NAME));
        template.getBucketStatus(BUCKET_NAME);
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#getBucketStatus(java.lang.String)}
     * .
     * 
     * @throws S3ServiceException
     */
    @Test(expected = AwsException.class)
    public void testGetBucketStatusCauseS3Exception() throws S3ServiceException {
        doReturn(-1).when(s3Service).checkBucketStatus(eq(BUCKET_NAME));
        template.getBucketStatus(BUCKET_NAME);
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#getBucketStatus(java.lang.String)}
     * .
     * 
     * @throws S3ServiceException
     */
    @Test
    public void testGetBucketStatus() throws S3ServiceException {
        BucketStatus bucketStatus;

        doReturn(S3Service.BUCKET_STATUS__MY_BUCKET).when(s3Service).checkBucketStatus(eq(BUCKET_NAME));
        bucketStatus = template.getBucketStatus(BUCKET_NAME);
        assertEquals(BucketStatus.MINE, bucketStatus);

        doReturn(S3Service.BUCKET_STATUS__DOES_NOT_EXIST).when(s3Service).checkBucketStatus(eq(BUCKET_NAME));
        bucketStatus = template.getBucketStatus(BUCKET_NAME);
        assertEquals(BucketStatus.DOES_NOT_EXIST, bucketStatus);

        doReturn(S3Service.BUCKET_STATUS__ALREADY_CLAIMED).when(s3Service).checkBucketStatus(eq(BUCKET_NAME));
        bucketStatus = template.getBucketStatus(BUCKET_NAME);
        assertEquals(BucketStatus.ALREADY_CLAIMED, bucketStatus);

        verify(s3Service, times(3)).checkBucketStatus(eq(BUCKET_NAME));
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#listBucketObjects(java.lang.String)}
     * .
     * 
     * @throws S3ServiceException
     */
    @Test(expected = AwsCommunicationException.class)
    public void testListBucketObjectsCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).listObjects(argThat(S3_BUCKET_NAME_MATCHER));
        template.listBucketObjects(BUCKET_NAME);
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#listBucketObjects(java.lang.String)}
     * .
     * 
     * @throws S3ServiceException
     */
    @Test
    public void testListBucketObjects() throws S3ServiceException {
        long currentMills = System.currentTimeMillis();
        S3Object[] objs = new S3Object[] { new S3Object(), new S3Object(), new S3Object() };
        for (int i = 0; i < objs.length; i++) {
            objs[i].setKey("key" + i);
            objs[i].setETag("eTag" + i);
            objs[i].setLastModifiedDate(new Date(currentMills - (24 * 60 * 60 * 1000 * i)));
        }

        doReturn(objs).when(s3Service).listObjects(argThat(S3_BUCKET_NAME_MATCHER));
        List<BlobObject> bucketObjects = template.listBucketObjects(BUCKET_NAME);
        verify(s3Service).listObjects(argThat(S3_BUCKET_NAME_MATCHER));
        assertEquals(objs.length, bucketObjects.size());

        for (int i = 0; i < objs.length; i++) {
            assertEquals(objs[i].getKey(), bucketObjects.get(i).getKey());
            assertEquals(objs[i].getETag(), bucketObjects.get(i).getETag());
            assertEquals(objs[i].getLastModifiedDate(), bucketObjects.get(i).getLastModified());
        }
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#send(java.lang.String, java.lang.String)}
     * 
     * @throws S3ServiceException
     * 
     */
    @Test(expected = AwsCommunicationException.class)
    public void testSendStringCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).putObject(argThat(S3_BUCKET_NAME_MATCHER),
                argThat(S3_OBJECT_MATCHER));
        template.send(BUCKET_NAME, KEY, "test-payload");
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#send(java.lang.String, java.lang.String)}
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
     * {@link org.opencredo.aws.s3.S3Template#send(java.lang.String, java.lang.String, java.io.File)}
     * .
     * 
     * @throws S3ServiceException
     */
    @Test(expected = AwsCommunicationException.class)
    public void testSendFileCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).putObject(argThat(S3_BUCKET_NAME_MATCHER),
                argThat(new S3ObjectMatcher(TEST_FILE_NAME)));
        template.send(BUCKET_NAME, TEST_FILE);
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#send(java.lang.String, java.lang.String, java.io.File)}
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
     * {@link org.opencredo.aws.s3.S3Template#send(java.lang.String, java.lang.String, java.io.InputStream)}
     * .
     * 
     * @throws S3ServiceException
     */
    @Test(expected = AwsCommunicationException.class)
    public void testSendInputStreamCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).putObject(argThat(S3_BUCKET_NAME_MATCHER),
                argThat(S3_OBJECT_MATCHER));
        template.send(BUCKET_NAME, KEY, TEST_INPUT_STREAM);
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#send(java.lang.String, java.lang.String, java.io.InputStream)}
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
     * {@link org.opencredo.aws.s3.S3Template#receiveAsString(java.lang.String)}
     * .
     * 
     * @throws S3ServiceException
     */
    @Test(expected = AwsCommunicationException.class)
    public void testReceiveAsStringCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).getObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));
        template.receiveAsString(BUCKET_NAME, KEY);
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#receiveAsString(java.lang.String)}
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
     * {@link org.opencredo.aws.s3.S3Template#receiveAsFile(java.lang.String)}.
     * 
     * @throws S3ServiceException
     */
    @Test(expected = AwsCommunicationException.class)
    public void testReceiveAsFileCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).getObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));
        template.receiveAsFile(BUCKET_NAME, KEY);
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#receiveAsFile(java.lang.String)}.
     * 
     * @throws S3ServiceException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void testReceiveAsFile() throws S3ServiceException, NoSuchAlgorithmException, IOException {
        String orgFileContent = FileUtils.readFileToString(TEST_FILE);
        doReturn(new S3Object(TEST_FILE)).when(s3Service).getObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));

        File received = template.receiveAsFile(BUCKET_NAME, KEY);
        System.out.println("Received file: " + received);

        verify(s3Service).getObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));

        assertTrue("Received file does not exist", received.exists());

        String receivedFileContent = FileUtils.readFileToString(received);

        assertEquals(orgFileContent, receivedFileContent);
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#receiveAsInputStream(java.lang.String)}
     * .
     * 
     * @throws S3ServiceException
     */
    @Test(expected = AwsCommunicationException.class)
    public void testReceiveAsInputStreamStringCauseS3CommunicationException() throws S3ServiceException {
        doThrow(new S3ServiceException()).when(s3Service).getObject(argThat(S3_BUCKET_NAME_MATCHER), eq(KEY));
        template.receiveAsInputStream(BUCKET_NAME, KEY);
    }

    /**
     * Test method for
     * {@link org.opencredo.aws.s3.S3Template#receiveAsInputStream(java.lang.String)}
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
     * 
     * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
     * 
     */
    static class S3BucketNameMatcher extends ArgumentMatcher<S3Bucket> {
        public boolean matches(Object obj) {
            return ((S3Bucket) obj).getName() == BUCKET_NAME;
        }
    }

    /**
     * 
     * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
     * 
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
