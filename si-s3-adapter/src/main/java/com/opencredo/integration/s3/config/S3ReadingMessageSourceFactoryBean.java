package com.opencredo.integration.s3.config;

import java.util.Comparator;

import org.jets3t.service.model.S3Object;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

import com.opencredo.integration.s3.S3ObjectListFilter;
import com.opencredo.integration.s3.S3ReadingMessageSource;
import com.opencredo.integration.s3.S3Resource;

/*
 * Resource dependency is exposed as bean property of type S3Resource. see: ResourceLoaderAware
 */
public class S3ReadingMessageSourceFactoryBean implements FactoryBean {

	private volatile S3ReadingMessageSource source;
	
	private volatile String bucket;
	
	private volatile S3ObjectListFilter filter;
	
	private volatile Comparator<S3Object> comparator; 
	
	private final Object initializationMonitor = new Object();
	
	
	public Object getObject() throws Exception {
		if (this.source == null) {
			initSource();
		}
		return this.source;
	}

	public Class<?> getObjectType() {
		return S3ReadingMessageSource.class;
	}

	public boolean isSingleton() {
		return true;
	}
	
	public void setBucket(String bucket) {
		Assert.hasText(bucket, "bucket must not be empty");
		this.bucket = bucket;
	}

	public void setComparator(Comparator<S3Object> comparator) {
		this.comparator = comparator;
	}
	
	public void setFilter(S3ObjectListFilter filter) {
		this.filter = filter;
	}
	
	private void initSource() {
		synchronized (this.initializationMonitor) {
			if (this.source != null) {
				return;
			}
			this.source = (this.comparator != null) ? new S3ReadingMessageSource(this.comparator) : new S3ReadingMessageSource();
			this.source.setS3Resource(new S3Resource(this.bucket));
			
			if (this.filter != null) {
				this.source.setFilter(this.filter);
			}
			
			this.source.afterPropertiesSet();
		}
	}

}
