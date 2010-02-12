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
package org.opencredo.cloud.storage.si.comparator.internal;

import java.util.Date;

import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.si.comparator.BlobObjectComparator;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class BlobObjectLastModifiedDateComparator implements BlobObjectComparator {

    public int compare(BlobDetails b1, BlobDetails b2) {
        Date b1LastModifiedDate = b1.getLastModified();
        Date b2LastModifiedDate = b2.getLastModified();

        if (b1LastModifiedDate.after(b2LastModifiedDate))
            return 1;
        else if (b1LastModifiedDate.before(b2LastModifiedDate))
            return -1;
        else
            return 0;
    }
}
