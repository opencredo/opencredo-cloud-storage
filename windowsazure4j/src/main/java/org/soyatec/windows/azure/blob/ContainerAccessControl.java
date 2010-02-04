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

import java.util.ArrayList;
import java.util.List;

import org.soyatec.windows.azure.authenticate.SignedIdentifier;

public class ContainerAccessControl {
	
	public static final ContainerAccessControl Private = new ContainerAccessControl(false,false);
	public static final ContainerAccessControl Public = new ContainerAccessControl(true,false);

	private boolean isPublic;
	private boolean mutable;
	
	private List<SignedIdentifier> sigendIdentifiers;

	public ContainerAccessControl(boolean isPublic){
		this(isPublic, true);
	}
	
	ContainerAccessControl(boolean isPublic,boolean mutable){
		this.isPublic = isPublic;
		this.mutable = mutable;
	}
	
	public boolean isPublic() {
		return isPublic;
	}	

	public ContainerAccessControl addSignedIdentifier(SignedIdentifier id){
		if(!mutable)
			throw new IllegalStateException("The built-in instance is not mutable! Please create a new instance by yourself.");
		getSigendIdentifiers().add(id);
		return this;
	}
	
	public int getSize(){
		return getSigendIdentifiers().size();
	}
	
	public SignedIdentifier getSignedIdentifier(int index){
		List<SignedIdentifier> list = getSigendIdentifiers();
		if(index > list.size())
			return null;
		return list.get(index);
	}

	/**
	 * @return the sigendIdentifiers
	 */
	List<SignedIdentifier> getSigendIdentifiers() {
		if (sigendIdentifiers == null) {
			sigendIdentifiers = new ArrayList<SignedIdentifier>();
		}
		return sigendIdentifiers;
	}

	/**
	 * @param sigendIdentifiers
	 *            the sigendIdentifiers to set
	 */
	public void setSigendIdentifiers(List<SignedIdentifier> sigendIdentifiers) {
		if(!mutable)
			throw new IllegalStateException("The built-in instance is not mutable! Please create a new instance by yourself.");
		this.sigendIdentifiers = sigendIdentifiers;
	}

}
