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
package org.opencredo.storage.azure;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class HttpRequestFactory {

    private final static Logger LOG = LoggerFactory.getLogger(HttpRequestFactory.class);

    private static final String X_MS_DATE = "x-ms-date";
    private static final String X_MS_VERSION = "x-ms-version";

    private static final String DEFAULT_STORAGE_VERSION = "2009-09-19";

    private static final String RFC1123_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";

    private HeadersComparator headersComparator;
    private String urlTemplate = "http://%s.blob.core.windows.net/%s";

    private String[] standardHeaders = { HTTP.CONTENT_ENCODING,// 
            "Content-Language", //
            HTTP.CONTENT_LEN,//
            "Content-MD5",//
            HTTP.CONTENT_TYPE,//
            HTTP.DATE_HEADER,//
            "If-Modified-Since",//
            "If-Match",//
            "If-None-Match",//
            "If-Unmodified-Since",//
            "Range" };

    private static HttpRequestFactory instance = new HttpRequestFactory();

    private HttpRequestFactory() {
        headersComparator = new HeadersComparator();
    }

    /**
     * @return the instance
     */
    public static HttpRequestFactory getInstance() {
        return instance;
    }

    /**
     * 
     * @param credentials
     * @param urlSufix
     * @return
     */
    public HttpGet createGetHttpRequest(AzureCredentials credentials, String urlSufix) {
        HttpGet req = new HttpGet(String.format(urlTemplate, credentials.getAccountName(), urlSufix));

        prepareHttpRequest(credentials, req);

        return req;
    }

    /**
     * 
     * @param credentials
     * @param urlSufix
     * @return
     */
    public HttpPut createPutHttpRequest(AzureCredentials credentials, String urlSufix, HttpEntity entity) {
        return createPutHttpRequest(credentials, urlSufix, entity, null);
    }

    public HttpPut createPutHttpRequest(AzureCredentials credentials, String urlSufix, HttpEntity entity,
            Map<String, String> headers) {
        HttpPut req = new HttpPut(String.format(urlTemplate, credentials.getAccountName(), urlSufix));

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                req.addHeader(entry.getKey(), entry.getValue());
            }
        }

        if (entity != null) {
            // "Content-Length" is required to construct proper signature
            // string.
            req.addHeader(HTTP.CONTENT_LEN, String.valueOf(entity.getContentLength()));
            req.addHeader(entity.getContentType());
        } else {
            req.addHeader(HTTP.CONTENT_LEN, "0");
        }

        prepareHttpRequest(credentials, req);

        // "Content-Length" should be removed and recalculated by
        // httpclient. If it is not removed - exception will be thrown.
        req.removeHeaders(HTTP.CONTENT_LEN);

        return req;
    }

    /**
     * 
     * @param credentials
     * @param urlSufix
     * @return
     */
    public HttpDelete createDeleteHttpRequest(AzureCredentials credentials, String urlSufix) {
        HttpDelete req = new HttpDelete(String.format(urlTemplate, credentials.getAccountName(), urlSufix));

        prepareHttpRequest(credentials, req);

        return req;
    }

    /**
     * 
     * @param credentials
     * @param req
     * @throws RequestCreationException
     */
    public void prepareHttpRequest(AzureCredentials credentials, HttpUriRequest req) throws RequestCreationException {
        addAzureHeaders(req);
        String signatureString = constructSignatureString(credentials, req);
        LOG.debug("signatureString: '{}'", signatureString);

        signatureString = createSignature(credentials, signatureString);
        LOG.debug("signature: '{}'", signatureString);

        req.addHeader("Authorization", "SharedKey " + credentials.getAccountName() + ":" + signatureString);
    }

    /**
     * 
     * @param credentials
     * @param signatureString
     * @return
     */
    private String createSignature(AzureCredentials credentials, String signatureString)
            throws RequestCreationException {
        String encoding = "UTF-8";
        String encryptionAlgorithm = "HmacSHA256";
        try {
            Mac mac = Mac.getInstance(encryptionAlgorithm);
            mac.init(new SecretKeySpec(Base64.decodeBase64(credentials.getSecretKey()), mac.getAlgorithm()));
            byte[] dataToMAC = signatureString.getBytes(encoding);
            mac.update(dataToMAC);
            byte[] result = mac.doFinal();
            return new String(Base64.encodeBase64(result));
        } catch (InvalidKeyException e) {
            throw new RequestCreationException("Provided secret key is inappropriate to encrypt signature-string.", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RequestCreationException("No algorithm [" + encryptionAlgorithm
                    + "] to encrypt signature-string.", e);
        } catch (UnsupportedEncodingException e) {
            throw new RequestCreationException("Unable to convert signature-string to encoding - '" + encoding + "'.",
                    e);
        } catch (IllegalStateException e) {
            throw new RequestCreationException("Illegal signature-string encryption state.", e);
        }
    }

    /**
     * 
     * @param req
     */
    private void addAzureHeaders(HttpUriRequest req) {
        req.addHeader(X_MS_DATE, DateFormatUtils.formatUTC(System.currentTimeMillis(), RFC1123_PATTERN));
        req.addHeader(X_MS_VERSION, DEFAULT_STORAGE_VERSION);
    }

    /**
     * 
     * @param credentials
     * @param req
     * @return
     * @throws RequestCreationException
     */
    private String constructSignatureString(AzureCredentials credentials, HttpUriRequest req)
            throws RequestCreationException {
        StringBuilder sb = new StringBuilder();
        // VERB
        sb.append(req.getMethod().toUpperCase()).append("\n");

        // Standard Headers String
        constructStandartHeaderString(req, sb);

        // Canonicalized Headers String
        constructCanonicalizedHeadersString(req, sb);

        // Canonicalized Resource String
        constructCanonicalizedResourceString(credentials, req, sb);

        return sb.toString();
    }

    /**
     * 
     * @param req
     * @param sb
     * @throws RequestCreationException
     */
    private void constructStandartHeaderString(HttpUriRequest req, StringBuilder sb) throws RequestCreationException {
        String standartHeader;
        Header[] headers;
        for (int i = 0; i < standardHeaders.length; i++) {
            standartHeader = standardHeaders[i];
            headers = req.getHeaders(standartHeader);
            if (headers.length > 1) {
                throw new RequestCreationException("Multiple standard header [" + standartHeader + "] found.");
            }

            // If header specified and is not "Date" header
            if (headers.length == 1 && i != 5) {
                sb.append((headers[0].getValue() == null ? "" : headers[0].getValue()));
            }
            sb.append("\n");
        }
    }

    /**
     * 
     * @param req
     * @param sb
     */
    private void constructCanonicalizedHeadersString(HttpUriRequest req, StringBuilder sb) {

        // Get all x-ms-... headers and sort them.
        Header[] allHeaders = req.getAllHeaders();

        List<Header> list = new ArrayList<Header>(allHeaders.length);

        for (Header header : allHeaders) {
            if (header.getName().startsWith("x-ms-")) {
                list.add(header);
            }
        }

        Collections.sort(list, headersComparator);

        for (Header header : list) {
            sb.append(header.getName().toLowerCase()).append(":").append(header.getValue().trim());
            sb.append("\n");
        }
    }

    /**
     * 
     * @param credentials
     * @param req
     * @param sb
     */
    private void constructCanonicalizedResourceString(AzureCredentials credentials, HttpUriRequest req, StringBuilder sb) {
        URI uri = req.getURI();
        sb.append("/").append(credentials.getAccountName());
        sb.append(uri.getPath());

        String queryStr = uri.getQuery();
        if (queryStr != null) {
            List<String> queryItems = Arrays.asList(queryStr.split("&"));
            Collections.sort(queryItems);
            for (String query : queryItems) {
                sb.append("\n");
                sb.append(query.replace('=', ':'));
            }
        }
    }

}
