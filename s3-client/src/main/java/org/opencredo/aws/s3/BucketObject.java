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
package org.opencredo.aws.s3;

import java.util.Date;

import org.jets3t.service.model.S3Object;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class BucketObject {

    private final String bucketName;

    private final String key;

    private final String eTag;

    private final Date lastModified;

    /**
     * @param s
     */
    public BucketObject(S3Object s) {
        bucketName = s.getBucketName();
        key = s.getKey();
        eTag = s.getETag();
        lastModified = s.getLastModifiedDate();
    }

    /**
     * @return the bucketName
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the eTag
     */
    public String getETag() {
        return eTag;
    }

    /**
     * @return the lastModified
     */
    public Date getLastModified() {
        return lastModified;
    }

}
