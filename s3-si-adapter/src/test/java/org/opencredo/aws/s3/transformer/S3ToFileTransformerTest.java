package org.opencredo.aws.s3.transformer;
 
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
 
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
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
