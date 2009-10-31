package com.opencredo.integration.s3;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.jets3t.service.model.S3Object;
import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageHandlingException;

public class S3WritingMessageHandler implements MessageHandler {

	private S3Resource s3Resource;
	private static int keyIndex = 0;

	public S3WritingMessageHandler(S3Resource s3resource){
		this.s3Resource = s3resource;
	}

	//write the content of Message to S3
    public void handleMessage(Message<?> message){

		Object payload = message.getPayload();
		S3Object objectToSend = null;
		try {
			if (payload instanceof File) {
				objectToSend = fileToS3Handler((File) payload);
			}
			else if(payload instanceof String){
				objectToSend = stringToS3Handler((String) payload);
			}
			else if(payload instanceof S3Object){
				objectToSend = (S3Object) payload;
			}
			else {
				throw new IllegalArgumentException(
						"unsupported Message payload type [" + payload.getClass().getName() + "]");
			}
			s3Resource.setS3Object(objectToSend);
			s3Resource.sendS3ObjectToBucket();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			throw new MessageHandlingException(message, "failed to write Message payload to file", e);
		}	
	}
    
    
    private S3Object fileToS3Handler(File fileInput){
    	try {
			return new S3Object(fileInput);
		} 
    	catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} 
    	catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    private S3Object stringToS3Handler(String stringInput){ 
    	try {
			return new S3Object(stringKeyGenerator(), stringInput);
		} 
    	catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} 
    	catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }

	private String stringKeyGenerator() {
		keyIndex++;
		return "stringData"+keyIndex;
	}
    
}
