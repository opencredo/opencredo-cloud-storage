/* Copyright 2008 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.opencredo.integration.s3.transformer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Object;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.transformer.Transformer;

import com.opencredo.integration.s3.S3IntegrationException;
import com.opencredo.integration.s3.S3Resource;

import java.util.Map;

/**
 * transforms the message with a metadata map payload 
 * to message with the actual content
 */
public class S3MessageTransformer implements Transformer{

	private final Log logger = LogFactory.getLog(this.getClass());
	
	public Message<S3Object> transform(Message<?> s3MetaDataMapMessage) {
		if (logger.isDebugEnabled()) logger.debug("S3MessageTransformer.transform() called.");
		if (logger.isDebugEnabled()) logger.debug(s3MetaDataMapMessage.getPayload());
		Map<String, Object> metaDataMap = (Map<String, Object>) s3MetaDataMapMessage.getPayload();
		try {
			S3Service s3Service = new RestS3Service(S3Resource.awsCredentials);
			MessageBuilder<S3Object> builder;
			builder = (MessageBuilder<S3Object>) MessageBuilder.withPayload(s3Service.getObject(s3Service.getBucket(metaDataMap.get("bucketName").toString()), metaDataMap.get("key").toString()));
			return builder.build();
		} 
		catch (S3ServiceException e) {
			throw new S3IntegrationException("Message Transform Error", e);
		}				
	}

}
