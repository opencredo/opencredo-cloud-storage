package com.opencredo.integration.s3;

import java.io.File;

import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageHandlingException;

public class S3FileWritingMessageHandler implements MessageHandler {

	private S3Resource s3Resource;

	public S3FileWritingMessageHandler(S3Resource s3resource){
		this.s3Resource = s3resource;
	}

	//write the content of Message to S3
    public void handleMessage(Message<?> message){

		Object payload = message.getPayload();
		
		try {
			if (payload instanceof File) {
				s3Resource.setFile((File) payload);
				s3Resource.sendFileToBucket();
			}
			else {
				throw new IllegalArgumentException(
						"unsupported Message payload type [" + payload.getClass().getName() + "]");
			}
		}
		catch (Exception e) {
			throw new MessageHandlingException(message, "failed to write Message payload to file", e);
		}	
	}
    
    
    
}
