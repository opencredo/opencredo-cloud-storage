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
package org.soyatec.windows.azure.constants;

public class BlobBlockConstants {
	public static final int KB = 1024;
	public static final int MB = 1024 * KB;
	/**
	* When transmitting a blob that is larger than this constant, this library automatically
	* transmits the blob as individual blocks. I.e., the blob is (1) partitioned
	* into separate parts (these parts are called blocks) and then (2) each of the blocks is
	* transmitted separately.
	* The maximum size of this constant as supported by the real blob storage service is currently
	* 64 MB; the development storage tool currently restricts this value to 2 MB.
	* Setting this constant can have a significant impact on the performance for uploading or
	* downloading blobs.
	* As a general guideline: If you run in a reliable environment increase this constant to reduce
	* the amount of roundtrips. In an unreliable environment keep this constant low to reduce the
	* amount of data that needs to be retransmitted in case of connection failures.
	*/
	public static final long MaximumBlobSizeBeforeTransmittingAsBlocks = 2 * MB;
	
	/**
	* The size of a single block when transmitting a blob that is larger than the
	* MaximumBlobSizeBeforeTransmittingAsBlocks constant (see above).
	* The maximum size of this constant is currently 4 MB; the development storage
	* tool currently restricts this value to 1 MB.
	* Setting this constant can have a significant impact on the performance for uploading or
	* downloading blobs.
	* As a general guideline: If you run in a reliable environment increase this constant to reduce
	* the amount of roundtrips. In an unreliable environment keep this constant low to reduce the
	* amount of data that needs to be retransmitted in case of connection failures.
	*/
	public static final long BlockSize = 1 * MB;
}
