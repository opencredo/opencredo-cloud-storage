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
package org.opencredo.cloud.storage;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class.
 *
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public final class StorageUtils {

    private StorageUtils() {
    }

    /**
     * Write InputStream to provided class.
     *
     * @param is     Input stream.
     * @param toFile Class where input stream should be written.
     * @throws IOException
     */
    public static void writeStreamToFile(InputStream is, File toFile) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(toFile);
            byte[] buffer = new byte[4096];
            for (int n; (n = is.read(buffer)) != -1; ) {
                os.write(buffer, 0, n);
            }
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    public static void writeFileToFile(File inFile, File toFile) throws IOException {
        OutputStream os = null;
        InputStream is = null;
        try {

            is = new FileInputStream(inFile);
            os = new FileOutputStream(toFile);
            byte[] buffer = new byte[4096];
            for (int n; (n = is.read(buffer)) != -1; ) {
                os.write(buffer, 0, n);
            }
        } finally {
            if (os != null) {
                os.close();
            }
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Create required parent directories.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static void createParentDirs(File file) throws IOException {
        File parentFile = file.getParentFile();
        if (parentFile != null) {
            FileUtils.forceMkdir(parentFile);
        }
    }
}
