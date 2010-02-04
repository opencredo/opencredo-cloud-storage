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


/// The properties of a container.
/// No member of this class makes a storage service request.
public class ContainerProperties {
	
	private String name;
	
	private String eTag;
	
	private Timestamp lastModifiedTime;
	
	private URI uri;
	
	private NameValueCollection metadata;
	
	public ContainerProperties(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getETag() {
		return eTag;
	}

	public Timestamp getLastModifiedTime() {
		return lastModifiedTime;
	}

	public URI getUri() {
		return uri;
	}

	public NameValueCollection getMetadata() {
		return metadata;
	}

	void setName(String name) {
		this.name = name;
	}

	void setETag(String tag) {
		eTag = tag;
	}

	void setLastModifiedTime(Timestamp lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	void setUri(URI uri) {
		this.uri = uri;
	}

	void setMetadata(NameValueCollection metadata) {
		this.metadata = metadata;
	}
	
	
	
}
