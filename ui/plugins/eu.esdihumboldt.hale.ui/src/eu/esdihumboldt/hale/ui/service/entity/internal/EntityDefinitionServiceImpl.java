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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.ChildEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Manages instance contexts and the corresponding entity definitions.
 * @author Simon Templer
 * @since 2.5
 */
public class EntityDefinitionServiceImpl implements EntityDefinitionService {
	
	//XXX information if source or target schema needed?!
	//FIXME problems with overlapping names?!
	
	/**
	 * Stores additional instance contexts. The key is the corresponding entity
	 * definition w/ the default context.
	 * XXX use entity definitions as values instead?
	 */
	private final Multimap<EntityDefinition, Integer> additionalContexts = HashMultimap.create();

	/**
	 * @see EntityDefinitionService#getChildren(EntityDefinition)
	 */
	@Override
	public Collection<? extends EntityDefinition> getChildren(
			EntityDefinition entity) {
		List<ChildContext> path = entity.getPropertyPath();
		
		Collection<? extends ChildDefinition<?>> children;
		
		if (path == null || path.isEmpty()) {
			// entity is a type, children are the type children
			children = entity.getType().getChildren();
		}
		else {
			// get parent context
			ChildContext parentContext = path.get(path.size() - 1);
			if (parentContext.getChild().asGroup() != null) {
				children = parentContext.getChild().asGroup().getDeclaredChildren();
			}
			else if (parentContext.getChild().asProperty() != null) {
				children = parentContext.getChild().asProperty().getPropertyType().getChildren();
			}
			else {
				throw new IllegalStateException("Illegal child definition type encountered");
			}
		}
		
		if (children == null || children.isEmpty()) {
			return Collections.emptyList();
		}
		
		Collection<EntityDefinition> result = new ArrayList<EntityDefinition>(children.size());
		for (ChildDefinition<?> child : children) {
			// add default child entity definition to result
			ChildContext context = new ChildContext(child);
			EntityDefinition defaultEntity = createEntity(entity.getType(), 
					createPath(entity.getPropertyPath(), context));
			result.add(defaultEntity);
			// look up additional instance contexts and add them
			for (Integer contextName : additionalContexts.get(defaultEntity)) {
				ChildContext namedContext = new ChildContext(contextName, child);
				EntityDefinition namedChild = createEntity(entity.getType(), 
						createPath(entity.getPropertyPath(), namedContext));
				result.add(namedChild);
			}
		}
		
		return result;
	}

	/**
	 * Create a property path
	 * @param parentPath the parent path
	 * @param context the child context
	 * @return the property path including the child context
	 */
	private List<ChildContext> createPath(List<ChildContext> parentPath,
			ChildContext context) {
		if (parentPath == null || parentPath.isEmpty()) {
			return Collections.singletonList(context);
		}
		else {
			List<ChildContext> result = new ArrayList<ChildContext>(parentPath);
			result.add(context);
			return result;
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
	private static EntityDefinition createEntity(TypeDefinition type, 
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
