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
package org.opencredo.storage.azure.rest;

import org.opencredo.storage.StorageException;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class AzureRestResponseHandlingException extends StorageException {

    private static final long serialVersionUID = -276354276775535652L;

    /**
     * 
     */
    public AzureRestResponseHandlingException() {
        super();
    }

    /**
     * @param message
     */
    public AzureRestResponseHandlingException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public AzureRestResponseHandlingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public AzureRestResponseHandlingException(Throwable cause) {
        super(cause);
    }

    /**
     * 
     * @param messageFormat
     * @param params
     */
    public AzureRestResponseHandlingException(String messageFormat, Object... params) {
        super(messageFormat, params);
    }
    
    /**
     * 
     * @param cause
     * @param messageFormat
     * @param params
     */
    public AzureRestResponseHandlingException(Throwable cause, String messageFormat, Object... params) {
        super(cause, messageFormat, params);
    }
}
