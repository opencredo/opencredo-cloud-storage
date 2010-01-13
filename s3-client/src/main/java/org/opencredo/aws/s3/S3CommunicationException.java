/* Copyright 2009-2010 the original author or authors.
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

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class S3CommunicationException extends S3Exception {

	private static final long serialVersionUID = 8080789275931833330L;
	
	public S3CommunicationException() {
		super();
	}

	/**
     * @param message
     * @param cause
     */
	public S3CommunicationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
     * @param message
     */
	public S3CommunicationException(String message) {
		super(message);
	}

	/**
     * @param cause
     */
	public S3CommunicationException(Throwable cause) {
		super(cause);
	}

}