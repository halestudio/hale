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
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Definition of an entity. Represents either a type or a property.
 * @author Simon Templer
 * @since 2.5
 */
public interface EntityDefinition {
	
	/**
	 * Get the definition of the type or property represented by the entity
	 * definition.
	 * @return the definition of the type or property
	 */
	public Definition<?> getDefinition();
	
	/**
	 * Get the type definition that is associated with the entity. This is
	 * either the type represented by the entity or the topmost parent to the
	 * property represented by the entity.
	 * @return the type definition
	 */
	public TypeDefinition getType();
	
	/**
	 * Get the property path. Each path item is an instance context name
	 * paired with a child definition. The default instance context name
	 * is <code>null</code>.
	 * @return the path down to the property represented by the entity,
	 *   an empty list if the entity represents a type
	 */
	public List<ChildContext> getPropertyPath();

}
