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

package org.opencredo.aws.si.s3.config;

import java.util.regex.Pattern;

import org.opencredo.aws.s3.BucketObject;
import org.opencredo.aws.si.s3.filter.AbstractBucketObjectFilter;
import org.opencredo.aws.si.s3.filter.BucketObjectFilter;
import org.opencredo.aws.si.s3.filter.CompositeBucketObjectFilter;
import org.opencredo.aws.si.s3.filter.internal.AcceptOnceBucketObjectFilter;
import org.opencredo.aws.si.s3.filter.internal.PatternMatchingBucketObjectFilter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.integration.file.FileListFilter;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class S3ObjectListFilterFactoryBean implements FactoryBean {

	private volatile BucketObjectFilter s3ObjectListFilter;

	private volatile BucketObjectFilter filterReference;

	private volatile Pattern keynamePattern;

	private volatile Boolean preventDuplicates;

	private final Object monitor = new Object();

	/**
	 * @param filterReference
	 */
	public void setFilterReference(BucketObjectFilter filterReference) {
		this.filterReference = filterReference;
	}

	/**
	 * @param keynamePattern
	 */
	public void setKeynamePattern(Pattern keynamePattern) {
		this.keynamePattern = keynamePattern;
	}

	/**
	 * @param preventDuplicates
	 */
	public void setPreventDuplicates(Boolean preventDuplicates) {
		this.preventDuplicates = preventDuplicates;
	}

	public Object getObject() throws Exception {
		if (this.s3ObjectListFilter == null) {
			synchronized (this.monitor) {
				this.intializeS3ObjectListFilter();
			}
		}
		return this.s3ObjectListFilter;
	}

	public Class<?> getObjectType() {
		return (this.s3ObjectListFilter != null)
			? this.s3ObjectListFilter.getClass() : FileListFilter.class;
	}

	public boolean isSingleton() {
		return true;
	}

	private void intializeS3ObjectListFilter() {
		if (this.s3ObjectListFilter != null) {
			return;
		}
		BucketObjectFilter s3lf = null;
		if (this.filterReference != null && this.keynamePattern != null) {
			throw new IllegalArgumentException("The 'filter' reference and " +
					"'filename-pattern' attributes are mutually exclusive.");
		}
		if (this.filterReference != null) {
			if (Boolean.TRUE.equals(this.preventDuplicates)) {
				s3lf = this.createCompositeWithAcceptOnceFilter(this.filterReference);
			}
			else { 
				s3lf = this.filterReference; 
			}
		}
		else if (this.keynamePattern != null) {
			PatternMatchingBucketObjectFilter patternFilter = new PatternMatchingBucketObjectFilter(this.keynamePattern);
			if (Boolean.FALSE.equals(this.preventDuplicates)) {
				s3lf = patternFilter;
			}
			else { // preventDuplicates is either TRUE or NULL
				s3lf = this.createCompositeWithAcceptOnceFilter(patternFilter);
			}
		}
		else if (Boolean.FALSE.equals(this.preventDuplicates)) {
			s3lf = new AbstractBucketObjectFilter() {
                @Override
                protected boolean accept(BucketObject s3Object) {
                    return true;
                }
			};
		}
		else { // preventDuplicates is either TRUE or NULL
			s3lf = new AcceptOnceBucketObjectFilter();
		}
		this.s3ObjectListFilter = s3lf;
	}
	
	/**
	 * @param otherFilter
	 */
	private BucketObjectFilter createCompositeWithAcceptOnceFilter(BucketObjectFilter otherFilter) {
		CompositeBucketObjectFilter compositeFilter = new CompositeBucketObjectFilter();
		compositeFilter.addFilter(new AcceptOnceBucketObjectFilter(), otherFilter);
		return compositeFilter;
	}

}
