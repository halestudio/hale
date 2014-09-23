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

package eu.esdihumboldt.hale.common.core.io.project.model;

import java.net.URI;

/**
 * Object holding information about a project file stored at a given URI
 * 
 * @author Simon Templer
 */
public class ProjectFileInfo implements Cloneable {

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

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected ProjectFileInfo clone() {
		ProjectFileInfo copy = new ProjectFileInfo();

		copy.setLocation(getLocation());
		copy.setName(getName());

		return copy;
	}

}
