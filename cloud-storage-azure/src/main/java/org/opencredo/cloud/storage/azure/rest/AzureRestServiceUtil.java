/* Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opencredo.cloud.storage.azure.rest;

import org.apache.commons.lang.time.DateFormatUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utils for Azure REST API.
 *
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public final class AzureRestServiceUtil {

    public static final String RFC1123_DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final DateFormat rfc1123_dateFormatter = new SimpleDateFormat(RFC1123_DATE_PATTERN);

    private AzureRestServiceUtil() {
    }

    /**
     * @return Returns current time string in RFC1123 pattern.
     */
    public static String currentTimeStringInRFC1123() {
        return DateFormatUtils.formatUTC(System.currentTimeMillis(), RFC1123_DATE_PATTERN);
    }

    /**
     * Parses date in RFC1123 string.
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date parseRFC1123TimeString(String dateStr) throws ParseException {
        return rfc1123_dateFormatter.parse(dateStr);
    }

}
