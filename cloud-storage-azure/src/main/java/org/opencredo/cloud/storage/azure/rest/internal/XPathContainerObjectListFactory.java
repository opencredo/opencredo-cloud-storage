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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

import org.apache.http.HttpEntity;
import org.opencredo.cloud.storage.BlobObject;
import org.opencredo.cloud.storage.azure.rest.AzureRestResponseHandlingException;
import org.opencredo.cloud.storage.azure.rest.AzureRestServiceUtil;
import org.opencredo.cloud.storage.azure.rest.ContainerObjectListFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.xml.xpath.XPathException;
import org.springframework.xml.xpath.XPathOperations;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class XPathContainerObjectListFactory implements ContainerObjectListFactory {
    private final static Logger LOG = LoggerFactory.getLogger(XPathContainerObjectListFactory.class);

    private final XPathOperations xpathOperations;

    /**
     * @param xpathOperations
     */
    public XPathContainerObjectListFactory(XPathOperations xpathOperations) {
        super();
        this.xpathOperations = xpathOperations;
    }

    /**
     * @param entity
     * @return
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.cloud.storage.azure.rest.ContainerObjectListFactory#createContainerObjectsList(org.apache.http.HttpEntity)
     */
    @SuppressWarnings("unchecked")
    public List<BlobObject> createContainerObjectsList(final String containerName, HttpEntity entity)
            throws AzureRestResponseHandlingException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(entity.getContent());

            List<Node> nodeList = xpathOperations.evaluateAsNodeList("/EnumerationResults/Blobs/Blob", new DOMSource(
                    doc));

            if (nodeList == null) {
                return new ArrayList<BlobObject>(0);
            }

            ArrayList<BlobObject> containerNames = new ArrayList<BlobObject>(nodeList.size());

            DOMSource domSource;
            String name;
            String eTag;
            String dateStr;
            Date date;
            for (Node node : nodeList) {
                domSource = new DOMSource(node);
                name = xpathOperations.evaluateAsString("./Name/text()", domSource);
                eTag = xpathOperations.evaluateAsString("./Properties/Etag/text()", domSource);
                dateStr = xpathOperations.evaluateAsString("./Properties/Last-Modified/text()", domSource);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Create blob with - conatiner-name: '{}', name: '{}', eTag: '{}', last-modified: '{}'",
                            new Object[] { containerName, name, eTag, dateStr });
                }

                date = AzureRestServiceUtil.parseRFC1123TimeString(dateStr);
                containerNames.add(new BlobObject(containerName, name, eTag, date));
            }

            return containerNames;
        } catch (XPathException e) {
            throw new AzureRestResponseHandlingException("Failed to evaluate XPath expression", e);
        } catch (IllegalStateException e) {
            throw new AzureRestResponseHandlingException("Failed to get content", e);
        } catch (ParserConfigurationException e) {
            throw new AzureRestResponseHandlingException("Failed to create document builder", e);
        } catch (SAXException e) {
            throw new AzureRestResponseHandlingException("Failed to parse", e);
        } catch (IOException e) {
            throw new AzureRestResponseHandlingException("Unexpected IO exception while creating container object list", e);
        } catch (ParseException e) {
            throw new AzureRestResponseHandlingException("Failed to parse Last-Modified date", e);
        }
    }
}
