/* Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencredo.aws.si.s3.filter.internal;

import java.util.HashSet;
import java.util.Set;

import org.opencredo.aws.s3.BucketObject;
import org.opencredo.aws.si.s3.filter.AbstractBucketObjectFilter;

/**
 * Filters S3Objects based on key values
 * 
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class AcceptOnceBucketObjectFilter extends AbstractBucketObjectFilter {

    private final Set<String> seenKeys;
    private final Object monitor = new Object();

    public AcceptOnceBucketObjectFilter() {
        this.seenKeys = new HashSet<String>();
    }

    /**
     * 
     * @param obj
     * @return
     * @see org.opencredo.aws.si.s3.filter.AbstractBucketObjectFilter#accept(org.opencredo.aws.s3.BucketObject)
     */
    protected boolean accept(BucketObject obj) {
        synchronized (this.monitor) {
            if (seenKeys.contains(obj.getKey())) {
                return false;
            }
            
            seenKeys.add(obj.getKey());
            return true;
        }
    }

}
