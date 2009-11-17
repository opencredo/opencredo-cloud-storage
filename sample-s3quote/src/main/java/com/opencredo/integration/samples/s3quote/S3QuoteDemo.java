
package com.opencredo.integration.samples.s3quote;

import java.io.IOException;
import java.util.Random;

import org.jets3t.service.S3ServiceException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.message.MessageBuilder;

public class S3QuoteDemo {

	public static void main(String[] args) throws IOException, S3ServiceException {
		AbstractApplicationContext context = new ClassPathXmlApplicationContext("s3quoteDemo.xml", S3QuoteDemo.class);
		S3TickerUploader tickerUploader = (S3TickerUploader) context.getBean("s3TickerUploader");
		//sending requests for tickers to be uploaded to S3, so that they can later be read by another application.
		for (int i = 0; i <4; i++) {
			char[] chars = new char[3];
			for (int j = 0; j < 3; j++) {
				chars[j] = (char) (new Random().nextInt(25) + 65);
			}
			String ticker = new String(chars);
			MessageBuilder<String> builder = MessageBuilder.withPayload(ticker);
			tickerUploader.sendTicker(builder.build());
		}

	}
}
