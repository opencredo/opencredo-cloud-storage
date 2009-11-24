package com.opencredo.integration.s3.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractPollingInboundChannelAdapterParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.w3c.dom.Element;

public class S3InboundChannelAdapterParser extends AbstractPollingInboundChannelAdapterParser {
	 
	@Override
	protected String parseSource(Element element, ParserContext parserContext) {
		 BeanDefinitionBuilder builder = BeanDefinitionBuilder.
		 	genericBeanDefinition("com.opencredo.integration.s3.config.S3ReadingMessageSourceFactoryBean");
		 
		 IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, S3AdapterParserUtils.BUCKET_NAME_ATTRIBUTE);
		 IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, S3AdapterParserUtils.FILTER_ATTRIBUTE);

		 return BeanDefinitionReaderUtils.registerWithGeneratedName(builder.getBeanDefinition(), parserContext.getRegistry());
	}

}
