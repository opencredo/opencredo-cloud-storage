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
package org.opencredo.storage.azure.rest.internal;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.entity.FileEntity;
import org.junit.Before;
import org.junit.Test;
import org.springframework.xml.xpath.Jaxp13XPathTemplate;
import org.springframework.xml.xpath.XPathOperations;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class XPathContainerListFactoryTest {

    private XPathContainerListFactory factory;

    @Before
    public void setUp() {
        XPathOperations xpathOperations = new Jaxp13XPathTemplate();
        factory = new XPathContainerListFactory(xpathOperations);
    }

    @Test
    public void testCreateContainersList() throws Exception {
        String fileName = getClass().getPackage().getName().replace('.', '/') + "/containerList.xml";
        URL resource = getClass().getClassLoader().getResource(fileName);

        assertNotNull("Unable to find file: " + fileName, resource);
        HttpEntity entity = new FileEntity(new File(resource.getFile()), null);
        List<String> containersList = factory.createContainersList(entity);
        assertEquals("Incorrect amount of containers", 3, containersList.size());
        for (String containerName : containersList) {
            System.out.println("Container: " + containerName);
        }
    }
}
