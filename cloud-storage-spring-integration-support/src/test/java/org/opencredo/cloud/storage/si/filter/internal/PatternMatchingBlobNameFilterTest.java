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

package org.opencredo.cloud.storage.si.filter.internal;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.si.filter.internal.PatternMatchingBlobNameFilter;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class PatternMatchingBlobNameFilterTest {

    private Date currentDate = new Date(System.currentTimeMillis());

    private final String testKey = "testKey";

    @Test
    public void matchSingleObject() {

        BlobDetails[] blobDetails = new BlobDetails[] { new BlobDetails("", testKey, "", currentDate) };
        PatternMatchingBlobNameFilter filter = new PatternMatchingBlobNameFilter(testKey);
        List<BlobDetails> accepted = filter.filter(Arrays.asList(blobDetails));

        assertEquals(1, accepted.size());
    }

    @Test
    public void matchSubset() {

        BlobDetails[] blobDetails = new BlobDetails[] { new BlobDetails("", testKey, "", currentDate),
                new BlobDetails("", "a", "", currentDate) };
        PatternMatchingBlobNameFilter filter = new PatternMatchingBlobNameFilter(testKey);
        List<BlobDetails> accepted = filter.filter(Arrays.asList(blobDetails));

        assertEquals(1, accepted.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullPattern() {
        new PatternMatchingBlobNameFilter(null);
    }

}