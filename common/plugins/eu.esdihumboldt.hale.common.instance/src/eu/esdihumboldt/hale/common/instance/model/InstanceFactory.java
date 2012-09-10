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

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Factory for creating new instances
 * 
 * @author Simon Templer
 * @since 2.5
 */
public interface InstanceFactory {

	/**
	 * Create an empty instance of the given type
	 * 
	 * @param type the type of the instance to create
	 * @return the created instance
	 */
	public MutableInstance createInstance(TypeDefinition type);

}
