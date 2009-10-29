package com.opencredo.integration.s3;

import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;


import org.jets3t.service.Constants;
import org.jets3t.service.S3ObjectsChunk;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import static  org.jets3t.service.S3Service.*;

import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageSource;
import org.springframework.integration.core.Message;

/** 
 * MessageSource that creates messages from a Simple Queue Service.
 */
public class S3FileReadingMessageSource implements MessageSource<S3Object> {
	
	class S3ObjectLastModifiedDateComparator implements Comparator<S3Object>{

		public int compare(S3Object s3obj1, S3Object s3obj2){
			Date s3obj1LastModifiedDate = s3obj1.getLastModifiedDate();
			Date s3obj2LastModifiedDate = s3obj2.getLastModifiedDate();
			
			if( s3obj1LastModifiedDate.after(s3obj2LastModifiedDate) )
				return 1;
			else if( s3obj1LastModifiedDate.before(s3obj2LastModifiedDate) )
				return -1;
			else
				return 0;
		}
	}
	
	private static final int INTERNAL_QUEUE_CAPACITY = 5;
	
	private volatile AcceptOnceS3ObjectListFilter filter = new AcceptOnceS3ObjectListFilter();

	private final Queue<S3Object> toBeReceived;
	private S3Service s3Service;
	private S3Bucket s3Bucket;
	
	//private Map<String, String> sentKeysMap = new HashMap<String, String>(); // (etag, last_modified_timestamp)
	
	public S3Service getS3Service() {
		return s3Service;
	}

	public void setS3Service(S3Service s3Service) {
		this.s3Service = s3Service;
	}

	public S3Bucket getsBucket() {
		return s3Bucket;
	}

	public void setsBucket(S3Bucket sBucket) {
		this.s3Bucket = sBucket;
	}
	
	public Queue<S3Object> getQueueToBeReceived(){
		return toBeReceived;
	}
    
    public S3FileReadingMessageSource(S3Service s3Service, S3Bucket sBucket){ 
    	this.s3Service = s3Service;
    	this.s3Bucket = sBucket;
    	this.toBeReceived = new PriorityBlockingQueue<S3Object>(INTERNAL_QUEUE_CAPACITY, new S3ObjectLastModifiedDateComparator());
    }
	
	public Message<S3Object> receive(){
		
		//TODO: poll, get fileName and uri on new files 
		
		try {
			if (s3Service.checkBucketStatus(s3Bucket.getName()) == BUCKET_STATUS__MY_BUCKET){
				//objectsInBucket contain only minimal information, not the actual content
				S3ObjectsChunk chunk = s3Service.listObjectsChunked(s3Bucket.getName(),
			             null, null, Constants.DEFAULT_OBJECT_LIST_CHUNK_SIZE, null, true);
				List<S3Object> filteredS3Objects = this.filter.filterFiles(chunk.getObjects());
				Set<S3Object> newS3Objects = new HashSet<S3Object>(filteredS3Objects);
				if (!newS3Objects.isEmpty()) 
					toBeReceived.addAll(newS3Objects);
				MessageBuilder<S3Object> builder = MessageBuilder.withPayload(toBeReceived.poll());
				return builder.build();
	
			}
			else return null;
		} 
		catch (S3ServiceException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	        	          
}
