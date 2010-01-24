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

package org.opencredo.aws.si.s3.filter;

import java.util.ArrayList;
import java.util.List;

import org.opencredo.aws.s3.BucketObject;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public abstract class AbstractBucketObjectFilter implements BucketObjectFilter {

	/**
	 * 
	 * @param objects
	 * @return
	 * @see org.opencredo.aws.si.s3.filter.BucketObjectFilter#filter(java.util.List)
	 */
	public final List<BucketObject> filter(List<BucketObject> objects) {
		List<BucketObject> accepted = new ArrayList<BucketObject>();
		if (objects != null) {
			for (BucketObject obj : objects) {
				if (this.accept(obj)) {
					accepted.add(obj);
				}
			}
		}
		return accepted;
	}

	/**
	 * 
	 * @param s3Object
	 * @return
	 */
	protected abstract boolean accept(BucketObject s3Object);

}
