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

package eu.esdihumboldt.hale.ui.service.entity;

import java.util.Collection;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Service that manages entity definitions and the associated instance contexts
 * 
 * @author Simon Templer
 * @since 2.5
 */
public interface EntityDefinitionService {

	/**
	 * Get the children of the given entity definition, i.e. its properties and
	 * groups for each of the corresponding instance contexts.
	 * 
	 * @param entity the entity definition
	 * @return the collection of child entity definitions
	 */
	public Collection<? extends EntityDefinition> getChildren(EntityDefinition entity);

	/**
	 * Get all available entity definitions for a given type.
	 * 
	 * @param type the type definition
	 * @param schemaSpace the type schema space
	 * @return the type entities
	 */
	public Collection<? extends TypeEntityDefinition> getTypeEntities(TypeDefinition type,
			SchemaSpaceID schemaSpace);

	/**
	 * Get the parent entity definition for the given entity definition.
	 * 
	 * @param entity the entity definition
	 * @return the parent entity definition or <code>null</code> if the given
	 *         entity definition represents a type
	 */
	public EntityDefinition getParent(EntityDefinition entity);

	/**
	 * Add a new named instance context for the given entity definition and
	 * create a new sibling associated to the new context.
	 * 
	 * @param sibling the entity definition which is a sibling of the entity
	 *            definition to create
	 * @return the entity definition associated to the new instance context
	 */
	public EntityDefinition addNamedContext(EntityDefinition sibling);

	/**
	 * Add a new index context for the given entity definition and create a new
	 * sibling associated to the new context.
	 * 
	 * @param sibling the entity definition which is a sibling of the entity
	 *            definition to create
	 * @param index the property index associated to the context, if
	 *            <code>null</code> will be determined automatically
	 * @return the entity definition associated to the index context
	 */
	public EntityDefinition addIndexContext(EntityDefinition sibling, Integer index);

	/**
	 * Add a new condition context for the given entity definition and create a
	 * new sibling associated to the new context.
	 * 
	 * @param sibling the entity definition which is a sibling of the entity
	 *            definition to create
	 * @param filter the condition filter
	 * @return the entity definition associated to the index context
	 */
	public EntityDefinition addConditionContext(EntityDefinition sibling, Filter filter);

	/**
	 * Remove the instance context associated with the given entity definition
	 * (if possible).
	 * 
	 * @param entity the entity definition or <code>null</code> if creating an
	 *            instance context is not possible FIXME report success/failure?
	 */
	public void removeContext(EntityDefinition entity);

	/**
	 * Adds a listener to the service
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(EntityDefinitionServiceListener listener);

	/**
	 * Removes a listener from the service
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(EntityDefinitionServiceListener listener);

}
