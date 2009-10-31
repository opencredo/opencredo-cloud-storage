package com.opencredo.integration.s3;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Object;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.transformer.Transformer;
import java.util.Map;

public class S3MessageTransformer implements Transformer{

	private final Log logger = LogFactory.getLog(this.getClass());
	
	public Message<S3Object> transform(Message<?> s3MetaDataMapMessage) {
		//TODO: transform the message with metadata to message with real content
		logger.debug("S3MessageTransformer.transform() called.");
		logger.debug(s3MetaDataMapMessage.getPayload());
		Map<String, Object> metaDataMap = (Map<String, Object>) s3MetaDataMapMessage.getPayload();
		try {
			S3Service s3Service = new RestS3Service(S3Resource.awsCredentials);
			MessageBuilder<S3Object> builder;
			builder = (MessageBuilder<S3Object>) MessageBuilder.withPayload(s3Service.getObject(s3Service.getBucket(metaDataMap.get("bucketName").toString()), metaDataMap.get("key").toString()));
			return builder.build();
		} 
		catch (S3ServiceException e) {
			e.printStackTrace();
			return null;
		}				
	}

}
