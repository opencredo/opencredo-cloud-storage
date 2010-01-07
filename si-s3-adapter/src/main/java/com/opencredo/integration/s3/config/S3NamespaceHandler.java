package com.opencredo.integration.s3.config;

import org.springframework.integration.config.xml.AbstractIntegrationNamespaceHandler;

import com.opencredo.integration.ec2.config.S3InboundChannelAdapterParser;
import com.opencredo.integration.ec2.config.EC2OutboundChannelAdapterParser;

public class S3NamespaceHandler extends AbstractIntegrationNamespaceHandler {

	 public void init() {
		 this.registerBeanDefinitionParser("inbound-channel-adapter", new S3InboundChannelAdapterParser());
		 this.registerBeanDefinitionParser("outbound-channel-adapter", new EC2OutboundChannelAdapterParser());
		 
	 }
}
