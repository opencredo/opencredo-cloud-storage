/*
 * Copyright 2002-2008 the original author or authors.
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

package org.opencredo.cloud.storage.si.filter;

import java.util.ArrayList;
import java.util.List;

import org.opencredo.cloud.storage.BlobObject;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public abstract class AbstractBucketObjectFilter implements BlobObjectFilter {

    /**
     * 
     * @param objects
     * @return
     * @see org.opencredo.cloud.storage.si.filter.BlobObjectFilter#filter(java.util.List)
     */
    public final List<BlobObject> filter(List<BlobObject> objects) {
        List<BlobObject> accepted = new ArrayList<BlobObject>();
        if (objects != null) {
            for (BlobObject obj : objects) {
                if (this.accept(obj)) {
                    accepted.add(obj);
                }
            }
        }
        return accepted;
    }

    /**
     * 
     * @param s3Object
     * @return
     */
    protected abstract boolean accept(BlobObject s3Object);

}
