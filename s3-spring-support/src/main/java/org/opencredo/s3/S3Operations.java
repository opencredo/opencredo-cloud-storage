package org.opencredo.s3;

import java.io.File;
import java.io.InputStream;

public interface S3Operations {
	
	//private Jets3tExceptionTranslator defaultJets3tExceptionTranslator;

	public void send(String key, String stringToSend) throws S3CommunicationException;
	public void send(String bucketName, String key, String stringToSend);
	
	public void send(File fileToSend);
	public void send(String bucketName, File fileToSend);
	
	public void send(String key, InputStream is);
	public void send(String key, String bucketName, InputStream is);
	
	public String receiveAsString(String keyName);	
	public String receiveAsString(String bucketName, String keyName);
	
	public File receiveAsFile(String key);	
	public File receiveAsFile(String bucketName, String key);
	
	public InputStream receiveAsInputStream(String key);
	public InputStream receiveAsInputStream(String bucketName, String key);	
}
