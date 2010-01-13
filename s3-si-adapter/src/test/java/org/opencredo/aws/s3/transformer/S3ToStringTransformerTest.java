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

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Object;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opencredo.aws.s3.AWSCredentials;
import org.opencredo.aws.s3.S3Template;
import org.opencredo.aws.s3.transformer.S3ToStringTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.core.Message;

public class S3ToStringTransformerTest {
	
	@Autowired(required = true)
	private AWSCredentials awsCredentials;
	
	private S3ToStringTransformer systemUnderTest;
	private S3Template s3Template;
	private final String bucketName = "oc-test";
	private final String key = "testStringTransformer";
	private final String text = "String Transformer Test";
	private final String deleteWhenReceived = "true";
	
	@Before
    public void init() throws S3ServiceException, IOException, NoSuchAlgorithmException { 
        systemUnderTest = new S3ToStringTransformer();
        s3Template = new S3Template(awsCredentials);
		S3Object s3ObjectToUpload = new S3Object(key, text);
		s3Template.send(bucketName, s3ObjectToUpload); 
    }
    
	
	@SuppressWarnings("unchecked")
	@Test
	public void testTransformToStringMessage() throws IOException {

		Message<Map> messageToTransformMock = mock(Message.class);
		Map<String, Object> testMetaData = new HashMap<String, Object>();
		testMetaData.put("bucketName", bucketName);
		testMetaData.put("key", key);
		testMetaData.put("deleteWhenReceived", deleteWhenReceived);
		when(messageToTransformMock.getPayload()).thenReturn(testMetaData);
		
		Message<String> messageTransformed = systemUnderTest.transform(messageToTransformMock);
		
		assertNotNull(messageTransformed);
		assertEquals("String lengths do not match", text.length(), messageTransformed.getPayload().toString().length());
	 }
	
	 @After
	 public void after() throws S3ServiceException{
		 s3Template.getS3Service().deleteObject(bucketName, key);
	 }
	 
}
