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
package org.opencredo.cloud.storage.si.enricher;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class BlobEnrichException extends RuntimeException {

    private static final long serialVersionUID = 6115661721139432321L;

    /**
     * 
     */
    public BlobEnrichException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public BlobEnrichException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public BlobEnrichException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public BlobEnrichException(Throwable cause) {
        super(cause);
    }
}