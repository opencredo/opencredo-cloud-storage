/*
 * Copyright 2002-2009 the original author or authors.
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

package org.opencredo.cloud.storage.si.filter.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.si.filter.BlobDetailsFilter;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class CompositeBlobDetailsFilter implements BlobDetailsFilter {

    private final List<BlobDetailsFilter> blobDetailsFilters;

    /**
     * @param blobDetailsFilters
     */
    public CompositeBlobDetailsFilter(BlobDetailsFilter... blobDetailsFilters) {
        this.blobDetailsFilters = new ArrayList<BlobDetailsFilter>(Arrays.asList(blobDetailsFilters));
    }

    /**
     * @param blobDetailsFilters
     */
    public CompositeBlobDetailsFilter(Collection<BlobDetailsFilter> blobDetailsFilters) {
        this.blobDetailsFilters = new ArrayList<BlobDetailsFilter>(blobDetailsFilters);
    }

    /**
     * @param filters
     */
    public CompositeBlobDetailsFilter addFilter(BlobDetailsFilter... filters) {
        return addFilters(Arrays.asList(filters));
    }

    /**
     * @param filtersToAdd
     */
    public CompositeBlobDetailsFilter addFilters(Collection<BlobDetailsFilter> filtersToAdd) {
        this.blobDetailsFilters.addAll(filtersToAdd);
        return this;
    }

    /**
     * @param objects
     * @return
     * @see org.opencredo.cloud.storage.si.filter.BlobDetailsFilter#filter(java.util.List)
     */
    public List<BlobDetails> filter(final List<BlobDetails> objects) {

        if (objects == null) {
            return null;
        }

        List<BlobDetails> accepted = new ArrayList<BlobDetails>(objects);

        for (int i = 0; i < blobDetailsFilters.size() && !accepted.isEmpty(); i++) {
            accepted = blobDetailsFilters.get(0).filter(accepted);
        }

        return accepted;
    }

}
