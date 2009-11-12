package com.opencredo.integration.s3;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class S3ResourceTest {
	
	String bucketName;
	
	@Before
	public void init(){
		bucketName = "sibucket";
	}

	@Test
	public void testPutObjectCalled() throws NoSuchAlgorithmException, S3ServiceException, IOException{
		S3Service s3ServiceMock = mock(S3Service.class);

		S3Resource s3Resource = new S3Resource(bucketName);
		s3Resource.setS3Service(s3ServiceMock);
		File testFile = File.createTempFile("testFile", "s3");
		testFile.deleteOnExit();
		s3Resource.sendFileToS3(testFile);
		
		verify(s3ServiceMock).putObject(any(S3Bucket.class), any(S3Object.class));
	}
	
}
