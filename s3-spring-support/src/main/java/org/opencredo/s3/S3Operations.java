package org.opencredo.s3;

import java.io.File;

import org.jets3t.service.model.S3Object;

public interface S3Operations {
	
	//private Jets3tExceptionTranslator defaultJets3tExceptionTranslator;

	public void sendString(String key, String stringToSend) throws S3CommunicationException;
	
	public String receiveString(String key) throws S3CommunicationException;
	
	public void sendFile(File fileToSend) throws S3CommunicationException;
	
	public File receiveFile(String key) throws S3CommunicationException;
	
}
