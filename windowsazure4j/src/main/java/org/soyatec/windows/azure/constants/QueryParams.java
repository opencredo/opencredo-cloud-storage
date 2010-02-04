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

public class QueryParams {
	
	public static final String SeparatorForParameterAndValue = "=";
	public static final String QueryParamTimeout = "timeout";
	public static final String QueryParamComp = "comp";
	
	public static final String QueryRestType = "restype";

	// Other query string parameter names
	public static final String QueryParamBlockId = "blockid";
	public static final String QueryParamPrefix = "prefix";
	public static final String QueryParamMarker = "marker";
	public static final String QueryParamMaxResults = "maxresults";
	public static final String QueryParamDelimiter = "delimiter";
	public static final String QueryParamModifiedSince = "modifiedsince";

	// Table query parameter names
	public static final String QueryParamTableNextName = "NextTableName";
	public static final String QueryParamTableFilterPrefix = "$filter";
	public static final String QueryParamTableOrderbyPrefix = "$orderby";
	public static final String QueryParamTableTopPrefix = "$top";
}
