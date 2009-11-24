package com.opencredo.integration.s3.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.integration.file.FileNameGenerator;

import com.opencredo.integration.s3.S3Resource;
import com.opencredo.integration.s3.S3WritingMessageHandler;

public class S3WritingMessageHandlerFactoryBean implements FactoryBean {

	private volatile S3WritingMessageHandler handler;

	private volatile String bucket;
	
	private volatile FileNameGenerator fileNameGenerator;

	private final Object initializationMonitor = new Object();

	
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public void setFileNameGenerator(FileNameGenerator fileNameGenerator) {
		this.fileNameGenerator = fileNameGenerator;
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
			if (this.fileNameGenerator != null) {
				this.handler.setS3KeyNameGenerator(this.fileNameGenerator);
			}
			
			this.handler.afterPropertiesSet();
		}
	}
}