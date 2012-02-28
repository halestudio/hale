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

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
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
	 * Get the filter applied to the entity type.
	 * @return the entity filter, may be <code>null</code>
	 */
	public Filter getFilter();
	
	/**
	 * Get the property path. Each path item is an instance context name
	 * paired with a child definition. The default instance context name
	 * is <code>null</code>.
	 * @return the path down to the property represented by the entity,
	 *   an empty list if the entity represents a type
	 */
	public List<ChildContext> getPropertyPath();
	
	/**
	 * Get the schema space the entity definition is associated to.
	 * The schema space itself is no characteristic of the entity, but is needed
	 * as additional information to differentiate between source and target
	 * schema entities with the same names.
	 * @return the identifier of the entity definition's schema space
	 */
	public SchemaSpaceID getSchemaSpace();

}
