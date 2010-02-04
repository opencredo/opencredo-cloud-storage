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

import java.io.IOException;

/**
 * This interface is the superclass of all classes representing a stream of
 * bytes.
 * 
 * <p>
 * Applications that need to define a subclass of <code>Stream</code> must
 * always provide implementation of these abstract methods.
 * 
 */
public interface Stream {
	/**
	 * Indicates whether this stream can be seeked.
	 * 
	 * @return true/false
	 */
	public boolean canSeek();

	/**
	 * Gets the length of this stream.
	 * 
	 * @return the length of this stream.
	 * @throws IOException
	 */
	public long length() throws IOException;

	/**
	 * Close this stream.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException;

	/**
	 * Gets the current position of this stream.
	 * 
	 * @return current position of this stream.
	 * @throws IOException
	 */
	public long getPosition() throws IOException;

	/**
	 * Sets the current position of this stream.
	 * 
	 * @param position
	 *            the current position of this stream.
	 * @throws IOException
	 */
	public void setPosition(long position) throws IOException;

	/**
	 * Reads some number of bytes from the stream and stores them into the
	 * buffer array <code>b</code>.
	 * 
	 * @param buffer
	 *            the buffer into which the data is read.
	 * @return the total number of bytes read into the buffer, or
	 *         <code>-1</code> is there is no more data because the end of the
	 *         stream has been reached.
	 * @throws IOException
	 */
	public int read(byte[] buffer) throws IOException;

	/**
	 * Reads up to <code>len</code> bytes of data from the stream into an array
	 * of bytes. An attempt is made to read as many as <code>len</code> bytes,
	 * but a smaller number may be read. The number of bytes actually read is
	 * returned as an integer.
	 * 
	 * @param buffer
	 *            the buffer into which the data is read.
	 * @param offset
	 *            the start offset in array <code>b</code> at which the data is
	 *            written.
	 * @param len
	 *            the maximum number of bytes to read.
	 * @return the total number of bytes read into the buffer, or
	 *         <code>-1</code> if there is no more data because the end of the
	 *         stream has been reached.
	 * @throws IOException
	 */
	public int read(byte[] buffer, int offset, int len) throws IOException;

	/**
	 * Writes <code>len</code> bytes from the specified byte array starting at
	 * offset <code>off</code> to this stream. The general contract for
	 * <code>write(b, off, len)</code> is that some of the bytes in the array
	 * <code>b</code> are written to the stream in order; element
	 * <code>b[off]</code> is the first byte written and
	 * <code>b[off+len-1]</code> is the last byte written by this operation.
	 * 
	 * @param buffer
	 *            the data.
	 * @param offset
	 *            the start offset in the data.
	 * @param len
	 *            the number of bytes to write.
	 * @throws IOException
	 */
	public void write(byte[] buffer, int offset, int len) throws IOException;

	/**
	 * Gets the byte array of data that is stored in the stream.
	 * 
	 * @return byte array of data that is stored in the stream.
	 * @throws IOException
	 */
	public byte[] getBytes() throws IOException;
}
