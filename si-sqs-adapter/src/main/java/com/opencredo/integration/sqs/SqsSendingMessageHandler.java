package com.opencredo.integration.sqs;

import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.message.MessageRejectedException;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.integration.message.MessageDeliveryException;
import org.springframework.integration.core.Message;
import com.xerox.amazonws.sqs2.MessageQueue;
import com.xerox.amazonws.sqs2.SQSUtils;
import com.xerox.amazonws.sqs2.SQSException;


public class SqsSendingMessageHandler implements MessageHandler {

    private final MessageQueue messageQueue;

    public SqsSendingMessageHandler(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    public SqsSendingMessageHandler(String queueName, String awsAccessKeyId, String awsSecretKey) throws SQSException {
        this.messageQueue = SQSUtils.connectToQueue(queueName, awsAccessKeyId, awsSecretKey);
    }


    public void handleMessage(Message<?> message) throws MessageRejectedException, MessageHandlingException, MessageDeliveryException {
        //convert to appropriate representation

    }
}
