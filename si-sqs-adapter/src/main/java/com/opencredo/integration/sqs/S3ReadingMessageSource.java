package com.opencredo.integration.sqs;

import org.springframework.integration.message.MessageSource;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessagingException;

import org.apache.log4j.Logger;



/**
 * MessageSource that creates messages from an S3 Service.
 */
public class S3ReadingMessageSource implements MessageSource<Message> {

	//private final String awsAccessKey = "AKIAJJC4KITQHSAY43MQ";
	//private final String awsSecretKey = "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";

    private s3Channel;
    private static final Logger LOGGER = Logger.getLogger(S3ReadingMessageSource.class);

    public S3ReadingMessageSource(MessageQueue messageQueue) {
        //this.s3Queue = messageQueue;
    }

    public S3ReadingMessageSource(String queueName, String awsAccessKeyId, String awsSecretKey) throws SQSException {
        //this.s3Queue = SQSUtils.connectToQueue(queueName, awsAccessKeyId, awsSecretKey);
    	S3Service s3Service = new RestS3Service(new AWSCredentials(awsAccessKeyId, awsSecretKey));
    
    	//list buckets to test if connection works correctly
    	S3Bucket[] myBuckets = s3Service.listAllBuckets();
    	//TODO:log4j
    	System.out.println("Number of buckets: " + myBuckets.length);
    	
    	// Retrieve the HEAD of the data object we created previously.
    	S3Object objectDetailsOnly = s3Service.getObjectDetails(testBucket, "helloWorld.txt");
    	System.out.println("S3Object, details only: " + objectDetailsOnly);
    	
    	
    	// Retrieve the whole data object we created previously
    	S3Object objectComplete = s3Service.getObject(testBucket, "helloWorld.txt");
    	System.out.println("S3Object, complete: " + objectComplete);
    	// Read the data from the object's DataInputStream using a loop, and print it out.
    	System.out.println("Greeting:");
    	BufferedReader reader = new BufferedReader(
    	        new InputStreamReader(objectComplete.getDataInputStream()));
    	String data = null;
    	while ((data = reader.readLine()) != null) {
    	        System.out.println(data);
    	}
    	 	
    }

    public Message<Message> receive() {
        try {
            Message message = this.s3Queue.receiveMessage();
            MessageBuilder builder = MessageBuilder.withPayload(message.getMessageBody());

            //decide what headers we want to map
            return builder.build();
        } catch (SQSException sqsE) {
            throw new MessagingException("Exception retrieving message from S3 queue " + messageQueue.getUrl());
        }
    }
}
