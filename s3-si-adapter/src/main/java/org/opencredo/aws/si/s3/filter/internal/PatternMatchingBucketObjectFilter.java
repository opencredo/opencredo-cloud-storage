/*
 * Copyright 2002-2008 the original author or authors.
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

package org.opencredo.aws.si.s3.filter.internal;

import java.util.regex.Pattern;

import org.opencredo.aws.s3.BucketObject;
import org.opencredo.aws.si.s3.filter.AbstractBucketObjectFilter;
import org.springframework.util.Assert;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class PatternMatchingBucketObjectFilter extends AbstractBucketObjectFilter {

	private final Pattern pattern;

	/**
	 * @param pattern
	 */
	public PatternMatchingBucketObjectFilter(Pattern pattern) {
		Assert.notNull(pattern, "pattern must not be null");
		this.pattern = pattern;
	}

	/**
	 * @param s3Object
	 */
	protected boolean accept(BucketObject s3Object) {
		return (s3Object != null) && (s3Object.getKey() != null) && this.pattern.matcher(s3Object.getKey()).matches();
	}

}
