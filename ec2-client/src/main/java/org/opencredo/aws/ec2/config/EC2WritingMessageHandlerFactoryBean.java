package org.opencredo.aws.ec2.config;

import org.opencredo.aws.ec2.EC2WritingMessageHandler;
import org.springframework.beans.factory.FactoryBean;


public class EC2WritingMessageHandlerFactoryBean implements FactoryBean {

	private volatile EC2WritingMessageHandler handler;

	private volatile String bucket;
	
	private final Object initializationMonitor = new Object();

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public Object getObject() throws Exception {
		if (this.handler == null) {
			initHandler();
		}
		return this.handler;
	}

	public Class<?> getObjectType() {
		return EC2WritingMessageHandler.class;
	}

	public boolean isSingleton() {
		return true;
	}

	private void initHandler() throws Exception {
		synchronized (this.initializationMonitor) {
			if (this.handler != null) {
				return;
			}
		
			this.handler = new EC2WritingMessageHandler();
		
		}
	}
}