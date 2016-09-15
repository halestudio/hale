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

package eu.esdihumboldt.hale.io.geoserver;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstract class representing a generic datastore resource.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public abstract class DataStore extends AbstractResource {

	/**
	 * "Datastore ID" attribute.
	 */
	public static final String ID = "dataStoreId";
	/**
	 * "Datastore name" attribute.
	 */
	public static final String NAME = "dataStoreName";
	/**
	 * "Workspace ID" attribute.
	 */
	public static final String WORKSPACE_ID = "workspaceId";
	/**
	 * "Connection parameters" attribute.
	 */
	public static final String CONNECTION_PARAMS = "connectionParameters";

	private static final String TEMPLATE_LOCATION = "/eu/esdihumboldt/hale/io/geoserver/template/data/datastore-template.vm";

	private static final Set<String> allowedAttributes = new HashSet<String>();

	static {
		allowedAttributes.add(ID);
		allowedAttributes.add(NAME);
		allowedAttributes.add(WORKSPACE_ID);
		allowedAttributes.add(CONNECTION_PARAMS);
	}

	/**
	 * Constructor.
	 * 
	 * @param name the datastore name
	 */
	public DataStore(String name) {
		setAttribute(DataStore.NAME, name);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.Resource#name()
	 */
	@Override
	public String name() {
		return (String) getAttribute(NAME);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.AbstractResource#allowedAttributes()
	 */
	@Override
	protected Set<String> allowedAttributes() {
		return allowedAttributes;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.AbstractResource#templateLocation()
	 */
	@Override
	protected String templateLocation() {
		return TEMPLATE_LOCATION;
	}

	/**
	 * Returns the datastore connection parameters.
	 * 
	 * @return the connection parameters
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getConnectionParameters() {
		return (Map<String, String>) getAttribute(CONNECTION_PARAMS);
	}
}
