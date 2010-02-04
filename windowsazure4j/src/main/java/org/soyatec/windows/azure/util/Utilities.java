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
package org.soyatec.windows.azure.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.soyatec.windows.azure.authenticate.Base64;
import org.soyatec.windows.azure.blob.io.Stream;
import org.soyatec.windows.azure.constants.RegularExpressionStrings;
import org.soyatec.windows.azure.table.TableStorageConstants;

/**
 * Utilities for deal with string and time.
 * 
 */
public class Utilities {

	private static final String ERROR_MESSAGE = "<message (.*)>(.*)<\\/message>";

	private static final Pattern PATTERN = Pattern.compile(ERROR_MESSAGE);
	/**
	 * 
	 */
	private static final String YYYY_MM_DD_T_HH_MM_SS_SSS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	/**
	 * 
	 */
	private static final String YYYY_MM_DD_T_HH_MM_SS_SSS_DETAIL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS";

	private static final String EEE_DD_MMM_YYYY_HH_MM_SS_Z_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

	private static final String TIME_ZONE_GMT = "GMT";

	private static final String MIN_TIME = "0000-00-00 00:00:00.000000000";

	public static boolean isNullOrEmpty(String str) {
		return (str == null || str.trim().length() == 0);
	}

	public static String emptyString() {
		return "";
	}

	public static String encode(String input) {
		try {
			input = java.net.URLEncoder.encode(input, "UTF-8");
			input = input.replaceAll("%3D", "=");
			input = input.replaceAll("%2C", ",");
			input = input.replaceAll("%2F", "/");
			input = input.replaceAll("%28", "(");
			input = input.replaceAll("%27", "'");
			input = input.replaceAll("%29", ")");
			input = input.replaceAll("%24", "\\$"); // %24
			input = input.replaceAll("\\+", "\\%20"); // %24
		} catch (UnsupportedEncodingException e) {
			Logger.error(e.getMessage(), e);
		}
		return input;
	}

	/**
	 * The value of this constant is equivalent to 0000-00-00
	 * 00:00:00.000000000, January 1, 0001.
	 * 
	 * @return Timestamp
	 */
	public static Timestamp minTime() {
		// yyyy-mm-dd hh:mm:ss[.fffffffff]
		return Timestamp.valueOf(MIN_TIME);
	}

	public static boolean isValidTableName(String name) {
		if (isNullOrEmpty(name)) {
			return false;
		}
		Pattern pattern = Pattern
				.compile(RegularExpressionStrings.ValidTableNameRegex);
		return pattern.matcher(name).matches();
	}

	public static boolean isValidContainerOrQueueName(String name) {
		if (isNullOrEmpty(name)) {
			return false;
		}
		Pattern pattern = Pattern
				.compile(RegularExpressionStrings.ValidContainerNameRegex);
		return pattern.matcher(name).matches();
	}

	public static String getEmptyString() {
		return "";
	}

	public static Timestamp convertTime(String time) {
		SimpleDateFormat formatter = (SimpleDateFormat) DateFormat
				.getDateTimeInstance(DateFormat.MONTH_FIELD, DateFormat.LONG,
						Locale.US);
		formatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_GMT));
		Date date = null;
		try {
			date = formatter.parse(time);

		} catch (ParseException e) {
			date = new Date();
		}
		return new Timestamp(date.getTime());
	}

	public static String getUTCTime() {
		SimpleDateFormat formatter = (SimpleDateFormat) DateFormat
				.getDateTimeInstance(DateFormat.MONTH_FIELD, DateFormat.LONG,
						Locale.US);
		formatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_GMT));
		formatter.applyPattern(EEE_DD_MMM_YYYY_HH_MM_SS_Z_FORMAT);
		return formatter.format(new Date());
	}

	public static long copyStream(InputStream sourceStream,
			Stream destinationStream) throws IOException {
		final int bufferSize = 0x10000;
		byte[] buffer = new byte[bufferSize];
		int n = 0;
		long totalRead = 0;

		do {
			try {
				n = sourceStream.read(buffer, 0, bufferSize);
			} catch (IOException e) {
				Logger.error(e.getMessage(), e);
				break;
			}
			if (n > 0) {
				totalRead += n;
				destinationStream.write(buffer, 0, n);
			}

		} while (n > 0);
		return totalRead;
	}

	public static void copyStream(Stream sourceStream,
			Stream destinationStream, int length) throws IOException {

		final int bufferSize = 0x10000;
		byte[] buffer = new byte[bufferSize];
		int n = 0;
		int amountLeft = length;

		do {
			try {
				amountLeft -= n;
				n = sourceStream.read(buffer, 0, Math.min(bufferSize,
						amountLeft));
				if (n > 0) {
					destinationStream.write(buffer, 0, n);
				}
			} catch (IOException e) {
				Logger.error(e.getMessage(), e);
				break;
			}

		} while (n > 0);

	}

	public static Timestamp tryGetDateTimeFromHttpString(String stringValue)
			throws ParseException {
		SimpleDateFormat formatter = (SimpleDateFormat) DateFormat
				.getDateTimeInstance(DateFormat.MONTH_FIELD, DateFormat.LONG,
						Locale.US);
		formatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_GMT));
		formatter.applyPattern(EEE_DD_MMM_YYYY_HH_MM_SS_Z_FORMAT);
		Date date = formatter.parse(stringValue);
		return new Timestamp(date.getTime());
	}

	/**
	 * Parse time string like 2009-03-11T20:25:15.9334924Z or
	 * 2009-06-01T06:12:45Z
	 * 
	 * @param stringValue
	 * @return java.sql.Timestamp
	 * @throws ParseException
	 */
	public static Timestamp tryGetDateTimeFromTableEntry(String stringValue)
			throws ParseException {
		SimpleDateFormat formatter = (SimpleDateFormat) DateFormat
				.getDateTimeInstance(DateFormat.MONTH_FIELD, DateFormat.LONG,
						Locale.US);
		formatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE_GMT));
		// 2009-03-11T20:25:15.9334924Z
		Date date = null;
		try {

			formatter.applyPattern(YYYY_MM_DD_T_HH_MM_SS_SSS_FORMAT);
			date = formatter.parse(stringValue);
		} catch (Exception e) {
			formatter.setLenient(true);
			formatter.applyPattern(YYYY_MM_DD_T_HH_MM_SS_SSS_DETAIL_FORMAT);
			date = formatter.parse(stringValue);
		}
		return new Timestamp(date.getTime());

	}

	public static void checkStringParameter(String s, boolean canBeNullOrEmpty,
			String name) {
		if (isNullOrEmpty(s) && !canBeNullOrEmpty) {
			throw new IllegalArgumentException(MessageFormat.format(
					"The parameter {0} cannot be null or empty.", name));
		}
		if (s.length() > TableStorageConstants.MaxStringPropertySizeInChars) {
			throw new IllegalArgumentException(MessageFormat.format(
					"The parameter {0} cannot be longer than {1} characters.",
					name, TableStorageConstants.MaxStringPropertySizeInChars));
		}
	}

	public static String getTimestamp() {
		return formatTimeStamp(new Timestamp(new Date().getTime()));
	}

	public static String formatTimeStamp(Timestamp t) {
		SimpleDateFormat formatter = (SimpleDateFormat) DateFormat
				.getDateTimeInstance(DateFormat.MONTH_FIELD, DateFormat.LONG,
						Locale.US);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		formatter.applyPattern("yyyy-MM-dd");
		String timestamp = formatter.format(t);
		formatter.applyPattern("hh:mm:ss");
		return timestamp + "T" + formatter.format(t) + "Z";
	}

	public static String convertObjectToString(Object object)
			throws IOException {
		// if (object instanceof java.io.Serializable) {
		// throw new IllegalArgumentException(object
		// + " is not support Serializable. See java.io.Serializable");
		// }
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteStream);
		out.writeObject(object);
		out.flush();
		out.close();
		String string = Base64.encode(byteStream.toByteArray());
		byteStream.close();
		return string;
	}

	public static Object convertStringToObject(String string)
			throws IOException, ClassNotFoundException {
		if (string == null) {
			throw new IllegalArgumentException(string + "Object String");
		}
		byte[] decode = Base64.decode(string);
		if (decode == null) {
			throw new IOException(MessageFormat.format(
					"Base64 decode for {0} running error.", string));
		}
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
				decode));
		Object readObject = ois.readObject();
		ois.close();
		return readObject;
	}

	public static String MD5(String str) {
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(str.getBytes());
			byte[] array = m.digest();
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < array.length; ++j) {
				int b = array[j] & 0xFF;
				if (b < 0x10)
					sb.append('0');
				sb.append(Integer.toHexString(b));
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String retrieveErrorMessages(String content) {
		if (isNullOrEmpty(content)) {
			return null;
		}
		Matcher matcher = PATTERN.matcher(content);
		StringBuffer buf = new StringBuffer();
		while (matcher.find()) {
			buf.append(matcher.group(2));
		}
		if (buf.length() == 0)
			return null;
		else
			return buf.toString();

		// return content.substring(matcher.start(), matcher.end());
	}

	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}
	
	public static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

}
