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

package org.opencredo.aws.s3;

import java.util.regex.Pattern;

import org.jets3t.service.model.S3Object;
import org.springframework.util.Assert;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class PatternMatchingS3ObjectListFilter extends AbstractS3ObjectListFilter {

	private final Pattern pattern;

	/**
	 * @param pattern
	 */
	public PatternMatchingS3ObjectListFilter(Pattern pattern) {
		Assert.notNull(pattern, "pattern must not be null");
		this.pattern = pattern;
	}

	/**
	 * @param s3Object
	 */
	protected boolean accept(S3Object s3Object) {
		return (s3Object != null) && (s3Object.getKey() != null) && this.pattern.matcher(s3Object.getKey()).matches();
	}

}