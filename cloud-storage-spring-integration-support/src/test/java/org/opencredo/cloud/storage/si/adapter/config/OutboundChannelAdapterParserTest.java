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

package org.opencredo.cloud.storage.si.adapter.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.opencredo.cloud.storage.si.BlobNameBuilder;
import org.opencredo.cloud.storage.si.adapter.WritingMessageHandler;
import org.opencredo.cloud.storage.si.internal.DefaultBlobNameBuilder;
import org.opencredo.cloud.storage.test.TestPropertiesAccessor;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.core.Message;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class OutboundChannelAdapterParserTest {

    @Test
    public void testOutboundAdapterLoadWithMinimumSettings() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "OutboundChannelAdapterParserTest-context.xml", this.getClass());

        Object bean = context.getBean("outbound-adapter");
        assertNotNull("Adapter not found", bean);
        System.out.println(bean.getClass());

        DirectFieldAccessor beanDirect = new DirectFieldAccessor(bean);
        Object value = beanDirect.getPropertyValue("handler");
        assertNotNull("'handler' not found", value);
        System.out.println(value.getClass());

        WritingMessageHandler a = (WritingMessageHandler) value;
        DirectFieldAccessor adapterDirect = new DirectFieldAccessor(a);
        assertNotNull("'template' not found", adapterDirect.getPropertyValue("template"));
        assertEquals(TestPropertiesAccessor.getDefaultContainerName(), adapterDirect.getPropertyValue("containerName"));
        assertNotNull("'blobNameBuilder' queue not found", adapterDirect.getPropertyValue("blobNameBuilder"));
        assertTrue(adapterDirect.getPropertyValue("blobNameBuilder") instanceof DefaultBlobNameBuilder);
    }

    @Test
    public void testOutboundAdapterLoadWithFullSettings() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "OutboundChannelAdapterParserTest-full-context.xml", this.getClass());

        Object bean = context.getBean("outbound-adapter");
        assertNotNull("Adapter not found", bean);
        System.out.println(bean.getClass());

        DirectFieldAccessor beanDirect = new DirectFieldAccessor(bean);
        Object value = beanDirect.getPropertyValue("handler");
        assertNotNull("'handler' not found", value);
        System.out.println(value.getClass());

        WritingMessageHandler a = (WritingMessageHandler) value;
        DirectFieldAccessor adapterDirect = new DirectFieldAccessor(a);
        assertNotNull("'template' not found", adapterDirect.getPropertyValue("template"));
        assertEquals(TestPropertiesAccessor.getDefaultContainerName(), adapterDirect.getPropertyValue("containerName"));
        assertNotNull("'blobNameBuilder' queue not found", adapterDirect.getPropertyValue("blobNameBuilder"));
        assertTrue(adapterDirect.getPropertyValue("blobNameBuilder") instanceof MockBlobNameBuilder);
    }

    @Test(expected = BeanDefinitionStoreException.class)
    public void testOutboundAdapterLoadNoTemplate() {
        try {
            new ClassPathXmlApplicationContext("OutboundChannelAdapterParserTest-noTemplate-context.xml", this
                    .getClass());
            fail("Context load should fail");
        } catch (BeanDefinitionStoreException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    @Test(expected = BeanDefinitionStoreException.class)
    public void testOutboundAdapterLoadNoContainer() {
        try {
            new ClassPathXmlApplicationContext("OutboundChannelAdapterParserTest-noContainer-context.xml", this.getClass());
            fail("Context load should fail");
        } catch (BeanDefinitionStoreException e) {
            System.err.println(e.getMessage());
            throw e;
        }
    }

    /**
     * Used in spring context for parser tests.
     * 
     * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
     * 
     */
    static class MockBlobNameBuilder implements BlobNameBuilder {
        public String createBlobName(Message<?> message) {
            throw new RuntimeException("Mock implementation");
        }
    }

}
