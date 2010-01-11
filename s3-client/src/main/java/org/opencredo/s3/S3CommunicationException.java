package org.opencredo.s3;

public class S3CommunicationException extends S3Exception {

	private static final long serialVersionUID = 8080789275931833330L;
	
	public S3CommunicationException() {
		super();
	}

	public S3CommunicationException(String message, Throwable cause) {
		super(message, cause);
	}

	public S3CommunicationException(String message) {
		super(message);
	}

	public S3CommunicationException(Throwable cause) {
		super(cause);
	}

}
