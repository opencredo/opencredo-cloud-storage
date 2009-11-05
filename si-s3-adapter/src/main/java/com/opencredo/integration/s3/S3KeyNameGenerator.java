package com.opencredo.integration.s3;

import java.io.File;
import java.util.HashMap;

import org.jets3t.service.model.S3Object;
import org.springframework.integration.core.Message;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.FileNameGenerator;
import org.springframework.util.Assert;

public class S3KeyNameGenerator implements FileNameGenerator{

	private String defaultStringKey = new String("string.s3");
	
	public void setDefaultStringKey(String defaultStringKey) {
		this.defaultStringKey = defaultStringKey;
	}

	public String generateFileName(Message<?> message) {
		Assert.notNull(message, "message must not be null");
		Class<?> c = message.getClass();
		Assert.notNull(c, "class type must not be null");
		if (c.isInstance(String.class) ){
			return defaultStringKey;
		}
		else if ((c.isInstance(File.class)) || (c.isInstance(S3Object.class)) ) {
			return (new HashMap(message.getHeaders())).get(FileHeaders.FILENAME).toString();
		}
		else return null;
	}

}
