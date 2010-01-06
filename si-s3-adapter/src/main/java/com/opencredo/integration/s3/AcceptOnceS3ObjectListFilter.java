/* Copyright 2008 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.opencredo.integration.s3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import org.jets3t.service.model.S3Object;

/*
 * Filters S3Objects based on key values
 */
public class AcceptOnceS3ObjectListFilter implements S3ObjectListFilter{
	
	/*
	private final List<String> seenKeys = new CopyOnWriteArrayList<String>();
	private final Object monitor = new Object();

	public AcceptOnceS3ObjectListFilter() {
		
	}

	public final List<S3Object> filterS3Objects(S3Object[] s3Objects) {
		List<S3Object> acceptedObjects = Arrays.asList(s3Objects);
		Map <String, S3Object> acceptedObjectsMap = new HashMap<String, S3Object>();
		Iterator<S3Object> it = acceptedObjects.iterator();
		while (it.hasNext()) {
			S3Object next = it.next();
			acceptedObjectsMap.put(next.getKey(), next);
		}
		
		List<String> acceptedKeys = Arrays.asList((String [])acceptedObjectsMap.keySet().toArray());
		
		acceptedKeys.removeAll(this.seenKeys);
		this.seenKeys.addAll(acceptedKeys);
		
	}
	*/

	private final Queue<String> seenKeys;
	private final Object monitor = new Object();

	public AcceptOnceS3ObjectListFilter(int maxCapacity) {
		this.seenKeys = new LinkedBlockingQueue<String>(maxCapacity);

	}

	public AcceptOnceS3ObjectListFilter() {
		this.seenKeys = new LinkedBlockingQueue<String>();
	}

	public final List<S3Object> filterS3Objects(S3Object[] s3Objects) {
		List<S3Object> accepted = new ArrayList<S3Object>();
		if (s3Objects != null) {
			for (S3Object s3Object : s3Objects) {
				if (this.accept(s3Object)) {
					accepted.add(s3Object);
				}
			}
		}
		return accepted;
	}

	protected boolean accept(S3Object s3Object) {
		synchronized (this.monitor) {
			if (seenKeys.contains(s3Object.getKey())) {
				return false;
			}
			if (!seenKeys.offer(s3Object.getKey())) {
				seenKeys.poll();
				seenKeys.add(s3Object.getKey());
			}
			return true;
		}
	}


}
