package org.opencredo.aws.s3;

import org.springframework.integration.core.Message;

public interface S3KeyNameGenerator {

	String generateKeyName(Message<?> message);

}
