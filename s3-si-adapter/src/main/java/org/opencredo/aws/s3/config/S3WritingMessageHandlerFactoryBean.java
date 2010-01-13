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

import org.opencredo.aws.s3.AWSCredentials;
import org.opencredo.aws.s3.S3KeyNameGenerator;
import org.opencredo.aws.s3.S3WritingMessageHandler;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class S3WritingMessageHandlerFactoryBean implements FactoryBean {

	private volatile S3WritingMessageHandler handler;

	private String bucketName;
	
	private AWSCredentials awsCredentials;
	
	private volatile S3KeyNameGenerator s3KeyNameGenerator;

	private final Object initializationMonitor = new Object();

	/**
	 * @param awsCredentials
	 */
	public void setAwsCredentials(AWSCredentials awsCredentials) {
		this.awsCredentials = awsCredentials;
	}
	
	/**
	 * @param bucketName
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * @param s3KeyNameGenerator
	 */
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
			
			this.handler = new S3WritingMessageHandler(awsCredentials);
			this.handler.setBucketName(bucketName);
			if (this.s3KeyNameGenerator != null) {
				this.handler.setS3KeyNameGenerator(this.s3KeyNameGenerator);
			}
			
			this.handler.afterPropertiesSet();
		}
	}
}