package org.opencredo.aws.ec2.config;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class EC2OutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser { //implements BeanDefinitionParser {
	 
	 @Override
	 protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.
	 	genericBeanDefinition("com.opencredo.integration.s3.config.S3WritingMessageHandlerFactoryBean");
	  
		String bucket = element.getAttribute(EC2AdapterParserUtils.BUCKET_NAME_ATTRIBUTE);

		if (!StringUtils.hasText(bucket)) {
			 throw new BeanCreationException( "A " + EC2AdapterParserUtils.BUCKET_NAME_ATTRIBUTE + " should be provided.");
		}
		
		if (StringUtils.hasText(bucket)) builder.addPropertyValue(EC2AdapterParserUtils.BUCKET_NAME_PROPERTY, bucket);
		return builder.getBeanDefinition();
	}

}
