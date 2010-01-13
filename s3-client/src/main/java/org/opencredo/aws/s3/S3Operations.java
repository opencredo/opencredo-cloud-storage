/* Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencredo.aws.s3;

import java.io.File;
import java.io.InputStream;

public interface S3Operations {
	
	//private Jets3tExceptionTranslator defaultJets3tExceptionTranslator;

	public void send(String key, String stringToSend) throws S3CommunicationException;
	public void send(String bucketName, String key, String stringToSend) throws S3CommunicationException;
	
	public void send(File fileToSend) throws S3CommunicationException;
	public void send(String bucketName, File fileToSend) throws S3CommunicationException;
	
	public void send(String key, InputStream is) throws S3CommunicationException;
	public void send(String key, String bucketName, InputStream is) throws S3CommunicationException;
	
	public String receiveAsString(String keyName) throws S3CommunicationException;	
	public String receiveAsString(String bucketName, String keyName) throws S3CommunicationException;
	
	public File receiveAsFile(String key) throws S3CommunicationException;	
	public File receiveAsFile(String bucketName, String key) throws S3CommunicationException;
	
	public InputStream receiveAsInputStream(String key) throws S3CommunicationException;
	public InputStream receiveAsInputStream(String bucketName, String key) throws S3CommunicationException;	
	
	public void createBucket(String bucketName) throws S3CommunicationException;
	public void deleteBucket(String bucketName) throws S3CommunicationException;
	public String[] listBuckets() throws S3CommunicationException;
	
}
