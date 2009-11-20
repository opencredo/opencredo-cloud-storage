package com.opencredo.integration.s3.config;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractPollingInboundChannelAdapterParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class S3InboundAdapterParser extends AbstractPollingInboundChannelAdapterParser {
	 
	@Override
	protected String parseSource(Element element, ParserContext parserContext) {
		 BeanDefinitionBuilder builder = BeanDefinitionBuilder.
		 	genericBeanDefinition("com.opencredo.integration.s3.S3InboundPollingAdapter");
		  
		 String directory = element.getAttribute(S3AdapterParserUtils.DIRECTORY_ATTRIBUTE);
		 String filter = element.getAttribute(S3AdapterParserUtils.FILTER_ATTRIBUTE);
		  
		 if (!StringUtils.hasText(directory)) {
			 throw new BeanCreationException( "A " + S3AdapterParserUtils.DIRECTORY_ATTRIBUTE + " should be provided.");
		}
		 
		builder.addPropertyReference(S3AdapterParserUtils.DIRECTORY_PROPERTY, directory);
		builder.addPropertyValue(S3AdapterParserUtils.FILTER_PROPERTY, filter);

		return BeanDefinitionReaderUtils.registerWithGeneratedName(builder.getBeanDefinition(), parserContext.getRegistry());
	}

}
