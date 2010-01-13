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
 
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
 
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Object;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.opencredo.aws.s3.AWSCredentials;
import org.opencredo.aws.s3.S3Template;
import org.opencredo.aws.s3.transformer.S3ToFileTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.core.Message;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class S3ToFileTransformerTest {

	@Autowired(required = true)
	private AWSCredentials awsCredentials;

	private S3ToFileTransformer systemUnderTest;

	@Before
	public void init() {
		systemUnderTest = new S3ToFileTransformer();
	}

	@Test
	public void testUploadLocalFile() throws IOException,
			NoSuchAlgorithmException, S3ServiceException {
		S3Template s3Template = new S3Template(awsCredentials);
		ClassLoader cl = this.getClass().getClassLoader();
		URL urlFileToUpload = this.getClass().getResource("testFile.test");
		File fileToUpload = new File(urlFileToUpload.getPath());
		S3Object s3ObjectToUpload = new S3Object(fileToUpload.getName());
		s3ObjectToUpload.setDataInputFile(fileToUpload);
		s3Template.getS3Service().putObject("oc-test", s3ObjectToUpload);
	}

	@Test
	public void testTransformToFileMessage() throws IOException {

		String fileName = "testFile.test";

		Message<Map> messageToTransformMock = mock(Message.class);
		Map<String, Object> testMetaData = new HashMap<String, Object>();
		testMetaData.put("bucketName", "oc-test");
		testMetaData.put("key", fileName);
		when(messageToTransformMock.getPayload()).thenReturn(testMetaData);

		Message<File> messageTransformed = systemUnderTest
				.transform(messageToTransformMock);

		assertNotNull(messageTransformed);
		assertEquals("not the expected file name", fileName, messageTransformed
				.getPayload().getName());
	}
}
