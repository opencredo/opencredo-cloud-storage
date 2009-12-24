package com.opencredo.integration.s3.config;

import org.springframework.beans.factory.FactoryBean;

import com.opencredo.integration.s3.S3KeyNameGenerator;
import com.opencredo.integration.s3.S3Resource;
import com.opencredo.integration.s3.S3WritingMessageHandler;

public class S3WritingMessageHandlerFactoryBean implements FactoryBean {

	private volatile S3WritingMessageHandler handler;

	private volatile String bucket;
	
	private volatile S3KeyNameGenerator s3KeyNameGenerator;

	private final Object initializationMonitor = new Object();

	
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public void setS3KeyNameGenerator(S3KeyNameGenerator s3KeyNameGenerator) {
		this.s3KeyNameGenerator = s3KeyNameGenerator;
	}

	public Object getObject() throws Exception {
		if (this.handler == null) {
			initHandler();
		}
		return this.handler;
	}

	public Class<?> getObjectType() {
		return S3WritingMessageHandler.class;
	}

	public boolean isSingleton() {
		return true;
	}

	private void initHandler() throws Exception {
		synchronized (this.initializationMonitor) {
			if (this.handler != null) {
				return;
			}
		
			this.handler = new S3WritingMessageHandler(new S3Resource(this.bucket));
			if (this.s3KeyNameGenerator != null) {
				this.handler.setS3KeyNameGenerator(this.s3KeyNameGenerator);
			}
			
			this.handler.afterPropertiesSet();
		}
	}
}