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

package eu.esdihumboldt.hale.common.align.model;

import java.util.List;

import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * Definition of an entity
 * @author Simon Templer
 */
public interface EntityDefinition {
	
	/**
	 * Get the definition of the type or property represented by the entity
	 * definition.
	 * @return the definition of the type or property
	 */
	public Definition<?> getDefinition();
	
	/**
	 * Get the definition path. For a type this only consists of the type 
	 * definition, for a property the path ends with the property definition,
	 * the first element in the path is always a type definition. 
	 * @return the definition path
	 */
	public List<? extends Definition<?>> getPath();

}
