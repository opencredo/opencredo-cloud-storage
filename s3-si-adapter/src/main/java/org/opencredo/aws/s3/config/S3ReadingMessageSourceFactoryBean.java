/* Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencredo.aws.s3.config;

import java.util.Comparator;

import org.jets3t.service.model.S3Object;
import org.opencredo.aws.s3.AWSCredentials;
import org.opencredo.aws.s3.S3ObjectListFilter;
import org.opencredo.aws.s3.S3ReadingMessageSource;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class S3ReadingMessageSourceFactoryBean implements FactoryBean {

	private volatile S3ReadingMessageSource source;
	
	private AWSCredentials awsCredentials;
	private volatile String bucketName;
	private volatile S3ObjectListFilter filter;
	private volatile Comparator<S3Object> comparator; 
	private volatile String deleteWhenReceived;	
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
	
	/**
	 * @param bucketName
	 */
	public void setBucketName(String bucketName) {
		Assert.hasText(bucketName, "bucket must not be empty");
		this.bucketName = bucketName;
	}

	/**
	 * @param comparator
	 */
	public void setComparator(Comparator<S3Object> comparator) {
		this.comparator = comparator;
	}
	
	public void setFilter(S3ObjectListFilter filter) {
		this.filter = filter;
	}
	
	public void setDeleteWhenReceived (String deleteWhenReceived) {
		this.deleteWhenReceived = deleteWhenReceived;
	}
	
	public void setAwsCredentials(AWSCredentials awsCredentials) {
		this.awsCredentials = awsCredentials;
	}
	
	private void initSource() {
		synchronized (this.initializationMonitor) {
			if (this.source != null) {
				return;
			}
			this.source = (this.comparator != null) ? new S3ReadingMessageSource(this.comparator) : new S3ReadingMessageSource(awsCredentials);
			this.source.setBucketName(bucketName);	
			if (this.filter != null) {
				this.source.setFilter(this.filter);
			}
			this.source.setDeleteWhenReceived(this.deleteWhenReceived);
			this.source.afterPropertiesSet();
		}
	}

}
