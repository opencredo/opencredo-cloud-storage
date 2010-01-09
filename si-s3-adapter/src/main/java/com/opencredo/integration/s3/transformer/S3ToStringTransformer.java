package com.opencredo.integration.s3.transformer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

import com.opencredo.integration.s3.S3IntegrationException;
import com.opencredo.integration.s3.S3Resource;

public class S3ToStringTransformer {
	private final Log logger = LogFactory.getLog(this.getClass());
		
		@SuppressWarnings("unchecked")
		public Message<String> transform(Message<?> s3MetaDataMapMessage) {
			Map<String, Object> metaDataMap = (Map<String, Object>) s3MetaDataMapMessage.getPayload();
			if (logger.isDebugEnabled()) logger.debug("metaDataMap: "+s3MetaDataMapMessage.getPayload());
			try {
				S3Service s3Service = new RestS3Service(S3Resource.awsCredentials);
				/*
				int length = Integer.parseInt(metaDataMap.get("Content-Length").toString());
				byte[] s3Content = new byte[length];
				s3Service.getObject(s3Service.getBucket(metaDataMap.get("bucketName").toString()), metaDataMap.get("key").toString()).getDataInputStream().read(s3Content, 0, length);
				*/
				InputStream is = s3Service.getObject(s3Service.getBucket(metaDataMap.get("bucketName").toString()), metaDataMap.get("key").toString()).getDataInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) { 
					sb.append(line + "\n"); 
				}
				reader.close();
				MessageBuilder<String> builder = (MessageBuilder<String>) MessageBuilder.withPayload(sb.toString());
				return builder.build();
			} 
			catch (S3ServiceException e) {
				throw new S3IntegrationException("Message Transform Error", e);
			} catch (IOException e) {
				throw new S3IntegrationException("Message Transform IO Error", e);
			}				
		}
	}