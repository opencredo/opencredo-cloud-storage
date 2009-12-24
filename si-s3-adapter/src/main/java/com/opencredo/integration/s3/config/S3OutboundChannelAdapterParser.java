package com.opencredo.integration.s3.config;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class S3OutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser { //implements BeanDefinitionParser {
	 
	 @Override
	 protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.
	 	genericBeanDefinition("com.opencredo.integration.s3.config.S3WritingMessageHandlerFactoryBean");
	  
		String bucket = element.getAttribute(S3AdapterParserUtils.BUCKET_NAME_ATTRIBUTE);
		String filter = element.getAttribute(S3AdapterParserUtils.FILTER_ATTRIBUTE);
		String keyNameGenerator = element.getAttribute(S3AdapterParserUtils.KEY_NAME_GENERATOR_ATTRIBUTE);
	  
		if (!StringUtils.hasText(bucket)) {
			 throw new BeanCreationException( "A " + S3AdapterParserUtils.BUCKET_NAME_ATTRIBUTE + " should be provided.");
		}
		
		//builder.addPropertyReference...;
		if (StringUtils.hasText(bucket)) builder.addPropertyValue(S3AdapterParserUtils.BUCKET_NAME_PROPERTY, bucket);
		if (StringUtils.hasText(filter)) builder.addPropertyValue(S3AdapterParserUtils.FILTER_PROPERTY, filter);
		//if (StringUtils.hasText(keynameGenerator)) builder.addPropertyValue(S3AdapterParserUtils.KEY_NAME_GENERATOR_PROPERTY, keynameGenerator);
		if (StringUtils.hasText(keyNameGenerator)) builder.addPropertyReference(S3AdapterParserUtils.KEY_NAME_GENERATOR_PROPERTY, keyNameGenerator);
		return builder.getBeanDefinition();
	}

}
