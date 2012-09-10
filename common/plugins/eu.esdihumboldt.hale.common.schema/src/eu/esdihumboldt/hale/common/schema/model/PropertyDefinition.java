/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.schema.model;

/**
 * Represents a property definition
 * 
 * @author Simon Templer
 */
public interface PropertyDefinition extends ChildDefinition<PropertyConstraint> {

	/**
	 * Get the type associated with the property
	 * 
	 * @return the property type
	 */
	public TypeDefinition getPropertyType();

}
