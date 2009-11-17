package com.opencredo.integration.samples.s3quote;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.core.Message;

/*
 * Creates 3 letter ticker symbols and builds messages that contain them.
 * Then sends them to the tickers channel for uploading.
 */
public interface S3TickerUploader {

	@Gateway(requestChannel="tickers")
	void sendTicker(Message<String> ticker);

	

}
