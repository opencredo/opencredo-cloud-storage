package org.opencredo.aws.ec2.config;

import org.springframework.integration.config.xml.AbstractIntegrationNamespaceHandler;

public class EC2NamespaceHandler extends AbstractIntegrationNamespaceHandler {

	 public void init() {
		
		 this.registerBeanDefinitionParser("outbound-channel-adapter", new EC2OutboundChannelAdapterParser());
		 
	 }
}
