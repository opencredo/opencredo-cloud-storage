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
package org.opencredo.aws.si.comparator.internal;

import java.util.Date;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opencredo.aws.BlobObject;
import org.opencredo.aws.si.comparator.BlobObjectComparator;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class BlobObjectLastModifiedDateComparatorTest {

    private BlobObjectComparator comparator = new BlobObjectLastModifiedDateComparator();

    @Test
    public void compareBlobObjects() {

        long currentMils = System.currentTimeMillis();
        long dayInMils = 24 * 60 * 60 * 1000;

        BlobObject bo1 = new BlobObject("bucketName", "key", "eTag", new Date(currentMils - dayInMils * 1));
        BlobObject bo2 = new BlobObject("bucketName", "key", "eTag", new Date(currentMils - dayInMils * 2));

        assertEquals(1, comparator.compare(bo1, bo2));
        assertEquals(0, comparator.compare(bo1, bo1));
        assertEquals(-1, comparator.compare(bo2, bo1));
    }

}
