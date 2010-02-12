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

import org.apache.http.HttpEntity;
import org.opencredo.cloud.storage.azure.rest.AzureRestRequestCreationException;
import org.springframework.util.Assert;

/**
 * Abstract class representing Azure blob.
 * 
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public abstract class Blob<T> {

    private final String name;

    public Blob(String name) {
        Assert.hasText(name, "Blob name must be specified.");
        this.name = name;
    }

    /**
     * @return the objectName
     */
    public String getName() {
        return name;
    }

    /**
     * @return the data
     */
    public abstract T getData();

    /**
     * Creates request body from data it has.
     * 
     * @return
     */
    public abstract HttpEntity createRequestBody() throws AzureRestRequestCreationException;

}
