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

import java.net.URI;
import java.sql.Timestamp;

import org.soyatec.windows.azure.util.NameValueCollection;


/// The properties of a blob.
/// No member of this class makes a storage service request.
public class BlobProperties {

	private String name;

	private URI uri;

	private String contentEncoding;

	private String contentType;

	private String contentLanguage;

	private long contentLength;

	private NameValueCollection metadata;

	private Timestamp lastModifiedTime;

	private String eTag;

	public BlobProperties(String name) {
		this.name = name;
	}

	void assign(BlobProperties other) {
		name = other.name;
		uri = other.uri;
		contentEncoding = other.contentEncoding;
		contentLength = other.contentLength;
		contentType = other.contentType;
		eTag = other.eTag;
		lastModifiedTime = other.lastModifiedTime;
		if(other.metadata!=null){
			metadata = new NameValueCollection();
			metadata.putAll(other.metadata);
		}
	}

	public String getName() {
		return name;
	}

	public URI getUri() {
		return uri;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}

	public String getContentType() {
		return contentType;
	}

	public String getContentLanguage() {
		return contentLanguage;
	}

	public long getContentLength() {
		return contentLength;
	}

	public NameValueCollection getMetadata() {
		return metadata;
	}

	public Timestamp getLastModifiedTime() {
		return lastModifiedTime;
	}

	public String getETag() {
		return eTag;
	}

	void setName(String name) {
		this.name = name;
	}

	void setUri(URI uri) {
		this.uri = uri;
	}

	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setContentLanguage(String contentLanguage) {
		this.contentLanguage = contentLanguage;
	}

	void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	public void setMetadata(NameValueCollection metadata) {
		this.metadata = metadata;
	}

	void setLastModifiedTime(Timestamp lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public void setETag(String tag) {
		eTag = tag;
	}
}
