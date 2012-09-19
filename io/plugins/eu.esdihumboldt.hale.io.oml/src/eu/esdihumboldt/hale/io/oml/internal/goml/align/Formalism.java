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

package eu.esdihumboldt.hale.io.oml.internal.goml.align;

import java.net.URI;

import eu.esdihumboldt.hale.io.oml.internal.model.align.IFormalism;

/**
 * A {@link Formalism} identifies the formal language or structure used to
 * express a {@link Schema}.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("javadoc")
public class Formalism implements IFormalism {

	/**
	 * The name of this {@link Formalism}.
	 */
	private String name;

	/**
	 * The {@link URI} identifying this {@link Formalism}.
	 */
	private URI location;

	// constructors ............................................................

	/**
	 * @param name
	 * @param location
	 */
	public Formalism(String name, URI location) {
		super();
		this.name = name;
		this.location = location;
	}

	// getters / setters .......................................................

	/**
	 * @return the name
	 */
	@Override
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
	@Override
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
