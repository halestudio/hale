/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core.io.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.zip.GZIPInputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;

/**
 * Detects if a stream is compressed with GZip. Optionally uses an internal
 * content describer to verify the uncompressed content of a stream.
 * 
 * @author Simon Templer
 */
public class GZipContentDescriber implements IContentDescriber, IExecutableExtension {

	/**
	 * The GZip content type identifier as specified in the plugin.xml
	 */
	public static final String GZIP_CONTENT_TYPE_ID = "eu.esdihumboldt.hale.common.core.gzip";

	private final IContentDescriber internalContentDescriber;

	/**
	 * Default constructor.
	 */
	public GZipContentDescriber() {
		this(null);
	}

	/**
	 * Constructor using an internal content describer for the uncompressed
	 * content.
	 * 
	 * @param internalContentDescriber the content describer for the
	 *            uncompressed content
	 */
	public GZipContentDescriber(IContentDescriber internalContentDescriber) {
		super();
		this.internalContentDescriber = internalContentDescriber;
	}

	/**
	 * @see IContentDescriber#describe(InputStream, IContentDescription)
	 */
	@Override
	public int describe(InputStream contents, IContentDescription description) throws IOException {
		// we need a pushbackstream to look ahead
		PushbackInputStream pb = new PushbackInputStream(contents, 2);
		byte[] signature = new byte[2];
		pb.read(signature); // read the signature
		pb.unread(signature); // push back the signature to the stream

		int head = (signature[0] & 0xff) | ((signature[1] << 8) & 0xff00);
		if (GZIPInputStream.GZIP_MAGIC == head) {
			if (internalContentDescriber == null) {
				// only check for GZip -> valid
				return VALID;
			}
			else {
				// test the compressed contents
				contents = new GZIPInputStream(pb);
				return internalContentDescriber.describe(contents, description);
			}
		}

		return INVALID; // not Gziped
	}

	/**
	 * @see IContentDescriber#getSupportedOptions()
	 */
	@Override
	public QualifiedName[] getSupportedOptions() {
		if (internalContentDescriber != null) {
			return internalContentDescriber.getSupportedOptions();
		}

		// no properties
		return new QualifiedName[0];
	}

	/**
	 * Determines if the given content type is a GZip content type.
	 * 
	 * @param contentType the content type
	 * @return <code>true</code> if the content type is the GZip content type or
	 *         it extends the GZip content type
	 */
	public static boolean isGZipContentType(IContentType contentType) {
		if (contentType == null) {
			return false;
		}

		if (GZIP_CONTENT_TYPE_ID.equals(contentType.getId())) {
			return true;
		}

		return isGZipContentType(contentType.getBaseType());
	}

	/**
	 * @see IExecutableExtension#setInitializationData(IConfigurationElement,
	 *      String, Object)
	 */
	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		if (internalContentDescriber instanceof IExecutableExtension) {
			// forward initialization data for internal configuration
			((IExecutableExtension) internalContentDescriber).setInitializationData(config,
					propertyName, data);
		}
	}

}
