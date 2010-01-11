package org.opencredo.aws.s3.config;

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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.jets3t.service.model.S3Object;
import org.junit.Test;

import org.opencredo.aws.s3.AbstractS3ObjectListFilter;
import org.opencredo.aws.s3.AcceptOnceS3ObjectListFilter;
import org.opencredo.aws.s3.CompositeS3ObjectListFilter;
import org.opencredo.aws.s3.PatternMatchingS3ObjectListFilter;
import org.opencredo.aws.s3.S3ObjectListFilter;
import org.opencredo.aws.s3.config.S3ObjectListFilterFactoryBean;
import org.springframework.beans.DirectFieldAccessor;


public class S3ObjectListFilterFactoryBeanTest {

	@Test(expected = IllegalArgumentException.class)
	public void customFilterAndFilenamePatternAreMutuallyExclusive() throws Exception {
		S3ObjectListFilterFactoryBean factory = new S3ObjectListFilterFactoryBean();
		factory.setFilterReference(new TestFilter());
		factory.setKeynamePattern(Pattern.compile("foo"));
		factory.getObject();
	}

	@Test
	public void customFilterAndPreventDuplicatesNull() throws Exception {
		S3ObjectListFilterFactoryBean factory = new S3ObjectListFilterFactoryBean();
		TestFilter testFilter = new TestFilter();
		factory.setFilterReference(testFilter);
		S3ObjectListFilter result = (S3ObjectListFilter) factory.getObject();
		assertFalse(result instanceof CompositeS3ObjectListFilter);
		assertSame(testFilter, result);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void customFilterAndPreventDuplicatesTrue() throws Exception {
		S3ObjectListFilterFactoryBean factory = new S3ObjectListFilterFactoryBean();
		TestFilter testFilter = new TestFilter();
		factory.setFilterReference(testFilter);
		factory.setPreventDuplicates(Boolean.TRUE);
		S3ObjectListFilter result = (S3ObjectListFilter) factory.getObject();
		assertTrue(result instanceof CompositeS3ObjectListFilter);
		Collection filters = (Collection) new DirectFieldAccessor(result).getPropertyValue("s3ObjectFilters");
		assertTrue(filters.iterator().next() instanceof AcceptOnceS3ObjectListFilter);
		assertTrue(filters.contains(testFilter));
	}

	@Test
	public void customFilterAndPreventDuplicatesFalse() throws Exception {
		S3ObjectListFilterFactoryBean factory = new S3ObjectListFilterFactoryBean();
		TestFilter testFilter = new TestFilter();
		factory.setFilterReference(testFilter);
		factory.setPreventDuplicates(Boolean.FALSE);
		S3ObjectListFilter result = (S3ObjectListFilter) factory.getObject();
		assertFalse(result instanceof CompositeS3ObjectListFilter);
		assertSame(testFilter, result);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void filenamePatternAndPreventDuplicatesNull() throws Exception {
		S3ObjectListFilterFactoryBean factory = new S3ObjectListFilterFactoryBean();
		factory.setKeynamePattern(Pattern.compile("foo"));
		S3ObjectListFilter result = (S3ObjectListFilter) factory.getObject();
		assertTrue(result instanceof CompositeS3ObjectListFilter);
		Collection filters = (Collection) new DirectFieldAccessor(result).getPropertyValue("s3ObjectFilters");
		Iterator<S3ObjectListFilter> iterator = filters.iterator();
		assertTrue(iterator.next() instanceof AcceptOnceS3ObjectListFilter);
		assertTrue(iterator.next() instanceof PatternMatchingS3ObjectListFilter);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void filenamePatternAndPreventDuplicatesTrue() throws Exception {
		S3ObjectListFilterFactoryBean factory = new S3ObjectListFilterFactoryBean();
		factory.setKeynamePattern(Pattern.compile("foo"));
		factory.setPreventDuplicates(Boolean.TRUE);
		S3ObjectListFilter result = (S3ObjectListFilter) factory.getObject();
		assertTrue(result instanceof CompositeS3ObjectListFilter);
		Collection filters = (Collection) new DirectFieldAccessor(result).getPropertyValue("s3ObjectFilters");
		Iterator<S3ObjectListFilter> iterator = filters.iterator();
		assertTrue(iterator.next() instanceof AcceptOnceS3ObjectListFilter);
		assertTrue(iterator.next() instanceof PatternMatchingS3ObjectListFilter);
	}

	@Test
	public void filenamePatternAndPreventDuplicatesFalse() throws Exception {
		S3ObjectListFilterFactoryBean factory = new S3ObjectListFilterFactoryBean();
		factory.setKeynamePattern(Pattern.compile("foo"));
		factory.setPreventDuplicates(Boolean.FALSE);
		S3ObjectListFilter result = (S3ObjectListFilter) factory.getObject();
		assertFalse(result instanceof CompositeS3ObjectListFilter);
		assertTrue(result instanceof PatternMatchingS3ObjectListFilter);
	}


	private static class TestFilter extends AbstractS3ObjectListFilter {

		@Override
		protected boolean accept(S3Object s3object) {
			return true;
		}

	}

}
