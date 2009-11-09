package com.opencredo.integration.s3;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.model.S3Object;
import org.springframework.integration.core.Message;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.FileNameGenerator;
import org.springframework.util.Assert;

public class S3KeyNameGenerator implements FileNameGenerator{
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	String defaultStringKey = new String("string.s3");
	
	public void setDefaultStringKey(String defaultStringKey) {
		this.defaultStringKey = defaultStringKey;
	}

	public String generateFileName(Message<?> message) {
		Assert.notNull(message, "message must not be null");
		if (logger.isDebugEnabled()) logger.debug("message: "+message);
		Class<?> c = message.getPayload().getClass();
		if (logger.isDebugEnabled()) logger.debug("c: "+c);
		if (c.equals(String.class) ){
			return defaultStringKey;
		}
		else if (c.equals(File.class))  {
			//Assert.notNull(message.getHeaders().get(FileHeaders.FILENAME));
			//return (new HashMap(message.getHeaders())).get(FileHeaders.FILENAME).toString();
			return ((File) message.getPayload()).getName();
		}
		else if (c.equals(S3Object.class)) {
			return ((S3Object) message.getPayload()).getKey();
		}
		else return null;
	}

}
