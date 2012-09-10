/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.core.io.project.model;

import java.net.URI;

/**
 * Object holding information about a project file stored at a given URI
 * 
 * @author Simon Templer
 */
public class ProjectFileInfo {

	private String name;

	private URI location;

	/**
	 * Default constructor
	 */
	public ProjectFileInfo() {
		super();
	}

	/**
	 * Create a project file info
	 * 
	 * @param name the project file name
	 * @param location the project file location
	 */
	public ProjectFileInfo(String name, URI location) {
		super();
		this.name = name;
		this.location = location;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the location
	 */
	public URI getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(URI location) {
		this.location = location;
	}

}
