
package com.opencredo.integration.sqs;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.Message;


@MessageEndpoint
public class SendToSQSService {

	
	
	private final String sqsQueName = null;
	private final String awsAccessKeyId = null;
	private final String awsSecretKey = null;
	
	private SqsSendingMessageHandler sqsSendingMessageHandler;

    SendToSQSService() throws Exception{
        sqsSendingMessageHandler = new SqsSendingMessageHandler(sqsQueName, awsAccessKeyId, awsSecretKey);
    }
	
	@ServiceActivator(inputChannel="checkS3Channel")
	public void sendToSQS(Message<?> siMessage) {
			
		sqsSendingMessageHandler.handleMessage(siMessage);
			
	}

}