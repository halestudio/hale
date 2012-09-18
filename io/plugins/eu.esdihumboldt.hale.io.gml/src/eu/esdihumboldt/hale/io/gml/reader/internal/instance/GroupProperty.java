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

package eu.esdihumboldt.hale.io.gml.reader.internal.instance;

import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;

/**
 * Represents a property at the end of a group path
 * 
 * @author Simon Templer
 */
public class GroupProperty {

	private final PropertyDefinition property;

	private final GroupPath path;

	/**
	 * Create a group property
	 * 
	 * @param property the property definition
	 * @param path the group path
	 */
	public GroupProperty(PropertyDefinition property, GroupPath path) {
		super();
		this.property = property;
		this.path = path;
	}

	/**
	 * @return the property
	 */
	public PropertyDefinition getProperty() {
		return property;
	}

	/**
	 * @return the path
	 */
	public GroupPath getPath() {
		return path;
	}

}
