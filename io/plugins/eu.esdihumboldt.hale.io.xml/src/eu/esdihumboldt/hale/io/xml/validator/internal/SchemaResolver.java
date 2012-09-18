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

package eu.esdihumboldt.hale.io.xml.validator.internal;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import com.google.common.io.InputSupplier;

import eu.esdihumboldt.hale.common.cache.Request;
import eu.esdihumboldt.util.resource.Resources;

/**
 * Resolve imported/included schemas
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class SchemaResolver implements LSResourceResolver {

	private final URI mainSchemaURI;

	/**
	 * Constructor
	 * 
	 * @param mainSchemaURI the location of the main schema
	 */
	public SchemaResolver(URI mainSchemaURI) {
		super();
		this.mainSchemaURI = mainSchemaURI;
	}

	/**
	 * @see LSResourceResolver#resolveResource(String, String, String, String,
	 *      String)
	 */
	@Override
	public LSInput resolveResource(String type, String namespaceURI, String publicId,
			String systemId, String baseURI) {
		String schemaLocation;
		if (baseURI != null) {
			schemaLocation = baseURI.substring(0, baseURI.lastIndexOf("/") + 1); //$NON-NLS-1$
		}
		else {
			String loc = mainSchemaURI.toString();
			schemaLocation = loc.substring(0, loc.lastIndexOf("/") + 1); //$NON-NLS-1$
		}

		if (systemId.indexOf("http://") < 0) { //$NON-NLS-1$
			systemId = schemaLocation + systemId;
		}

		URI uri;
		try {
			uri = new URI(systemId);
			uri = uri.normalize();
		} catch (URISyntaxException e1) {
			return null;
		}

		InputStream inputStream = null;

		// try resolving using (local) Resources
		try {
			InputSupplier<? extends InputStream> input = Resources.tryResolve(uri,
					Resources.RESOURCE_TYPE_XML_SCHEMA);
			if (input != null) {
				inputStream = input.getInput();
			}
		} catch (Throwable e) {
			// ignore
		}

		// try resolving using cache
		if (inputStream == null) {
			try {
				inputStream = Request.getInstance().get(uri);
			} catch (Throwable e) {
				// ignore
			}
		}

		// fall-back
		if (inputStream == null) {
			try {
				inputStream = uri.toURL().openStream();
			} catch (Throwable e) {
				return null;
			}
		}

		LSInput lsin = new LSInputImpl();
		lsin.setSystemId(uri.toString());
		lsin.setByteStream(inputStream);
		return lsin;
	}
}
