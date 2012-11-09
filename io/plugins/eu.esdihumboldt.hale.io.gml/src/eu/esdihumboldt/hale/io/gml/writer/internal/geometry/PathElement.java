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

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Path element in a definition path
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface PathElement {

	/**
	 * Get the path element name. This is either a property, group or sub-type
	 * name.
	 * 
	 * @return the element name
	 */
	public abstract QName getName();

	/**
	 * Get the path element type definition.
	 * 
	 * @return the path element type definition, , may be <code>null</code> if
	 *         the element is transient
	 */
	public abstract TypeDefinition getType();

	/**
	 * Determines if this path element represents a property, otherwise it
	 * represents a sub-type or a group.
	 * 
	 * @return if this path element represents a property
	 */
	public abstract boolean isProperty();

	/**
	 * Determines if the the path element is transient and thus doesn't
	 * represent an element.
	 * 
	 * @return if the element is transient
	 */
	public boolean isTransient();

	/**
	 * Determines if this path element represents a type downcast. This means
	 * xsi:type has to be used when writing this element.
	 * 
	 * @return if this path element represents a type downcast
	 */
	public boolean isDowncast();

	/**
	 * Determines if this path element represents an element that can't be
	 * repeated.
	 * 
	 * @return if this path element represents an element that can't be repeated
	 */
	public boolean isUnique();

	/**
	 * Write additional attributes/elements after the path element has been
	 * started.
	 * 
	 * @param writer the XML stream writer
	 * @throws XMLStreamException if an error occurs writing XML to the stream
	 */
	public abstract void prepareWrite(XMLStreamWriter writer) throws XMLStreamException;

}
