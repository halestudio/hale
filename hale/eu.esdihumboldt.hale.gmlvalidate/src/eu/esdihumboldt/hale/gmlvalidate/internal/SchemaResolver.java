/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.gmlvalidate.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import eu.esdihumboldt.hale.cache.Request;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * Resolve imported/included schemas
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
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
	 * @see LSResourceResolver#resolveResource(String, String, String, String, String)
	 */
	@Override
	public LSInput resolveResource(String type, String namespaceURI,
			String publicId, String systemId, String baseURI) {
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

		LSInput lsin = new LSInputImpl();
		URI uri;
		try {
			uri = new URI(systemId);
			uri = uri.normalize();
		} catch (URISyntaxException e1) {
			return null;
		}

		InputStream inputStream;
		try {
//			inputStream = uri.toURL().openStream();
			inputStream = Request.getInstance().get(uri);
		} catch (Exception e) {
			return null;
		}

		lsin.setSystemId(uri.toString());
		lsin.setByteStream(inputStream);
		return lsin;
	}
}
