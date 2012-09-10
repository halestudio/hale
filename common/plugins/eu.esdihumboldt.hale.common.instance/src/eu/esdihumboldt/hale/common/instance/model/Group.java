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