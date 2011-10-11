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

import org.opencredo.cloud.storage.si.adapter.ReadingMessageSource;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractPollingInboundChannelAdapterParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class InboundChannelAdapterParser extends AbstractPollingInboundChannelAdapterParser {

    /**
     * @param element
     * @param parserContext
     */
    @Override
    protected BeanMetadataElement parseSource(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ReadingMessageSource.class
                .getName());

        String templateRef = element.getAttribute(AdapterParserUtils.TEMPLATE_REF_ATTRIBUTE);
        String containerName = element.getAttribute(AdapterParserUtils.CONTAINER_NAME_ATTRIBUTE);
        String filterRef = element.getAttribute(AdapterParserUtils.FILTER_ATTRIBUTE);
        String comparatorRef = element.getAttribute(AdapterParserUtils.COMPARATOR_ATTRIBUTE);

        builder.addConstructorArgReference(templateRef);
        builder.addConstructorArgValue(containerName);

        if (StringUtils.hasText(filterRef)) {
            builder.addConstructorArgReference(filterRef);
        }

        if (StringUtils.hasText(comparatorRef)) {
            builder.addConstructorArgReference(comparatorRef);
        }

        return builder.getBeanDefinition();
    }
}
