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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package eu.esdihumboldt.hale.io.xsd.reader.internal;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.ws.commons.schema.resolver.CollectionURIResolver;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.xml.sax.InputSource;

import com.google.common.io.InputSupplier;

import eu.esdihumboldt.hale.common.cache.Request;
import eu.esdihumboldt.util.resource.Resources;

/**
 * This resolver provides the means of resolving the imports and includes of a
 * given schema document. The system will call this default resolver if there is
 * no other resolver present in the system.
 * 
 * @author Bernd Schneiders
 * @author Simon Templer
 */
public class HumboldtURIResolver implements CollectionURIResolver {

	private String collectionBaseURI;

	/**
	 * @see URIResolver#resolveEntity(String, String, String)
	 */
	@Override
	public InputSource resolveEntity(String namespace, String schemaLocation, String baseUri) {
		if (baseUri != null) {
			try {
				if (baseUri.startsWith("file:/")) { //$NON-NLS-1$
					baseUri = new URI(baseUri).getPath();
				}

				File baseFile = new File(baseUri);
				if (baseFile.exists()) {
					baseUri = baseFile.toURI().toString();
				}
				else if (collectionBaseURI != null) {
					baseFile = new File(collectionBaseURI);
					if (baseFile.exists()) {
						baseUri = baseFile.toURI().toString();
					}
				}

				URI ref = new URI(baseUri).resolve(new URI(schemaLocation));

				// try resolving using (local) Resources
				InputSupplier<? extends InputStream> input = Resources.tryResolve(ref,
						Resources.RESOURCE_TYPE_XML_SCHEMA);
				if (input != null) {
					try {
						InputSource is = new InputSource(input.getInput());
						is.setSystemId(ref.toString());
						return is;
					} catch (Throwable e) {
						// ignore
					}
				}

				// try resolving through cache
				try {
					InputSource is = new InputSource(Request.getInstance().get(ref));
					is.setSystemId(ref.toString());
					return is;
				} catch (Throwable e) {
					// ignore
				}

				// fall-back
				return new InputSource(ref.toString());
			} catch (URISyntaxException e1) {
				throw new RuntimeException(e1);
			}
		}

		// try resolving using (local) Resources
		try {
			URI locationUri = new URI(schemaLocation);
			InputSupplier<? extends InputStream> input = Resources.tryResolve(locationUri,
					Resources.RESOURCE_TYPE_XML_SCHEMA);
			if (input != null) {
				InputSource is = new InputSource(input.getInput());
				is.setSystemId(schemaLocation);
				return is;
			}
		} catch (Throwable e) {
			// ignore
		}

		// try resolving through cache
		try {
			InputSource is = new InputSource(Request.getInstance().get(schemaLocation));
			is.setSystemId(schemaLocation);
			return is;
		} catch (Throwable e) {
			// ignore
		}

		// fall-back
		return new InputSource(schemaLocation);
	}

	/**
	 * Find whether a given URI is relative or not
	 * 
	 * @param uri the URI
	 * 
	 * @return if the URI is absolute
	 */
	protected boolean isAbsolute(String uri) {
		return uri.startsWith("http://"); //$NON-NLS-1$
	}

	/**
	 * @see CollectionURIResolver#getCollectionBaseURI()
	 */
	@Override
	public String getCollectionBaseURI() {
		return collectionBaseURI;
	}

	/**
	 * @see CollectionURIResolver#setCollectionBaseURI(String)
	 */
	@Override
	public void setCollectionBaseURI(String collectionBaseURI) {
		this.collectionBaseURI = collectionBaseURI;
	}
}
