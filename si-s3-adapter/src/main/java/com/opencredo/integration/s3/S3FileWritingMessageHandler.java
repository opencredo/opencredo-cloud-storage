package com.opencredo.integration.s3;

import java.io.File;
import java.io.IOException;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.security.AWSCredentials;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.core.Message;
import org.springframework.integration.file.DefaultFileNameGenerator;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.FileNameGenerator;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import org.springframework.integration.handler.ReplyMessageHolder;
import org.springframework.integration.message.MessageHandlingException;

public class S3FileWritingMessageHandler extends FileWritingMessageHandler {

	private S3Service s3Service;
	private S3Bucket sBucket;
	
	private final static Resource destinationDirectoryResource = (Resource) new FileSystemResource("resources/si-s3-destination/");
	private static final String TEMPORARY_FILE_SUFFIX = "_temp";
	private File destinationDirectory;
	private volatile FileNameGenerator fileNameGenerator = new DefaultFileNameGenerator();

    public S3FileWritingMessageHandler(String bucketName, String awsAccessKeyId, String awsSecretKey){
    	super(destinationDirectoryResource);
    	try {
			s3Service = new RestS3Service(new AWSCredentials(awsAccessKeyId, awsSecretKey));	
			sBucket = s3Service.getBucket(bucketName);
    	} 
    	catch (S3ServiceException e) {
			e.printStackTrace();
		}
    }

    protected void handleRequestMessage(Message<?> message, ReplyMessageHolder replyMessageHolder){

		Object payload = message.getPayload();
		String generatedFileName = fileNameGenerator.generateFileName(message);
		File originalFileFromHeader = retrieveOriginalFileFromHeader(message);
		File tempFile = new File(destinationDirectory, generatedFileName + TEMPORARY_FILE_SUFFIX);
		File resultFile = new File(destinationDirectory, generatedFileName);
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
