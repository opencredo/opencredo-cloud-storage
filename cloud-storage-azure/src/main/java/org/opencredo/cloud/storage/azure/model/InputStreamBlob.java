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
package org.opencredo.cloud.storage.azure.model;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.InputStreamEntity;

/**
 * Blob abstraction for sending input stream to Azure cloud storage. This
 * abstraction is also used to receive blob data from Azure cloud storage.
 * 
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class InputStreamBlob extends Blob<InputStream> {

    private final InputStream data;

    /**
     * @param name
     */
    public InputStreamBlob(String name, InputStream data) {
        super(name);
        this.data = data;
    }

    /**
     * @return
     * @see org.opencredo.cloud.storage.azure.model.Blob#getData()
     */
    @Override
    public InputStream getData() {
        return data;
    }

    /**
     * @return
     * @see org.opencredo.cloud.storage.azure.model.Blob#createRequestBody()
     */
    @Override
    public HttpEntity createRequestBody() {
        // FIXME: Calculate size - currently is 0
        return new InputStreamEntity(data, 0);
    }

}
