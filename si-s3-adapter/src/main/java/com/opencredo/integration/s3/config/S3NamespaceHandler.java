package com.opencredo.integration.s3.config;

import org.springframework.integration.config.xml.AbstractIntegrationNamespaceHandler;

public class S3NamespaceHandler extends AbstractIntegrationNamespaceHandler {

	 public void init() {
		 this.registerBeanDefinitionParser("inbound-channel-adapter", new S3InboundAdapterParser());
		 this.registerBeanDefinitionParser("outbound-channel-adapter", new S3OutboundAdapterParser());
		 this.registerBeanDefinitionParser("s3-directory", new S3ResourceParser());
	 }
}
