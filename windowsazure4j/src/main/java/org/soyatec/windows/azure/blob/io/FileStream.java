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
package org.soyatec.windows.azure.blob.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileStream implements Stream {

	private RandomAccessFile file;

	public FileStream(File file) throws IOException {
		this.file = new RandomAccessFile(file, "rw");
	}

	public FileStream(String fileName) throws IOException {
		File file = new File(fileName);
		if (!file.exists()) {
			file.createNewFile();
		}

		this.file = new RandomAccessFile(file, "rw");
	}

	public void close() throws IOException {
		if (file != null) {
			file.close();
		}
	}

	public long length() throws IOException {
		return file.length();
	}

	public long getPosition() throws IOException {
		return file.getFilePointer();
	}

	public int read(byte[] b) throws IOException {
		return file.read(b);
	}

	public void write(byte[] buffer, int off, int len) throws IOException {
		file.write(buffer, off, len);
	}

	public boolean canSeek() {
		return true;
	}

	public byte[] getBytes() throws IOException {
		byte[] buf = new byte[(int) file.length()];
		file.readFully(buf);
		return buf;
	}

	public void setPosition(long position) throws IOException {
		file.seek(position);
	}

	public int read(byte[] buffer, int offset, int len) throws IOException {
		return file.read(buffer,offset,len);
	}

}
