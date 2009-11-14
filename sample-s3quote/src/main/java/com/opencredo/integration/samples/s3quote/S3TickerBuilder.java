package com.opencredo.integration.samples.s3quote;
	
import java.io.IOException;
import java.util.Random;

import org.jets3t.service.S3ServiceException;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageBuilder;

public class S3TickerBuilder {

	public Message<String> buildTicker() throws IOException, S3ServiceException {
		char[] chars = new char[3];
		for (int j = 0; j < 3; j++) {
			chars[j] = (char) (new Random().nextInt(25) + 65);
		}
		String ticker = new String(chars);
		MessageBuilder<String> builder = MessageBuilder.withPayload(ticker);
		return builder.build();	
	}

}
