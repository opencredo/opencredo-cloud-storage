package com.opencredo.integration.s3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Object;
import org.springframework.integration.core.Message;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.message.MessageBuilder;

/*
 * This application is used to test if the adapter produces the 
 * expected behaviour.
 */

public class MainApp {
	S3FileReadingMessageSource messageSource;
	S3MessageTransformer transformer;
	S3WritingMessageHandler handler;
	
	public static void main(String[] args) throws IOException, S3ServiceException{
		String bucketName = new String("sibucket");
		S3Resource resource = new S3Resource(bucketName);
		MainApp mainApp = new MainApp(new S3FileReadingMessageSource(resource.getS3Service(), resource.getS3Bucket()), new S3MessageTransformer(), new S3WritingMessageHandler(resource));
		mainApp.implementApp();	
	}
	
	public MainApp(S3FileReadingMessageSource messageSource, S3MessageTransformer transformer,
			S3WritingMessageHandler handler) throws IOException, S3ServiceException{
		setMessageSource(messageSource);
		setTransformer(transformer);
		setHandler(handler);
		implementApp();
	}
	
	private void implementApp() throws IOException, S3ServiceException {
		String testString = new String("AppendTestString");
		Message<Map> receivedMessage = messageSource.receive();
		Message<S3Object> transformedMessage = transformer.transform(receivedMessage);
		S3Object payload = transformedMessage.getPayload();
		InputStream is = payload.getDataInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) { 
			sb.append(line + "\n"); 
		}
		sb.append(testString);

		MessageBuilder<S3Object> builder = MessageBuilder.withPayload(new S3Object(sb.toString()));
		//builder.setHeader(FileHeaders.FILENAME, testHandler.getName());
		handler.handleMessage(builder.build());
		
	}

	public S3FileReadingMessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(S3FileReadingMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public S3MessageTransformer getTransformer() {
		return transformer;
	}

	public void setTransformer(S3MessageTransformer transformer) {
		this.transformer = transformer;
	}

	public S3WritingMessageHandler getHandler() {
		return handler;
	}

	public void setHandler(S3WritingMessageHandler handler) {
		this.handler = handler;
	}

}
