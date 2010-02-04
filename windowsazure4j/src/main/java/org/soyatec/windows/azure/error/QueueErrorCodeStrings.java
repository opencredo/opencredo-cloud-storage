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
public class QueueErrorCodeStrings {
	   public static final String QueueNotFound = "QueueNotFound";
       public static final String QueueDisabled = "QueueDisabled";
       public static final String QueueAlreadyExists = "QueueAlreadyExists";
       public static final String QueueNotEmpty = "QueueNotEmpty";
       public static final String QueueBeingDeleted = "QueueBeingDeleted";
       public static final String PopReceiptMismatch = "PopReceiptMismatch";
       public static final String InvalidParameter = "InvalidParameter";
       public static final String MessageNotFound = "MessageNotFound";
       public static final String MessageTooLarge = "MessageTooLarge";
       public static final String InvalidMarker = "InvalidMarker";
}
