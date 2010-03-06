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

import org.springframework.integration.core.Message;

/**
 * Creates 3 letter ticker symbols and builds messages that contain them. Then
 * sends them to the tickers channel for uploading.
 * 
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public interface TickerUploader {

    void sendTicker(Message<String> ticker);

}
