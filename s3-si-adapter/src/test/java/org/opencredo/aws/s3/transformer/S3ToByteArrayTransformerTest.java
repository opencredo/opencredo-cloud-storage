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

package org.opencredo.aws.s3.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Object;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opencredo.aws.s3.AWSCredentials;
import org.opencredo.aws.s3.S3Template;
import org.opencredo.aws.s3.transformer.S3ToByteArrayTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.core.Message;
import org.springframework.util.FileCopyUtils;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class S3ToByteArrayTransformerTest {
	
	@Autowired(required = true)
	private AWSCredentials awsCredentials;

	private S3ToByteArrayTransformer systemUnderTest;
	private S3Template s3Template;
	
	private final String bucketName = "oc-test";
	private final String key = "testFile.test";
	
	private int fileToUploadByteArrayLength;
	
	@Before
    public void init() throws S3ServiceException, IOException { 
        systemUnderTest = new S3ToByteArrayTransformer();

        s3Template = new S3Template(awsCredentials);
		ClassLoader cl = this.getClass().getClassLoader();
		URL urlFileToUpload = this.getClass().getResource(key);
		File fileToUpload = new File (urlFileToUpload.getPath());
		byte[] b = FileCopyUtils.copyToByteArray(fileToUpload);
		fileToUploadByteArrayLength = b.length;
		S3Object s3ObjectToUpload = new S3Object(fileToUpload.getName());
		s3ObjectToUpload.setDataInputFile(fileToUpload);
		s3Template.getS3Service().putObject(bucketName, s3ObjectToUpload); 
    }
	
	@Test
	public void testTransformToByteArrayMessage() throws IOException {

		Message<Map> messageToTransformMock = mock(Message.class);
		Map<String, Object> testMetaData = new HashMap<String, Object>();
		testMetaData.put("bucketName", bucketName);
		testMetaData.put("key", key);
		when(messageToTransformMock.getPayload()).thenReturn(testMetaData);
		
		Message<byte[]> messageTransformed = systemUnderTest.transform(messageToTransformMock);
		
		assertNotNull(messageTransformed);
		assertEquals("Byte array sizes do not match", fileToUploadByteArrayLength, messageTransformed.getPayload().length);
	 }
	
	 @After
	 public void after() throws S3ServiceException{
		 s3Template.getS3Service().deleteObject(bucketName, key);
	 }
}
