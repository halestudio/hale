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

package eu.esdihumboldt.hale.schemaprovider.provider.internal.apache;

import org.apache.ws.commons.schema.resolver.CollectionURIResolver;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.xml.sax.InputSource;

import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.schemaprovider.Messages;

/**
 * Decorator for URI resolvers that supports a {@link ProgressIndicator}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@Deprecated
public class ProgressURIResolver implements CollectionURIResolver {

	private final URIResolver decoratee;

	private final ProgressIndicator progress;

	private String collectionBaseURI;

	/**
	 * @param decoratee
	 * @param progress
	 */
	public ProgressURIResolver(URIResolver decoratee, ProgressIndicator progress) {
		super();
		this.decoratee = decoratee;
		this.progress = progress;
	}

	/**
	 * @see CollectionURIResolver#getCollectionBaseURI()
	 */
	@Override
	public String getCollectionBaseURI() {
		if (decoratee instanceof CollectionURIResolver) {
			return ((CollectionURIResolver) decoratee).getCollectionBaseURI();
		} else {
			return collectionBaseURI;
		}
	}

	/**
	 * @see CollectionURIResolver#setCollectionBaseURI(String)
	 */
	@Override
	public void setCollectionBaseURI(String uri) {
		if (decoratee instanceof CollectionURIResolver) {
			((CollectionURIResolver) decoratee).setCollectionBaseURI(uri);
		} else {
			collectionBaseURI = uri;
		}
	}

	/**
	 * @see URIResolver#resolveEntity(String, String, String)
	 */
	@Override
	public InputSource resolveEntity(String targetNamespace,
			String schemaLocation, String baseUri) {
		InputSource is = decoratee.resolveEntity(targetNamespace,
				schemaLocation, baseUri);

		String url = is.getSystemId();
		progress.setCurrentTask(Messages.getString("ProgressURIResolver.0") + ((url == null) ? (schemaLocation) : (url))); //$NON-NLS-1$

		return is;
	}

}
