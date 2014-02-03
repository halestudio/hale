/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.schema.persist.json

import javax.xml.namespace.QName

import eu.esdihumboldt.util.groovy.json.JsonStreamBuilder


/**
 * Represents an element in a tree node, e.g. types in a schema, properties in a type.
 * 
 * @author Simon Templer
 */
class Element {

	/**
	 * The element label.
	 */
	String label

	/**
	 * The element name.
	 */
	QName name

	/**
	 * The name of the element type, e.g. the property type if the element represents a property.
	 */
	QName typeName

	/**
	 * The element description.
	 */
	String description

	void toJson(JsonStreamBuilder json) {
		json.'elements[]' {
			elemLabel label
			elemQName name as String
			if (typeName) {
				elemTypeDef typeName as String
			}
			if (description) {
				elemDescription description
			}
		}
	}
}
