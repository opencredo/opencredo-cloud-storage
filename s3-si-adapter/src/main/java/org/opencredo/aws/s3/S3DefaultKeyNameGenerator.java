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

package org.opencredo.aws.s3;

import java.io.File;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jets3t.service.model.S3Object;
import org.springframework.integration.core.Message;
import org.springframework.util.Assert;

public class S3DefaultKeyNameGenerator implements S3KeyNameGenerator{
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	private String defaultStringKeyInitial = new String("string.s3.");
	
	public void setDefaultStringKeyInitial(String defaultStringKeyInitial) {
		this.defaultStringKeyInitial = defaultStringKeyInitial;
	}

	public String generateKeyName(Message<?> message) {
		Assert.notNull(message, "message must not be null");
		if (logger.isDebugEnabled()) logger.debug("message: "+message);
		Class<?> c = message.getPayload().getClass();
		if (c.equals(String.class) ){
			return defaultStringKeyInitial+(new Date().getTime());
		}
		else if (c.equals(File.class))  {
			return ((File) message.getPayload()).getName();
		}
		else if (c.equals(S3Object.class)) {
			return ((S3Object) message.getPayload()).getKey();
		}
		else return null;
	}
	
	public String getDefaultStringKeyInitial() {
		return this.defaultStringKeyInitial;
	}

}
