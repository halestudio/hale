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

import java.util.ArrayList;
import java.util.List;

import de.cs3d.util.eclipse.TypeSafeListenerList;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.ChildEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionServiceListener;

/**
 * Abstract entity definition service implementation. Manages service listeners.
 * @author Simon Templer
 */
public abstract class AbstractEntityDefinitionService implements
		EntityDefinitionService {
	
	private final TypeSafeListenerList<EntityDefinitionServiceListener> listeners = new TypeSafeListenerList<EntityDefinitionServiceListener>();

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
	 * @param contextEntity the entity definition representing the instance
	 *   context
	 */
	public void notifyContextAdded(EntityDefinition contextEntity) {
		for (EntityDefinitionServiceListener listener : listeners) {
			listener.contextAdded(contextEntity);
		}
	}
	
	/**
	 * Called when an instance context has been removed.
	 * @param contextEntity the entity definition representing the instance
	 *   context
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
		List<ChildContext> path = entity.getPropertyPath();
		
		if (path == null || path.isEmpty()) {
			// entity is a type and has no parent
			return null;
		}
		else {
			List<ChildContext> newPath = new ArrayList<ChildContext>(path);
			newPath.remove(newPath.size() - 1);
			return createEntity(entity.getType(), newPath);
		}
	}

	/**
	 * Create an entity definition from a type and a child path
	 * @param type the path parent
	 * @param path the child path 
	 * @return the created entity definition
	 */
	protected EntityDefinition createEntity(TypeDefinition type, 
			List<ChildContext> path) {
		if (path == null || path.isEmpty()) {
			// entity is a type
			return new TypeEntityDefinition(type);
		}
		else if (path.get(path.size() - 1).getChild() instanceof PropertyDefinition) {
			// last element in path is a property
			return new PropertyEntityDefinition(type, path);
		}
		else {
			// last element is a child but no property
			return new ChildEntityDefinition(type, path);
		}
	}

}
