package com.opencredo.integration.s3;

import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.message.MessageRejectedException;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.integration.message.MessageDeliveryException;
import org.springframework.integration.core.Message;
import org.springframework.core.io.Resource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.handler.ReplyMessageHolder;



public class S3FileWritingMessageHandler extends FileWritingMessageHandler {

    private Resource destinationDirectory;

    public S3FileWritingMessageHandler(Resource destinationDirectory) {
    	super(destinationDirectory);
        this.destinationDirectory = destinationDirectory;
    }

    protected void handleRequestMessage(Message<?> message, ReplyMessageHolder replyMessageHolder){
        //convert to appropriate representation
    	MessageBuilder<?> mBuilder = replyMessageHolder.add(message);

    }
}
