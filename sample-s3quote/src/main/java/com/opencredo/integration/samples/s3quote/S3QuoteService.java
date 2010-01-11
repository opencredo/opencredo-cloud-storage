

package com.opencredo.integration.samples.s3quote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Object;
import org.springframework.integration.core.Message;

public class S3QuoteService {

	public void lookupQuote(Message<String> s3TickerMessage) throws IOException, S3ServiceException {
		BigDecimal price = new BigDecimal(new Random().nextDouble() * 100);	
		/*
		BufferedReader br = new BufferedReader(new InputStreamReader(s3TickerMessage.getPayload().getDataInputStream()));
		Quote quote = new Quote(br.readLine(), price.setScale(2, RoundingMode.HALF_EVEN));
		*/
		Quote quote = new Quote(s3TickerMessage.getPayload(), price.setScale(2, RoundingMode.HALF_EVEN));
		System.out.println("Quote Update... "+quote);
	}

}
