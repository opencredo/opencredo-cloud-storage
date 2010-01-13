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

package org.opencredo.aws.s3.transformer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Object;
import org.opencredo.aws.s3.AWSCredentials;
import org.opencredo.aws.s3.S3IntegrationException;
import org.opencredo.aws.s3.S3Template;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class S3ToByteArrayTransformer {
	private Log logger = LogFactory.getLog(this.getClass());

	private AWSCredentials awsCredentials;
	
	/**
	 * @param s3MetaDataMapMessage
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public Message<byte[]> transform(Message<?> s3MetaDataMapMessage) throws IOException {
		if (logger.isDebugEnabled()) logger.debug(s3MetaDataMapMessage.getPayload());
		Map<String, Object> metaDataMap = (Map<String, Object>) s3MetaDataMapMessage.getPayload();
		try {
			S3Template s3Template = new S3Template(awsCredentials);
			
			MessageBuilder<byte[]> builder;
			String key = metaDataMap.get("key").toString();
			String bucketName = metaDataMap.get("bucketName").toString();
			S3Object s3object = s3Template.getS3Service().getObject(s3Template.getS3Service().getBucket(bucketName), key);
			String contentType = s3object.getContentType();
			byte[] b = null;
			if (contentType.compareTo("application/octet-stream") == 0) {
				BufferedInputStream bis = new BufferedInputStream(s3object.getDataInputStream());
				int length = bis.available();
				b = new byte[length];
				bis.read(b, 0, bis.available());
				bis.close();
			}
			else if (contentType.compareTo("text/plain") == 0) {
				InputStream is = s3object.getDataInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = br.readLine()) != null) { 
					sb.append(line + "\n"); 
				}
				b = sb.toString().getBytes();
				br.close();
			}
			
			builder = (MessageBuilder<byte[]>) MessageBuilder.withPayload(b);
			return builder.build();
		} 
		catch (S3ServiceException e) {
			throw new S3IntegrationException("Message Transform Error", e);
		}				
	}

}
