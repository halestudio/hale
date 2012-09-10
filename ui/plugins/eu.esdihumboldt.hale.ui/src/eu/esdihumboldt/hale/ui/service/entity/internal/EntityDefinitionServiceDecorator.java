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

package eu.esdihumboldt.hale.ui.service.entity.internal;

import java.util.Collection;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionServiceListener;

/**
 * Entity definition service decorator.
 * 
 * @author Kai Schwierczek
 */
public class EntityDefinitionServiceDecorator implements EntityDefinitionService {

	/**
	 * The decorated entity definition service.
	 */
	protected EntityDefinitionService entityDefinitionService;

	/**
	 * Create a decorator for the entity definition service.
	 * 
	 * @param entityDefinitionService the entity definition service
	 */
	public EntityDefinitionServiceDecorator(EntityDefinitionService entityDefinitionService) {
		this.entityDefinitionService = entityDefinitionService;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService#getChildren(eu.esdihumboldt.hale.common.align.model.EntityDefinition)
	 */
	@Override
	public Collection<? extends EntityDefinition> getChildren(EntityDefinition entity) {
		return entityDefinitionService.getChildren(entity);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService#getTypeEntities(eu.esdihumboldt.hale.common.schema.model.TypeDefinition,
	 *      eu.esdihumboldt.hale.common.schema.SchemaSpaceID)
	 */
	@Override
	public Collection<? extends TypeEntityDefinition> getTypeEntities(TypeDefinition type,
			SchemaSpaceID schemaSpace) {
		return entityDefinitionService.getTypeEntities(type, schemaSpace);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService#getParent(eu.esdihumboldt.hale.common.align.model.EntityDefinition)
	 */
	@Override
	public EntityDefinition getParent(EntityDefinition entity) {
		return entityDefinitionService.getParent(entity);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService#addNamedContext(eu.esdihumboldt.hale.common.align.model.EntityDefinition)
	 */
	@Override
	public EntityDefinition addNamedContext(EntityDefinition sibling) {
		return entityDefinitionService.addNamedContext(sibling);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService#addIndexContext(eu.esdihumboldt.hale.common.align.model.EntityDefinition,
	 *      java.lang.Integer)
	 */
	@Override
	public EntityDefinition addIndexContext(EntityDefinition sibling, Integer index) {
		return entityDefinitionService.addIndexContext(sibling, index);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService#addConditionContext(eu.esdihumboldt.hale.common.align.model.EntityDefinition,
	 *      eu.esdihumboldt.hale.common.instance.model.Filter)
	 */
	@Override
	public EntityDefinition addConditionContext(EntityDefinition sibling, Filter filter) {
		return entityDefinitionService.addConditionContext(sibling, filter);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService#removeContext(eu.esdihumboldt.hale.common.align.model.EntityDefinition)
	 */
	@Override
	public void removeContext(EntityDefinition entity) {
		entityDefinitionService.removeContext(entity);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService#addListener(eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionServiceListener)
	 */
	@Override
	public void addListener(EntityDefinitionServiceListener listener) {
		entityDefinitionService.addListener(listener);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService#removeListener(eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionServiceListener)
	 */
	@Override
	public void removeListener(EntityDefinitionServiceListener listener) {
		entityDefinitionService.removeListener(listener);
	}
}
