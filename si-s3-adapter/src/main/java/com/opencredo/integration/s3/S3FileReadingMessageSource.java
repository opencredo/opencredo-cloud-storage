package com.opencredo.integration.s3;

import org.springframework.integration.file.FileListFilter;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessagingException;

import org.apache.log4j.Logger;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.model.S3Bucket;


//https://sibucket.s3.amazonaws.com:443/

/** 
 * MessageSource that creates messages from a Simple Queue Service.
 */
public class S3FileReadingMessageSource extends FileReadingMessageSource {

    //private final s3queue;
	private static final Logger LOGGER = Logger.getLogger(S3FileReadingMessageSource.class);
	private S3Resource inputDirectory;
	//private S3Bucket sibucket;
	private final String awsAccessKeyId = "AKIAJJC4KITQHSAY43MQ";
	private final String awsSecretKey = "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";
	
    public void FileReadingMessageSource() {

    	S3Service s3Service;
		try {
			s3Service = new RestS3Service(new AWSCredentials(awsAccessKeyId, awsSecretKey));
			this.inputDirectory = new S3Resource(s3Service);
							
			//sibucket = inputDirectory.getBucket("sibucket"); 
 
		} 
		catch (S3ServiceException e) {
			e.printStackTrace();
		} 

    }
    
    public void FileReadingMessageSource(java.util.Comparator<java.io.File> receptionOrderComparator){
    	
    }

    //Adds the failed message back to the 'toBeReceived' queue.
	public void onFailure(Message<java.io.File> failedMessage, java.lang.Throwable t){
		
	} 
	   
	//The message is just logged. It was already removed from the queue during the call to receive()
	public void	onSend(Message<java.io.File> sentMessage){
		
	} 

	//Retrieve the next available message from this source.
	public Message<java.io.File> receive(){
		try {

			//TODO: Convert a newly created s3 object to a Message
			
			//this.inputDirectory.
			
			
			return null;
        } catch (Exception e) {
            throw new MessagingException("Exception retrieving message from S3 source ");
        }
	} 
	 
	//Specify whether to create the source directory automatically if it does not yet exist upon initialization.
	public void	setAutoCreateDirectory(boolean autoCreateDirectory){
		
	} 
	  
	//Sets a FileListFilter.
	public void	setFilter(FileListFilter filter){
		
	} 
	          
	//Specify the input directory.
	public void	setInputDirectory(org.springframework.core.io.Resource inputDirectory){
		
	} 
	          
}
