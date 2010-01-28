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

package org.opencredo.aws.si.filter.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencredo.aws.BlobObject;
import org.opencredo.aws.si.filter.BlobObjectFilter;
import org.opencredo.aws.si.filter.CompositeBlobObjectFilter;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class CompositeBlobObjectListFilterTest {

    @Mock
    private BlobObjectFilter s3ObjectFilterMock1;

    @Mock
    private BlobObjectFilter s3ObjectFilterMock2;

    @Mock
    private BlobObject mockBucketObject;

    @Test
    public void testForwardedToFilters() throws Exception {
        CompositeBlobObjectFilter compositeS3ObjectFilter = new CompositeBlobObjectFilter(s3ObjectFilterMock1,
                s3ObjectFilterMock2);
        List<BlobObject> returnedObjects = Arrays.asList(new BlobObject[] { mockBucketObject });

        when(s3ObjectFilterMock1.filter(anyListOf(BlobObject.class))).thenReturn(returnedObjects);
        when(s3ObjectFilterMock2.filter(anyListOf(BlobObject.class))).thenReturn(returnedObjects);

        assertEquals(returnedObjects, compositeS3ObjectFilter.filter(Arrays
                .asList(new BlobObject[] { mockBucketObject })));
    }

    @Test
    public void testForwardedToAddedFilters() throws Exception {
        CompositeBlobObjectFilter compositeS3ObjectFilter = new CompositeBlobObjectFilter().addFilter(
                s3ObjectFilterMock1, s3ObjectFilterMock2);
        List<BlobObject> returnedObjects = Arrays.asList(new BlobObject[] { mockBucketObject });

        when(s3ObjectFilterMock1.filter(anyListOf(BlobObject.class))).thenReturn(returnedObjects);
        when(s3ObjectFilterMock2.filter(anyListOf(BlobObject.class))).thenReturn(returnedObjects);

        assertEquals(returnedObjects, compositeS3ObjectFilter.filter(Arrays
                .asList(new BlobObject[] { mockBucketObject })));
    }

    @Test
    public void testNegative() throws Exception {
        CompositeBlobObjectFilter compositeS3ObjectFilter = new CompositeBlobObjectFilter(s3ObjectFilterMock1,
                s3ObjectFilterMock2);

        when(s3ObjectFilterMock1.filter(anyListOf(BlobObject.class))).thenReturn(new ArrayList<BlobObject>(0));
        when(s3ObjectFilterMock2.filter(anyListOf(BlobObject.class))).thenReturn(new ArrayList<BlobObject>(0));

        assertTrue(compositeS3ObjectFilter.filter(Arrays.asList(new BlobObject[] { mockBucketObject })).isEmpty());
    }

}
