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
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class AzureRestRequestCreationException extends AzureRestServiceException {

    private static final long serialVersionUID = -7464153324139255021L;

    /**
     * 
     */
    public AzureRestRequestCreationException() {
        super();
    }

    /**
     * @param message
     */
    public AzureRestRequestCreationException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public AzureRestRequestCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public AzureRestRequestCreationException(Throwable cause) {
        super(cause);
    }

    /**
     * @param messageFormat
     * @param params
     */
    public AzureRestRequestCreationException(String messageFormat, Object... params) {
        super(messageFormat, params);
    }

    /**
     * @param cause
     * @param messageFormat
     * @param params
     */
    public AzureRestRequestCreationException(Throwable cause, String messageFormat, Object... params) {
        super(cause, messageFormat, params);
    }
}
