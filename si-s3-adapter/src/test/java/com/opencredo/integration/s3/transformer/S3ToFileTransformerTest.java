package com.opencredo.integration.s3.transformer;
 
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
import org.springframework.integration.core.Message;

import com.opencredo.integration.s3.S3Resource;
 
public class S3ToFileTransformerTest {
	
     private S3ToFileTransformer systemUnderTest;
	
	 @Before
     public void init() { 
         systemUnderTest = new S3ToFileTransformer();
     }
	 
	 @Test
	 public void testUploadLocalFile() throws IOException, NoSuchAlgorithmException, S3ServiceException {
		 RestS3Service service = new RestS3Service(S3Resource.awsCredentials);
		 ClassLoader cl = this.getClass().getClassLoader();
		 URL urlFileToUpload = this.getClass().getResource("testFile.test");
		 File fileToUpload = new File (urlFileToUpload.getPath());
		 S3Object s3ObjectToUpload = new S3Object(fileToUpload.getName());
		 s3ObjectToUpload.setDataInputFile(fileToUpload);
		 service.putObject("oc-test", s3ObjectToUpload); 
	 }
	 
	 @Test
	 public void testTransformToFileMessage() throws IOException {
		 
		 String fileName = "testFile.test";
		 
		 Message<Map> messageToTransformMock = mock(Message.class);
		 Map<String, Object> testMetaData = new HashMap<String, Object>();
		 testMetaData.put("bucketName", "oc-test");
		 testMetaData.put("key", fileName);
		 when(messageToTransformMock.getPayload()).thenReturn(testMetaData);
		 
		 Message<File> messageTransformed = systemUnderTest.transform(messageToTransformMock);
		 
		 assertNotNull(messageTransformed);
		 assertEquals("not the expected file name", fileName, messageTransformed.getPayload().getName());
	 }
}
