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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class QuoteService {
    private final static Logger LOG = LoggerFactory.getLogger(QuoteService.class);

    public void lookupQuote(Message<String> tickerMessage) {
        BigDecimal price = new BigDecimal(new Random().nextDouble() * 100);
        LOG.info("Quote Update... {}: {}", tickerMessage.getPayload(), price.setScale(2, RoundingMode.HALF_EVEN));
    }

}
