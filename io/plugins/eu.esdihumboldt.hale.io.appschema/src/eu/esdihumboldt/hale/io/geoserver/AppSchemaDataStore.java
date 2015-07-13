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

/**
 * Class representing an app-schema datastore resource.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class AppSchemaDataStore extends DataStore {

	/**
	 * Constructor.
	 * 
	 * @param name the datastore name
	 */
	public AppSchemaDataStore(String name) {
		super(name);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.geoserver.AbstractResource#templateLocation()
	 */
	@Override
	protected String templateLocation() {
		return "/eu/esdihumboldt/hale/io/geoserver/template/data/datastore-appschema-template.vm";
	}

}
