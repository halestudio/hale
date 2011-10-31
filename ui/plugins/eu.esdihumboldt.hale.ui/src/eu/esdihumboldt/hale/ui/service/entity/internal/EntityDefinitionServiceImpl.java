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
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Manages instance contexts and the corresponding entity definitions.
 * @author Simon Templer
 * @since 2.5
 */
public class EntityDefinitionServiceImpl extends AbstractEntityDefinitionService {
	
	//XXX information if source or target schema needed?!
	//FIXME problems with overlapping names?!
	
	/**
	 * Stores additional instance contexts. The key is the corresponding entity
	 * definition w/ the default context.
	 * XXX use entity definitions as values instead?
	 */
	private final Multimap<EntityDefinition, Integer> additionalContexts = HashMultimap.create();

	/**
	 * @see EntityDefinitionService#addContext(EntityDefinition)
	 */
	@Override
	public synchronized EntityDefinition addContext(EntityDefinition sibling) {
		List<ChildContext> path = sibling.getPropertyPath();
		if (path.isEmpty()) {
			return null;
		}
		
		//XXX any checks? see InstanceContextTester
		
		EntityDefinition def = getDefaultEntity(sibling);
		Integer newName;
		
		// get registered context names
		Collection<Integer> names = additionalContexts.get(def);
		if (names == null || names.isEmpty()) {
			newName = Integer.valueOf(0);
		}
		else {
			// get the maximum value available as a name
			SortedSet<Integer> sortedNames = new TreeSet<Integer>(names);
			int max = sortedNames.last();
			// and use its value increased by one
			newName = Integer.valueOf(max + 1);
		}
		
		additionalContexts.put(def, newName);
		
		List<ChildContext> newPath = new ArrayList<ChildContext>(path);
		ChildDefinition<?> lastChild = newPath.get(newPath.size() - 1).getChild();
		newPath.remove(path.size() - 1);
		newPath.add(new ChildContext(newName, lastChild));
		EntityDefinition result = createEntity(def.getType(), newPath);
		
		notifyContextAdded(result);
		
		return result ;
	}

	/**
	 * @see EntityDefinitionService#removeContext(EntityDefinition)
	 */
	@Override
	public synchronized void removeContext(EntityDefinition entity) {
		List<ChildContext> path = entity.getPropertyPath();
		if (path.isEmpty()) {
			return;
		}
		
		ChildContext lastContext = path.get(path.size() - 1);
		if (lastContext.getContextName() == null) {
			return;
		}
		
		//XXX any checks? Alignment must still be valid! see also InstanceContextTester
		
		EntityDefinition def = getDefaultEntity(entity);
		additionalContexts.remove(def, lastContext.getContextName());
		
		notifyContextRemoved(entity);
	}

	/**
	 * @see EntityDefinitionService#getChildren(EntityDefinition)
	 */
	@Override
	public synchronized Collection<? extends EntityDefinition> getChildren(
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
	private static List<ChildContext> createPath(List<ChildContext> parentPath,
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
	 * Get the entity definition with the default instance context which
	 * is a sibling to (or the same as) the given entity definition.
	 * @param entity the entity definition
	 * @return the entity definition with the default context in the last
	 * path element
	 */
	private EntityDefinition getDefaultEntity(EntityDefinition entity) {
		List<ChildContext> path = entity.getPropertyPath();
		
		if (path == null || path.isEmpty() || path.get(path.size() - 1).getContextName() == null) {
			return entity;
		}
		
		List<ChildContext> newPath = new ArrayList<ChildContext>(path);
		ChildDefinition<?> lastChild = newPath.get(newPath.size() - 1).getChild();
		newPath.remove(newPath.size() - 1);
		newPath.add(new ChildContext(lastChild));
		return createEntity(entity.getType(), newPath);
	}

}
