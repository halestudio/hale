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
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter;

/**
 * Manages instance contexts and the corresponding entity definitions.
 * @author Simon Templer
 * @since 2.5
 */
public class EntityDefinitionServiceImpl extends AbstractEntityDefinitionService {
	
	/**
	 * Stores additional instance contexts. The key is the corresponding entity
	 * definition w/ the default context.
	 * XXX use entity definitions as values instead?
	 */
	private final SetMultimap<EntityDefinition, Integer> additionalContexts = HashMultimap.create();
	
	/**
	 * Create the entity definition service
	 * @param alignmentService the alignment service
	 * @param projectService the project service
	 */
	public EntityDefinitionServiceImpl(AlignmentService alignmentService,
			ProjectService projectService) {
		super();
		
		alignmentService.addListener(new AlignmentServiceListener() {
			
			@Override
			public void cellReplaced(Cell oldCell, Cell newCell) {
				addMissingContexts(Collections.singleton(newCell));
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				addMissingContexts(cells);
			}
			
			@Override
			public void cellRemoved(Cell cell) {
				//XXX do anything?
			}
			
			@Override
			public void alignmentCleared() {
				//XXX remove all created contexts?
			}
		});
		
		projectService.addListener(new ProjectServiceAdapter() {

			@Override
			public void onClean() {
				// remove all context definitions
				clean();
			}
			
		});
		
		//TODO remove contexts when schema doesn't contain corresponding definition?!
	}
	
	/**
	 * Remove all defined contexts
	 */
	protected void clean() {
		synchronized (additionalContexts) {
			additionalContexts.clear();
		}
	}

	/**
	 * @see EntityDefinitionService#addContext(EntityDefinition)
	 */
	@Override
	public EntityDefinition addContext(EntityDefinition sibling) {
		List<ChildContext> path = sibling.getPropertyPath();
		if (path.isEmpty()) {
			return null;
		}
		
		//XXX any checks? see InstanceContextTester
		
		EntityDefinition def = AlignmentUtil.getDefaultEntity(sibling);
		Integer newName;
		
		synchronized (additionalContexts) {
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
		}
		
		List<ChildContext> newPath = new ArrayList<ChildContext>(path);
		ChildDefinition<?> lastChild = newPath.get(newPath.size() - 1).getChild();
		newPath.remove(path.size() - 1);
		newPath.add(new ChildContext(newName, lastChild));
		EntityDefinition result = createEntity(def.getType(), newPath,
				sibling.getSchemaSpace());
		
		notifyContextAdded(result);
		
		return result ;
	}
	
	/**
	 * Add missing contexts for the given cells 
	 * @param cells the cells
	 */
	protected void addMissingContexts(Iterable<Cell> cells) {
		for (Cell cell : cells) {
			Collection<EntityDefinition> addedContexts = new ArrayList<EntityDefinition>();
			synchronized (additionalContexts) {
				if (cell.getSource() != null) {
					addedContexts.addAll(addMissingEntityContexts(cell.getSource().values()));
				}
				addedContexts.addAll(addMissingEntityContexts(cell.getTarget().values()));
			}
			if (!addedContexts.isEmpty()) {
				notifyContextsAdded(addedContexts);
			}
		}
	}

	/**
	 * Add missing contexts for the given entities
	 * @param entities the entities
	 * @return all entity definitions for which new contexts have been added
	 */
	private Collection<EntityDefinition> addMissingEntityContexts(Iterable<? extends Entity> entities) {
		Collection<EntityDefinition> addedContexts = new ArrayList<EntityDefinition>();
		
		for (Entity entity : entities) {
			EntityDefinition entityDef = entity.getDefinition();
			
			// collect the entity definition and all of its parents
			LinkedList<EntityDefinition> hierarchy = new LinkedList<EntityDefinition>();
			EntityDefinition parent = entityDef;
			while (parent != null) {
				hierarchy.addFirst(parent);
				parent = getParent(parent);
			}
			
			// check if the entity definitions are known starting with the topmost parent
			for (EntityDefinition candidate : hierarchy) {
				Integer contextName = getContext(candidate);
				if (contextName != null) {
					// add context
					boolean added = additionalContexts.put(
							AlignmentUtil.getDefaultEntity(candidate), contextName);
					if (added) {
						addedContexts.add(candidate);
					}
				}
			}
		}
		
		return addedContexts;
	}

	/**
	 * Get the context name of the given entity definition.
	 * @param candidate the entity definition
	 * @return the context name, <code>null</code> for the default context
	 */
	private static Integer getContext(EntityDefinition candidate) {
		List<ChildContext> path = candidate.getPropertyPath();
		
		if (path == null || path.isEmpty()) {
			// type currently always a default context
			return null;
		}
		else {
			return path.get(path.size() - 1).getContextName();
		}
	}

	/**
	 * @see EntityDefinitionService#removeContext(EntityDefinition)
	 */
	@Override
	public void removeContext(EntityDefinition entity) {
		List<ChildContext> path = entity.getPropertyPath();
		if (path.isEmpty()) {
			return;
		}
		
		ChildContext lastContext = path.get(path.size() - 1);
		if (lastContext.getContextName() == null) {
			return;
		}
		
		//XXX any checks? Alignment must still be valid! see also InstanceContextTester
		
		EntityDefinition def = AlignmentUtil.getDefaultEntity(entity);
		synchronized (additionalContexts) {
			additionalContexts.remove(def, lastContext.getContextName());
		}
		
		notifyContextRemoved(entity);
	}

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
					createPath(entity.getPropertyPath(), context), 
					entity.getSchemaSpace());
			result.add(defaultEntity);
			// look up additional instance contexts and add them
			synchronized (additionalContexts) {
				for (Integer contextName : additionalContexts.get(defaultEntity)) {
					ChildContext namedContext = new ChildContext(contextName, child);
					EntityDefinition namedChild = createEntity(entity.getType(), 
							createPath(entity.getPropertyPath(), namedContext),
							entity.getSchemaSpace());
					result.add(namedChild);
				}
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

}
