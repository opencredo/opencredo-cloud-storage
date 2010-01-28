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

package org.opencredo.aws.si.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.opencredo.aws.BlobObject;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class CompositeBlobObjectFilter implements BlobObjectFilter {

    private final List<BlobObjectFilter> s3ObjectFilters;

    /**
     * @param s3ObjectFilters
     */
    public CompositeBlobObjectFilter(BlobObjectFilter... s3ObjectFilters) {
        this.s3ObjectFilters = new ArrayList<BlobObjectFilter>(Arrays.asList(s3ObjectFilters));
    }

    /**
     * @param s3ObjectFilters
     */
    public CompositeBlobObjectFilter(Collection<BlobObjectFilter> s3ObjectFilters) {
        this.s3ObjectFilters = new ArrayList<BlobObjectFilter>(s3ObjectFilters);
    }

    /**
     * @param filters
     */
    public CompositeBlobObjectFilter addFilter(BlobObjectFilter... filters) {
        return addFilters(Arrays.asList(filters));
    }

    /**
     * @param filtersToAdd
     */
    public CompositeBlobObjectFilter addFilters(Collection<BlobObjectFilter> filtersToAdd) {
        this.s3ObjectFilters.addAll(filtersToAdd);
        return this;
    }

    /**
     * @param objects
     * @return
     * @see org.opencredo.aws.si.filter.BlobObjectFilter#filter(java.util.List)
     */
    public List<BlobObject> filter(final List<BlobObject> objects) {
        
        if (objects == null) {
            return null;
        }
        
        List<BlobObject> accepted = new ArrayList<BlobObject>(objects);
        
        for (int i = 0; i < s3ObjectFilters.size() && !accepted.isEmpty(); i++) {
            accepted = s3ObjectFilters.get(0).filter(accepted);
        }
        
        return accepted;
    }

   
}
