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
package org.opencredo.cloud.storage.azure.rest;

import org.apache.http.ProtocolException;

/**
 * This exception occurs if creation of Azure Blob REST API request fails (e.g.
 * request authorization fails).
 * 
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class RequestAuthorizationException extends ProtocolException {

    private static final long serialVersionUID = -7464153324139255021L;

    /**
     * 
     */
    public RequestAuthorizationException() {
        super();
    }

    /**
     * @param message
     */
    public RequestAuthorizationException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public RequestAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param messageFormat
     * @param params
     */
    public RequestAuthorizationException(String messageFormat, Object... params) {
        this(String.format(messageFormat, params));
    }

    /**
     * @param cause
     * @param messageFormat
     * @param params
     */
    public RequestAuthorizationException(Throwable cause, String messageFormat, Object... params) {
        this(String.format(messageFormat, params), cause);
    }
}
