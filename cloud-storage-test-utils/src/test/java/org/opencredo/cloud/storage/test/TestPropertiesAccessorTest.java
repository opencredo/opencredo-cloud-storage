package org.opencredo.cloud.storage.test;

import org.junit.Test;
import org.opencredo.cloud.storage.test.TestPropertiesAccessor;

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
