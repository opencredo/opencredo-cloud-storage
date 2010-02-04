/**
 * Copyright  2006-2009 Soyatec
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * $Id$
 */
package org.soyatec.windows.azure.util;

import java.util.concurrent.TimeUnit;

public class TimeSpan {
	private TimeUnit unit;
	private long duration;

	public static final TimeSpan ZERO = new TimeSpan(0, TimeUnit.MILLISECONDS);

	TimeSpan(long value, TimeUnit unit) {
		this.unit = unit;
		this.duration = value;
	}

	public static TimeSpan fromSeconds(long value) {
		return new TimeSpan(value, TimeUnit.SECONDS);
	}

	public static TimeSpan fromMilliseconds(long value) {
		return new TimeSpan(value, TimeUnit.MILLISECONDS);
	}

	public long toMilliseconds() {
		return unit.toMillis(duration);
	}

	public long toSeconds() {
		return unit.toSeconds(duration);
	}

	public long compareTo(TimeSpan o) {
		return unit.toMillis(duration) - o.unit.toMillis(o.duration);
	}
}
