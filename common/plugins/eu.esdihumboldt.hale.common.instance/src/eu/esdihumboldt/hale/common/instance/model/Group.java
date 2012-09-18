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

package eu.esdihumboldt.hale.common.instance.model;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;

/**
 * Represents a group of properties
 * 
 * @author Simon Templer
 */
public interface Group {

	/**
	 * Get the values for the property with the given name. Values can also be
	 * groups or instances.
	 * 
	 * @param propertyName the property name
	 * @return the property values, may be <code>null</code> if none exist
	 */
	public Object[] getProperty(QName propertyName);

	/**
	 * Get the property names for which any values exist. Especially helpful
	 * when there is (for whatever reason) no type definition associated to the
	 * instance.
	 * 
	 * @return the list of property names with associated values
	 */
	public Iterable<QName> getPropertyNames();

	/**
	 * Get the group definition
	 * 
	 * @return the group definition
	 */
	public DefinitionGroup getDefinition();

}
