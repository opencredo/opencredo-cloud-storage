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

package org.opencredo.aws;

import org.springframework.util.Assert;

/**
 * @author Eren Aykin (eren.aykin@opencredo.com)
 */
public class AwsCredentials {

    private final String accessKey;
    private final String secretAccessKey;

    /**
     * @param ak
     *            AWS Access Key
     * @param sak
     *            AWS Secret Access Key
     */
    public AwsCredentials(String ak, String sak) {
        Assert.notNull(ak, "Access key is not provided");
        Assert.notNull(sak, "Secret access key is not provided");

        accessKey = ak;
        secretAccessKey = sak;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }
}
