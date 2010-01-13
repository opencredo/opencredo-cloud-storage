package org.opencredo.aws.s3.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.S3ServiceException;
import org.opencredo.aws.s3.AWSCredentials;
import org.opencredo.aws.s3.S3IntegrationException;
import org.opencredo.aws.s3.S3Template;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;


public class S3ToStringTransformer {
	private final Log logger = LogFactory.getLog(this.getClass());
	
	private AWSCredentials awsCredentials;
		
		@SuppressWarnings("unchecked")
		public Message<String> transform(Message<?> s3MetaDataMapMessage) {
			//Map<String, Object> metaDataMapHeaders = (Map<String, Object>) s3MetaDataMapMessage.getHeaders();
			Map<String, Object> metaDataMapMessagePayload = (Map<String, Object>) s3MetaDataMapMessage.getPayload();
			//if (logger.isDebugEnabled()) logger.debug("metaDataMap: "+s3MetaDataMapMessage.getPayload());
			try {
				S3Template s3Template = new S3Template(awsCredentials);

				InputStream is = s3Template.getS3Service().getObject(s3Template.getS3Service().getBucket(metaDataMapMessagePayload.get("bucketName").toString()), metaDataMapMessagePayload.get("key").toString()).getDataInputStream();

				StringWriter writer = new StringWriter();
				IOUtils.copy(is, writer);
				String transformedString = writer.toString();

				MessageBuilder<String> builder = (MessageBuilder<String>) MessageBuilder.withPayload(transformedString);
				Message<String> transformedMessage = builder.build();
				if ( (metaDataMapMessagePayload.containsKey("deleteWhenReceived")) && (metaDataMapMessagePayload.get("deleteWhenReceived").toString().compareTo("true") == 0) ) {
					s3Template.getS3Service().deleteObject(s3Template.getS3Service().getBucket(metaDataMapMessagePayload.get("bucketName").toString()), metaDataMapMessagePayload.get("key").toString());
				}
				return transformedMessage;
			} 
			catch (S3ServiceException e) {
				throw new S3IntegrationException("Message Transform Error", e);
			} catch (IOException e) {
				throw new S3IntegrationException("Message Transform IO Error", e);
			}					
		}
	}