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
package org.opencredo.cloud.storage.si.enricher;

import org.opencredo.cloud.storage.StorageOperations;
import org.springframework.util.Assert;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public abstract class AbstractBlobEnricher<T> implements BlobEnricher<T> {

    private final StorageOperations template;
    private final boolean deleteBlob;

    public AbstractBlobEnricher(StorageOperations template) {
        this(template, false);
    }

    /**
     * @param template
     * @param deleteBlob
     */
    public AbstractBlobEnricher(StorageOperations template, boolean deleteBlob) {
        super();
        Assert.notNull(template, "Template must be specified");
        this.template = template;
        this.deleteBlob = deleteBlob;
    }
    

    /**
     * @param containerName
     * @param name
     */
    public void deleteBlobIfNeeded(String containerName, String blobName) {
        if (deleteBlob) {
            template.deleteObject(containerName, blobName);
        }
    }

    /**
     * @return the template
     */
    public StorageOperations getTemplate() {
        return template;
    }

    /**
     * @return the deleteBlob
     */
    public boolean isDeleteBlob() {
        return deleteBlob;
    }
}
