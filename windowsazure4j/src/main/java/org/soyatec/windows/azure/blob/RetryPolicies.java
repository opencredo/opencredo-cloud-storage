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
package org.soyatec.windows.azure.blob;

import java.util.Random;
import java.util.concurrent.Callable;

import org.soyatec.windows.azure.error.StorageException;
import org.soyatec.windows.azure.util.HttpUtilities;
import org.soyatec.windows.azure.util.TimeSpan;


public class RetryPolicies {
	public static final TimeSpan StandardMinBackoff = TimeSpan
			.fromMilliseconds(100);
	public static final TimeSpan StandardMaxBackoff = TimeSpan.fromSeconds(30);
	private static final Random Random = new Random();

	// / <summary>
	// / Policy that does no retries i.e., it just invokes <paramref
	// name="action"/> exactly once
	// / </summary>
	// / <param name="action">The action to retry</param>
	// / <returns>The return value of <paramref name="action"/></returns>
	public static RetryPolicy noRetry() {
		return new NoRetry();
	}

	// / <summary>
	// / Policy that retries a specified number of times with a specified fixed
	// time interval between retries
	// / </summary>
	// / <param name="numberOfRetries">The number of times to retry. Should be a
	// non-negative number</param>
	// / <param name="intervalBetweenRetries">The time interval between retries.
	// Use TimeSpan.Zero to specify immediate
	// / retries</param>
	// / <returns></returns>
	// / <remarks>When <paramref name="numberOfRetries"/> is 0 and <paramref
	// name="intervalBetweenRetries"/> is
	// / TimeSpan.Zero this policy is equivalent to the NoRetry policy</remarks>
	public static RetryPolicy retryN(int numberOfRetries,
			TimeSpan intervalBetweenRetries) {
		return new RetryN(numberOfRetries, intervalBetweenRetries);
	}

	// / <summary>
	// / Policy that retries a specified number of times with a randomized
	// exponential backoff scheme
	// / </summary>
	// / <param name="numberOfRetries">The number of times to retry. Should be a
	// non-negative number.</param>
	// / <param name="deltaBackoff">The multiplier in the exponential backoff
	// scheme</param>
	// / <returns></returns>
	// / <remarks>For this retry policy, the minimum amount of milliseconds
	// between retries is given by the
	// / StandardMinBackoff constant, and the maximum backoff is predefined by
	// the StandardMaxBackoff constant.
	// / Otherwise, the backoff is calculated as random(2^currentRetry) *
	// deltaBackoff.</remarks>
	public static RetryPolicy retryExponentialN(int numberOfRetries,
			TimeSpan deltaBackoff) {
		return new RetryExponentialN(numberOfRetries, StandardMinBackoff,
				StandardMaxBackoff, deltaBackoff);
	}

	// / <summary>
	// / Policy that retries a specified number of times with a randomized
	// exponential backoff scheme
	// / </summary>
	// / <param name="numberOfRetries">The number of times to retry. Should be a
	// non-negative number</param>
	// / <param name="deltaBackoff">The multiplier in the exponential backoff
	// scheme</param>
	// / <param name="minBackoff">The minimum backoff interval</param>
	// / <param name="maxBackoff">The maximum backoff interval</param>
	// / <returns></returns>
	// / <remarks>For this retry policy, the minimum amount of milliseconds
	// between retries is given by the
	// / minBackoff parameter, and the maximum backoff is predefined by the
	// maxBackoff parameter.
	// / Otherwise, the backoff is calculated as random(2^currentRetry) *
	// deltaBackoff.</remarks>
	public static RetryPolicy retryExponentialN(int numberOfRetries,
			TimeSpan minBackoff, TimeSpan maxBackoff, TimeSpan deltaBackoff) {
		if (minBackoff.compareTo(maxBackoff) > 0) {
			throw new IllegalArgumentException(
					"The minimum backoff must not be larger than the maximum backoff period.");
		}
		if (minBackoff.compareTo(TimeSpan.ZERO) < 0) {
			throw new IllegalArgumentException(
					"The minimum backoff period must not be negative.");
		}
		return new RetryExponentialN(numberOfRetries, minBackoff, maxBackoff,
				deltaBackoff);
	}

	static class NoRetry implements RetryPolicy {

		public Object execute(Callable action) throws StorageException {
			try {
				return action.call();
			} catch (Exception e) {
				throw HttpUtilities.translateWebException(e);
			}
		}
	}

	static class RetryN implements RetryPolicy {
		private int numberOfRetries;
		private TimeSpan intervalBetweenRetries;

		public RetryN(int numberOfRetries, TimeSpan intervalBetweenRetries) {
			this.numberOfRetries = numberOfRetries;
			this.intervalBetweenRetries = intervalBetweenRetries;
		}

		public Object execute(Callable action) throws StorageException {
			do {
				try {
					return action.call();
				} catch (Exception e) {
					if (numberOfRetries == 0) {
						throw HttpUtilities.translateWebException(e);
					}
					if (intervalBetweenRetries.compareTo(TimeSpan.ZERO) > 0) {
						try {
							Thread.sleep(intervalBetweenRetries
									.toMilliseconds());
						} catch (InterruptedException e1) {
						}
					}
				}
			} while (numberOfRetries-- > 0);
			return null;
		}

	}

	static class RetryExponentialN implements RetryPolicy {

		private int numberOfRetries;
		private TimeSpan minBackoff;
		private TimeSpan maxBackoff;
		private TimeSpan deltaBackoff;

		public RetryExponentialN(int numberOfRetries, TimeSpan minBackoff,
				TimeSpan maxBackoff, TimeSpan deltaBackoff) {
			this.numberOfRetries = numberOfRetries;
			this.minBackoff = minBackoff;
			this.maxBackoff = maxBackoff;
			this.deltaBackoff = deltaBackoff;
		}

		public Object execute(Callable action) throws StorageException {
			int totalNumberOfRetries = numberOfRetries;
			TimeSpan backoff;
			// sanity check
			// this is already checked when creating the retry policy in case
			// other than the standard settings are used
			// because this library is available in source code, the standard
			// settings can be changed and thus we
			// check again at this point
			if (minBackoff.compareTo(maxBackoff) > 0) {
				throw new IllegalArgumentException(
						"The minimum backoff must not be larger than the maximum backoff period.");
			}
			if (minBackoff.compareTo(TimeSpan.ZERO) < 0) {
				throw new IllegalArgumentException(
						"The minimum backoff period must not be negative.");
			}
			do {
				try {
					return action.call();
				} catch (Exception e) {
					if (numberOfRetries == 0) {
						throw HttpUtilities.translateWebException(e);
					}
					backoff = calculateCurrentBackoff(minBackoff, maxBackoff,
							deltaBackoff, totalNumberOfRetries
									- numberOfRetries);
					if (backoff.compareTo(TimeSpan.ZERO) > 0) {
						try {
							Thread.sleep(backoff.toMilliseconds());
						} catch (InterruptedException e1) {
						}
					}
				}
			} while (numberOfRetries-- > 0);
			return null;
		}

		private TimeSpan calculateCurrentBackoff(TimeSpan minBackoff,
				TimeSpan maxBackoff, TimeSpan deltaBackoff, int curRetry) {
			TimeSpan backoff;
			if (curRetry > 30) {
				backoff = maxBackoff;
			} else {
				try {
					// only randomize the multiplier here
					// it would be as correct to randomize the whole backoff
					// result
					long delay = Random.nextInt((1 << curRetry) + 1);
					delay *= deltaBackoff.toMilliseconds();
					delay += minBackoff.toMilliseconds();
					backoff = TimeSpan.fromMilliseconds(delay);
				} catch (ArithmeticException e) {
					backoff = maxBackoff;
				}
				if (backoff.compareTo(maxBackoff) > 0) {
					backoff = maxBackoff;
				}
			}
			return backoff;
		}

	}

}
