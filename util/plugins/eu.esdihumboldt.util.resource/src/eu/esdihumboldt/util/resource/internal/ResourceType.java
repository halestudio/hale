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

package eu.esdihumboldt.util.resource.internal;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;

/**
 * Represents a resource type.
 * 
 * @author Simon Templer
 */
public class ResourceType implements Identifiable {

	private final String id;

	private final String name;

	/**
	 * @param id the resource type ID
	 * @param name the resource type name, may be <code>null</code>
	 */
	public ResourceType(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	/**
	 * @see Identifiable#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * Get the resource type name. If no name was explicitly specified the name
	 * is equal to the resource type ID.
	 * 
	 * @return the resource type name
	 */
	public String getName() {
		return (name != null) ? (name) : (id);
	}

}
