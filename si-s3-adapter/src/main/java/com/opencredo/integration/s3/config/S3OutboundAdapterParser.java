package com.opencredo.integration.s3.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class S3OutboundAdapterParser implements BeanDefinitionParser {

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.
	 	genericBeanDefinition("com.opencredo.integration.s3.S3OutboundAdapter");
	  
		String directory = element.getAttribute(S3AdapterParserUtils.DIRECTORY_ATTRIBUTE);
		String filter = element.getAttribute(S3AdapterParserUtils.FILTER_ATTRIBUTE);
		String keynameGenerator = element.getAttribute(S3AdapterParserUtils.KEY_NAME_GENERATOR_ATTRIBUTE);
	  
		builder.addPropertyReference(S3AdapterParserUtils.DIRECTORY_PROPERTY, directory);
		builder.addPropertyValue(S3AdapterParserUtils.FILTER_PROPERTY, filter);
		builder.addPropertyValue(S3AdapterParserUtils.KEY_NAME_GENERATOR_PROPERTY, keynameGenerator);
	  
		return builder.getBeanDefinition();
	}

}
