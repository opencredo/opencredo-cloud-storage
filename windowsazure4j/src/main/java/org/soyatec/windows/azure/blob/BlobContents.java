/**
 * Copyright  2006-2009 Soyatec
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * $Id$
 */
package org.soyatec.windows.azure.blob;

import org.soyatec.windows.azure.blob.io.MemoryStream;
import org.soyatec.windows.azure.blob.io.Stream;

/// The contents of the Blob in various forms.
public class BlobContents {
	
	private Stream stream;
	private byte[] bytes;

	
    public BlobContents(Stream stream)
    {
        this.stream = stream;
    }

    /// <summary>
    /// Construct a new BlobContents object from a byte array.
    /// </summary>
    public BlobContents(byte[] value)
    {
        this.bytes = value;
        this.stream = new MemoryStream(value);
    }

    public Stream getStream() {
		return stream;
	}

    public byte[] getBytes() {
		return bytes;
	}

}
