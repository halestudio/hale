/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.geoserver.rest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import com.google.common.base.Joiner;

import eu.esdihumboldt.hale.io.geoserver.Namespace;

/**
 * Resource manager to manage datastore resources.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class NamespaceManager extends AbstractResourceManager<Namespace> {

	/**
	 * Constructor.
	 * 
	 * @param geoserverUrl the base GeoServer URL
	 * @throws MalformedURLException if the provided URL is invalid
	 */
	public NamespaceManager(String geoserverUrl) throws MalformedURLException {
		super(geoserverUrl);
	}

	/**
	 * Constructor.
	 * 
	 * @param geoserverUrl the base GeoServer URL
	 */
	public NamespaceManager(URL geoserverUrl) {
		super(geoserverUrl);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.AbstractResourceManager#getResourceListPath()
	 */
	@Override
	protected String getResourceListPath() {
		return "namespaces";
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.AbstractResourceManager#getResourcePath()
	 */
	@Override
	protected String getResourcePath() {
		return Joiner.on('/').join(Arrays.asList(getResourceListPath(), resource.name()));
	}

}
