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

package eu.esdihumboldt.hale.io.oml.internal.goml.rdf;

import eu.esdihumboldt.hale.io.oml.internal.model.rdf.IResource;

/**
 * Simple implementation of {@link IResource}.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("javadoc")
public class Resource implements IResource {

	private String location;

	// constructors ............................................................

	/**
	 * @param location
	 */
	public Resource(String location) {
		super();
		this.location = location;
	}

	// getters / setters .......................................................

	@Override
	public String toString() {
		return this.location.toString();
	}

	/**
	 * @return the location
	 */
	@Override
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

}
