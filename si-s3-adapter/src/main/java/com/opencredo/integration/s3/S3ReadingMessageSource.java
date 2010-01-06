/* Copyright 2008 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.opencredo.integration.s3;

import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import org.jets3t.service.Constants;
import org.jets3t.service.S3ObjectsChunk;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Object;
import static  org.jets3t.service.S3Service.*;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.message.MessageBuilder;
import org.springframework.integration.message.MessageSource;
import org.springframework.integration.core.Message;
import org.springframework.integration.file.FileHeaders;
import org.springframework.util.Assert;

/** 
 * MessageSource that creates messages containing meta-data maps of S3Objects
 */
public class S3ReadingMessageSource implements MessageSource<Map>, InitializingBean {
	
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
	private static final String FILENAME = "filename";
	private static final int DEFAULT_OBJECT_LIST_CHUNK_SIZE = 1000;
	
	private final Log logger = LogFactory.getLog(this.getClass());
	private final Queue<S3Object> toBeReceived;
	
	private S3Resource s3Resource;
	private volatile S3ObjectListFilter filter = new AcceptOnceS3ObjectListFilter();
	private Comparator<S3Object> comparator;
	
    public S3ReadingMessageSource(){ 

    	this.toBeReceived = new PriorityBlockingQueue<S3Object>(INTERNAL_QUEUE_CAPACITY, new S3ObjectLastModifiedDateComparator());
    }
    
	public S3ReadingMessageSource(Comparator<S3Object> receptionOrderComparator) {
		this.toBeReceived = new PriorityBlockingQueue<S3Object>(INTERNAL_QUEUE_CAPACITY, receptionOrderComparator);
	}

	public Message<Map> receive(){
		if (logger.isDebugEnabled()) logger.debug("receive() call received");
		
		Assert.notNull(s3Resource, "S3Resource cannot be null");
    	Assert.notNull(s3Resource.getS3Service(), "S3Service cannot be null");
    	Assert.notNull(s3Resource.getS3Bucket(), "S3Bucket cannot be null");
		
		try {
			if (s3Resource.getS3Service().checkBucketStatus(s3Resource.getS3Bucket().getName()) == BUCKET_STATUS__MY_BUCKET){
	
				//typical info contained in a list: key, lastmodified, etag, size, owner, storageclass
				//Because the completeListing parameter is true, follow-up requests will be sent to provide a complete S3Object listing
				S3ObjectsChunk chunk = s3Resource.getS3Service().listObjectsChunked(s3Resource.getS3Bucket().getName(),
			             null, null, DEFAULT_OBJECT_LIST_CHUNK_SIZE, null, true);
				if (logger.isDebugEnabled()) logger.debug("chunk created: "+chunk);
				List<S3Object> filteredS3Objects = addBucketInfo(this.filter.filterS3Objects(chunk.getObjects()));
				if (logger.isDebugEnabled()) logger.debug("filteredS3Objects: "+filteredS3Objects);
				Set<S3Object> newS3Objects = new HashSet<S3Object>(filteredS3Objects);
				if (!newS3Objects.isEmpty()) 
					toBeReceived.addAll(newS3Objects);
				if (!toBeReceived.isEmpty()) {
					Map metaDataMapPayload = toBeReceived.poll().getMetadataMap();
					MessageBuilder<Map> builder = MessageBuilder.withPayload(metaDataMapPayload);
					builder.setHeader(FILENAME, metaDataMapPayload.get("key"));
					if (logger.isDebugEnabled()) logger.debug("metaDataMapPayload: "+metaDataMapPayload);
					return builder.build();
				}
				else return null;
			}
			else return null;
		} 
		catch (S3ServiceException e) {
			throw new S3IntegrationException(e);
		}		
	}

	/*
	 * add bucket info to metadata so that the transformer knows where the original file is stored 
	 * without injection
	 */
	private List<S3Object> addBucketInfo(List<S3Object> filteredS3Objects) {
		Iterator<S3Object> it = filteredS3Objects.iterator();
		S3Object tempS3Object;
		while(it.hasNext()){
			tempS3Object = it.next();
			tempS3Object.addMetadata("bucketName", s3Resource.getS3Bucket().getName());
			tempS3Object.addMetadata("key", tempS3Object.getKey());
		}
		return filteredS3Objects;
	}

    
    public void setS3Resource(S3Resource s3Resource){
    	this.s3Resource = s3Resource;
    }
    
	public Queue<S3Object> getQueueToBeReceived(){
		return toBeReceived;
	}
	
	public void setFilter(S3ObjectListFilter filter) {
		Assert.notNull(filter, "'filter' should not be null");
		this.filter = filter;
	}

	public void afterPropertiesSet() {
		Assert.isTrue(this.s3Resource.exists(),
				"Source directory [" + s3Resource + "] does not exist.");
		Assert.isTrue(this.s3Resource.isReadable(),
				"Source directory [" + this.s3Resource + "] is not readable.");
	}
	        	          
}
