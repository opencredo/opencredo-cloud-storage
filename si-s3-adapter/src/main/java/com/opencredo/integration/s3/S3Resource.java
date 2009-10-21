package com.opencredo.integration.s3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.springframework.core.io.Resource;

public class S3Resource implements Resource{

	private S3Service s3Service;
	
	
	public S3Resource(){
		super();
	}
	
	public S3Resource(S3Service s3Service){
		this.s3Service = s3Service;
	}
	
	public Resource createRelative(String arg0) throws IOException {
		
		return null;
	}

	public boolean exists() {
		
		return false;
	}

	public String getDescription() {
		String properties;
		properties = new String("JetS3t Properties: " + s3Service.getJetS3tProperties().toString());
		return properties;
	}

	public File getFile() throws IOException {
		return null;
	}

	public String getFilename() {
		
		return null;
	}

	public URI getURI() throws IOException {
		
		return null;
	}

	public URL getURL() throws IOException {
		return null;
	}

	public boolean isOpen() {
		
		return false;
	}

	public boolean isReadable() {
		
		return false;
	}

	public long lastModified() throws IOException {
		
		return 0;
	}

	public InputStream getInputStream() throws IOException {
		
		return null;
	}

	public S3Bucket getBucket(String bucket) {
		
		try {
			return this.s3Service.getBucket(bucket);
		} 
		catch (S3ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void setBucket(String string) {
		// TODO Auto-generated method stub
		
	}

}
