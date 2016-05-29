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

import eu.esdihumboldt.hale.common.cache.Request;
import eu.esdihumboldt.util.io.InputSupplier;
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
		final URI uriLoc;
		final String stringLoc;
		if (baseUri != null) {
			try {
				if (baseUri.startsWith("jar:file:")) {
					// scheme definition w/ colon seems to break resolving
					// -> resolve against file URI and add Jar part later
					baseUri = baseUri.substring(4);
					uriLoc = new URI(baseUri).resolve(new URI(schemaLocation));
					stringLoc = "jar:" + uriLoc.toString();
				}
				else {
					// XXX don't really understand what this File/URI juggling
					// is supposed to to

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

					uriLoc = new URI(baseUri).resolve(new URI(schemaLocation));
					stringLoc = uriLoc.toString();
				}
			} catch (URISyntaxException e1) {
				throw new RuntimeException(e1);
			}
		}
		else {
			stringLoc = schemaLocation;
			URI uri;
			try {
				uri = new URI(schemaLocation);
			} catch (URISyntaxException e) {
				uri = null;
			}
			uriLoc = uri;
		}

		// create a lazy input source because we cannot be sure that the
		// stream is actually consumed (and if not consumed we get a
		// problem because the connection is not released)
		InputSource is = new InputSource(stringLoc) {

			private boolean initializedStream = false;

			@Override
			public InputStream getByteStream() {
				if (!initializedStream) {
					initializedStream = true;
					InputStream in = null;

					// try resolving using (local) Resources
					if (uriLoc != null) {
						InputSupplier<? extends InputStream> input = Resources.tryResolve(uriLoc,
								Resources.RESOURCE_TYPE_XML_SCHEMA);
						if (input != null) {
							try {
								in = input.getInput();
							} catch (Exception e) {
								// ignore
							}
						}
					}

					// try resolving through cache
					if (in == null) {
						try {
							in = Request.getInstance().get(getSystemId());
						} catch (Exception e) {
							// ignore
						}
					}

					// fall-back
					if (in == null && uriLoc != null) {
						try {
							in = uriLoc.toURL().openStream();
						} catch (Exception e) {
							// ignore
						}
					}

					setByteStream(in);
				}

				return super.getByteStream();
			}

		};

		return is;
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
