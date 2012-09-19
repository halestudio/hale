/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.service.entity.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionServiceListener;

/**
 * Abstract entity definition service implementation. Manages service listeners.
 * 
 * @author Simon Templer
 */
public abstract class AbstractEntityDefinitionService implements EntityDefinitionService {

	private final CopyOnWriteArraySet<EntityDefinitionServiceListener> listeners = new CopyOnWriteArraySet<EntityDefinitionServiceListener>();

	/**
	 * @see EntityDefinitionService#addListener(EntityDefinitionServiceListener)
	 */
	@Override
	public void addListener(EntityDefinitionServiceListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see EntityDefinitionService#removeListener(EntityDefinitionServiceListener)
	 */
	@Override
	public void removeListener(EntityDefinitionServiceListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Called when a new instance context has been added.
	 * 
	 * @param contextEntity the entity definition representing the instance
	 *            context
	 */
	public void notifyContextAdded(EntityDefinition contextEntity) {
		for (EntityDefinitionServiceListener listener : listeners) {
			listener.contextAdded(contextEntity);
		}
	}

	/**
	 * Called when multiple new instance contexts have been added.
	 * 
	 * @param contextEntities the entity definitions representing the instance
	 *            contexts
	 */
	public void notifyContextsAdded(Iterable<EntityDefinition> contextEntities) {
		for (EntityDefinitionServiceListener listener : listeners) {
			listener.contextsAdded(contextEntities);
		}
	}

	/**
	 * Called when an instance context has been removed.
	 * 
	 * @param contextEntity the entity definition representing the instance
	 *            context
	 */
	public void notifyContextRemoved(EntityDefinition contextEntity) {
		for (EntityDefinitionServiceListener listener : listeners) {
			listener.contextRemoved(contextEntity);
		}
	}

	/**
	 * @see EntityDefinitionService#getParent(EntityDefinition)
	 */
	@Override
	public EntityDefinition getParent(EntityDefinition entity) {
		return AlignmentUtil.getParent(entity);
	}

	/**
	 * Create an entity definition from a type and a child path
	 * 
	 * @param type the path parent
	 * @param path the child path
	 * @param schemaSpace the associated schema space
	 * @param filter the type entity filter
	 * @return the created entity definition
	 */
	protected EntityDefinition createEntity(TypeDefinition type, List<ChildContext> path,
			SchemaSpaceID schemaSpace, Filter filter) {
		return AlignmentUtil.createEntity(type, path, schemaSpace, filter);
	}

}
