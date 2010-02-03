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
package org.opencredo.storage;

import java.util.Date;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class BlobObject {

    private final String bucketName;

    private final String id;

    private final String eTag;

    private final Date lastModified;

    // /**
    // * @param s
    // */
    // public BlobObject(S3Object s) {
    // this(s.getBucketName(), s.getKey(), s.getETag(),
    // s.getLastModifiedDate());
    // }

    /**
     * @param bucketName
     * @param id
     * @param eTag
     * @param lastModified
     */
    public BlobObject(String bucketName, String id, String eTag, Date lastModified) {
        super();
        this.bucketName = bucketName;
        this.id = id;
        this.eTag = eTag;
        this.lastModified = lastModified;
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
    public String getId() {
        return id;
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
