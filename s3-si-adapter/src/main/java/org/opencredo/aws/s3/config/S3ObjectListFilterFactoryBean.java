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

package org.opencredo.aws.s3.config;

import java.util.regex.Pattern;

import org.jets3t.service.model.S3Object;
import org.opencredo.aws.s3.AbstractS3ObjectListFilter;
import org.opencredo.aws.s3.AcceptOnceS3ObjectListFilter;
import org.opencredo.aws.s3.CompositeS3ObjectListFilter;
import org.opencredo.aws.s3.PatternMatchingS3ObjectListFilter;
import org.opencredo.aws.s3.S3ObjectListFilter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.integration.file.FileListFilter;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class S3ObjectListFilterFactoryBean implements FactoryBean {

	private volatile S3ObjectListFilter s3ObjectListFilter;

	private volatile S3ObjectListFilter filterReference;

	private volatile Pattern keynamePattern;

	private volatile Boolean preventDuplicates;

	private final Object monitor = new Object();

	/**
	 * @param filterReference
	 */
	public void setFilterReference(S3ObjectListFilter filterReference) {
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
		S3ObjectListFilter s3lf = null;
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
			PatternMatchingS3ObjectListFilter patternFilter = new PatternMatchingS3ObjectListFilter(this.keynamePattern);
			if (Boolean.FALSE.equals(this.preventDuplicates)) {
				s3lf = patternFilter;
			}
			else { // preventDuplicates is either TRUE or NULL
				s3lf = this.createCompositeWithAcceptOnceFilter(patternFilter);
			}
		}
		else if (Boolean.FALSE.equals(this.preventDuplicates)) {
			s3lf = new AbstractS3ObjectListFilter() {
				@Override
				protected boolean accept(S3Object s3object) {
					return true;
				}
			};
		}
		else { // preventDuplicates is either TRUE or NULL
			s3lf = new AcceptOnceS3ObjectListFilter();
		}
		this.s3ObjectListFilter = s3lf;
	}
	
	/**
	 * @param otherFilter
	 */
	private S3ObjectListFilter createCompositeWithAcceptOnceFilter(S3ObjectListFilter otherFilter) {
		CompositeS3ObjectListFilter compositeFilter = new CompositeS3ObjectListFilter();
		compositeFilter.addFilter(new AcceptOnceS3ObjectListFilter(), otherFilter);
		return compositeFilter;
	}

}
