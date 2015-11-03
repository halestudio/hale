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

import java.net.URL;
import java.util.Map;

import org.w3c.dom.Document;

import eu.esdihumboldt.hale.io.geoserver.Resource;

/**
 * Interface defining the API for GeoServer REST resource managers.
 * 
 * @author Stefano Costa, GeoSolutions
 * @param <T> the type of the managed resource
 */
public interface ResourceManager<T extends Resource> {

	/**
	 * Set the credentials that will be used to authenticate with the GeoServer
	 * REST services.
	 * 
	 * @param user the user name to set
	 * @param password the password to set
	 */
	public void setCredentials(String user, String password);

	/**
	 * Issue a GET request to retrieve the list of available resources.
	 * 
	 * @return XML document containing the list of available resources
	 */
	public Document list();

	/**
	 * Set the resource that we want to manage.
	 * 
	 * @param resource the resource to manage
	 */
	public void setResource(T resource);

	/**
	 * @return <code>true</code> if the resource exists, <code>false</code>
	 *         otherwise
	 */
	public boolean exists();

	/**
	 * Issue a GET request to fetch the managed resource.
	 * 
	 * @return XML document containing the managed resource
	 */
	public Document read();

	/**
	 * Issue a GET request to fetch the managed resource.
	 * 
	 * @param parameters request parameters
	 * @return XML document containing the managed resource
	 */
	public Document read(Map<String, String> parameters);

	/**
	 * Issue a POST request to create a new resource.
	 * 
	 * @return the URL of the new resource
	 */
	public URL create();

	/**
	 * Issue a POST request to create a new resource.
	 * 
	 * @param parameters request parameters
	 * @return the URL of the new resource
	 */
	public URL create(Map<String, String> parameters);

	/**
	 * Issue PUT request to update the managed resource.
	 */
	public void update();

	/**
	 * Issue PUT request to update the managed resource.
	 * 
	 * @param parameters request parameters
	 */
	public void update(Map<String, String> parameters);

	/**
	 * Issue DELETE request to delete the managed resource.
	 */
	public void delete();

	/**
	 * Issue DELETE request to delete the managed resource.
	 * 
	 * @param parameters request parameters
	 */
	public void delete(Map<String, String> parameters);

}
