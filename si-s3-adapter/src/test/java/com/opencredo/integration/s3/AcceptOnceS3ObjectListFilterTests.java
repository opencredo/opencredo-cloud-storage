package com.opencredo.integration.s3;

import java.util.List;
import org.jets3t.service.model.S3Object;
import org.junit.Assert;
import org.junit.Test;


public class AcceptOnceS3ObjectListFilterTests {

	AcceptOnceS3ObjectListFilter sut;
	
	@Test
	public void testUnseenS3ObjectAdded(){
		sut = new AcceptOnceS3ObjectListFilter();
		S3Object s3a = new S3Object(); s3a.setKey("a");
		S3Object s3b = new S3Object(); s3b.setKey("b");
		S3Object s3c = new S3Object(); s3c.setKey("c");
		sut.filterS3Objects(new S3Object[]{s3a, s3b});
		List<S3Object> acceptedObjects = sut.filterS3Objects(new S3Object[]{s3c});
		Assert.assertEquals(1, acceptedObjects.size());
	}
	
	@Test
	public void testSeenS3ObjectNotAdded(){
		sut = new AcceptOnceS3ObjectListFilter();
		S3Object s3a = new S3Object(); s3a.setKey("a");
		S3Object s3b = new S3Object(); s3b.setKey("b");
		sut.filterS3Objects(new S3Object[]{s3a, s3b});
		List<S3Object> acceptedObjects = sut.filterS3Objects(new S3Object[]{s3a});
		Assert.assertEquals(0, acceptedObjects.size());
	}
	
}
