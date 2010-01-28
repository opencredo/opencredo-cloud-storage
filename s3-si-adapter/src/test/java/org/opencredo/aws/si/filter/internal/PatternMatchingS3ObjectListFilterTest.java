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
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.jets3t.service.model.S3Object;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencredo.aws.BlobObject;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class PatternMatchingS3ObjectListFilterTest {

    @Mock
    S3Object s3ObjectMock;

    private final String testKey = "testKey";

    @Test
    public void matchSingleObject() {
        when(s3ObjectMock.getKey()).thenReturn(testKey);

        BlobObject[] s3Objects = new BlobObject[] { new BlobObject(s3ObjectMock) };
        PatternMatchingBlobObjectFilter filter = new PatternMatchingBlobObjectFilter(testKey);
        List<BlobObject> accepted = filter.filter(Arrays.asList(s3Objects));

        assertEquals(1, accepted.size());
    }

    @Test
    public void matchSubset() {
        when(s3ObjectMock.getKey()).thenReturn(testKey);

        BlobObject[] s3Objects = new BlobObject[] { new BlobObject(s3ObjectMock),
                new BlobObject(new S3Object()) };
        PatternMatchingBlobObjectFilter filter = new PatternMatchingBlobObjectFilter(testKey);
        List<BlobObject> accepted = filter.filter(Arrays.asList(s3Objects));

        assertEquals(1, accepted.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullPattern() {
        new PatternMatchingBlobObjectFilter(null);
    }

}