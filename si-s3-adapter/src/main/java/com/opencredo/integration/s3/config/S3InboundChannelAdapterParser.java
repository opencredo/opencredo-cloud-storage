package com.opencredo.integration.s3.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractPollingInboundChannelAdapterParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class S3InboundChannelAdapterParser extends AbstractPollingInboundChannelAdapterParser {
	 
	@Override
	protected String parseSource(Element element, ParserContext parserContext) {
		 BeanDefinitionBuilder builder = BeanDefinitionBuilder.
		 	genericBeanDefinition("com.opencredo.integration.s3.config.S3ReadingMessageSourceFactoryBean");
		 
		 IntegrationNamespaceUtils.setValueIfAttributeDefined(builder, element, S3AdapterParserUtils.BUCKET_NAME_ATTRIBUTE);
		 IntegrationNamespaceUtils.setReferenceIfAttributeDefined(builder, element, "comparator");
		 String filterBeanName = this.registerS3ListFilter(element, parserContext);
		 builder.addPropertyReference("filter", filterBeanName);
		 
		 return BeanDefinitionReaderUtils.registerWithGeneratedName(builder.getBeanDefinition(), parserContext.getRegistry());
	}
	
	private String registerS3ListFilter(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder factoryBeanBuilder = BeanDefinitionBuilder.genericBeanDefinition(
				"com.opencredo.integration.s3.config.S3ObjectListFilterFactoryBean");
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
