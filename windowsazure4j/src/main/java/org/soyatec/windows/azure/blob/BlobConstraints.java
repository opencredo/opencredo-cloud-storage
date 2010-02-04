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
package org.soyatec.windows.azure.blob;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.message.BasicHeader;
import org.soyatec.windows.azure.constants.HeaderNames;

/**
 * The <code>BlobConstraints</code> class specifies constraints for blob that
 * are used in the <code>BlobContainer</code> class.
 * 
 */
public class BlobConstraints {

	/**
	 * The list of all constraints to be used.
	 */
	private List<BasicHeader> constraints;

	/**
	 * Creates a <code>BlobConstraints</code> object.
	 */
	private BlobConstraints() {
		constraints = new ArrayList<BasicHeader>();
	}

	/**
	 * Creates a new BlobConstraint instance.
	 * 
	 * @return BlobConstraint instance
	 */
	public static BlobConstraints newInstance() {
		return new BlobConstraints();
	}

	/**
	 * Gets all the blob constraints.
	 * 
	 * @return Constraint list.
	 */
	public List<BasicHeader> getConstraints() {
		return constraints;
	}

	/**
	 * Sets the blob constraints.
	 * 
	 * @param constraints
	 *            Constraint list
	 */
	public void setConstraints(List<BasicHeader> constraints) {
		this.constraints = constraints;
	}

	/**
	 * Gets a new blob constraints to specifies the modification time for
	 * resource objects.
	 * 
	 * @param time
	 *            The timestamp to specifies after what time the source is
	 *            modified.
	 * @return BlobConstraints
	 */
	public BlobConstraints isSourceModifiedSince(Timestamp time) {
		constraints.add(new BasicHeader(HeaderNames.IfSourceModifiedSince,
				formatTime(time)));
		return this;
	}

	/**
	 * Gets a new blob constraints to specifies the unmodification time for
	 * resource objects.
	 * 
	 * @param time
	 *            The timestamp to specifies after what time the source is not
	 *            modified.
	 * @return BlobConstraints
	 */
	public BlobConstraints isSourceUnmodifiedSince(Timestamp time) {
		constraints.add(new BasicHeader(HeaderNames.IfSourceUnmodifiedSince,
				formatTime(time)));
		return this;
	}

	/**
	 * Gets a new blob constraints to specifies the matching resource objects.
	 * 
	 * @param etag
	 *            the matched etag.
	 * @return BlobConstraints
	 */
	public BlobConstraints isSourceMatch(String etag) {
		constraints.add(new BasicHeader(HeaderNames.IfSourceMatch, etag));
		return this;
	}

	/**
	 * Gets a new blob constraints to specifies the unmatching resource objects.
	 * 
	 * @param etag
	 *            the unmatched etag.
	 * @return BlobConstraints
	 */
	public BlobConstraints isSourceNoneMatch(String etag) {
		constraints.add(new BasicHeader(HeaderNames.IfSourceNoneMatch, etag));
		return this;
	}

	/**
	 * Gets a new blob constraints to specifies the modification time for
	 * destination object.
	 * 
	 * @param time
	 *            The timestamp to specifies after what time the destination is
	 *            modified.
	 * @return BlobConstraints
	 */
	public BlobConstraints isDestModifiedSince(Timestamp time) {
		constraints.add(new BasicHeader(HeaderNames.IfModifiedSince,
				formatTime(time)));
		return this;
	}

	/**
	 * Gets a new blob constraints to specifies the unmodification time for
	 * destination object.
	 * 
	 * @param time
	 *            The timestamp to specifies after what time the destination is
	 *            not modified.
	 * @return BlobConstraints
	 */
	public BlobConstraints isDestUnmodifiedSince(Timestamp time) {
		constraints.add(new BasicHeader(HeaderNames.IfUnmodifiedSince,
				formatTime(time)));
		return this;
	}

	/**
	 * Gets a new blob constraints to specifies the matching destination
	 * objects.
	 * 
	 * @param etag
	 *            the matched etag.
	 * @return BlobConstraints
	 */
	public BlobConstraints isDestMatch(String etag) {
		constraints.add(new BasicHeader(HeaderNames.IfMatch, etag));
		return this;
	}

	/**
	 * Gets a new blob constraints to specifies the unmatching destination
	 * objects.
	 * 
	 * @param etag
	 *            the matched etag.
	 * @return BlobConstraints
	 */
	public BlobConstraints isDestNoneMatch(String etag) {
		constraints.add(new BasicHeader(HeaderNames.IfNoneMatch, etag));
		return this;
	}

	private String formatTime(Timestamp time) {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss", Locale.US);
		return formatter.format(time) + " GMT";
	}

}
