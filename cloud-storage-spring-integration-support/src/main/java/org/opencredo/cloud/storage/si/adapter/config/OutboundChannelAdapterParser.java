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

import org.opencredo.cloud.storage.si.adapter.WritingMessageHandler;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class OutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser {
    /**
     * @param element
     * @param parserContext
     */
    @Override
    protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(WritingMessageHandler.class
                .getName());

        String templateRef = element.getAttribute(AdapterParserUtils.TEMPLATE_REF_ATTRIBUTE);
        String containerName = element.getAttribute(AdapterParserUtils.CONTAINER_NAME_ATTRIBUTE);
        String nameGeneratorRef = element.getAttribute(AdapterParserUtils.NAME_GENERATOR_ATTRIBUTE);

        builder.addConstructorArgReference(templateRef);
        builder.addConstructorArgValue(containerName);

        if (StringUtils.hasText(nameGeneratorRef)) {
            builder.addConstructorArgReference(nameGeneratorRef);
        }

        return builder.getBeanDefinition();
    }

}
