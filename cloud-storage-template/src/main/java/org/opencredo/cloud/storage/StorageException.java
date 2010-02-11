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
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class StorageException extends RuntimeException {

    /**
     * Generated Serial Version UID
     */
    private static final long serialVersionUID = -3456173051068969721L;

    public StorageException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public StorageException(Throwable cause) {
        super(cause);
    }

    /**
     * 
     * @param messageFormat
     * @param params
     */
    public StorageException(String messageFormat, Object... params) {
        super(String.format(messageFormat, params));
    }
    
    /**
     * 
     * @param cause
     * @param messageFormat
     * @param params
     */
    public StorageException(Throwable cause, String messageFormat, Object... params) {
        this(String.format(messageFormat, params), cause);
    }
}
