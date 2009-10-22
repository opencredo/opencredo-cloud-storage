package com.opencredo.integration.s3;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;


@MessageEndpoint
public class S3ObjectFormatter {

	@ServiceActivator(inputChannel="s3InputChannel2", outputChannel="s3OutputMessageChannel")
	public Message<?> convertS3(Object s3Object){
	
		//convert s3 objects to spring integration messages and sends them to outputChannel
        
        //MessageBuilder builder = MessageBuilder.withPayload(s3Object.getMessageBody());

        //decide what headers we want to map
        //return builder.build();
		
		return null;
	}
}

