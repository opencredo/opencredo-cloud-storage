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
package org.opencredo.storage.azure;

import java.util.Comparator;

import org.apache.http.Header;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public class HeadersComparator implements Comparator<Header> {

    /**
     * @param o1
     * @param o2
     * @return
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Header o1, Header o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
    }

}
