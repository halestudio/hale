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

package eu.esdihumboldt.hale.io.csv;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Type class for the property types
 * 
 * @author Kevin Mais
 */
public interface PropertyType {

	/**
	 * Converts the value into the property type of the class
	 * 
	 * @param value the value to convert
	 * @return the converted value
	 */
	public Object convertFromField(String value);

	/**
	 * Getter for the type definition
	 * 
	 * @return the TypeDefinition of the property type
	 */
	public TypeDefinition getTypeDefinition();

}
