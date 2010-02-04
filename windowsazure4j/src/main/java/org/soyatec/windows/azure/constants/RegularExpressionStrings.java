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

/// Contains regular expressions for checking whether container and table names conform
/// to the rules of the storage REST protocols.
public class RegularExpressionStrings {
	
	/**
	 * Container or queue names that match against this regular expression are valid.
	 * The container name must be a valid DNS name, conforming to the following naming rules:
	 * 1. Container names must start with a letter or number, and can contain only letters, numbers, and the dash (-) character.
	 * 2. Every dash (-) character must be immediately preceded and followed by a letter or number.
	 * 3. All letters in a container name must be lowercase.
	 * 4. Container names must be from 3 through 63 characters long.
	 */
    public static final String ValidContainerNameRegex = "^([a-z]|\\d){1}([a-z]|-|\\d){1,61}([a-z]|\\d){1}$";

 
    /**
     * Table names that match against this regular expression are valid.
     * Table names must conform to these rules:
     * 1. Table names may contain only alphanumeric characters.
     * 2. A table name may not begin with a numeric character.
     * 3. Table names are case-insensitive.
     * 4. Table names must be from 3 through 63 characters long.
     */
    public static final String ValidTableNameRegex = "^([a-z]|[A-Z]){1}([a-z]|[A-Z]|\\d){2,62}$";
}
