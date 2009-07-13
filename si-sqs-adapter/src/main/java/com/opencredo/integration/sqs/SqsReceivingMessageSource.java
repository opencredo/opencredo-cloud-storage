package com.opencredo.integration.sqs;

import org.springframework.integration.message.MessageSource;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessagingException;
import com.xerox.amazonws.sqs2.MessageQueue;
import com.xerox.amazonws.sqs2.SQSException;
import com.xerox.amazonws.sqs2.SQSUtils;


public class SqsReceivingMessageSource implements MessageSource<Message> {

    private final MessageQueue messageQueue;

    public SqsReceivingMessageSource(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    public SqsReceivingMessageSource(String queueName, String awsAccessKeyId, String awsSecretKey) throws SQSException {
        this.messageQueue = SQSUtils.connectToQueue(queueName, awsAccessKeyId, awsSecretKey);
    }


    public Message<Message> receive() {
        try {
            com.xerox.amazonws.sqs2.Message message = this.messageQueue.receiveMessage();
            MessageBuilder builder = MessageBuilder.withPayload(message.getMessageBody());
            //decide what headers we want to map
            return builder.build();
        } catch (SQSException sqsE) {
            throw new MessagingException("Exception retreiving message from SQS queue " + messageQueue.getUrl());
        }
    }
}
