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
package org.soyatec.windows.azure.util.storage;

import org.soyatec.windows.azure.table.ETableColumnType;

/**
 * 
 * @author xiaowei.ye (xiaowei.ye@soyatec.com)
 * 
 */
public class TableColumn {

	private String name;
	private String value;
	private ETableColumnType type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ETableColumnType getType() {
		return type;
	}

	public void setType(ETableColumnType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Column: " + name + "  " + type.getLiteral()).append("\n");
		return sb.toString();
	}
}
