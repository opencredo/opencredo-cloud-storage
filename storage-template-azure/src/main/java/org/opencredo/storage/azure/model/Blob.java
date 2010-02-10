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

import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.Assert;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class Blob {

    private final String name;

    private String stringContent;

    public Blob(String name, String stringContent) {
        Assert.hasText(name, "Blob name must be specified.");
        this.name = name;
        this.stringContent = stringContent;
    }

    /**
     * @param blobName
     * @param is
     * @throws IOException
     */
    public Blob(String name, InputStream is) throws IOException {
        this.name = name;
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            byte[] b = new byte[4096];
            for (int n; (n = is.read(b)) != -1;) {
                sb.append(new String(b, 0, n));
            }

            stringContent = sb.toString();
        }
    }

    /**
     * @return the objectName
     */
    public String getName() {
        return name;
    }

    /**
     * @return the stringContent
     */
    public String getStringContent() {
        return stringContent;
    }

}
