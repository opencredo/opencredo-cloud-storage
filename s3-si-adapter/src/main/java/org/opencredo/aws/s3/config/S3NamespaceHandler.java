package org.opencredo.aws.s3.config;

import org.springframework.integration.config.xml.AbstractIntegrationNamespaceHandler;

public class S3NamespaceHandler extends AbstractIntegrationNamespaceHandler {

	 public void init() {
		 this.registerBeanDefinitionParser("inbound-channel-adapter", new S3InboundChannelAdapterParser());
		 this.registerBeanDefinitionParser("outbound-channel-adapter", new S3OutboundChannelAdapterParser());
		 
	 }
}
