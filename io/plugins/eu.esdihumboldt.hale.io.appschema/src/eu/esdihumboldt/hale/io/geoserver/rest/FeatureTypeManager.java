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

import eu.esdihumboldt.hale.io.geoserver.FeatureType;

/**
 * Resource manager to manage feature type resources.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class FeatureTypeManager extends AbstractResourceManager<FeatureType> {

	private String workspace;
	private String dataStore;

	/**
	 * Constructor.
	 * 
	 * @param geoserverUrl the base GeoServer URL
	 * @throws MalformedURLException if the provided URL is invalid
	 */
	public FeatureTypeManager(String geoserverUrl) throws MalformedURLException {
		super(geoserverUrl);
	}

	/**
	 * Constructor.
	 * 
	 * @param geoserverUrl the base GeoServer URL
	 */
	public FeatureTypeManager(URL geoserverUrl) {
		super(geoserverUrl);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.AbstractResourceManager#getResourceListPath()
	 */
	@Override
	protected String getResourceListPath() {
		checkWorkspaceSet();
		checkDataStoreSet();

		return Joiner.on('/').join(
				Arrays.asList("workspaces", workspace, "datastores", dataStore, "featuretypes"));
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.rest.AbstractResourceManager#getResourcePath()
	 */
	@Override
	protected String getResourcePath() {
		checkWorkspaceSet();
		checkDataStoreSet();

		return Joiner.on('/').join(Arrays.asList(getResourceListPath(), resource.name()));
	}

	private void checkWorkspaceSet() {
		if (workspace == null || workspace.isEmpty()) {
			throw new IllegalStateException("Workspace not set");
		}
	}

	private void checkDataStoreSet() {
		if (dataStore == null || dataStore.isEmpty()) {
			throw new IllegalStateException("Data Store not set");
		}
	}

	/**
	 * Retrieve the name of the workspace to which the feature type resources
	 * managed by this manager belong.
	 * 
	 * @return the workspace name
	 */
	public String getWorkspace() {
		return workspace;
	}

	/**
	 * Set the name of the workspace to which the feature type resources managed
	 * by this manager belong.
	 * 
	 * @param workspace the workspace name
	 */
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	/**
	 * Retrieve the name of the data store to which the feature type resources
	 * managed by this manager belong.
	 * 
	 * @return the data store name
	 */
	public String getDataStore() {
		return dataStore;
	}

	/**
	 * Set the name of the data store to which the feature type resources
	 * managed by this manager belong.
	 * 
	 * @param dataStore the data store name
	 */
	public void setDataStore(String dataStore) {
		this.dataStore = dataStore;
	}

}
