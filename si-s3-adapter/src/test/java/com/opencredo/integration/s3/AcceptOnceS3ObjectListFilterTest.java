package com.opencredo.integration.s3;

import java.util.List;
import org.jets3t.service.model.S3Object;
import org.junit.Assert;
import org.junit.Test;


public class AcceptOnceS3ObjectListFilterTest {

	AcceptOnceS3ObjectListFilter sut;
	
	@Test
	public void testUnseenS3ObjectAdded(){
		sut = new AcceptOnceS3ObjectListFilter();
		sut.filterS3Objects(new S3Object[]{new S3Object(), new S3Object()});
		List<S3Object> acceptedObjects = sut.filterS3Objects(new S3Object[]{new S3Object()});
		Assert.assertEquals(1, acceptedObjects.size());
	}
	
	@Test
	public void testSeenS3ObjectNotAdded(){
		sut = new AcceptOnceS3ObjectListFilter();
		S3Object seenS3Object = new S3Object();
		sut.filterS3Objects(new S3Object[]{new S3Object(), seenS3Object});
		List<S3Object> acceptedObjects = sut.filterS3Objects(new S3Object[]{seenS3Object});
		Assert.assertEquals(0, acceptedObjects.size());
	}
	
}
