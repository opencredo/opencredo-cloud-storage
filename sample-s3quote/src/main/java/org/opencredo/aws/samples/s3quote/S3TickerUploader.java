package org.opencredo.aws.samples.s3quote;

import org.springframework.integration.core.Message;

/*
 * Creates 3 letter ticker symbols and builds messages that contain them.
 * Then sends them to the tickers channel for uploading.
 */
public interface S3TickerUploader {

	void sendTicker(Message<String> ticker);

}
