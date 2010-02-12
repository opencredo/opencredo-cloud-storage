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
package org.opencredo.cloud.storage.azure.rest.internal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.opencredo.cloud.storage.azure.AzureCredentials;
import org.opencredo.cloud.storage.azure.rest.RequestAuthorizationException;
import org.opencredo.cloud.storage.azure.rest.AzureRestServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Add comments.
 * 
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class RequestAuthorizationInterceptor implements HttpRequestInterceptor {
    private final static Logger LOG = LoggerFactory.getLogger(RequestAuthorizationInterceptor.class);

    private static final String DEFAULT_STORAGE_VERSION = "2009-09-19";

    private enum MandatoryHeader {
        X_MS_DATE("x-ms-date"), X_MS_VERSION("x-ms-version");

        String headerName;

        private MandatoryHeader(String headerName) {
            this.headerName = headerName;
        }
    }

    private final String[] standardHeaders = { HTTP.CONTENT_ENCODING,// 
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

    private final AzureCredentials credentials;
    private final HeadersComparator headersComparator;

    /**
     * 
     */
    public RequestAuthorizationInterceptor(AzureCredentials credentials) {
        super();
        this.credentials = credentials;
        headersComparator = new HeadersComparator();
    }

    /**
     * @param req
     * @param context
     * @throws HttpException
     * @throws IOException
     * @see org.apache.http.HttpRequestInterceptor#process(org.apache.http.HttpRequest,
     *      org.apache.http.protocol.HttpContext)
     */
    public void process(HttpRequest req, HttpContext context) throws HttpException, IOException {

        addMandatoryHeaders(req);

        String signatureString = constructSignatureString(req);
        LOG.trace("signatureString: '{}'", signatureString);

        signatureString = createSignature(signatureString);
        LOG.debug("signature: '{}'", signatureString);

        // Add authorization header
        req.addHeader("Authorization", "SharedKey " + credentials.getAccountName() + ":" + signatureString);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Request: '\n{}\n{}'", req.getRequestLine(), getHeadersAsString(req));
        }
    }

    /**
     * 
     * 
     * @param signatureString
     * @return
     */
    private String createSignature(String signatureString) throws RequestAuthorizationException {
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
            throw new RequestAuthorizationException(
                    "Provided secret key is inappropriate to encrypt signature-string.", e);
        } catch (NoSuchAlgorithmException e) {
            throw new RequestAuthorizationException("No algorithm [" + encryptionAlgorithm
                    + "] to encrypt signature-string.", e);
        } catch (UnsupportedEncodingException e) {
            throw new RequestAuthorizationException("Unable to convert signature-string to encoding - '" + encoding
                    + "'.", e);
        } catch (IllegalStateException e) {
            throw new RequestAuthorizationException("Illegal signature-string encryption state.", e);
        }
    }

    /**
     * 
     * 
     * @param req
     * @return
     * @throws RequestAuthorizationException
     */
    private String constructSignatureString(HttpRequest req) throws RequestAuthorizationException {
        StringBuilder sb = new StringBuilder();
        // VERB
        sb.append(req.getRequestLine().getMethod().toUpperCase()).append("\n");

        // Standard Headers String
        constructStandartHeaderString(req, sb);

        // Canonicalized Headers String
        constructCanonicalizedHeadersString(req, sb);

        // Canonicalized Resource String
        constructCanonicalizedResourceString(req, sb);

        return sb.toString();
    }

    /**
     * 
     * @param req
     * @param sb
     *            Signature string.
     * @throws RequestAuthorizationException
     */
    private void constructStandartHeaderString(HttpRequest req, StringBuilder sb)
            throws RequestAuthorizationException {
        String standartHeader;
        Header[] headers;
        for (int i = 0; i < standardHeaders.length; i++) {
            standartHeader = standardHeaders[i];
            headers = req.getHeaders(standartHeader);
            if (headers.length > 1) {
                throw new RequestAuthorizationException("Multiple standard header [" + standartHeader + "] found.");
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
     *            Signature string.
     */
    private void constructCanonicalizedHeadersString(HttpRequest req, StringBuilder sb) {

        // Get all x-ms-... headers and sort them.
        Header[] allHeaders = req.getAllHeaders();

        List<Header> list = new ArrayList<Header>(allHeaders.length);

        // FIXME: Need to ensure that headers does not repeat.
        for (Header header : allHeaders) {
            if (header.getName().startsWith("x-ms-")) {
                list.add(header);
            }
        }

        Collections.sort(list, headersComparator);

        // Append all x-ms-... headers to signatureString
        // FIXME: Unfold the string by replacing any breaking white space with a
        // single space.
        for (Header header : list) {
            sb.append(header.getName().toLowerCase()).append(":").append(header.getValue().trim());
            sb.append("\n");
        }
    }

    /**
     * 
     * @param req
     * @param sb
     *            Signature string.
     * @throws RequestAuthorizationException
     */
    private void constructCanonicalizedResourceString(HttpRequest req, StringBuilder sb)
            throws RequestAuthorizationException {
        URI uri;
        try {
            uri = new URI(req.getRequestLine().getUri());
        } catch (URISyntaxException e) {
            throw new RequestAuthorizationException("Failed to create uri from request line: "
                    + req.getRequestLine().getUri(), e);
        }

        // Account name
        sb.append("/").append(credentials.getAccountName());

        // URI path
        sb.append(uri.getPath());

        // If query exists, all query params in alphabetical way.
        String queryStr = uri.getQuery();
        if (queryStr != null) {
            List<String> queryItems = Arrays.asList(queryStr.split("&"));
            Collections.sort(queryItems);
            // FIXME: If in query two params are the same, their values should
            // be separated by comma and appear in single line.
            for (String query : queryItems) {
                sb.append("\n");
                sb.append(query.replace('=', ':'));
            }
        }
    }

    /**
     * 
     * @param req
     * @return
     */
    private String getHeadersAsString(HttpRequest req) {
        StringBuilder sb = new StringBuilder();
        Header[] allHeaders = req.getAllHeaders();

        for (Header header : allHeaders) {
            sb.append(header.getName()).append(": ").append(header.getValue()).append("\n");
        }

        return sb.toString();
    }

    /**
     * 
     * @param req
     */
    private void addMandatoryHeaders(HttpRequest req) {
        req.addHeader(MandatoryHeader.X_MS_DATE.headerName, AzureRestServiceUtil.currentTimeStringInRFC1123());
        req.addHeader(MandatoryHeader.X_MS_VERSION.headerName, DEFAULT_STORAGE_VERSION);
    }
}
