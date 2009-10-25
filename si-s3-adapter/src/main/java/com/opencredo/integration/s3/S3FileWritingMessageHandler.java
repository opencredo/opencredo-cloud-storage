package com.opencredo.integration.s3;

import java.io.File;
import java.io.IOException;

import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.core.Message;
import org.springframework.integration.file.DefaultFileNameGenerator;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.FileNameGenerator;
import org.springframework.integration.file.FileWritingMessageHandler;


import org.springframework.integration.handler.ReplyMessageHolder;
import org.springframework.integration.message.MessageHandlingException;

public class S3FileWritingMessageHandler extends FileWritingMessageHandler {

	private S3Resource s3Resource;
	
	private static final String TEMPORARY_FILE_SUFFIX = "_temp";
	private volatile FileNameGenerator fileNameGenerator = new DefaultFileNameGenerator();

	public S3FileWritingMessageHandler(S3Resource s3resource){
		super(s3resource);
		this.s3Resource = s3resource;
	}

    protected void handleRequestMessage(Message<?> message, ReplyMessageHolder replyMessageHolder){

		Object payload = message.getPayload();
		String generatedFileName = fileNameGenerator.generateFileName(message);
		File originalFileFromHeader = retrieveOriginalFileFromHeader(message);
		File tempFile = new File(s3Resource.tempDestinationDirectory, generatedFileName + TEMPORARY_FILE_SUFFIX);
		File resultFile = new File(s3Resource.tempDestinationDirectory, generatedFileName);
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
	}

	/**
	 * Retrieves the File instance from the {@link FileHeaders#ORIGINAL_FILE}
	 * header if available. If the value is not a File instance or a String
	 * representation of a file path, this will return <code>null</code>. 
	 */
	private File retrieveOriginalFileFromHeader(Message<?> message) {
		Object value = message.getHeaders().get(FileHeaders.ORIGINAL_FILE);
		if (value instanceof File) {
			return (File) value;
		}
		if (value instanceof String) {
			return new File((String) value);
		}
		return null;
	}

	private File handleFileMessage(File sourceFile, File tempFile, File resultFile) throws IOException {
		//TODO: Handle message's file content
		return resultFile;
    }
}
