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
