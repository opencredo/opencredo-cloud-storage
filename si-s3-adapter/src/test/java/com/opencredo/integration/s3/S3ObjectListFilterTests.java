package com.opencredo.integration.s3;

import java.util.Arrays;
import java.util.List;

import org.jets3t.service.model.S3Object;

public class S3ObjectListFilterTests implements S3ObjectListFilter {
	
	public List<S3Object> filterS3Objects(S3Object[] s3Objects) {
		return Arrays.asList(s3Objects);
	}

}
