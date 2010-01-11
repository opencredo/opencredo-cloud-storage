package org.opencredo.aws.s3;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencredo.aws.s3.S3Resource;
import org.springframework.util.Assert;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class S3ResourceTest {
	
	S3Resource s3Resource;
	String bucketName;
	
	@Mock
	S3Service s3ServiceMock;
	
	@Before
	public void init(){
		bucketName = "sibucket";
	}

	@Test
	public void testBucketCreated() {
		s3Resource = new S3Resource(bucketName);
		Assert.isTrue(bucketName.equals(s3Resource.getS3Bucket().getName()));
	}
	
	@Test
	public void testPutObjectCalled() throws NoSuchAlgorithmException, S3ServiceException, IOException {
		
		s3Resource = new S3Resource(bucketName);
		s3Resource.setS3Service(s3ServiceMock);
		File testFile = File.createTempFile("testFile", "s3");
		testFile.deleteOnExit();
		s3Resource.sendFileToS3(testFile);
		
		verify(s3ServiceMock).putObject(any(S3Bucket.class), any(S3Object.class));
	}
	
}
