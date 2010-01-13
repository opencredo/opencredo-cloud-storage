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

package org.opencredo.aws.s3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jets3t.service.model.S3Object;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencredo.aws.s3.CompositeS3ObjectListFilter;
import org.opencredo.aws.s3.S3ObjectListFilter;

@RunWith(MockitoJUnitRunner.class)
public class CompositeS3ObjectListFilterTest {
	
	@Mock
	private S3ObjectListFilter s3ObjectFilterMock1;
	
	@Mock
	private S3ObjectListFilter s3ObjectFilterMock2;
	
	@Mock
	private S3Object s3ObjectMock; 

	@Test
	public void testForwardedToFilters() throws Exception {
		CompositeS3ObjectListFilter compositeS3ObjectFilter = new CompositeS3ObjectListFilter(
				s3ObjectFilterMock1, s3ObjectFilterMock2);
		List<S3Object> returnedObjects = Arrays.asList(new S3Object[] { s3ObjectMock });
		
		when(s3ObjectFilterMock1.filterS3Objects(any(S3Object[].class))).thenReturn(returnedObjects);
		when(s3ObjectFilterMock2.filterS3Objects(any(S3Object[].class))).thenReturn(returnedObjects);

		assertEquals(returnedObjects, compositeS3ObjectFilter.filterS3Objects(new S3Object[] { s3ObjectMock }));
	}

	@Test
	public void testForwardedToAddedFilters() throws Exception {
		CompositeS3ObjectListFilter compositeS3ObjectFilter = new CompositeS3ObjectListFilter()
				.addFilter(s3ObjectFilterMock1, s3ObjectFilterMock2);
		List<S3Object> returnedObjects = Arrays.asList(new S3Object[] { s3ObjectMock });
		
		when(s3ObjectFilterMock1.filterS3Objects(any(S3Object[].class))).thenReturn(returnedObjects);
		when(s3ObjectFilterMock2.filterS3Objects(any(S3Object[].class))).thenReturn(returnedObjects);

		assertEquals(returnedObjects, compositeS3ObjectFilter.filterS3Objects(new S3Object[] { s3ObjectMock }));
	}

	
	@Test
	public void testNegative() throws Exception {
		CompositeS3ObjectListFilter compositeS3ObjectFilter = new CompositeS3ObjectListFilter(
				s3ObjectFilterMock1, s3ObjectFilterMock2);
		
		when(s3ObjectFilterMock1.filterS3Objects(any(S3Object[].class))).thenReturn(new ArrayList<S3Object>());
		when(s3ObjectFilterMock2.filterS3Objects(any(S3Object[].class))).thenReturn(new ArrayList<S3Object>());

		assertTrue(compositeS3ObjectFilter.filterS3Objects(new S3Object[] { s3ObjectMock }).isEmpty());
	}
	
}
