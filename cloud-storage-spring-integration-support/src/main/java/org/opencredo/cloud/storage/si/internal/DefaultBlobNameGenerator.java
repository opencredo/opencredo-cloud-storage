/* Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencredo.cloud.storage.si.internal;

import java.util.Date;

import org.opencredo.cloud.storage.si.BlobNameGenerator;
import org.springframework.integration.core.Message;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class DefaultBlobNameGenerator implements BlobNameGenerator {

    private final String keyPrefix;

    public DefaultBlobNameGenerator() {
        this("object.");
    }

    /**
     * @param keyPrefix
     */
    public DefaultBlobNameGenerator(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    /**
     * @param message
     */
    public String generateBlobName(Message<?> message) {
        return keyPrefix + (new Date().getTime());
    }

    public String getKeyPrefix() {
        return this.keyPrefix;
    }
}
