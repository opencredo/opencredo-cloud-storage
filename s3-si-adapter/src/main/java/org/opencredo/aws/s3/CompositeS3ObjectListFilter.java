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

package org.opencredo.aws.s3;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jets3t.service.model.S3Object;
import org.springframework.util.Assert;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class CompositeS3ObjectListFilter implements S3ObjectListFilter {

	private final Set<S3ObjectListFilter> s3ObjectFilters;

	/**
	 * @param s3ObjectFilters
	 */
	public CompositeS3ObjectListFilter(S3ObjectListFilter... s3ObjectFilters) {
		this.s3ObjectFilters = new LinkedHashSet<S3ObjectListFilter>(Arrays.asList(s3ObjectFilters));
	}

	/**
	 * @param s3ObjectFilters
	 */
	public CompositeS3ObjectListFilter(Collection<S3ObjectListFilter> s3ObjectFilters) {
		this.s3ObjectFilters = new LinkedHashSet<S3ObjectListFilter>(s3ObjectFilters);
	}

	/**
	 * @param filters
	 */
	public CompositeS3ObjectListFilter addFilter(S3ObjectListFilter... filters) {
		return addFilters(Arrays.asList(filters));
	}

	/**
	 * @param filtersToAdd
	 */
	public CompositeS3ObjectListFilter addFilters(Collection<S3ObjectListFilter> filtersToAdd) {
		this.s3ObjectFilters.addAll(filtersToAdd);
		return this;
	}

	/**
	 * @param s3Objects
	 */
	public List<S3Object> filterS3Objects(S3Object[] s3Objects) {
		Assert.notNull(s3Objects, "'s3Objects' should not be null");
		List<S3Object> leftOver = Arrays.asList(s3Objects);
		for (S3ObjectListFilter s3ObjectFilter : this.s3ObjectFilters) {
			leftOver = s3ObjectFilter.filterS3Objects(leftOver.toArray(new S3Object[] {}));
		}
		return leftOver;
	}

}
