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

package org.opencredo.cloud.storage.si.filter.internal;

import java.util.HashSet;
import java.util.Set;

import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.si.filter.AbstractBlobDetailsFilter;

/**
 * Filters blob objects based on name values.
 * 
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class AcceptOnceBlobNameFilter extends AbstractBlobDetailsFilter {

    private final Set<String> seenKeys;
    private final Object monitor = new Object();

    public AcceptOnceBlobNameFilter() {
        this.seenKeys = new HashSet<String>();
    }

    /**
     * 
     * @param obj
     * @return
     * @see org.opencredo.cloud.storage.si.filter.AbstractBlobDetailsFilter#accept(org.opencredo.cloud.storage.BlobDetails)
     */
    protected boolean accept(BlobDetails obj) {
        synchronized (this.monitor) {
            if (seenKeys.contains(obj.getName())) {
                return false;
            }

            seenKeys.add(obj.getName());
            return true;
        }
    }

}
