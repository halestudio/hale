/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry;

import javax.xml.namespace.QName;

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

}