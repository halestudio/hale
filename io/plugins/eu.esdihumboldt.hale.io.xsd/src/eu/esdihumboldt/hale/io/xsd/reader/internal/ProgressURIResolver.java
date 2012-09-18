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

import org.apache.ws.commons.schema.resolver.CollectionURIResolver;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.xml.sax.InputSource;

import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.io.xsd.internal.Messages;

/**
 * Decorator for URI resolvers that supports a {@link ProgressIndicator}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ProgressURIResolver implements CollectionURIResolver {

	private final URIResolver decoratee;

	private final ProgressIndicator progress;

	private String collectionBaseURI;

	/**
	 * Create an URI resolver that triggers the given progress indicator
	 * 
	 * @param decoratee the internal URI resolver
	 * @param progress the progress indicator
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
		}
		else {
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
		}
		else {
			collectionBaseURI = uri;
		}
	}

	/**
	 * @see URIResolver#resolveEntity(String, String, String)
	 */
	@Override
	public InputSource resolveEntity(String targetNamespace, String schemaLocation, String baseUri) {
		InputSource is = decoratee.resolveEntity(targetNamespace, schemaLocation, baseUri);

		String url = is.getSystemId();
		progress.setCurrentTask(Messages.getString("ProgressURIResolver.0") + ((url == null) ? (schemaLocation) : (url))); //$NON-NLS-1$

		return is;
	}

}
