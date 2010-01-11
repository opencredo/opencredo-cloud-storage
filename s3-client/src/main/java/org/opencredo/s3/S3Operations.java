package org.opencredo.s3;

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
