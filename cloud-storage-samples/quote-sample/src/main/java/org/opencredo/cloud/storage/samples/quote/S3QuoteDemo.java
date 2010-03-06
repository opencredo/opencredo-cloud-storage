/* Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencredo.cloud.storage.samples.quote;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.message.MessageBuilder;

/**
 * In order to run this sample you have to have account in Amazon AWS.
 * Amazon AWS account details should be specified in properties file:
 * quote.properties.
 * 
 * This sample shows how to integrate cloud storage with Spring-Integration.
 * 
 * @author Eren Aykin (eren.aykin@opencredo.com)
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 */
public class S3QuoteDemo {
    private final static Logger LOG = LoggerFactory.getLogger(S3QuoteDemo.class);

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("quoteDemo-context.xml");

        TickerUploader tickerUploader = (TickerUploader) context.getBean("tickerUploader");
        // sending requests for tickers to be uploaded to S3, so that they can
        // later be read by another application.
        // can be commented out if already uploaded enough information
        for (int i = 0; i < 4; i++) {
            char[] chars = new char[3];
            for (int j = 0; j < 3; j++) {
                chars[j] = (char) (new Random().nextInt(25) + 65);
            }
            String ticker = new String(chars);
            LOG.info("ticker to upload: {}", ticker);
            MessageBuilder<String> builder = MessageBuilder.withPayload(ticker);
            tickerUploader.sendTicker(builder.build());
        }
    }
}
