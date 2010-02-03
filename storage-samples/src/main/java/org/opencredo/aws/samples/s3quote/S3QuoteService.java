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

package org.opencredo.aws.samples.s3quote;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import org.springframework.integration.core.Message;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class S3QuoteService {

    public void lookupQuote(Message<String> s3TickerMessage) {
        BigDecimal price = new BigDecimal(new Random().nextDouble() * 100);
        /*
         * BufferedReader br = new BufferedReader(new
         * InputStreamReader(s3TickerMessage
         * .getPayload().getDataInputStream())); Quote quote = new
         * Quote(br.readLine(), price.setScale(2, RoundingMode.HALF_EVEN));
         */
        Quote quote = new Quote(s3TickerMessage.getPayload(), price.setScale(2, RoundingMode.HALF_EVEN));
        System.out.println("Quote Update... " + quote);
    }

}
