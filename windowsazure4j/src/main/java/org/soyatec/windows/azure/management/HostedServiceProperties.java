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

public class HostedServiceProperties {
	private String url;
	private String description;
	private String affinityGroup;
	private String location;
	private String label;
	private List<Deployment> deployments;

	public void addDeployment(Deployment deploy){
		if(deployments == null)
			deployments = new ArrayList<Deployment>();
		deployments.add(deploy);
	}
	
	public List<Deployment> getDeployments() {
		return deployments;
	}

	public void setDeployments(List<Deployment> deployments) {
		this.deployments = deployments;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAffinityGroup() {
		return affinityGroup;
	}

	public void setAffinityGroup(String affinityGroup) {
		this.affinityGroup = affinityGroup;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
