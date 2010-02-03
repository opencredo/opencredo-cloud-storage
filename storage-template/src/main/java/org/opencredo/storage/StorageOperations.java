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
package org.opencredo.storage;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public interface StorageOperations {

    /**
     * List all buckets.
     * 
     * @return
     * @throws StorageCommunicationException
     */
    public String[] listBuckets() throws StorageCommunicationException;

    /**
     * 
     * @param bucketName
     * @return
     * @throws StorageCommunicationException
     */
    public List<BlobObject> listBucketObjects(String bucketName) throws StorageCommunicationException;

    // **********************************
    // SEND/RECEIVE
    // **********************************

    /**
     * Send string data to s3 bucket. Default bucket name must be provided in
     * the template.
     * 
     * @param key
     * @param stringToSend
     */
    public void send(String key, String stringToSend) throws StorageCommunicationException;

    /**
     * Send string data to s3 bucket.
     * 
     * @param bucketName
     * @param key
     * @param stringToSend
     * @throws StorageCommunicationException
     */
    public void send(String bucketName, String key, String stringToSend) throws StorageCommunicationException;

    /**
     * Send File to s3 bucket. Default bucket name must be provided in the
     * template.
     * 
     * @param fileToSend
     * @throws StorageCommunicationException
     */
    public void send(File fileToSend) throws StorageCommunicationException;

    /**
     * Send File to s3 bucket.
     * 
     * @param bucketName
     * @param fileToSend
     * @throws StorageCommunicationException
     */
    public void send(String bucketName, File fileToSend) throws StorageCommunicationException;

    /**
     * Send File to s3 bucket with provided key.
     * 
     * @param bucketName
     * @param key
     * @param fileToSend
     * @throws StorageCommunicationException
     */
    public void send(String bucketName, String key, File fileToSend) throws StorageCommunicationException;

    /**
     * Send InputStream to s3 bucket. Default bucket name must be provided in
     * the template.
     * 
     * @param key
     * @param is
     * @throws StorageCommunicationException
     */
    public void send(String key, InputStream is) throws StorageCommunicationException;

    /**
     * Send InputStream to s3 bucket.
     * 
     * @param bucketName
     * @param key
     * @param is
     * 
     * @throws StorageCommunicationException
     */
    public void send(String bucketName, String key, InputStream is) throws StorageCommunicationException;

    /**
     * Receive the s3 object as String. Default bucket name must be provided in
     * the template.
     * 
     * @param keyName
     * @return
     * @throws StorageCommunicationException
     */
    public String receiveAsString(String keyName) throws StorageCommunicationException;

    /**
     * Receive the s3 object as String.
     * 
     * @param bucketName
     * @param keyName
     * @return
     * @throws StorageCommunicationException
     */
    public String receiveAsString(String bucketName, String keyName) throws StorageCommunicationException;

    /**
     * Receive the s3 object as File. Default bucket name must be provided in
     * the template.
     * 
     * @param key
     * @return
     * @throws StorageCommunicationException
     */
    public File receiveAsFile(String key) throws StorageCommunicationException;

    /**
     * Receive the s3 object as File.
     * 
     * @param bucketName
     * @param key
     * @return
     * @throws StorageCommunicationException
     */
    public File receiveAsFile(String bucketName, String key) throws StorageCommunicationException;

    /**
     * Receive the s3 object as InputStream. Default bucket name must be
     * provided in the template.
     * 
     * @param key
     * @return
     * @throws StorageCommunicationException
     */
    public InputStream receiveAsInputStream(String key) throws StorageCommunicationException;

    /**
     * Receive the s3 object as InputStream.
     * 
     * @param bucketName
     * @param key
     * @return
     * @throws StorageCommunicationException
     */
    public InputStream receiveAsInputStream(String bucketName, String key) throws StorageCommunicationException;
}
