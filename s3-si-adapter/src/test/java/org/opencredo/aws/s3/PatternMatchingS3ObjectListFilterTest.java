package org.opencredo.aws.s3;

import static org.junit.Assert.*;
import java.util.List;
import java.util.regex.Pattern;

import org.jets3t.service.model.S3Object;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opencredo.aws.s3.PatternMatchingS3ObjectListFilter;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PatternMatchingS3ObjectListFilterTest {

	@Mock
	S3Object s3ObjectMock;
	
	private final String testKey = "testKey";
	
    @Test
    public void matchSingleObject() {
		when(s3ObjectMock.getKey()).thenReturn(testKey);
		
        S3Object[] s3Objects = new S3Object[] { s3ObjectMock };
        Pattern pattern = Pattern.compile(testKey);
        PatternMatchingS3ObjectListFilter filter = new PatternMatchingS3ObjectListFilter(pattern);
        List<S3Object> accepted = filter.filterS3Objects(s3Objects);
        
        assertEquals(1, accepted.size());
    }

    @Test
    public void matchSubset() {
    	when(s3ObjectMock.getKey()).thenReturn(testKey);
    	
        S3Object[] s3Objects = new S3Object[] { s3ObjectMock, new S3Object() };
        Pattern pattern = Pattern.compile(testKey);
        PatternMatchingS3ObjectListFilter filter = new PatternMatchingS3ObjectListFilter(pattern);
        List<S3Object> accepted = filter.filterS3Objects(s3Objects);
        
        assertEquals(1, accepted.size());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void nullPattern() {
            new PatternMatchingS3ObjectListFilter(null);
    }

}