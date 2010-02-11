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
package org.opencredo.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class StorageResponseUtils {

    public static void responseStreamToFile(InputStream is, File toFile) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(toFile);
            byte[] buffer = new byte[4096];
            for (int n; (n = is.read(buffer)) != -1;)
                os.write(buffer, 0, n);
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }
}
