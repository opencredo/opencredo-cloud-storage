package org.opencredo.aws.s3.config;

/**
* Utility methods and constants for S3 adapter parsers.
*/

public class S3AdapterParserUtils {

	 static final String BUCKET_NAME_ATTRIBUTE = "bucket";
	 static final String BUCKET_NAME_PROPERTY = "bucketName";
	 
	 //Inbound Properties
	 static final String FILTER_ATTRIBUTE = "s3-filter";
	 static final String FILTER_PROPERTY = "filter";
	 
	 static final String DELETE_WHEN_RECEIVED_ATTRIBUTE = "delete-when-received";
	 static final String DELETE_WHEN_RECEIVED_PROPERTY = "deleteWhenReceived";
	 
	 static final String COMPARATOR_ATTRIBUTE = "comparator";
	 static final String COMPARATOR_PROPERTY = "comparator";
	 
	 //Outbound Properties
	 static final String KEY_NAME_GENERATOR_ATTRIBUTE = "key-name-generator";
	 static final String KEY_NAME_GENERATOR_PROPERTY = "s3KeyNameGenerator";

}
