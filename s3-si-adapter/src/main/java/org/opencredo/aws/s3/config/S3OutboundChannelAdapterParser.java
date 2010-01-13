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

package org.opencredo.aws.s3.config;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class S3OutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser { //implements BeanDefinitionParser {
	 
	 @Override
	 protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.
	 	genericBeanDefinition("org.opencredo.aws.s3.config.S3WritingMessageHandlerFactoryBean");
	  
		String bucket = element.getAttribute(S3AdapterParserUtils.BUCKET_NAME_ATTRIBUTE);
		String filter = element.getAttribute(S3AdapterParserUtils.FILTER_ATTRIBUTE);
		String keyNameGenerator = element.getAttribute(S3AdapterParserUtils.KEY_NAME_GENERATOR_ATTRIBUTE);
	  
		if (!StringUtils.hasText(bucket)) {
			 throw new BeanCreationException( "A " + S3AdapterParserUtils.BUCKET_NAME_ATTRIBUTE + " should be provided.");
		}
		
		//builder.addPropertyReference...;
		if (StringUtils.hasText(bucket)) builder.addPropertyValue(S3AdapterParserUtils.BUCKET_NAME_PROPERTY, bucket);
		if (StringUtils.hasText(filter)) builder.addPropertyValue(S3AdapterParserUtils.FILTER_PROPERTY, filter);
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "awsCredentials");
		if (StringUtils.hasText(keyNameGenerator)) builder.addPropertyReference(S3AdapterParserUtils.KEY_NAME_GENERATOR_PROPERTY, keyNameGenerator);
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "awsCredentials");
		return builder.getBeanDefinition();
	}

}
