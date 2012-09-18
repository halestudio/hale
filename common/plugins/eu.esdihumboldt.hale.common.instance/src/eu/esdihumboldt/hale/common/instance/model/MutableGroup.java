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

/**
 * A mutable group that allows adding/changing properties
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface MutableGroup extends Group {

	/**
	 * Adds a property value
	 * 
	 * @param propertyName the property name
	 * @param value the property value
	 */
	public void addProperty(QName propertyName, Object value);

	/**
	 * Sets values for a property
	 * 
	 * @param propertyName the property name
	 * @param values the values for the property
	 */
	public void setProperty(QName propertyName, Object... values);

	// XXX more manipulation needed? e.g. for transformation?

}
