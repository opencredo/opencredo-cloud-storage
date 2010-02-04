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

/**
 * Azure data types. </p>
 * 
 * The following data types are supported:
 * 
 * <ul>
 * <li><strong>Edm.Binary</strong> - An array of bytes up to 64 KB in size.</li>
 * <li><strong>Edm.Boolean</strong> - A boolean value.</li>
 * <li><strong>Edm.DateTime</strong> - A 64-bit value expressed as Coordinated
 * Universal Time (UTC). The supported DateTime range begins from 12:00
 * midnight, January 1, 1601 A.D. (C.E.), Coordinated Universal Time (UTC). The
 * range ends at December 31st, 9999.</li>
 * <li><strong>Edm.Double</strong> - A 64-bit floating point value.</li>
 * <li><strong>Edm.Guid</strong> - A 128-bit globally unique identifier.</li>
 * <li><strong>Edm.Int32</strong> - A 32-bit integer.</li>
 * <li><strong>Edm.Int64</strong> - A 64-bit integer.</li>
 * <li><strong>Edm.String</strong> - A UTF-16-encoded value. String values may
 * be up to 64 KB in size.</li>
 * </ul>
 */
public enum ETableColumnType {

	TYPE_BINARY("Edm.Binary", "varbinary(max)"), TYPE_BOOL("Edm.Boolean", "bit"), TYPE_DATE_TIME(
			"Edm.DateTime", "datetime"), TYPE_DOUBLE("Edm.Double", "real"), TYPE_GUID(
			"Edm.Guid", "uniqueidentifier"), TYPE_INT("Edm.Int32", "int"), TYPE_LONG(
			"Edm.Int64", "bigint"), TYPE_STRING("Edm.String", "nvarchar(1000)");

	/**
	 * Literal valuea
	 */
	private String literal;

	/**
	 * For local storage, the azure table is represents by Morcosoft SQLserver.
	 * Map the azure type to sql server data type
	 */
	private String sqlType;

	/**
	 * A new column type for specified literal and sql type
	 * 
	 * @param literal
	 * @param sqlType
	 */
	ETableColumnType(final String literal, final String sqlType) {
		this.literal = literal;
		this.sqlType = sqlType;
	}

	/**
	 * Get the literal of the data type
	 * 
	 * @return
	 */
	public String getLiteral() {
		return this.literal;
	}

	/**
	 * Get the literal of sql type
	 * 
	 * @return
	 */
	public String getSqlType() {
		return this.sqlType;
	}

	/**
	 * Find type by it's literal
	 * 
	 * @param literal
	 * @return
	 */
	public static ETableColumnType getTypebyLiteral(String literal) {
		if (org.soyatec.windows.azure.util.Utilities.isNullOrEmpty(literal))
			return ETableColumnType.TYPE_STRING;
		for (ETableColumnType type : values()) {
			if (type.getLiteral().equalsIgnoreCase(literal))
				return type;
		}
		return null;
	}
}
