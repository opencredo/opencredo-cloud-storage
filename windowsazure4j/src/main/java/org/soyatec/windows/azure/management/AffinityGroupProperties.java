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
package org.soyatec.windows.azure.management;

import java.util.ArrayList;
import java.util.List;

public class AffinityGroupProperties {
	
	private String description;
	
	private String location;
	
	private List<String> hostedServices;
	
	private List<String> storageServices;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<String> getHostedServices() {
		return hostedServices;
	}

	public void setHostedServices(List<String> hostedServices) {
		this.hostedServices = hostedServices;
	}
	
	public void addHostedService(String service){
		if(hostedServices == null)
			hostedServices = new ArrayList<String>();
		hostedServices.add(service);
	}
	
	public void addStorageService(String service){
		if( storageServices == null)
			storageServices = new ArrayList<String>();
		storageServices.add(service);
	}

	public List<String> getStorageServices() {
		return storageServices;
	}

	public void setStorageServices(List<String> storageServices) {
		this.storageServices = storageServices;
	}
	
	
}
