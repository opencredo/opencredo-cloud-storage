/**
 * @author jetann
 */

package com.samples;

import java.io.IOException;
import java.io.File;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.soyatec.windows.azure.blob.BlobStorage;
import org.soyatec.windows.azure.blob.BlobContainer;
import org.soyatec.windows.azure.blob.RetryPolicies;

import org.soyatec.windows.azure.blob.ContainerProperties;

import org.soyatec.windows.azure.blob.BlobProperties; 
import org.soyatec.windows.azure.blob.BlobContents;

import org.soyatec.windows.azure.error.StorageException;
import org.soyatec.windows.azure.error.StorageServerException;

import org.soyatec.windows.azure.util.NameValueCollection;

import org.soyatec.windows.azure.util.TimeSpan;
import org.soyatec.windows.azure.blob.io.FileStream;
import org.soyatec.windows.azure.blob.io.MemoryStream;

import org.soyatec.windows.azure.blob.ContainerAccessControl;


/**
 * Sample Class demonstrating Blob functionality from 
 * Windows Azure SDK for Java Developer
 * @author jetann
 *
 */
public class BlobSample 
{
  protected static final String BLOB_NAMESPACE   = "http://blob.core.windows.net/";
  
//  public static final String AZURE_ACCOUNT_NAME  = "MyAccountName";
//  public static final String AZURE_ACCOUNT_KEY   = "MyAccountKey";
    
  private static final String CONTENT_TYPE_BINARY = "Binary";
  private static final String CONTENT_TYPE_FILE   = "File";
  
  private static final int BLOB_CONTAINER_DELETE_MSEC = 40000; 
  
  /**
   * Create client proxy to Blob Storage service.
   * 
   * @param strAccountName
   * @param strAccountKey
   * @return BlobStorage instance.
   * @throws Exception 
   */
  public static BlobStorage createStorageAccess( String strAccountName,
                                                 String strAccountKey
                                                 ) throws Exception
  {
    if ( null == strAccountName || strAccountName.isEmpty() ) {
      throw new IllegalArgumentException("String parameter 'strAccountName' is empty!");
    }
    if ( null == strAccountKey || strAccountKey.isEmpty() ) {
      throw new IllegalArgumentException("String parameter 'strAccountKey' is empty!");
    }    

    BlobStorage objBlobStorage = null;

    try {
      objBlobStorage = BlobStorage.create(
                          URI.create( BLOB_NAMESPACE ),
                                      false,
                                      strAccountName,
                                      strAccountKey
                                      );

      /*
       * Set retry policy for a time interval of 5 seconds.
       */
      objBlobStorage.setRetryPolicy(RetryPolicies.retryN(1, TimeSpan.fromSeconds(5)));
    } catch ( StorageException e ) {
      e.printStackTrace();
      throw e;
    } catch ( Exception e ) {
      e.printStackTrace();
      throw e;
    }

    return objBlobStorage;
  }

  /**
   * Get Blob Container.
   * If not exists, create it and set metadata.
   * If exists, get it and its metadata.
   * 
   * @param objBlobStorage
   * @param strBlobContainerName
   * @param objMetadata
   * @return
   * @throws IllegalArgumentException
   */
  public static BlobContainer getBlobContainer_wMetadata ( 
                                  BlobStorage objBlobStorage, 
                                  String strBlobContainerName,
                                  NameValueCollection objMetadata
                                  )
    throws IllegalArgumentException
  {
    BlobContainer objBlobContainer = null;

    if ( null == objBlobStorage ) {
      throw new IllegalArgumentException("BlobStorage parameter 'objBlobStorage' is not defined!");
    }

    if ( null == strBlobContainerName || strBlobContainerName.isEmpty() ) {
      throw new IllegalArgumentException("String parameter 'strBlobContainerName' is empty!");
    }

    try
    {
      /* Fetch objBlobContainer by name */
      objBlobContainer = objBlobStorage.getBlobContainer(strBlobContainerName);
      if (null == objBlobContainer) {
        throw new NullPointerException(
            String.format(  "BlobStorage returned null BlobContainer '%s'.", 
                            strBlobContainerName
                            ));
      }
            
      /* Check if it exist */
      if (!objBlobContainer.doesContainerExist()) {
        /* If objBlobContainer does not exist, create it */
        if(     !objBlobContainer.createContainer() 
            ||  !objBlobContainer.doesContainerExist()) {
          throw new Exception(
                      String.format("Failed to create blob container '%s'", 
                                    strBlobContainerName
                                    ));
        }
        
        /* Set metadata to Blob Container */
        if (null != objMetadata) {
          objBlobContainer.setContainerMetadata(objMetadata);
        }
      } else {
        if (null != objMetadata) {
          ContainerProperties objContainerProperties = objBlobContainer.getContainerProperties();
          NameValueCollection objMetadataTmp = new NameValueCollection();
          objMetadataTmp = objContainerProperties.getMetadata();
          
          objMetadata.putAll(objMetadataTmp);
        }        
      }
    } catch ( StorageException e ) {
      e.printStackTrace();
    } catch ( Exception e ) {
      e.printStackTrace();
    }
      
    return objBlobContainer;
  }

  /**
   * List of Blob Containers in storage account
   *
   * @param objBlobStorage BlobStorage 
   * @return boolean
   */
  public static boolean listBlobContainers( 
                          BlobStorage objBlobStorage,
                          List<BlobContainer> listBlobContainers,
                          boolean fPrintList
                          )
  {
    boolean fSuccess = false;
    
    if ( null == objBlobStorage ) {
      throw new IllegalArgumentException("BlobStorage parameter not defined!");
    }
    
    List<BlobContainer> listBlobContainersTmp = new ArrayList<BlobContainer>();
    
    try {        
      listBlobContainersTmp = objBlobStorage.listBlobContainers();
      
      if ( fPrintList ) {
        System.out.println("------------------");          
        if (null != listBlobContainers && !listBlobContainers.isEmpty()) {
          System.out.printf("List of Blob Containers in Account '%s'\n",
                    objBlobStorage.getAccountName()
                    );
        
            for ( BlobContainer objBlobContainer : listBlobContainers ) {
              System.out.println(objBlobContainer.getContainerName());
            }
        } else {
          System.out.printf("No Blob Containers in Account '%s'\n",
            objBlobStorage.getAccountName()
            );
        }
        System.out.println("------------------");
      }
      
      if ( null != listBlobContainersTmp ) {
        listBlobContainers.addAll(listBlobContainersTmp);
      }
      fSuccess = true;
    } catch ( StorageServerException e ) {
      e.printStackTrace();
    } catch ( Exception e ) {
      e.printStackTrace();
    }
    
    return fSuccess;
  }


  /**
   * List All Blobs in a Container with matching prefix.
   * @param objBlobContainer
   * @param strPrefix
   * @return boolean True upon success
   */
  public static boolean listBlobs (   BlobContainer objBlobContainer,
                                      String strPrefix,
                                      Collection<BlobProperties> listBlobProperties,
                                      boolean fPrintList
      )
  {
    boolean fSuccess = false;
    
    if ( null == objBlobContainer || !objBlobContainer.doesContainerExist() ) {
      throw new IllegalArgumentException("BlobContainer parameter is invalid!");
    }
    if ( null == strPrefix ) {
      strPrefix = "";
    }
    
    try {        
      Collection<BlobProperties> listBlobPropertiesTmp =
          new ArrayList<BlobProperties>();
      
      listBlobPropertiesTmp = objBlobContainer.listBlobs( strPrefix, false);
      
      if (fPrintList) {
        System.out.println("------------------");
        if (null != listBlobPropertiesTmp && !listBlobPropertiesTmp.isEmpty()) {
          if (strPrefix.isEmpty()) {
            System.out.printf(  "List of Blobs in Container '%s'\n", 
                      objBlobContainer.getContainerName() 
                      );
          } else {
            System.out.printf(  "List of Blobs with prefix '%s' in Container '%s'\n",
                strPrefix,
                objBlobContainer.getContainerName() 
                );          
          }
          
          for (BlobProperties objBlobProperties : listBlobPropertiesTmp) {
              System.out.println(objBlobProperties.getName());
          }
        } else {
          System.out.printf(  "No Blobs in Container %s\n", 
            objBlobContainer.getContainerName() );
        }
        System.out.println("------------------");
      }
      
      if (null != listBlobPropertiesTmp && null != listBlobProperties) {
        listBlobProperties.addAll(listBlobPropertiesTmp);
      }
      
      fSuccess = true;
    } catch ( StorageException e ) {
      e.printStackTrace();
    } catch ( Exception e ) {
      e.printStackTrace();
    }
    
    return fSuccess;
  }
  
  
  /**
   * List All Blobs in a Container.
   * 
   * @param objBlobContainer
   * @return None
   */
  public static boolean listBlobsAll (  BlobContainer objBlobContainer,
                                        Collection<BlobProperties> listBlobProperties,
                                        boolean fPrintList 
                                        )
  {
    if ( null == objBlobContainer || !objBlobContainer.doesContainerExist() ) {
      throw new IllegalArgumentException("BlobContainer parameter is invalid!");
    }
    
    return BlobSample.listBlobs(objBlobContainer, 
                                null, /* No prefix */
                                listBlobProperties, 
                                fPrintList 
                                );
  }
  
  /**
   * List All Blobs in a Container with matching prefix.
   * @param objBlobContainer
   * @param strPrefix
   * @return int Count
   * @throws Exception 
   */
  public static int countBlobs ( BlobContainer objBlobContainer,
                                  String strPrefix
      ) throws Exception
  {
    int count = 0;
    
    if ( null == objBlobContainer || !objBlobContainer.doesContainerExist() ) {
      throw new IllegalArgumentException("BlobContainer parameter is invalid!");
    }
    if ( null == strPrefix ) {
      strPrefix = "";
    }
    
    try {        
      Collection<BlobProperties> listBlobs 
                                      = objBlobContainer.listBlobs( strPrefix, false);
      
      if (null != listBlobs) {
        count = listBlobs.size();
      }
    } catch ( StorageException e ) {
      e.printStackTrace();
      throw e;
    } catch ( Exception e ) {
      e.printStackTrace();
      throw e;
    }
    
    return count;
  }
  
  /**
   * Remove all containers from Blob Storage.
   * 
   * @param objBlobStorage
   * @return boolean True upon success
   */
  public static boolean deleteBlobContainersAll(  
                                        BlobStorage objBlobStorage,
                                        boolean fConfirmDelete
                                        )
  {
    boolean fSuccess = false;
    
    if ( null == objBlobStorage ) {
      throw new IllegalArgumentException("BlobStorage parameter not defined!");
    }
    
    try {        
      List<BlobContainer> listBlobContainers = objBlobStorage.listBlobContainers();
      
      if (null != listBlobContainers && !listBlobContainers.isEmpty()) {    
        for (BlobContainer objBlobContainer : listBlobContainers) {
          String strContainerName = objBlobContainer.getContainerName();
          
          if (!BlobSample.deleteBlobContainer(objBlobContainer, false )) {
            throw new Exception("Failed to delete " + strContainerName);
          }
        }
        
        if (fConfirmDelete) {
          /* The amount of delay for deleting container. */
          Thread.sleep(BLOB_CONTAINER_DELETE_MSEC);
          
          /* Confirm all containers were deleted */
          for (BlobContainer objBlobContainer : listBlobContainers) {
            if ( objBlobContainer.doesContainerExist() ) {
              ContainerProperties objContainerProperties 
                = objBlobContainer.getContainerProperties();
              throw new Exception ( String.format(  "BlobContainer '%s' was not deleted.", 
                                                    objContainerProperties.getName()
                                                    ) );
            }
          } 
        }
      }

      fSuccess = true;
    } catch ( StorageServerException e ) {
      e.printStackTrace();
    } catch ( Exception e ) {
      e.printStackTrace();
    }

    return fSuccess;
  }

  /**
   * Delete a container from Blob Storage.
   * 
   * @param objBlobStorage BlobStorage
   * @param strContainerName String
   * @param fConfirmDelete boolean
   * @return boolean True upon success
   * @throws Exception
   */
  public static boolean deleteBlobContainer ( BlobStorage objBlobStorage, 
                                              String strContainerName,
                                              boolean fConfirmDelete
                                            )
  throws Exception
  {
    boolean fSuccess = false;
    
    if ( null == objBlobStorage ) {
      throw new IllegalArgumentException("BlobStorage parameter not defined!");
    }

    try {
      BlobContainer objBlobContainer = objBlobStorage.getBlobContainer(strContainerName);
      if ( null == objBlobContainer ) {
        throw new NullPointerException("BlobContainer");
      }
  
      fSuccess = BlobSample.deleteBlobContainer(objBlobContainer, fConfirmDelete);
    } catch ( Exception e ) {
      e.printStackTrace();
    }
    
    return fSuccess;
  }
  
  /**
   * Delete a container from storage if it exists.
   * 
   * @param objBlobContainer BlobContainer
   * @param fConfirmDelete boolean
   * @return boolean True upon success
   * @throws Exception
   */
  public static boolean deleteBlobContainer ( BlobContainer objBlobContainer,
                                              boolean fConfirmDelete
                                            )
    throws Exception
  {
    boolean fSuccess = false;

    if ( null == objBlobContainer ) {
      throw new IllegalArgumentException("BlobContainer parameter not defined!");
    }

    try {
      if(objBlobContainer.doesContainerExist()) {
        if(!objBlobContainer.deleteContainer()) {
          throw new Exception(
              String.format(  "Unexpected exception for Blob Container '%s'.", 
                  objBlobContainer.getContainerName()
                  ));
        }
        
        if (fConfirmDelete) {
          /* The amount of time to delete container. */
          Thread.sleep(BLOB_CONTAINER_DELETE_MSEC);
          if (objBlobContainer.doesContainerExist()) {
            throw new Exception(
                String.format(  "Blob Container '%s' was not deleted.", 
                    objBlobContainer.getContainerName()
                    ));
          }
        }
      }
      
      fSuccess = true;
    } catch ( Exception e ) {
      e.printStackTrace();
    }

    return fSuccess;
  }
  
  /**
   * Number of Blob containers in storage account.
   * 
   * @param objBlobStorage BlobStorage
   * @return int Count
   * @throws Exception 
   */
  public static int countBlobContainers( BlobStorage objBlobStorage ) 
    throws Exception
  {
    int count = 0;
    if ( null == objBlobStorage ) {
      throw new IllegalArgumentException("BlobStorage parameter not defined!");
    }
    
    try {
      List<BlobContainer> listBlobContainers = objBlobStorage.listBlobContainers();
      
      if (null != listBlobContainers) {
        count = listBlobContainers.size();
      }
    } catch ( StorageServerException e ) {
      e.printStackTrace();
      throw e;
    } catch ( Exception e ) {
      e.printStackTrace();
      throw e;
    }
   
    return count;
  }

  /**
   * Apply a simple collection of metadata<String,String> to a Blob Container.
   * 
   * @param objBlobContainer BlobContainer
   * @return boolean
   * @throws IllegalArgumentException
   */
  public static boolean setBlobContainer_wMetadata( BlobContainer objBlobContainer,
                                                    NameValueCollection objMetadata
                                                    )
    throws IllegalArgumentException
  {
    boolean fSuccess = false;
    if ( null == objBlobContainer || !objBlobContainer.doesContainerExist()) {
      throw new IllegalArgumentException("BlobContainer parameter is invalid!");
    }
    
    if ( null == objMetadata || objMetadata.isEmpty() ) {
      throw new IllegalArgumentException("NameValueCollection parameter is invalid!");
    }
    
    try {
      /* Set metadata to Blob Container */
      objBlobContainer.setContainerMetadata(objMetadata);
      fSuccess = true;
    } catch ( StorageException e ) {
      e.printStackTrace();
    } catch ( Exception e ) {
      e.printStackTrace();
    }

    return fSuccess;
  }
  
  

  /**
   * Put blob with binary data w\Metadata into a container.
   * 
   * @param objBlobContainer
   * @param strBlobName
   * @param binaryBlobData
   * @param objMetadata
   * @param fOverwrite
   * @return
   * @throws Exception
   */
  public static boolean putBlob_wBinaryData_wMetadata(  
                                            BlobContainer objBlobContainer, 
                                            String strBlobName, 
                                            byte[] binaryBlobData,
                                            NameValueCollection objMetadata,
                                            boolean fOverwrite 
                                            )
  throws Exception
  {
    boolean fSuccess = false;
    
    if ( null == objBlobContainer ) {
      throw new IllegalArgumentException("BlobContainer parameter is invalid!");
    }
    if ( strBlobName.isEmpty() ) {
      throw new IllegalArgumentException("Blob label is empty!");
    }
    if ( 0 == binaryBlobData.length ) {
      throw new IllegalArgumentException("Blob binary data is empty!");
    }

    try {
      if (!objBlobContainer.doesContainerExist()) {
        throw new IllegalArgumentException("BlobContainer does not exist!");
      }
      if ( objBlobContainer.doesBlobExist(strBlobName) && !fOverwrite ) {
        throw new Exception(String.format("Blob '%s' exists! Cannot overwrite.", strBlobName));
      }

      /* New Blob Properties */
      BlobProperties objBlobProperties = new BlobProperties(strBlobName);
      objBlobProperties.setContentType(CONTENT_TYPE_BINARY);
      
      /* Set Metadata */
      if (null != objMetadata && (objMetadata instanceof NameValueCollection)) {
        objBlobProperties.setMetadata(objMetadata);
      }
        
      /* Set Blob Contents */
      MemoryStream objStream         = new MemoryStream(binaryBlobData);
      BlobContents objBlobContents   = new BlobContents(objStream);
      
      if (!objBlobContainer.createBlob(   objBlobProperties, 
                                          objBlobContents, 
                                          fOverwrite
                                          )) {
        throw new Exception("Failed to create blob");
      }
      
      fSuccess = true;
    } catch ( StorageServerException e ) {
      e.printStackTrace();      
    } catch ( StorageException e ) {
      e.printStackTrace();
    } catch ( IOException e ) {
      e.printStackTrace();
    } catch ( Exception e ) {
      e.printStackTrace();
    }

    return fSuccess;
  }

  /**
   * Get binary data from blob in container.
   * 
   * @param objBlobContainer
   * @param strBlobName
   * @param objMetadata
   * @return byte[] Binary Data
   * @throws Exception
   */
  public static byte[] getBlob_wBinaryData_wMetadata( 
                          BlobContainer objBlobContainer, 
                          String strBlobName,
                          NameValueCollection objMetadata
                          )
    throws Exception
  {
    byte[] bytesResult = null;
    
    if ( null == objBlobContainer ) {
      throw new IllegalArgumentException("BlobContainer parameter is invalid!");
    }
    if ( null == strBlobName || strBlobName.isEmpty() ) {
      throw new IllegalArgumentException("Blob name parameter is invalid!");
    }

    try  {
      if (!objBlobContainer.doesContainerExist()) {
        throw new IllegalArgumentException("BlobContainer does not exist!");
      }
      if ( !objBlobContainer.doesBlobExist(strBlobName) ) { 
        throw new Exception("Blob does not exist!");
      }
      
      MemoryStream objStream       = new MemoryStream();
      BlobContents objBlobContents = new BlobContents(objStream);
      boolean fTransferAsChunks = false;

      BlobProperties objBlobProperties 
          = objBlobContainer.getBlob( strBlobName, 
                                      objBlobContents, 
                                      fTransferAsChunks
                                      );
      
      if (null == objBlobProperties) {
        throw new NullPointerException("BlobProperties");
      }
      
      /* Get Blob properties */
      String strContentType  = objBlobProperties.getContentType();
      if (!strContentType.equals(CONTENT_TYPE_BINARY)) {
        throw new Exception(String.format("Wrong content type: '%s'!", strContentType));
      }

      String strBlobNameProp = objBlobProperties.getName();
      if ( !strBlobName.equals(strBlobNameProp) ) {
        throw new Exception(String.format("Wrong blob: '%s'!", strBlobNameProp));
      }

      /* Get Blob metadata */
      if ( null != objMetadata ) {
        objMetadata  = objBlobProperties.getMetadata();
      }

      bytesResult = objStream.getBytes();
    } catch (StorageException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch ( Exception e ) {
      e.printStackTrace();
    }

    return bytesResult;
  }

  
  /**
   * Put blob with file into a container.
   * 
   * @param objBlobContainer BlobContainer
   * @param strBlobName String
   * @param objMetadata NameValueCollection
   * @param fOverwrite boolean
   * @return boolean True upon success
   * @throws Exception 
   */
  public static boolean putBlob_wFile_wMetadata (  
                                    BlobContainer objBlobContainer,
                                    String strBlobName, 
                                    String strFilePath, 
                                    NameValueCollection objMetadata,
                                    boolean fOverwrite
                                    )
  throws Exception
  {
    boolean fSuccess = false;
    
    if ( null == objBlobContainer ) {
      throw new IllegalArgumentException("BlobContainer parameter is invalid!");
    }
    if ( null == strBlobName || strBlobName.isEmpty() ) {
      throw new IllegalArgumentException("Blob Name parameter is invalid!");
    }
    if ( null == strFilePath || strFilePath.isEmpty() ) {
      throw new IllegalArgumentException("File Name parameter is invalid!");
    }

    try {
      if (!objBlobContainer.doesContainerExist()) {
        throw new IllegalArgumentException("BlobContainer does not exist!");
      }
      if ( objBlobContainer.doesBlobExist(strBlobName) && !fOverwrite ) {
        throw new Exception(String.format("Blob '%s' exists! Cannot overwrite.", strBlobName));
      }
      
      /* Check to see if file exist first. */
      File objFile = new File(strFilePath);
      if( null == objFile || !objFile.exists()) {
        throw new Exception(String.format("File '%s' was not found!", strFilePath));
      }

      /* Set Blob properties */
      BlobProperties objBlobProperties = new BlobProperties(strBlobName);
      objBlobProperties.setContentType(CONTENT_TYPE_FILE);
      
      /* Set Metadata */
      if (null != objMetadata && (objMetadata instanceof NameValueCollection)) {
        objBlobProperties.setMetadata(objMetadata);
      }
      
      /* Set Blob contents */
      FileStream objStream               = new FileStream(strFilePath);
      BlobContents objBlobContents       = new BlobContents(objStream);
      
      if (!objBlobContainer.createBlob(objBlobProperties, objBlobContents, fOverwrite)) {
        throw new Exception("Failed to create blob");
      }
      
      if ( !objBlobContainer.doesBlobExist(strBlobName) ) {
        throw new Exception(String.format("Blob '%s' was not created!", strBlobName));
      }
      
      fSuccess = true;
    } catch ( StorageServerException e ) {
      e.printStackTrace();
    } catch ( StorageException e ) {
      e.printStackTrace();
    } catch ( IOException e ) {
      e.printStackTrace();
    } catch ( Exception e ) {
      e.printStackTrace();
    }

    return fSuccess;
  }
  
  /**
   * Get file data from blob within specified container.
   * 
   * @param objBlobContainer
   * @param strBlobName
   * @param strFilePath
   * @param objMetadata NameValueCollection
   * @param fOverwrite
   * @return boolean true upon success
   * @throws Exception 
   */
  public static boolean getBlob_wFile_wMetadata (  
                                      BlobContainer objBlobContainer, 
                                      String strBlobName,
                                      String strFilePath,
                                      NameValueCollection objMetadata,
                                      boolean fOverwrite
                                      )
    throws Exception
  {
    boolean fSuccess = false;
    
    if ( null == objBlobContainer ) {
      throw new IllegalArgumentException("BlobContainer parameter is invalid!");
    }
    if ( ( null == strBlobName ) || strBlobName.isEmpty() ) {
      throw new IllegalArgumentException("Blob Name parameter is invalid!");
    }
    if ( null == strFilePath || strFilePath.isEmpty() ) {
      throw new IllegalArgumentException("File Path parameter is invalid!");
    }

    try  {
      if (!objBlobContainer.doesContainerExist()) {
        throw new IllegalArgumentException("BlobContainer does not exist!");
      }
      if ( !objBlobContainer.doesBlobExist(strBlobName) ) { 
        throw new Exception("Blob does not exist!");
      }
      
      /* Delete if it exists and only if overwrite is OK (true). */
      File objFile = new File(strFilePath);
      if( null != objFile && objFile.exists()) {
        if (!fOverwrite) {
          throw new Exception(String.format("Cannot overwrite, File '%s' exists!", strFilePath));
        }
        
        if( !objFile.delete() ) {
          throw new Exception(String.format("File '%s' was not deleted!", strFilePath));
        }
      }

      FileStream objStream          = new FileStream(strFilePath);
      BlobContents objBlobContents  = new BlobContents(objStream);
      boolean fTransferAsChunks     = false;

      BlobProperties objBlobProperties 
          = objBlobContainer.getBlob( strBlobName, 
                                      objBlobContents, 
                                      fTransferAsChunks
                                      );

      if (null == objBlobProperties) {
        throw new NullPointerException("BlobProperties");
      }
      
      /* Get Blob properties */
      String strContentType  = objBlobProperties.getContentType();
      if (!strContentType.equals(CONTENT_TYPE_FILE)) {
        throw new Exception(String.format("Wrong content type: '%s'!", strContentType));
      }

      String strBlobNameProp = objBlobProperties.getName();
      if ( !strBlobName.equals(strBlobNameProp) ) {
        throw new Exception(String.format("Wrong blob: '%s'!", strBlobNameProp));
      }

      /* Get Blob metadata */
      if ( null != objMetadata ) {
        objMetadata = objBlobProperties.getMetadata();
      }

      /* Validate if file was retrieved from blob. */
      objFile = new File(strFilePath);
      if( null == objFile || !objFile.exists()) {
        throw new Exception(String.format("File '%s' was not created!", strFilePath));
      }

      fSuccess = true;
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch ( Exception e ) {
      e.printStackTrace();
    }

    return fSuccess;
  }

  /**
   * Sets the access control list (ACL) for the specified 
   * container. The ACL indicates whether blobs in a container 
   * may be accessed publicly.
   * 
   * @param objBlobContainer BlobContainer
   * @param fPublicAccess boolean
   * @return boolean true upon success.
   * @throws IllegalArgumentException
   */
  public static boolean setBlobContainer_wACL ( BlobContainer objBlobContainer,
                                                boolean fPublicAccess
                                              )
  throws IllegalArgumentException
  {    
    boolean fSuccess = false;
    if ( null == objBlobContainer || !objBlobContainer.doesContainerExist() ) {
      throw new IllegalArgumentException("BlobContainer parameter not defined!");
    }
    
    try {
      ContainerAccessControl enumAccess 
            = fPublicAccess   ? ContainerAccessControl.Public 
                              : ContainerAccessControl.Private;
      
      objBlobContainer.setContainerAccessControl(enumAccess);
      fSuccess = true;
    } catch ( StorageException e ) {
      e.printStackTrace();
    } catch ( Exception e ) {
      e.printStackTrace();
    }
      
    return fSuccess;
  }

  /**
   * Copy Blob from Source to Destination Container.
   * 
   * @param objBlobStorage BlobStorage
   * @param strContainerSrc String
   * @param strContainerDest String
   * @param strBlobNameSrc String
   * @param strBlobNameDest String
   * @param fOverwriteDest boolean
   * @return
   */
  public static boolean copyBlob( BlobStorage objBlobStorage, 
                                  String strContainerSrc, 
                                  String strContainerDest, 
                                  String strBlobNameSrc,
                                  String strBlobNameDest,
                                  boolean fOverwriteDest
                                  )
  {
    boolean fSuccess = false;
    
    if ( null == objBlobStorage ) {
      throw new IllegalArgumentException("BlobStorage parameter is invalid!");
    }
    if ( null == strContainerSrc || strContainerSrc.isEmpty() ) {
      throw new IllegalArgumentException("Container Source parameter is invalid!");
    }    
    if ( null == strContainerDest || strContainerDest.isEmpty() ) {
      strContainerDest = strContainerSrc;
    }      
    if ( null == strBlobNameSrc || strBlobNameSrc.isEmpty() ) {
      throw new IllegalArgumentException("Blob Name parameter is invalid!");
    } 
    if ( null == strBlobNameDest || strBlobNameDest.isEmpty() ) {
      strBlobNameDest = strBlobNameSrc;
    } 
    
    try {
      /* Validate Source container exists */
      BlobContainer objBlobContainerSrc 
            = objBlobStorage.getBlobContainer(strContainerSrc);
      if (null == objBlobContainerSrc || !objBlobContainerSrc.doesContainerExist()) {
        throw new Exception(String.format(  "Source Container '%s' does not exist", 
                                            strContainerSrc));
      }
      
      /* Validate Source blob exists */       
      if ( !objBlobContainerSrc.doesBlobExist(strBlobNameSrc) ) {
        throw new Exception(String.format(  "Source Blob '%s' does not exist", 
                                            strBlobNameSrc));        
      }

      /* Validate Destination container exists */      
      BlobContainer objBlobContainerDest 
            = objBlobStorage.getBlobContainer(strContainerDest); 
      if (null == objBlobContainerDest || !objBlobContainerDest.doesContainerExist()) {
        throw new Exception(String.format(  "Destination Container '%s' does not exist", 
                                            strContainerSrc));
      }

      /* Validate if Destination blob exists and do not overwrite */    
      if ( objBlobContainerDest.doesBlobExist(strBlobNameDest) && !fOverwriteDest ) {
        throw new Exception(  String.format("Destination Blob '%s' does exist and do not overwrite.", 
                                            strBlobNameSrc));        
      }

      fSuccess = objBlobContainerSrc.copyBlob( strContainerDest, 
                                              strBlobNameDest, 
                                              strBlobNameSrc );
    } catch ( StorageException e ) {
      e.printStackTrace();
    } catch ( Exception e ) {
      e.printStackTrace();
    }

    return fSuccess;
  }
  
} /* End of class BlobSample */