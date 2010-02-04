/**
 * Copyright  2006-2009 Soyatec
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * $Id$
 */
package org.soyatec.windows.azure.error;

/// Error code strings that are specific to queue service
public class TableErrorCodeStrings {
	   public static final String XMethodNotUsingPost = "XMethodNotUsingPost";
       public static final String XMethodIncorrectValue = "XMethodIncorrectValue";
       public static final String XMethodIncorrectCount = "XMethodIncorrectCount";

       public static final String TableHasNoProperties = "TableHasNoProperties";
       public static final String DuplicatePropertiesSpecified = "DuplicatePropertiesSpecified";
       public static final String TableHasNoSuchProperty = "TableHasNoSuchProperty";
       public static final String DuplicateKeyPropertySpecified = "DuplicateKeyPropertySpecified";
       public static final String TableAlreadyExists = "TableAlreadyExists";
       public static final String TableNotFound = "TableNotFound";
       public static final String EntityNotFound = "EntityNotFound";
       public static final String EntityAlreadyExists = "EntityAlreadyExists";
       public static final String PartitionKeyNotSpecified = "PartitionKeyNotSpecified";
       public static final String OperatorInvalid = "OperatorInvalid";
       public static final String UpdateConditionNotSatisfied = "UpdateConditionNotSatisfied";
       public static final String PropertiesNeedValue = "PropertiesNeedValue";

       public static final String PartitionKeyPropertyCannotBeUpdated = "PartitionKeyPropertyCannotBeUpdated";
       public static final String TooManyProperties = "TooManyProperties";
       public static final String EntityTooLarge = "EntityTooLarge";
       public static final String PropertyValueTooLarge = "PropertyValueTooLarge";
       public static final String InvalidValueType = "InvalidValueType";
       public static final String TableBeingDeleted = "TableBeingDeleted";
       public static final String TableServerOutOfMemory = "TableServerOutOfMemory";
       public static final String PrimaryKeyPropertyIsInvalidType = "PrimaryKeyPropertyIsInvalidType";
       public static final String PropertyNameTooLong = "PropertyNameTooLong";
       public static final String PropertyNameInvalid = "PropertyNameInvalid";

       public static final String BatchOperationNotSupported = "BatchOperationNotSupported";
       public static final String JsonFormatNotSupported = "JsonFormatNotSupported";
       public static final String MethodNotAllowed = "MethodNotAllowed";
       public static final String NotImplemented = "NotImplemented";
}
