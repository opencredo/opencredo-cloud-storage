package com.opencredo.integration.s3;

import java.io.File;
import java.io.IOException;

import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageDeliveryException;
import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.message.MessageRejectedException;
import org.springframework.integration.core.Message;
import org.springframework.integration.file.DefaultFileNameGenerator;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.FileNameGenerator;
import org.springframework.integration.file.FileWritingMessageHandler;


import org.springframework.integration.handler.ReplyMessageHolder;
import org.springframework.integration.message.MessageHandlingException;

public class S3FileWritingMessageHandler implements MessageHandler {

	private S3Resource s3Resource;

	public S3FileWritingMessageHandler(S3Resource s3resource){
		this.s3Resource = s3resource;
	}

    public void handleMessage(Message<?> message){

		Object payload = message.getPayload();
		/*
		try {
			if (payload instanceof File) {
				resultFile = handleFileMessage((File) payload, tempFile, resultFile);
			}
			else {
				throw new IllegalArgumentException(
						"unsupported Message payload type [" + payload.getClass().getName() + "]");
			}
		}
		catch (Exception e) {
			throw new MessageHandlingException(message, "failed to write Message payload to file", e);
		}
		if (resultFile != null) {
			if (originalFileFromHeader == null && payload instanceof File) {
				replyMessageHolder.set(MessageBuilder.withPayload(resultFile)
						.setHeader(FileHeaders.ORIGINAL_FILE, (File) payload)
						.build());
			}
			else {
				replyMessageHolder.set(resultFile);
			}
		}
		*/
	}
}
