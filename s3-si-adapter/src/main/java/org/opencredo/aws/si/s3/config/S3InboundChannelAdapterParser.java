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

package org.opencredo.aws.si.s3.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractPollingInboundChannelAdapterParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class S3InboundChannelAdapterParser extends AbstractPollingInboundChannelAdapterParser {
	 
	/**
	 * @param element
	 * @param parserContext
	 */
	@Override
	protected String parseSource(Element element, ParserContext parserContext) {
		 BeanDefinitionBuilder builder = BeanDefinitionBuilder.
		 	genericBeanDefinition("org.opencredo.aws.s3.config.S3ReadingMessageSourceFactoryBean");
		 
		 IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, S3AdapterParserUtils.BUCKET_NAME_ATTRIBUTE);
		 IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, S3AdapterParserUtils.DELETE_WHEN_RECEIVED_ATTRIBUTE);
		 IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "comparator");
		 IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "awsCredentials");
		 String filterBeanName = this.registerS3ListFilter(element, parserContext);
		 builder.addPropertyReference("filter", filterBeanName);
		 
		 return BeanDefinitionReaderUtils.registerWithGeneratedName(builder.getBeanDefinition(), parserContext.getRegistry());
	}
	
	/**
	 * @param element
	 * @param parserContext
	 */
	private String registerS3ListFilter(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder factoryBeanBuilder = BeanDefinitionBuilder.genericBeanDefinition(
				"org.opencredo.aws.s3.config.S3ObjectListFilterFactoryBean");
		factoryBeanBuilder.setRole(BeanDefinition.ROLE_SUPPORT);
		String filter = element.getAttribute("filter");
		if (StringUtils.hasText(filter)) {
			factoryBeanBuilder.addPropertyReference("filterReference", filter);
		}
		String filenamePattern = element.getAttribute("keyname-pattern");
		if (StringUtils.hasText(filenamePattern)) {
			if (StringUtils.hasText(filter)) {
				parserContext.getReaderContext().error(
						"At most one of 'filter' and 'keyname-pattern' may be provided.", element);
			}
			factoryBeanBuilder.addPropertyValue("keynamePattern", filenamePattern);
		}
		String preventDuplicates = element.getAttribute("prevent-duplicates");
		if (StringUtils.hasText(preventDuplicates)) {
			factoryBeanBuilder.addPropertyValue("preventDuplicates", preventDuplicates);
		}
		return BeanDefinitionReaderUtils.registerWithGeneratedName(
				factoryBeanBuilder.getBeanDefinition(), parserContext.getRegistry());
	}

}
