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

import java.util.Arrays;
import java.util.List;
import org.jets3t.service.model.S3Object;
import org.junit.Assert;
import org.junit.Test;
import org.opencredo.aws.BlobObject;
import org.opencredo.aws.si.filter.internal.AcceptOnceBlobObjectFilter;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class AcceptOnceBlobObjectFilterTest {

    AcceptOnceBlobObjectFilter sut;

    @Test
    public void testUnseenS3ObjectAdded() {
        sut = new AcceptOnceBlobObjectFilter();
        S3Object s3a = new S3Object();
        s3a.setKey("a");
        S3Object s3b = new S3Object();
        s3b.setKey("b");
        S3Object s3c = new S3Object();
        s3c.setKey("c");
        sut.filter(Arrays.asList(new BlobObject[] { new BlobObject(s3a), new BlobObject(s3b) }));
        List<BlobObject> acceptedObjects = sut.filter(Arrays.asList(new BlobObject[] { new BlobObject(s3c) }));
        Assert.assertEquals(1, acceptedObjects.size());
    }

    @Test
    public void testSeenS3ObjectNotAdded() {
        sut = new AcceptOnceBlobObjectFilter();
        S3Object s3a = new S3Object();
        s3a.setKey("a");
        S3Object s3b = new S3Object();
        s3b.setKey("b");
        sut.filter(Arrays.asList(new BlobObject[] { new BlobObject(s3a), new BlobObject(s3b) }));
        List<BlobObject> acceptedObjects = sut.filter(Arrays.asList(new BlobObject[] { new BlobObject(s3a) }));
        Assert.assertEquals(0, acceptedObjects.size());
    }

}
