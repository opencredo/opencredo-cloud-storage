/* Copyright 2008 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.opencredo.integration.ec2;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.model.S3Object;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.util.Assert;

public class EC2WritingMessageHandler implements MessageHandler {
	
	private final Log logger = LogFactory.getLog(EC2WritingMessageHandler.class);
	
	public EC2WritingMessageHandler(){
		
	}

    public void handleMessage(Message<?> message){
		Object payload = message.getPayload();
		if (logger.isDebugEnabled()) logger.debug("message: "+message);
		S3Object objectToSend = null;
		try {
			//TODO: Based on the message content, do ec2 tasks
		}
		catch (Exception e) {
			throw new MessageHandlingException(message, "failed to write Message payload to file", e);
		}	
	}

}
