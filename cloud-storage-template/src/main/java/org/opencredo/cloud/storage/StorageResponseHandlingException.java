/* Copyright 2008 the original author or authors.
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
package org.opencredo.cloud.storage;

/**
 * This exception is thrown when exception occurs during response data handling
 * (e.g. Converting response byte stream to String).
 * 
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class StorageResponseHandlingException extends StorageException {

    private static final long serialVersionUID = -6954894808311421881L;

    /**
     * 
     */
    public StorageResponseHandlingException() {
        super();
    }

    /**
     * @param messageFormat
     * @param params
     */
    public StorageResponseHandlingException(String messageFormat, Object... params) {
        super(messageFormat, params);
    }

    /**
     * @param message
     * @param cause
     */
    public StorageResponseHandlingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public StorageResponseHandlingException(String message) {
        super(message);
    }

    /**
     * @param cause
     * @param messageFormat
     * @param params
     */
    public StorageResponseHandlingException(Throwable cause, String messageFormat, Object... params) {
        super(cause, messageFormat, params);
    }

    /**
     * @param cause
     */
    public StorageResponseHandlingException(Throwable cause) {
        super(cause);
    }

}
