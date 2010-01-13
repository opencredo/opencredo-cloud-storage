package org.opencredo.aws.s3;

public class AWSCredentials {
	
	static String accessKey;
	static String secretAccessKey;
	
	public AWSCredentials(String ak, String sak){
		accessKey = ak;
		secretAccessKey = sak;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String ak) {
		accessKey = ak;
	}

	public String getSecretAccessKey() {
		return secretAccessKey;
	}

	public void setSecretAccessKey(String sak) {
		secretAccessKey = sak;
	}
	
	
}
