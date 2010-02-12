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

/**
 * Root exception for Azure REST Blob API.
 * 
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class AzureRestException extends Exception {

    private static final long serialVersionUID = -276354276775535652L;

    /**
     * 
     */
    public AzureRestException() {
        super();
    }

    /**
     * @param message
     */
    public AzureRestException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public AzureRestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public AzureRestException(Throwable cause) {
        super(cause);
    }

    /**
     * 
     * @param messageFormat
     * @param params
     */
    public AzureRestException(String messageFormat, Object... params) {
        super(String.format(messageFormat, params));
    }

    /**
     * 
     * @param cause
     * @param messageFormat
     * @param params
     */
    public AzureRestException(Throwable cause, String messageFormat, Object... params) {
        this(String.format(messageFormat, params), cause);
    }
}
