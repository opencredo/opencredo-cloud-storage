package org.opencredo.cloud.storage.jcloud;

public enum CloudProvider {
    AWS_S3("aws-s3");

    private String providerString;

    CloudProvider(final String providerString) {
        this.providerString = providerString;
    }

    public String getString() {
        return providerString;
    }

}
