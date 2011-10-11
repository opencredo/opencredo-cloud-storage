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
package org.opencredo.cloud.storage.jcloud;

public enum CloudProvider {
    TRANSIENT("transient"),
    FILESYSTEM("filesystem"),
    EUCALYTPUS_PARTNERCLOUD_S3("eucalyptus-partnercloud-s3"),
    SYNAPTIC_STORAGE("synaptic-storage"),
    AZUREBLOB("azureblob"),
    CLOUDONESTORAAGE("cloudonestorage"),
    CLOUDFILES_US("cloudfiles-us"),
    CLOUDFILES_UK("cloudfiles-uk"),
    NINEFOLD_STORAGE("ninefold-storage"),
    AWS_S3("aws-s3"),
    GOOGLESTORAGE("googlestorage"),
    SCALEUP_STORAGE("scaleup-storage"),
    HOSTEUROPE_STORAGE("hosteurope-storage"),
    TISCALI_STORAGE("tiscali-storage");

    private String providerString;

    CloudProvider(final String providerString) {
        this.providerString = providerString;
    }

    public String getString() {
        return providerString;
    }
}

