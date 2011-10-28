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

/**
 * Service that manages entity definitions and the associated instance contexts
 * @author Simon Templer
 * @since 2.5
 */
public interface EntityDefinitionService {
	
	/**
	 * Get the children of the given entity definition, i.e. its properties
	 * and groups for each of the corresponding instance contexts.
	 * @param entity the entity definition
	 * @return the collection of child entity definitions
	 */
	public Collection<? extends EntityDefinition> getChildren(
			EntityDefinition entity);
	
	/**
	 * Get the parent entity definition for the given entity definition.
	 * @param entity the entity definition
	 * @return the parent entity definition or <code>null</code> if the given
	 *   entity definition represents a type
	 */
	public EntityDefinition getParent(EntityDefinition entity);
	
//	public EntityDefinition addContext(EntityDefinition parent, 
//			QName contextChildName);
//
//	public void removeContext(EntityDefinition entity);
	
}
