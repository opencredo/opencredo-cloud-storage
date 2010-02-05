package org.opencredo.aws.s3;

import org.junit.Test;

/**
 * @author Jonas Partner verifies AWS keys are present for testing
 */
public class TestPropertiesAccessorTest {

    @Test
    public void test() {
        TestPropertiesAccessor.getDefaultTestAwsKey();
        TestPropertiesAccessor.getDefaultTestAwsSecretKey();
        TestPropertiesAccessor.getS3DefaultBucketName();
        
        TestPropertiesAccessor.getAzureDefaultAccountName();
        TestPropertiesAccessor.getAzureDefaultSecretKey();
    }
}
