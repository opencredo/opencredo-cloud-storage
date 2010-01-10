package com.opencredo.integration.s3.transformer;

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
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Object;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.core.Message;
import org.springframework.util.FileCopyUtils;

import com.opencredo.integration.s3.S3Resource;

public class S3ToByteArrayTransformerTest {

	private S3ToByteArrayTransformer systemUnderTest;
	
	private final String bucketName = "oc-test";
	private final String key = "testFile.test";
	
	private int fileToUploadByteArrayLength;
	
	@Before
    public void init() throws S3ServiceException, IOException { 
        systemUnderTest = new S3ToByteArrayTransformer();

        RestS3Service service = new RestS3Service(S3Resource.awsCredentials);
		ClassLoader cl = this.getClass().getClassLoader();
		URL urlFileToUpload = this.getClass().getResource(key);
		File fileToUpload = new File (urlFileToUpload.getPath());
		byte[] b = FileCopyUtils.copyToByteArray(fileToUpload);
		fileToUploadByteArrayLength = b.length;
		S3Object s3ObjectToUpload = new S3Object(fileToUpload.getName());
		s3ObjectToUpload.setDataInputFile(fileToUpload);
		service.putObject(bucketName, s3ObjectToUpload); 
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
		 RestS3Service service = new RestS3Service(S3Resource.awsCredentials);
		 service.deleteObject(bucketName, key);
	 }
}
