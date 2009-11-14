
package com.opencredo.integration.samples.s3quote;

import java.io.IOException;

import org.jets3t.service.S3ServiceException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class S3QuoteDemo {

	public static void main(String[] args) throws IOException, S3ServiceException {
		//URL url = ClassLoader.getSystemResource("s3quoteDemo.xml");

		AbstractApplicationContext context = new ClassPathXmlApplicationContext("s3quoteDemo.xml", S3QuoteDemo.class);
		S3TickerBuilder tickerBuilder = (S3TickerBuilder) context.getBean("s3TickerBuilder");
		//sending requests for tickers to be uploaded to S3, so that they can later be read by another application.
		for (int i = 0; i <4; i++) tickerBuilder.buildTicker();

	}
}
