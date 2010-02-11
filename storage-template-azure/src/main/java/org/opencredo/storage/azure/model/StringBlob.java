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
package org.opencredo.storage.azure.model;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.opencredo.storage.azure.rest.AzureRestRequestCreationException;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class StringBlob extends Blob<String> {

    private final String data;

    /**
     * @param name
     */
    public StringBlob(String name, String data) {
        super(name);
        this.data = data;
    }

    /**
     * @return
     * @see org.opencredo.storage.azure.model.Blob#getData()
     */
    @Override
    public String getData() {
        return data;
    }

    /**
     * @return
     * @see org.opencredo.storage.azure.model.Blob#createRequestBody()
     */
    @Override
    public HttpEntity createRequestBody() throws AzureRestRequestCreationException {
        try {
            return new StringEntity(data);
        } catch (UnsupportedEncodingException e) {
            throw new AzureRestRequestCreationException("Usupported string encoding", e);
        }
    }
}
