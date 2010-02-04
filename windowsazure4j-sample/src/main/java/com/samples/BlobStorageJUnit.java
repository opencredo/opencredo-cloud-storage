package com.samples;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.soyatec.windows.azure.blob.BlobProperties;
import org.soyatec.windows.azure.blob.BlobStorage;
import org.soyatec.windows.azure.blob.BlobContainer;
import org.soyatec.windows.azure.util.NameValueCollection;

import com.samples.BlobSample;

public class BlobStorageJUnit extends TestCase {

  public static final String AZURE_ACCOUNT_NAME  = "octest";
  public static final String AZURE_ACCOUNT_KEY   = "MyAccountKey";
  
  private static final String METADATA_CREATED_BY_KEY       = "createdBy";
  private static final String METADATA_CREATED_BY_VALUE     = "Window Azure SDK for Java";
  
  private static final String METADATA_FILE_TYPE_JPG    = "jpg";
  private static final String METADATA_BINARY_CONTENTS  = "text";
  
  private BlobStorage m_objBlobStorage;
  
  /**
   * Sets up the test fixture.
   *
   * Called before every test case method.
   */
  protected void setUp() 
  {
    try {
      boolean fSuccess = false;
      m_objBlobStorage = BlobSample.createStorageAccess(  AZURE_ACCOUNT_NAME, 
                                                          AZURE_ACCOUNT_KEY
                                                          );
      assertNotNull(m_objBlobStorage);
      
      fSuccess = BlobSample.deleteBlobContainersAll(m_objBlobStorage, true);
      assertTrue(fSuccess);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Raised an Exception");
    }
  }

  /**
   * Tears down the test fixture.
   *
   * Called after every test case method.
   */
  protected void tearDown() 
  {
    try {
      boolean fSuccess = false;
      
      fSuccess = BlobSample.deleteBlobContainersAll(m_objBlobStorage, true);
      assertTrue(fSuccess);
      
      int countContainers = BlobSample.countBlobContainers(m_objBlobStorage);
      assertEquals(0, countContainers); 
      
      m_objBlobStorage = null;
    } catch (Exception e) {
      e.printStackTrace();
      fail("Raised an Exception");
    }
  }
  
  /**
   * Validate URL
   * @param strURL
   * @return boolean True if URL is valid.
   */
  private boolean validateURL(String strURL)
  {
    boolean fIsValid = false;
    try {
      URL url = new URL(strURL);
      URLConnection connection = url.openConnection();
      if (connection instanceof HttpURLConnection) {
        HttpURLConnection httpConnection = (HttpURLConnection)connection;
        httpConnection.connect();
        int response = httpConnection.getResponseCode();
        fIsValid = (response == 200);
      }   
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return fIsValid;
  }
  
  /**
   * Test that blob storage was created.
   */
  public void testValdidateSetUp()
  {
    try {
      int countContainers = 0;
      assertNotNull(m_objBlobStorage);
      
      countContainers = BlobSample.countBlobContainers(m_objBlobStorage);
      assertEquals(0, countContainers);
    } catch (Exception e) {
      e.printStackTrace();
      fail("Raised an Exception");
    }
  }
  
  /**
   * Test managing containers within blob storage.
   */
  public void testContainer()
  {
    try {
      assertNotNull(m_objBlobStorage);
      
      boolean fSuccess = false;
      String strContainerName = "testcontainer1";
      
      fSuccess = BlobSample.deleteBlobContainer(  m_objBlobStorage, 
                                                  strContainerName, 
                                                  true);
      assertTrue(fSuccess);
      
      int countPre = BlobSample.countBlobContainers(m_objBlobStorage);
      assertTrue(countPre >= 0);
      
      NameValueCollection objMetadataContainer = new NameValueCollection();
      BlobContainer objBlobContainer 
          = BlobSample.getBlobContainer_wMetadata(  
                                          m_objBlobStorage, 
                                          strContainerName,
                                          objMetadataContainer
                                          );
      assertNotNull(objBlobContainer);
      assertTrue(objMetadataContainer.isEmpty());
      
      int countPostActual = BlobSample.countBlobContainers(m_objBlobStorage);
      int countPostExpected = countPre + 1;
      assertEquals(countPostExpected, countPostActual);
      
      List<BlobContainer> listBlobContainers = new ArrayList<BlobContainer>();      
      fSuccess = BlobSample.listBlobContainers( m_objBlobStorage,
                                                listBlobContainers,
                                                true
                                                );
      assertTrue(fSuccess);
      countPostActual = listBlobContainers.size();
      assertEquals(countPostExpected, countPostActual);
      
      fSuccess = BlobSample.deleteBlobContainer(  m_objBlobStorage, 
                                                  strContainerName, 
                                                  true);
      assertTrue(fSuccess);
      
      int countPost = BlobSample.countBlobContainers(m_objBlobStorage);
      assertEquals(countPre, countPost);    
    
    } catch (Exception e) {
      e.printStackTrace();
      fail("Raised an Exception");
    }
  }
  

  /**
   * Test Container with metadata.
   */
  public void testContainer_wMetadata()  
  {
    try {
      boolean fSuccess = false;
      assertNotNull(m_objBlobStorage);
      String strContainerName = "testcontainer1";
      
      fSuccess = BlobSample.deleteBlobContainer(  m_objBlobStorage, 
                                                  strContainerName, 
                                                  true 
                                                  );
      assertTrue(fSuccess);
      
      NameValueCollection objMetadataContainerPre = new NameValueCollection();
      BlobContainer objBlobContainerPre 
          = BlobSample.getBlobContainer_wMetadata(  
              m_objBlobStorage, 
              strContainerName,
              objMetadataContainerPre /* No metadata */
              );
      assertNotNull(objBlobContainerPre);
      assertTrue(objBlobContainerPre.doesContainerExist());
      assertTrue(objMetadataContainerPre.isEmpty());
      
      NameValueCollection objMetadataPut = new NameValueCollection();
      objMetadataPut.put(METADATA_CREATED_BY_KEY, METADATA_CREATED_BY_VALUE);  
      fSuccess = BlobSample.setBlobContainer_wMetadata( objBlobContainerPre, 
                                                        objMetadataPut);
      assertTrue(fSuccess);

      NameValueCollection objMetadataContainerPost = new NameValueCollection();
      assertTrue(objMetadataContainerPost.isEmpty());
      BlobContainer objBlobContainerPost 
          = BlobSample.getBlobContainer_wMetadata(  
              m_objBlobStorage, 
              strContainerName,
              objMetadataContainerPost
              );
      assertNotNull(objBlobContainerPost);
      assertTrue(objBlobContainerPost.doesContainerExist());
      assertNotNull(objMetadataContainerPost);
      assertFalse(objMetadataContainerPost.isEmpty());
      
      int countMetadata = objMetadataContainerPost.size();
      assertEquals(1, countMetadata);
      
      for ( Iterator iter = objMetadataContainerPost.entrySet().iterator(); 
            iter.hasNext(); 
            ) {
        Map.Entry entry = (Map.Entry)iter.next();
        String strKey = (String) entry.getKey();
        Collection<Object> vals = (Collection) entry.getValue();
        for (Iterator<Object> valIter = vals.iterator(); valIter.hasNext(); ) {
            String strValue = (String) valIter.next();
            System.out.printf(  "key '%s'\tvalue '%s'\n", 
                strKey, 
                strValue
                );
        }
      }
      
      String strCreatedBy = (String) objMetadataContainerPost.get(METADATA_CREATED_BY_KEY);
      assertNotNull(strCreatedBy);
      assertFalse(strCreatedBy.isEmpty());
      assertTrue(strCreatedBy.equals(METADATA_CREATED_BY_VALUE));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Raised an Exception");
    }
  }

  /**
   * Test Blob Container enumeration.
   */
  public void testContainer_Enumerate() 
  {
    try {
      boolean fSuccess = false;
      int numContainers = 5;
      int countContainers = 0;
      ArrayList<String> aContainerNames = new ArrayList<String>();
      
      assertNotNull(m_objBlobStorage);

      aContainerNames.add("testcontainer1");
      aContainerNames.add("testcontainer2");
      aContainerNames.add("testcontainer3");
      aContainerNames.add("testcontainer4");
      aContainerNames.add("testcontainer5");

      for ( String strContainerName : aContainerNames ) {
        BlobContainer objBlobContainer 
          = BlobSample.getBlobContainer_wMetadata ( m_objBlobStorage, 
                                                    strContainerName,
                                                    null
                                                  );
        assertNotNull(objBlobContainer);
      }
      
      countContainers = BlobSample.countBlobContainers(m_objBlobStorage);
      assertEquals(numContainers, countContainers);
      
      List<BlobContainer> listBlobContainers = new ArrayList<BlobContainer>();      
      fSuccess = BlobSample.listBlobContainers( m_objBlobStorage,
                                                listBlobContainers,
                                                true
                                                );
      assertTrue(fSuccess);
      countContainers = listBlobContainers.size();
      assertEquals(numContainers, countContainers);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Raised an Exception");
    }
  }
  
  /**
   * Test Put and Get of Blob binary data into a Container
   */
  public void testBlob_wBinaryData()
  {
    try {
      boolean fSuccess = false;
      assertNotNull(m_objBlobStorage);
      String strContainerName = "testcontainer1";
      
      fSuccess = BlobSample.deleteBlobContainer(  m_objBlobStorage, 
                                                  strContainerName, 
                                                  true 
                                                  );
      assertTrue(fSuccess);
      
      NameValueCollection objMetadataContainer = new NameValueCollection();
      BlobContainer objBlobContainer 
        = BlobSample.getBlobContainer_wMetadata ( m_objBlobStorage, 
                                                  strContainerName,
                                                  objMetadataContainer
                                                );
      assertNotNull(objBlobContainer);
      
      /* 
       * Define the binary data to be held within a blob.
       * To create binary data, transform a String into a byte array. 
       */
      String strTest1 = "a2Xy337u7bg9sbbxNcAU3SyQt8TExeQI";
      String strBlobName = "RandomString";
      int intStrLength = strTest1.length();
      byte[] binaryData = strTest1.getBytes();
      byte[] binaryDataResult = null;
      
      /* False test, Blob should not exist */
      binaryDataResult 
                  = BlobSample.getBlob_wBinaryData_wMetadata( 
                                                  objBlobContainer, 
                                                  strBlobName,
                                                  null /* Get not metadata */
                                                  );
      assertNull(binaryDataResult);
      
      /* Create Blob "RandomString" with binary data */
      fSuccess = BlobSample.putBlob_wBinaryData_wMetadata(  
                                                objBlobContainer, 
                                                strBlobName, 
                                                binaryData,
                                                null, /* No metadata */
                                                false
                                                );
      assertTrue(fSuccess);
      
      int count = BlobSample.countBlobs(objBlobContainer, null);
      assertEquals(1, count);
      
      /* Get the binary data from Blob "RandomString" */
      NameValueCollection objMetadataGet = new NameValueCollection();
      binaryDataResult 
                  = BlobSample.getBlob_wBinaryData_wMetadata ( 
                                                  objBlobContainer, 
                                                  strBlobName,
                                                  objMetadataGet
                                                  );
      assertNotNull(binaryDataResult);
      
      /* With the binary data, Transform byte array into a String */
      String strResult = new String(binaryDataResult);
      assertEquals(intStrLength, strResult.length());
      
      /* Validate the retrieved data is valid */
      fSuccess = strTest1.equals(strResult);
      assertTrue(fSuccess);
      
      /* Validate URL access */
      boolean fValidateURL    = false;
      URI uriContainer        = objBlobContainer.getBaseUri();
      
      String strAccount       = objBlobContainer.getAccountName();
      String strHost          = uriContainer.getHost();
      String strContainer     = objBlobContainer.getContainerName();
      
      String urlString 
                = String.format(  "http://%s.%s/%s/%s", 
                                  strAccount, 
                                  strHost, 
                                  strContainer, 
                                  strBlobName 
                                  );
      
      /* Blob is not accessible while container access is private. */      
      fValidateURL = validateURL(urlString);
      assertFalse(fValidateURL);
      
      /* Make container access to be public */
      fSuccess = BlobSample.setBlobContainer_wACL(  objBlobContainer, 
                                                    true );
      assertTrue(fSuccess);
      
      /* Blob is accessible while container access is public */           
      fValidateURL = validateURL(urlString);
      assertTrue(fValidateURL);
      
      /* Delete blob */      
      fSuccess = objBlobContainer.deleteBlob(strBlobName);
      assertTrue(fSuccess);
      
      fSuccess = objBlobContainer.doesBlobExist(strBlobName);
      assertFalse(fSuccess);

      fValidateURL = validateURL(urlString);
      assertFalse(fValidateURL);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Raised an Exception");
    }
  }
  
  /**
   * Test Put and Get of Blob binary data into a Container
   */
  public void testBlob_wBinaryData_wMetadata()
  {
    try {
      boolean fSuccess = false;
      assertNotNull(m_objBlobStorage);
      String strContainerName = "testcontainer1";
      
      fSuccess = BlobSample.deleteBlobContainer(  m_objBlobStorage, 
                                                  strContainerName, 
                                                  true 
                                                  );
      assertTrue(fSuccess);
      
      NameValueCollection objMetadataContainer = new NameValueCollection();      
      BlobContainer objBlobContainer 
        = BlobSample.getBlobContainer_wMetadata ( m_objBlobStorage, 
                                                  strContainerName,
                                                  objMetadataContainer
                                                  );
      assertNotNull(objBlobContainer);
      
      /* 
       * Define the binary data to be held within a blob.
       * To create binary data, transform a String into a byte array. 
       */
      String strTest1 = "a2Xy337u7bg9sbbxNcAU3SyQt8TExeQI";
      String strBlobName = "RandomString";
      int intStrLength = strTest1.length();
      byte[] binaryData = strTest1.getBytes();
      byte[] binaryDataResult = null;
      
      /* False test, Blob should not exist */
      binaryDataResult 
                  = BlobSample.getBlob_wBinaryData_wMetadata( 
                                                  objBlobContainer, 
                                                  strBlobName,
                                                  null
                                                  );
      assertNull(binaryDataResult);
     
      /* Set Blob metadata */
      NameValueCollection objMetadataPut = new NameValueCollection();
      objMetadataPut.put(METADATA_CREATED_BY_KEY, METADATA_CREATED_BY_VALUE);  
      objMetadataPut.put("binarycontents", METADATA_BINARY_CONTENTS);
      
      /* Create Blob "RandomString" with binary data */
      fSuccess = BlobSample.putBlob_wBinaryData_wMetadata(  
                                                objBlobContainer, 
                                                strBlobName, 
                                                binaryData,
                                                objMetadataPut,
                                                false
                                                );
      assertTrue(fSuccess);
      
      int count = BlobSample.countBlobs(objBlobContainer, null);
      assertEquals(1, count);
      
      /* Get the binary data from Blob "RandomString" */
      NameValueCollection objMetadataGet = new NameValueCollection();
      binaryDataResult 
                  = BlobSample.getBlob_wBinaryData_wMetadata( 
                                                  objBlobContainer, 
                                                  strBlobName,
                                                  objMetadataGet
                                                  );
      assertNotNull(binaryDataResult);
      
      /* With the binary data, Transform byte array into a String */
      String strResult = new String(binaryDataResult);
      assertEquals(intStrLength, strResult.length());
      
      /* Validate the retrieved data is valid */
      fSuccess = strTest1.equals(strResult);
      assertTrue(fSuccess);
      
      /* Validate URL access */
      boolean fValidateURL  = false;
      URI uriContainer      = objBlobContainer.getBaseUri();
      
      String strAccount     = objBlobContainer.getAccountName();
      String strHost        = uriContainer.getHost();
      String strContainer   = objBlobContainer.getContainerName();
      
      String urlString 
                = String.format(  "http://%s.%s/%s/%s", 
                                  strAccount, 
                                  strHost, 
                                  strContainer, 
                                  strBlobName 
                                  );
      
      /* Blob is not accessible while container access is private. */      
      fValidateURL = validateURL(urlString);
      assertFalse(fValidateURL);
      
      /* Make container access to be public */
      fSuccess = BlobSample.setBlobContainer_wACL(  objBlobContainer, 
                                                    true
                                                    );
      assertTrue(fSuccess);
      
      /* Blob is accessible while container access is public */           
      fValidateURL = validateURL(urlString);
      assertTrue(fValidateURL);
      
      /* Delete blob */      
      fSuccess = objBlobContainer.deleteBlob(strBlobName);
      assertTrue(fSuccess);
      
      fSuccess = objBlobContainer.doesBlobExist(strBlobName);
      assertFalse(fSuccess);
      
      fValidateURL = validateURL(urlString);
      assertFalse(fValidateURL);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Raised an Exception");
    }
  }

  /**
   * Test Put and Get a Blob with File binary within a Container
   */
  public void testBlob_wFile()
  {
    try 
    {
      boolean fSuccess = false;
      assertNotNull(m_objBlobStorage);
      String strContainerName = "testcontainer1";
      
      fSuccess = BlobSample.deleteBlobContainer(  m_objBlobStorage, 
                                                  strContainerName, 
                                                  true 
                                                  );
      assertTrue(fSuccess);
      
      NameValueCollection objMetadataContainer = new NameValueCollection();
      BlobContainer objBlobContainer 
        = BlobSample.getBlobContainer_wMetadata(  m_objBlobStorage, 
                                                  strContainerName,
                                                  objMetadataContainer
                                                  );
      assertNotNull(objBlobContainer);
      assertTrue(objBlobContainer.doesContainerExist());
      
      /* Original Image File path */
      String strFilePath = "WindowsAzure.jpg";
      String strBlobName = "WindowsAzure.jpg";
      
      /* False test, Blob should not exist */
      fSuccess = BlobSample.getBlob_wFile_wMetadata(  objBlobContainer, 
                                          strBlobName,
                                          "WindowsAzure2x.jpg",
                                          null,
                                          true /* Overwrite if exists */
                                          );
      assertFalse(fSuccess);
      
      /* 
       * Put File "WindowsAzure.jpg" binary into a Blob,
       * Label Blob with name "WindowsAzureImage",
       * then put Blob "WindowsAzure.jpg" into strContainerName Container.
       */
      fSuccess = BlobSample.putBlob_wFile_wMetadata(  
                                          objBlobContainer, 
                                          strBlobName, 
                                          strFilePath, 
                                          null, /* No metadata */
                                          true
                                          );
      assertTrue(fSuccess);
      
      int count = BlobSample.countBlobs(objBlobContainer, null);
      assertEquals(1, count);

      /* 
       * Provide location to place retrieved File at "WindowsAzure2.jpg",
       * Get Blob with name "WindowsAzureImage" from strContainerName Container,
       * then copy retrieved Blob's File "WindowsAzure.jpg" binary into "WindowsAzure2.jpg".
       */ 
      NameValueCollection objMetadataGet = new NameValueCollection();
      fSuccess = BlobSample.getBlob_wFile_wMetadata(  objBlobContainer, 
                                          strBlobName,
                                          "WindowsAzure2.jpg",
                                          objMetadataGet,
                                          true /* Overwrite if exists */
                                          );
      assertTrue(fSuccess);
      assertTrue(objMetadataGet.isEmpty());

      /* Validate URL access */
      boolean fValidateURL = false;
      URI uriContainer = objBlobContainer.getBaseUri();
      
      String strAccount = objBlobContainer.getAccountName();
      String strHost = uriContainer.getHost();
      String strContainer = objBlobContainer.getContainerName();
      
      String urlString 
      = String.format(  "http://%s.%s/%s/%s", 
                        strAccount, 
                        strHost, 
                        strContainer, 
                        strBlobName 
                        );
      
      /* Blob is not accessible while container access is private. */      
      fValidateURL = validateURL(urlString);
      assertFalse(fValidateURL);
      
      /* Make container access to be public. */
      fSuccess = BlobSample.setBlobContainer_wACL(  objBlobContainer, 
                                                    true 
                                                    );
      assertTrue(fSuccess);
      
      /* Blob is accessible while container access is public */           
      fValidateURL = validateURL(urlString);
      assertTrue(fValidateURL);
      
      /* Delete blob */      
      fSuccess = objBlobContainer.deleteBlob(strBlobName);
      assertTrue(fSuccess);
      
      fSuccess = objBlobContainer.doesBlobExist(strBlobName);
      assertFalse(fSuccess);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Raised an Exception");
    }
  }  

 
  /**
   * Test Put and Get a Blob with File binary within a Container
   */
  public void testBlob_wFile_wMetadata()
  {
    try 
    {
      boolean fSuccess = false;
      assertNotNull(m_objBlobStorage);
      String strContainerName = "testcontainer1";
      
      fSuccess = BlobSample.deleteBlobContainer(  m_objBlobStorage, 
                                                  strContainerName, 
                                                  true 
                                                  );
      assertTrue(fSuccess);
      
      NameValueCollection objMetadataContainer = new NameValueCollection();
      BlobContainer objBlobContainer 
        = BlobSample.getBlobContainer_wMetadata(  m_objBlobStorage, 
                                                  strContainerName,
                                                  objMetadataContainer
                                                );
      assertNotNull(objBlobContainer);
      
      /* Original Image File path */
      String strFilePath = "WindowsAzure.jpg";
      String strBlobName = "WindowsAzure.jpg";
      
      /* False test, Blob should not exist */
      fSuccess = BlobSample.getBlob_wFile_wMetadata(  objBlobContainer, 
                                          strBlobName,
                                          "WindowsAzure2x.jpg",
                                          null,
                                          true /* Overwrite if exists */
                                          );
      assertFalse(fSuccess);
      
      /* Set Blob metadata */
      NameValueCollection objMetadataPut = new NameValueCollection();
      objMetadataPut.put(METADATA_CREATED_BY_KEY, METADATA_CREATED_BY_VALUE);  
      objMetadataPut.put("filetype", METADATA_FILE_TYPE_JPG);
      
      /* 
       * Put File "WindowsAzure.jpg" binary into a Blob,
       * Label Blob with name "WindowsAzureImage",
       * then put Blob "WindowsAzure.jpg" into strContainerName Container.
       */
      fSuccess = BlobSample.putBlob_wFile_wMetadata(  
                                          objBlobContainer, 
                                          strBlobName, 
                                          strFilePath, 
                                          objMetadataPut,
                                          true
                                          );
      assertTrue(fSuccess);
      
      int count = BlobSample.countBlobs(objBlobContainer, null);
      assertEquals(1, count);

      /* 
       * Provide location to place retrieved File at "WindowsAzure2.jpg",
       * Get Blob with name "WindowsAzureImage" from strContainerName Container,
       * then copy retrieved Blob's File "WindowsAzure.jpg" binary into "WindowsAzure2.jpg".
       */
      NameValueCollection objMetadataGet = new NameValueCollection();
      fSuccess = BlobSample.getBlob_wFile_wMetadata(  objBlobContainer, 
                                          strBlobName,
                                          "WindowsAzure2.jpg",
                                          objMetadataGet,
                                          true /* Overwrite if exists */
                                          );
      assertTrue(fSuccess);

      /* Validate URL access */
      boolean fValidateURL = false;
      URI uriContainer = objBlobContainer.getBaseUri();
      
      String strAccount = objBlobContainer.getAccountName();
      String strHost = uriContainer.getHost();
      String strContainer = objBlobContainer.getContainerName();
      
      String urlString 
      = String.format(  "http://%s.%s/%s/%s", 
                        strAccount, 
                        strHost, 
                        strContainer, 
                        strBlobName 
                        );
      
      /* Blob is not accessible while container access is private. */      
      fValidateURL = validateURL(urlString);
      assertFalse(fValidateURL);
      
      /* Make container access to be public. */
      fSuccess = BlobSample.setBlobContainer_wACL(  objBlobContainer, 
                                                    true 
                                                    );
      assertTrue(fSuccess);
      
      /* Blob is accessible while container access is public */           
      fValidateURL = validateURL(urlString);
      assertTrue(fValidateURL);
      
      /* Delete blob */      
      fSuccess = objBlobContainer.deleteBlob(strBlobName);
      assertTrue(fSuccess);
      
      fSuccess = objBlobContainer.doesBlobExist(strBlobName);
      assertFalse(fSuccess);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Raised an Exception");
    }
  }  

  /**
   * Test Enumerate all Blobs within a container
   */
  public void testBlob_Enumerate()
  {
    try {
      int count = 0;
      boolean fSuccess = false;
      String strContainerName = "testcontainer1";
      
      fSuccess = BlobSample.deleteBlobContainer(  m_objBlobStorage, 
                                                  strContainerName, 
                                                  true 
                                                  );
      assertTrue(fSuccess);
      
      NameValueCollection objMetadataContainer = new NameValueCollection();
      BlobContainer objBlobContainer 
        = BlobSample.getBlobContainer_wMetadata ( m_objBlobStorage, 
                                                  strContainerName,
                                                  objMetadataContainer
                                                  );
      assertNotNull(objBlobContainer);

      String[] aRandomString_1 = {
          "5HECNWRM54Q4rx6SxQBlpySR77MCCLKG",
          "spigla3qdStFqzyXpaiPIbGOhsrTebAH",
          "BTXX4ZnhSRWiqvgQFzFnKmc3ODW3PwJq",
          "qHntGLLzDIS5d9USIAgsXtvL7rOWYynp",
          "fKTWwEv0mndAw9teJJHGdcskEhgDQD46",
      };
      
      for ( String strRandom : aRandomString_1 ) {

        byte[] binaryData = strRandom.getBytes();
        String strBlobName = String.format("Random_1/blob_%s.txt", strRandom.substring(0, 5));
        
        /* Create Blob "RandomString" with binary data */
        fSuccess = BlobSample.putBlob_wBinaryData_wMetadata(  
                                                  objBlobContainer, 
                                                  strBlobName, 
                                                  binaryData,
                                                  null,
                                                  true
                                                  );
        assertTrue(fSuccess);
      }

      
      String[] aRandomString_2 = {
          "oW3VAy5XVixsr1GaKoRXBkhgBxUrbXxz",
          "Tzut8zr4SYwjZcuJBmGcGYsiwmKHkihd",
          "RMHZl94d7zx6M2PnowA5u4n2q8IKp1Xh",
          "MFh9NlmTUTZHVO6jlGoPJLQ0SzUizRzm",
          "wRukcQe8JdOE3UYnAmdk85u1DoidgSzN",
      };
      
      for ( String strRandom : aRandomString_2 ) {

        byte[] binaryData = strRandom.getBytes();
        String strBlobName = String.format("Random_2/blob_%s.txt", strRandom.substring(0, 5));
        
        /* Create Blob "RandomString" with binary data */
        fSuccess = BlobSample.putBlob_wBinaryData_wMetadata(  
                                                  objBlobContainer, 
                                                  strBlobName, 
                                                  binaryData,
                                                  null, /* No metadata */
                                                  true
                                                  );
        assertTrue(fSuccess);
      }

      List<BlobProperties> listBlobProperties = new ArrayList<BlobProperties>();
      BlobSample.listBlobsAll(objBlobContainer, listBlobProperties, true);
      assertNotNull(listBlobProperties);
      count = listBlobProperties.size();
      assertEquals(10, count);
      
      count = BlobSample.countBlobs(objBlobContainer, null);
      assertEquals(10, count);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Raised an Exception");
    }
  }
  
  /**
   * Test Enumerate Blobs by prefix within a container
   */
  public void testBlob_Enumerate_wPrefix()
  {
    try {
      boolean fSuccess = false;
      String strContainerName = "testcontainer1";
      Collection<BlobProperties> listBlobProperties = null;
      
      fSuccess = BlobSample.deleteBlobContainer(  m_objBlobStorage, 
                                                  strContainerName, 
                                                  true 
                                                  );
      assertTrue(fSuccess);
      
      NameValueCollection objMetadataContainer = new NameValueCollection();
      BlobContainer objBlobContainer 
        = BlobSample.getBlobContainer_wMetadata ( m_objBlobStorage, 
                                                  strContainerName,
                                                  objMetadataContainer
                                                  );
      assertNotNull(objBlobContainer);

      String[] aRandomString_1 = {
          "5HECNWRM54Q4rx6SxQBlpySR77MCCLKG",
          "spigla3qdStFqzyXpaiPIbGOhsrTebAH",
          "BTXX4ZnhSRWiqvgQFzFnKmc3ODW3PwJq",
          "qHntGLLzDIS5d9USIAgsXtvL7rOWYynp",
          "fKTWwEv0mndAw9teJJHGdcskEhgDQD46",
      };
      
      for ( String strRandom : aRandomString_1 ) {

        byte[] binaryData = strRandom.getBytes();
        String strBlobName = String.format( "Random_1/blob%s.txt", 
                                            strRandom.substring(0, 5).toUpperCase()
                                            );
        
        /* Create Blob "RandomString" with binary data */
        fSuccess = BlobSample.putBlob_wBinaryData_wMetadata(  
                                                  objBlobContainer, 
                                                  strBlobName, 
                                                  binaryData,
                                                  null, /* No metadata */
                                                  true
                                                  );
        assertTrue(fSuccess);
      }

      
      String[] aRandomString_2 = {
          "oW3VAy5XVixsr1GaKoRXBkhgBxUrbXxz",
          "Tzut8zr4SYwjZcuJBmGcGYsiwmKHkihd",
          "RMHZl94d7zx6M2PnowA5u4n2q8IKp1Xh",
          "MFh9NlmTUTZHVO6jlGoPJLQ0SzUizRzm",
          "wRukcQe8JdOE3UYnAmdk85u1DoidgSzN",
      };
      
      for ( String strRandom : aRandomString_2 ) {

        byte[] binaryData = strRandom.getBytes();
        String strBlobName = String.format( "Random_2/blob%s.txt", 
                                            strRandom.substring(0, 5).toUpperCase()
                                            );
        
        /* Create Blob "RandomString" with binary data */
        fSuccess = BlobSample.putBlob_wBinaryData_wMetadata(  
                                                  objBlobContainer, 
                                                  strBlobName, 
                                                  binaryData,
                                                  null, /* No metadata */
                                                  true
                                                  );
        assertTrue(fSuccess);
      }

      listBlobProperties = new ArrayList<BlobProperties>();
      BlobSample.listBlobs( objBlobContainer, 
                            "Random_1/", 
                            listBlobProperties, 
                            true
                            );
      
      int countRandom_1 = 0;
      countRandom_1 = BlobSample.countBlobs(objBlobContainer, "Random_1/");
      assertEquals(5, countRandom_1);
      
      countRandom_1 = listBlobProperties.size();
      assertEquals(5, countRandom_1);

      listBlobProperties = new ArrayList<BlobProperties>();
      BlobSample.listBlobs( objBlobContainer, 
          "Random_2/", 
          listBlobProperties, 
          true
          );
    
      int countRandom_2 = 0;
      countRandom_2 = BlobSample.countBlobs(objBlobContainer, "Random_2/");
      assertEquals(5, countRandom_2);
      
      countRandom_2 = listBlobProperties.size();
      assertEquals(5, countRandom_2);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Raised an Exception");
    }
  }
  
  /*
   * Test Copy Blob.
   */
  public void testBlob_Copy()
  {
   try {
     boolean fSuccess = false;
     assertNotNull(m_objBlobStorage);
     
     String strContainerSrc = "testcontainer-src"; 
     String strContainerDest = "testcontainer-dest";
     String strBlobNameSrc = "testblob.src";
     String strBlobNameDest = "testblob.dest";
     
     fSuccess = BlobSample.deleteBlobContainer( m_objBlobStorage, 
                                                strContainerSrc, 
                                                true);
     assertTrue(fSuccess);
     
     fSuccess = BlobSample.deleteBlobContainer(  m_objBlobStorage, 
                                                 strContainerDest, 
                                                 true);
     assertTrue(fSuccess);
     
     NameValueCollection objMetadataContainerSrc = new NameValueCollection();
     BlobContainer objBlobContainerSrc 
        = BlobSample.getBlobContainer_wMetadata ( m_objBlobStorage, 
                                                  strContainerSrc,
                                                  objMetadataContainerSrc
                                                  );
     assertNotNull(objBlobContainerSrc);

     NameValueCollection objMetadataContainerDest = new NameValueCollection();
     BlobContainer objBlobContainerDest 
       = BlobSample.getBlobContainer_wMetadata (   m_objBlobStorage, 
                                                   strContainerDest,
                                                   objMetadataContainerDest
                                                   );
     assertNotNull(objBlobContainerDest);

     /* 
      * Define the binary data to be held within a blob.
      * To create binary data, transform a String into a byte array. 
      */
     String strTest = "fOgiQnNXz562fgy4t0Yv3wlvDfabt1TI";

     int intStrLength = strTest.length();
     byte[] binaryDataSrc = strTest.getBytes();
     
     /* Create source blob */
     fSuccess = BlobSample.putBlob_wBinaryData_wMetadata (  
                                               objBlobContainerSrc, 
                                               strBlobNameSrc, 
                                               binaryDataSrc,
                                               null, /* No metadata */
                                               true
                                               );
     assertTrue(fSuccess);
     
     /* Copy source blob to destination blob */
     fSuccess = BlobSample.copyBlob (  m_objBlobStorage, 
                                       strContainerSrc, 
                                       strContainerDest, 
                                       strBlobNameSrc, 
                                       strBlobNameDest, 
                                       true
                                       );
     assertTrue(fSuccess);
     
     /* Get destination blob */
     NameValueCollection objMetadataGet = new NameValueCollection();     
     byte[] binaryDataDest 
       = BlobSample.getBlob_wBinaryData_wMetadata ( 
                                       objBlobContainerDest, 
                                       strBlobNameDest,
                                       objMetadataGet
                                       );
     assertNotNull(binaryDataDest);

     /* With the binary data, Transform byte array into a String */
     String strDest = new String(binaryDataDest);
     assertEquals(intStrLength, strDest.length());
     
     /* Validate the retrieved data is valid */
     fSuccess = strTest.equals(strDest);
     assertTrue(fSuccess);

   } catch (Exception e) {
     e.printStackTrace();
     fail("Raised an Exception");
   }
  }
  
  public void testContainer_ACL() {
    
  }
}
