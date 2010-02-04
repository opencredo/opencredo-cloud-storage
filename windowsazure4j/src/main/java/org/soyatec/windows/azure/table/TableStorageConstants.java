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
package org.soyatec.windows.azure.table;

import java.sql.Timestamp;

import org.soyatec.windows.azure.util.Utilities;

/**
 * Class representing some important table storage constants.
 * 
 */
public final class TableStorageConstants {

	/**
	 * The maximum size of strings per property/column is 64 kB (that is 32k
	 * characters.) Note: This constant is smaller for the development storage
	 * table service.
	 */
	public static final int MaxStringPropertySizeInBytes = 64 * 1024;

	/**
	 * One character in the standard UTF-16 character presentation is 2 bytes.
	 * Note: This constant is smaller for the development storage table service.
	 */
	public static final int MaxStringPropertySizeInChars = MaxStringPropertySizeInBytes / 2;

	public static final Timestamp MinSupportedDateTime = Utilities.minTime();

	/**
	 * Internal constant for querying tables.
	 */
	static final String TablesName = "Tables";

	/**
	 * Internal constant for querying tables.
	 */
	static final String TablesQuery = "/" + TablesName;

	public static final String AtomXml = "application/atom+xml";

	public static final String ApplicationXml = "application/xml";

	public static final String DataserviceNamespace = "http://schemas.microsoft.com/ado/2007/08/dataservices";

	public static final String MetadataNamespace = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata";

	public static final String AtomNamespace = "http://www.w3.org/2005/Atom";
}
