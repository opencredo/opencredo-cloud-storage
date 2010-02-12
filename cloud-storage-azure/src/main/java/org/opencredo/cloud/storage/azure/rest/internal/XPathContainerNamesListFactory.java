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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

import org.apache.http.HttpEntity;
import org.opencredo.cloud.storage.azure.rest.AzureRestResponseHandlingException;
import org.opencredo.cloud.storage.azure.rest.ContainerNamesListFactory;
import org.springframework.xml.xpath.XPathException;
import org.springframework.xml.xpath.XPathOperations;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Container names factory using XPath to get data.
 * 
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class XPathContainerNamesListFactory implements ContainerNamesListFactory {

    private final XPathOperations xpathOperations;

    /**
     * @param xpathOperations
     */
    public XPathContainerNamesListFactory(XPathOperations xpathOperations) {
        super();
        this.xpathOperations = xpathOperations;
    }

    /**
     * @param entity
     * @return
     * @throws AzureRestResponseHandlingException
     * @see org.opencredo.cloud.storage.azure.rest.ContainerNamesListFactory#createContainerNamesList(org.apache.http.HttpEntity)
     */
    @SuppressWarnings("unchecked")
    public List<String> createContainerNamesList(HttpEntity entity) throws AzureRestResponseHandlingException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(entity.getContent());

            List<Node> nodeList = xpathOperations.evaluateAsNodeList("/EnumerationResults/Containers/Container/Name/text()", new DOMSource(doc));
            
            if (nodeList == null) {
                return new ArrayList<String>(0);
            }
            
            ArrayList<String> containerNames = new ArrayList<String>(nodeList.size());
            
            for (Node node : nodeList) {
                containerNames.add(node.getNodeValue());
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
            throw new AzureRestResponseHandlingException("Unexpected IO exception while creating container list", e);
        }
    }
}
