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
import java.util.Map;

import org.w3c.dom.Document;

import com.google.common.base.Joiner;

import eu.esdihumboldt.hale.io.geoserver.DataStoreFile;

/**
 * Resource manager to manage datastore file resources.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class DataStoreFileManager extends AbstractResourceManager<DataStoreFile> {

	/**
	 * Constructor.
	 * 
	 * @param geoserverUrl the base GeoServer URL
	 * @throws MalformedURLException if the provided URL is invalid
	 */
	public DataStoreFileManager(String geoserverUrl) throws MalformedURLException {
		super(geoserverUrl);
	}

	/**
	 * Constructor.
	 * 
	 * @param geoserverUrl the base GeoServer URL
	 */
	public DataStoreFileManager(URL geoserverUrl) {
		super(geoserverUrl);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.AbstractResourceManager#list()
	 */
	@Override
	public Document list() {
		throw new UnsupportedOperationException(
				"list() operation not supported for resource type: "
						+ DataStoreFile.class.getName());
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.AbstractResourceManager#read(java.util.Map)
	 */
	@Override
	public Document read(Map<String, String> parameters) {
		throw new UnsupportedOperationException(
				"read() operation not supported for resource type: "
						+ DataStoreFile.class.getName());
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.AbstractResourceManager#getFormat()
	 */
	@Override
	protected String getFormat() {
		String extension = (String) resource.getAttribute(DataStoreFile.EXTENSION);
		return (extension != null) ? extension : super.getFormat();
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.AbstractResourceManager#getResourceListPath()
	 */
	@Override
	protected String getResourceListPath() {
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.AbstractResourceManager#getResourcePath()
	 */
	@Override
	protected String getResourcePath() {
		String ws = (String) resource.getAttribute(DataStoreFile.WORKSPACE);
		String ds = (String) resource.getAttribute(DataStoreFile.DATASTORE);

		return Joiner.on('/').join(Arrays.asList("workspaces", ws, "datastores", ds, "file"));
	}

}
