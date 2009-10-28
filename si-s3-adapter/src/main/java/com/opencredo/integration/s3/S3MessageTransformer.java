package com.opencredo.integration.s3;


import org.jets3t.service.model.S3Object;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessagingException;

public class S3MessageTransformer {


	public Message<S3Object> transform(Message<S3Object> s3Message) {
		//TODO: transform the message with metadata to message with real content
		try{
			System.out.println(s3Message.getPayload());
		} 
		catch (Exception e) {
			throw new MessagingException(s3Message, "failed to transform  Message", e);
		}
		return null;
	}

}
