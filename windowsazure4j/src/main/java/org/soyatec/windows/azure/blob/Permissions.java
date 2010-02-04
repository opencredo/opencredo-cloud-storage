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

public class Permissions {
	public static final int NONE = 0;
	public static final int READ = 1;
	public static final int WRITE = 2;
	public static final int DELETE = 4;
	public static final int LIST = 8;

	/**
	 * compose 4 rights.
	 */
	public static final int RWDL = READ | WRITE | DELETE | LIST;
	
	public static final int RWL = READ | WRITE | LIST;
	
	public static final int RL = READ | LIST;

	/**
	 * composte 2 rights
	 */
	public static final int RW = READ | WRITE;

	public static String toString(int value) {
		StringBuilder buf = new StringBuilder();
		if ((value & READ) != 0)
			buf.append('r');
		if ((value & WRITE) != 0)
			buf.append('w');
		if ((value & DELETE) != 0)
			buf.append('d');
		if ((value & LIST) != 0)
			buf.append('l');
		return buf.toString();
	}

	public static int valueOf(String perm) {
		int value = 0;
		if (perm.indexOf('r') > -1)
			value |= READ;
		if (perm.indexOf('w') > -1)
			value |= WRITE;
		if (perm.indexOf('d') > -1)
			value |= DELETE;
		if (perm.indexOf('l') > -1)
			value |= LIST;
		return value;
	}
	
	public static boolean hasPermission(int perm , int test){
		return (perm & test) != 0;
	}
	
	public static int addPermission(int perm, int value){
		return perm | value;
	}
	
	public static int removePermission(int perm, int value){
		return perm & (~value);
	}

}
