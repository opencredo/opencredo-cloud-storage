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

package org.opencredo.aws;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.opencredo.aws.s3.BucketObject;
import org.opencredo.aws.s3.BucketStatus;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public interface AwsOperations {

    // **********************************
    // CONFIGURATION 
    // **********************************

    /**
     * Create a new s3 bucket with the provided bucket name
     * 
     * @param bucketName
     * @throws AwsCommunicationException
     */
    public void createBucket(String bucketName) throws AwsCommunicationException;

    /**
     * Delete the s3 bucket with the provided bucket name
     * 
     * @param bucketName
     * @throws AwsCommunicationException
     */
    public void deleteBucket(String bucketName) throws AwsCommunicationException;

    /**
     * Delete the s3 bucket with the provided bucket name
     * 
     * @param bucketName
     * @param key
     * @throws AwsCommunicationException
     */
    public void deleteObject(String bucketName, String key) throws AwsCommunicationException;

    /**
     * List all buckets.
     * 
     * @return
     * @throws AwsCommunicationException
     */
    public String[] listBuckets() throws AwsCommunicationException;

    /**
     * Get the status of the bucket.
     * 
     * @param bucketName
     * @return
     */
    public BucketStatus getBucketStatus(String bucketName) throws AwsCommunicationException;

    /**
     * 
     * @param bucketName
     * @return
     * @throws AwsCommunicationException
     */
    public List<BucketObject> listBucketObjects(String bucketName) throws AwsCommunicationException;

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
    public void send(String key, String stringToSend) throws AwsCommunicationException;

    /**
     * Send string data to s3 bucket.
     * 
     * @param bucketName
     * @param key
     * @param stringToSend
     * @throws AwsCommunicationException
     */
    public void send(String bucketName, String key, String stringToSend) throws AwsCommunicationException;

    /**
     * Send File to s3 bucket. Default bucket name must be provided in the
     * template.
     * 
     * @param fileToSend
     * @throws AwsCommunicationException
     */
    public void send(File fileToSend) throws AwsCommunicationException;

    /**
     * Send File to s3 bucket.
     * 
     * @param bucketName
     * @param fileToSend
     * @throws AwsCommunicationException
     */
    public void send(String bucketName, File fileToSend) throws AwsCommunicationException;

    /**
     * Send File to s3 bucket with provided key.
     * 
     * @param bucketName
     * @param key
     * @param fileToSend
     * @throws AwsCommunicationException
     */
    public void send(String bucketName, String key, File fileToSend) throws AwsCommunicationException;

    /**
     * Send InputStream to s3 bucket. Default bucket name must be provided in
     * the template.
     * 
     * @param key
     * @param is
     * @throws AwsCommunicationException
     */
    public void send(String key, InputStream is) throws AwsCommunicationException;

    /**
     * Send InputStream to s3 bucket.
     * 
     * @param bucketName
     * @param key
     * @param is
     * 
     * @throws AwsCommunicationException
     */
    public void send(String bucketName, String key, InputStream is) throws AwsCommunicationException;

    /**
     * Receive the s3 object as String. Default bucket name must be provided in
     * the template.
     * 
     * @param keyName
     * @return
     * @throws AwsCommunicationException
     */
    public String receiveAsString(String keyName) throws AwsCommunicationException;

    /**
     * Receive the s3 object as String.
     * 
     * @param bucketName
     * @param keyName
     * @return
     * @throws AwsCommunicationException
     */
    public String receiveAsString(String bucketName, String keyName) throws AwsCommunicationException;

    /**
     * Receive the s3 object as File. Default bucket name must be provided in
     * the template.
     * 
     * @param key
     * @return
     * @throws AwsCommunicationException
     */
    public File receiveAsFile(String key) throws AwsCommunicationException;

    /**
     * Receive the s3 object as File.
     * 
     * @param bucketName
     * @param key
     * @return
     * @throws AwsCommunicationException
     */
    public File receiveAsFile(String bucketName, String key) throws AwsCommunicationException;

    /**
     * Receive the s3 object as InputStream. Default bucket name must be
     * provided in the template.
     * 
     * @param key
     * @return
     * @throws AwsCommunicationException
     */
    public InputStream receiveAsInputStream(String key) throws AwsCommunicationException;

    /**
     * Receive the s3 object as InputStream.
     * 
     * @param bucketName
     * @param key
     * @return
     * @throws AwsCommunicationException
     */
    public InputStream receiveAsInputStream(String bucketName, String key) throws AwsCommunicationException;
}
