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

