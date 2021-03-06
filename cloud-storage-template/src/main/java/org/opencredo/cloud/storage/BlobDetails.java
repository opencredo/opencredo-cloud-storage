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
package org.opencredo.cloud.storage;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This simple bean class is used to represent basic details of blob object in
 * cloud storage.
 * 
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class BlobDetails {

    private final String containerName;

    private final String name;

    private final String eTag;

    private final Date lastModified;

    /**
     * @param containerName
     * @param name
     * @param eTag
     * @param lastModified
     */
    public BlobDetails(String containerName, String name, String eTag, Date lastModified) {
        super();
        this.containerName = containerName;
        this.name = name;
        this.eTag = eTag;
        this.lastModified = lastModified;
    }

    /**
     * @return the containerName
     */
    public String getContainerName() {
        return containerName;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
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

   /**
    * 
    * @return
    * @see java.lang.Object#toString()
    */
    @Override
    public String toString() {
        return new ToStringBuilder(this)//
                .append("containerName", this.containerName)//
                .append("name", this.name)//
                .append("eTag", this.eTag)//
                .append("lastModified", this.lastModified)//
                .toString();
    }
}
