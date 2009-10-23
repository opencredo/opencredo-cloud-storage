package com.opencredo.integration.s3;

import org.jets3t.service.model.S3Object;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.Message;
//import org.springframework.integration.message.MessageBuilder;


@MessageEndpoint
public class S3ObjectFormatter {

	@ServiceActivator(inputChannel="s3InputChannel", outputChannel="s3OutputMessageChannel")
	public Message handleS3Message(Message<S3Object> s3Object){
	
        //TODO: handle Message<S3Object>
		
		return null;
	}
}

