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
package org.soyatec.windows.azure.util.xml;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.soyatec.windows.azure.authenticate.Base64;
import org.soyatec.windows.azure.authenticate.SignedIdentifier;
import org.soyatec.windows.azure.blob.ContainerAccessControl;
import org.soyatec.windows.azure.blob.Permissions;
import org.soyatec.windows.azure.table.AzureTableColumn;
import org.soyatec.windows.azure.table.ETableColumnType;
import org.soyatec.windows.azure.table.Guid;
import org.soyatec.windows.azure.table.SimpleTableStorageEntity;
import org.soyatec.windows.azure.table.TableStorageConstants;
import org.soyatec.windows.azure.table.TableStorageEntity;
import org.soyatec.windows.azure.util.Logger;
import org.soyatec.windows.azure.util.Utilities;

public abstract class AtomUtil {

	public static String createTableXml(final String tableName) {
		StringBuilder sb = new StringBuilder();
		// sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb.append(MessageFormat.format(
				"<entry xmlns:d=\"{0}\" xmlns:m=\"{1}\" xmlns=\"{2}\">",
				TableStorageConstants.DataserviceNamespace,
				TableStorageConstants.MetadataNamespace,
				TableStorageConstants.AtomNamespace));
		sb.append("<title />");
		sb.append(MessageFormat.format("<updated>{0}</updated>", Utilities
				.getTimestamp()));
		sb.append("<author><name /></author><id />");
		sb.append(MessageFormat.format("<content type=\"{0}\">",
				TableStorageConstants.ApplicationXml));
		sb.append("<m:properties>");
		sb.append(MessageFormat.format("<d:TableName>{0}</d:TableName>",
				tableName));
		sb.append("</m:properties>");
		sb.append("</content>");
		sb.append("</entry>");
		return sb.toString();
	}

	public static String tableEntityXml(String tableName,
			TableStorageEntity entity) {
		StringBuilder sb = new StringBuilder();
		sb.append(MessageFormat.format(
				"<entry xmlns:d=\"{0}\" xmlns:m=\"{1}\" xmlns=\"{2}\">",
				TableStorageConstants.DataserviceNamespace,
				TableStorageConstants.MetadataNamespace,
				TableStorageConstants.AtomNamespace));
		sb.append("<title />");
		sb.append(MessageFormat.format("<updated>{0}</updated>", Utilities
				.getTimestamp()));
		sb.append("<author><name /></author><id />");

		sb.append(convertToXml(tableName, entity));
		sb.append("</entry>");
		return sb.toString();
	}

	private static String convertToXml(String tableName,
			TableStorageEntity entity) {
		StringBuilder sb = new StringBuilder();
		sb.append(MessageFormat.format("<content type=\"{0}\">",
				TableStorageConstants.ApplicationXml));
		sb.append("<m:properties>");
		sb.append(MessageFormat.format("<d:PartitionKey>{0}</d:PartitionKey>",
				entity.getPartitionKey()));
		sb.append(MessageFormat.format("<d:RowKey>{0}</d:RowKey>", entity
				.getRowKey()));

		if (!(entity instanceof SimpleTableStorageEntity)) {
			List<?> ignoreFields = Arrays.asList(new String[] { "rowKey",
					"partitionKey", "timestamp" });
			Class<?> clazz = entity.getClass();
			Field[] fields = clazz.getDeclaredFields();

			for (Field f : fields) {
				String name = f.getName();
				if (ignoreFields.contains(name))
					continue;
				if (Modifier.isTransient(f.getModifiers()) || f.isSynthetic())
					continue;
				sb.append(convertToXml(f, entity));
			}
		} else {
			List<AzureTableColumn> properties = entity.getValues();
			for (AzureTableColumn key : properties) {
				sb.append(MessageFormat.format("<d:{0}>{1}</d:{0}>", key
						.getName(), key.getValue()));
			}
		}
		sb.append("<d:Timestamp m:type=\"Edm.DateTime\">"
				+ Utilities.formatTimeStamp(entity.getTimestamp())
				+ "</d:Timestamp>");
		sb.append("</m:properties></content>");
		return sb.toString();
	}

	private static String convertToXml(Field f, TableStorageEntity entity) {
		StringBuilder sb = new StringBuilder();
		try {
			f.setAccessible(true);
			Object value = f.get(entity);
			f.setAccessible(false);

			ETableColumnType type = getFieldType(f);
			String name = f.getName();
			sb.append("<d:").append(name);
			if (!type.equals(ETableColumnType.TYPE_STRING)) {
				sb.append(" m:type=\"").append(type.getLiteral()).append("\"");
			}

			// TODO, how to configure the field nullable or not?
			if (value == null) {
				sb.append(" m:null=\"true\"");
			}

			sb.append(">");
			if (value != null) {
				if (type.equals(ETableColumnType.TYPE_DATE_TIME)) {
					sb.append(Utilities.formatTimeStamp((Timestamp) value));
				} else if (type.equals(ETableColumnType.TYPE_BINARY)) {
					// encode byte array
					sb.append(Base64.encode((byte[]) value));
				} else if (type.equals(ETableColumnType.TYPE_STRING)
						&& !(value instanceof String)
						&& value instanceof Serializable) {

					try {
						// if object is Serializable
						sb.append(Utilities.convertObjectToString(value));
					} catch (Exception e) {
						// if fails, try toString
						sb.append(value);
					}

				} else {
					sb.append(value);
				}
			}
			sb.append("</d:").append(name).append(">");

		} catch (Exception e) {
			Logger.error("", e);
		}
		return sb.toString();
	}

	public static ETableColumnType getFieldType(Field f) {
		Class c = f.getType();
		if (c == int.class || c == Integer.class || c == byte.class
				|| c == Byte.class) {
			return ETableColumnType.TYPE_INT;
		} else if (c == long.class || c == Long.class) {
			return ETableColumnType.TYPE_LONG;
		} else if (c == float.class || c == Float.class || c == double.class
				|| c == Double.class) {
			return ETableColumnType.TYPE_DOUBLE;
		} else if (c == Timestamp.class) {
			return ETableColumnType.TYPE_DATE_TIME;
		} else if (c == byte[].class) {
			return ETableColumnType.TYPE_BINARY;
		} else if (c == boolean.class || c == Boolean.class) {
			return ETableColumnType.TYPE_BOOL;
		} else if (c == Guid.class) {
			return ETableColumnType.TYPE_GUID;
		} else {
			return ETableColumnType.TYPE_STRING;
		}
	}

	/**
	 * 
	 * @param control
	 * @return
	 */
	public static String convertACLToXml(ContainerAccessControl control) {
		StringBuffer polocies = new StringBuffer();
		polocies.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>").append(
				"\n");
		polocies.append("<SignedIdentifiers>");
		if (control.getSize() > 0) {
			
			for (int i = 0; i < control.getSize();  i ++) {
				SignedIdentifier signedIdentifier = control.getSignedIdentifier(i);
				polocies.append("<SignedIdentifier>");
				polocies.append("<Id>").append(signedIdentifier.getId())
						.append("</Id>");
				if (signedIdentifier.getPolicy() != null) {
					polocies.append("<AccessPolicy>");
					if (signedIdentifier.getPolicy().getStart() != null
							&& !signedIdentifier.getPolicy().getStart().equals(
									"")) {
						polocies.append("<Start>").append(
								signedIdentifier.getPolicy().getStart())
								.append("</Start>");
					}
					if (signedIdentifier.getPolicy().getExpiry() != null
							&& !signedIdentifier.getPolicy().getExpiry()
									.equals("")) {
						polocies.append("<Expiry>").append(
								signedIdentifier.getPolicy().getExpiry())
								.append("</Expiry>");
					}

					if (signedIdentifier.getPolicy().getPermission() != 0) {
						polocies.append("<Permission>").append(
								Permissions.toString(signedIdentifier
										.getPolicy().getPermission())).append(
								"</Permission>");
					}
					polocies.append("</AccessPolicy>");
				}
				polocies.append("</SignedIdentifier>");
			}
		}
		polocies.append("</SignedIdentifiers>");

		return polocies.toString();
	}
}
