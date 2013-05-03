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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Condition;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter;

/**
 * Manages instance contexts and the corresponding entity definitions.
 * 
 * @author Simon Templer
 * @since 2.5
 */
public class EntityDefinitionServiceImpl extends AbstractEntityDefinitionService {

	/**
	 * Stores named instance contexts. The key is the corresponding entity
	 * definition w/ the default context (in the last path element). XXX use
	 * entity definitions as values instead? XXX This storage is based on the
	 * assumption that a named context cannot be combined with any other
	 * context.
	 */
	private final SetMultimap<EntityDefinition, Integer> namedContexts = HashMultimap.create();

	/**
	 * Stores index contexts. The key is the corresponding entity definition w/
	 * the default context (in the last path element). XXX use entity
	 * definitions as values instead? XXX This storage is based on the
	 * assumption that an index context cannot be combined with any other
	 * context.
	 */
	private final SetMultimap<EntityDefinition, Integer> indexContexts = HashMultimap.create();

	/**
	 * Stores condition contexts. The key is the corresponding entity definition
	 * w/ the default context (in the last path element). XXX use entity
	 * definitions as values instead? XXX This storage is based on the
	 * assumption that a condition context cannot be combined with any other
	 * context.
	 */
	private final SetMultimap<EntityDefinition, Condition> conditionContexts = HashMultimap
			.create();

	/**
	 * Create the entity definition service
	 * 
	 * @param alignmentService the alignment service
	 * @param projectService the project service
	 */
	public EntityDefinitionServiceImpl(final AlignmentService alignmentService,
			ProjectService projectService) {
		super();

		alignmentService.addListener(new AlignmentServiceListener() {

			@Override
			public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
				addMissingContexts(cells.values());
				// XXX do anything about replaced cells?
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				addMissingContexts(cells);
			}

			@Override
			public void cellsRemoved(Iterable<Cell> cells) {
				// XXX do anything?
			}

			@Override
			public void alignmentCleared() {
				// XXX remove all created contexts?
			}

			@Override
			public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
				// currently no cell property that affects entity definition
				// contexts
			}

			@Override
			public void alignmentChanged() {
				// XXX clear first?
				addMissingContexts(alignmentService.getAlignment().getCells());
			}
		});

		// in case alignment was loaded before service was created -> add
		// missing contexts now
		addMissingContexts(alignmentService.getAlignment().getCells());

		projectService.addListener(new ProjectServiceAdapter() {

			@Override
			public void onClean() {
				// remove all context definitions
				clean();
			}

		});

		// TODO remove contexts when schema doesn't contain corresponding
		// definition?!
	}

	/**
	 * Remove all defined contexts
	 */
	protected void clean() {
		// XXX nested synchronized?
		synchronized (namedContexts) {
			namedContexts.clear();
		}
		synchronized (indexContexts) {
			indexContexts.clear();
		}
		synchronized (conditionContexts) {
			conditionContexts.clear();
		}
	}

	/**
	 * @see EntityDefinitionService#addNamedContext(EntityDefinition)
	 */
	@Override
	public EntityDefinition addNamedContext(EntityDefinition sibling) {
		List<ChildContext> path = sibling.getPropertyPath();
		if (sibling.getSchemaSpace() == SchemaSpaceID.SOURCE || path.isEmpty()) {
			// not supported for source entities
			// and not for type entity definitions
			// XXX throw exception instead?
			return null;
		}

		// XXX any checks? see InstanceContextTester

		EntityDefinition def = AlignmentUtil.getDefaultEntity(sibling);
		Integer newName;

		synchronized (namedContexts) {
			// get registered context names
			Collection<Integer> names = namedContexts.get(def);
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

			namedContexts.put(def, newName);
		}

		List<ChildContext> newPath = new ArrayList<ChildContext>(path);
		ChildDefinition<?> lastChild = newPath.get(newPath.size() - 1).getChild();
		newPath.remove(path.size() - 1);
		// new named context, w/o index or condition context
		newPath.add(new ChildContext(newName, null, null, lastChild));
		EntityDefinition result = createEntity(def.getType(), newPath, sibling.getSchemaSpace(),
				sibling.getFilter());

		notifyContextAdded(result);

		return result;
	}

	/**
	 * @see EntityDefinitionService#addIndexContext(EntityDefinition, Integer)
	 */
	@Override
	public EntityDefinition addIndexContext(EntityDefinition sibling, Integer index) {
		List<ChildContext> path = sibling.getPropertyPath();
		if (sibling.getSchemaSpace() == SchemaSpaceID.TARGET || path.isEmpty()) {
			// not supported for target entities
			// and not for type entity definitions
			// XXX throw exception instead?
			return null;
		}

		// XXX any checks? see InstanceContextTester

		EntityDefinition def = AlignmentUtil.getDefaultEntity(sibling);

		boolean doAdd = true;
		synchronized (indexContexts) {
			// get registered context indexes
			Set<Integer> existingIndexes = indexContexts.get(def);
			if (index == null) {
				// determine index automatically
				if (existingIndexes == null || existingIndexes.isEmpty()) {
					index = Integer.valueOf(0);
				}
				else {
					// get the sorted existing indexes
					SortedSet<Integer> sortedIndexes = new TreeSet<Integer>(existingIndexes);
					// find the smallest value not present
					int expected = 0;
					Iterator<Integer> it = sortedIndexes.iterator();
					while (index == null && it.hasNext()) {
						int existingIndex = it.next();
						if (existingIndex != expected) {
							index = expected;
						}
						expected++;
					}
					if (index == null) {
						index = expected;
					}
				}
			}
			else if (existingIndexes.contains(index)) {
				// this index context is not new, but already there
				doAdd = false;
			}

			if (doAdd) {
				indexContexts.put(def, index);
			}
		}

		List<ChildContext> newPath = new ArrayList<ChildContext>(path);
		ChildDefinition<?> lastChild = newPath.get(newPath.size() - 1).getChild();
		newPath.remove(path.size() - 1);
		// new index context, w/o name or condition context
		newPath.add(new ChildContext(null, index, null, lastChild));
		EntityDefinition result = createEntity(def.getType(), newPath, sibling.getSchemaSpace(),
				sibling.getFilter());

		if (doAdd) {
			notifyContextAdded(result);
		}

		return result;
	}

	/**
	 * @see EntityDefinitionService#addConditionContext(EntityDefinition,
	 *      Filter)
	 */
	@Override
	public EntityDefinition addConditionContext(EntityDefinition sibling, Filter filter) {
		if (filter == null) {
			throw new NullPointerException("Filter must not be null");
		}

		List<ChildContext> path = sibling.getPropertyPath();
		if (sibling.getSchemaSpace() == SchemaSpaceID.TARGET && path.isEmpty()) {
			// not supported for target type entities
			// XXX throw exception instead?
			return null;
		}

		Condition condition = new Condition(filter);

		EntityDefinition def = AlignmentUtil.getDefaultEntity(sibling);

		boolean doAdd = true;
		synchronized (conditionContexts) {
			// get registered context indexes
			Set<Condition> existingConditions = conditionContexts.get(def);
			if (existingConditions.contains(condition)) {
				// this condition context is not new, but already there
				doAdd = false;
			}

			if (doAdd) {
				conditionContexts.put(def, condition);
			}
		}

		EntityDefinition result;

		if (path.isEmpty()) {
			// create type entity definition with filter
			result = createEntity(def.getType(), def.getPropertyPath(), def.getSchemaSpace(),
					condition.getFilter());
		}
		else {
			List<ChildContext> newPath = new ArrayList<ChildContext>(path);
			ChildDefinition<?> lastChild = newPath.get(newPath.size() - 1).getChild();
			newPath.remove(path.size() - 1);
			// new condition context, w/o name or index context
			newPath.add(new ChildContext(null, null, condition, lastChild));
			result = createEntity(def.getType(), newPath, sibling.getSchemaSpace(),
					sibling.getFilter());
		}

		if (doAdd) {
			notifyContextAdded(result);
		}

		return result;
	}

	/**
	 * @see EntityDefinitionService#getTypeEntities(TypeDefinition,
	 *      SchemaSpaceID)
	 */
	@Override
	public Collection<? extends TypeEntityDefinition> getTypeEntities(TypeDefinition type,
			SchemaSpaceID schemaSpace) {
		TypeEntityDefinition ted = new TypeEntityDefinition(type, schemaSpace, null);

		Set<Condition> conditions;
		synchronized (conditionContexts) {
			conditions = conditionContexts.get(ted);
		}

		List<TypeEntityDefinition> result = new ArrayList<TypeEntityDefinition>();

		// add default type entity
		result.add(ted);
		// type entity definitions with filters
		for (Condition condition : conditions) {
			result.add(new TypeEntityDefinition(type, schemaSpace, condition.getFilter()));
		}

		return result;
	}

	/**
	 * Add missing contexts for the given cells
	 * 
	 * @param cells the cells
	 */
	protected void addMissingContexts(Iterable<? extends Cell> cells) {
		for (Cell cell : cells) {
			Collection<EntityDefinition> addedContexts = new ArrayList<EntityDefinition>();
			synchronized (namedContexts) {
				synchronized (indexContexts) {
					synchronized (conditionContexts) {
						if (cell.getSource() != null) {
							addedContexts
									.addAll(addMissingEntityContexts(cell.getSource().values()));
						}
						addedContexts.addAll(addMissingEntityContexts(cell.getTarget().values()));
					}
				}
			}
			if (!addedContexts.isEmpty()) {
				notifyContextsAdded(addedContexts);
			}
		}
	}

	/**
	 * Add missing contexts for the given entities
	 * 
	 * @param entities the entities
	 * @return all entity definitions for which new contexts have been added
	 */
	private Collection<EntityDefinition> addMissingEntityContexts(
			Iterable<? extends Entity> entities) {
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

			// check if the entity definitions are known starting with the
			// topmost parent
			for (EntityDefinition candidate : hierarchy) {
				Integer contextName = AlignmentUtil.getContextName(candidate);
				Integer contextIndex = AlignmentUtil.getContextIndex(candidate);
				Condition contextCondition = AlignmentUtil.getContextCondition(candidate);

				if (contextName != null || contextIndex != null || contextCondition != null) {
					if (contextName != null && contextIndex == null && contextCondition == null) {
						// add named context
						boolean added = namedContexts.put(
								AlignmentUtil.getDefaultEntity(candidate), contextName);
						if (added) {
							addedContexts.add(candidate);
						}
					}
					else if (contextIndex != null && contextName == null
							&& contextCondition == null) {
						// add index context
						boolean added = indexContexts.put(
								AlignmentUtil.getDefaultEntity(candidate), contextIndex);
						if (added) {
							addedContexts.add(candidate);
						}
					}
					else if (contextCondition != null && contextName == null
							&& contextIndex == null) {
						// add condition context
						boolean added = conditionContexts.put(
								AlignmentUtil.getDefaultEntity(candidate), contextCondition);
						if (added) {
							addedContexts.add(candidate);
						}
					}
					else {
						throw new IllegalArgumentException(
								"Illegal combination of instance contexts");
					}
				}
			}
		}

		return addedContexts;
	}

	/**
	 * @see EntityDefinitionService#removeContext(EntityDefinition)
	 */
	@Override
	public void removeContext(EntityDefinition entity) {
		EntityDefinition def = AlignmentUtil.getDefaultEntity(entity);

		// XXX any checks? Alignment must still be valid! see also
		// InstanceContextTester

		List<ChildContext> path = entity.getPropertyPath();
		if (path.isEmpty()) {
			// type entity definition
			Filter filter = entity.getFilter();
			if (filter != null) {
				synchronized (conditionContexts) {
					conditionContexts.remove(def, new Condition(filter));
				}
				// XXX what about the children of this context?
			}

			notifyContextRemoved(entity);

			return;
		}

		boolean removed = false;
		ChildContext lastContext = path.get(path.size() - 1);

		if (lastContext.getContextName() != null) {
			synchronized (namedContexts) {
				namedContexts.remove(def, lastContext.getContextName());
			}
			removed = true;
		}

		if (lastContext.getIndex() != null) {
			synchronized (indexContexts) {
				indexContexts.remove(def, lastContext.getIndex());
			}
			removed = true;
		}

		if (lastContext.getCondition() != null) {
			synchronized (conditionContexts) {
				conditionContexts.remove(def, lastContext.getCondition());
			}
			removed = true;
		}

		if (removed) {
			notifyContextRemoved(entity);
		}
	}

	/**
	 * @see EntityDefinitionService#getChildren(EntityDefinition)
	 */
	@Override
	public Collection<? extends EntityDefinition> getChildren(EntityDefinition entity) {
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
					createPath(entity.getPropertyPath(), context), entity.getSchemaSpace(),
					entity.getFilter());
			result.add(defaultEntity);
			// look up additional instance contexts and add them
			synchronized (namedContexts) {
				for (Integer contextName : namedContexts.get(defaultEntity)) {
					ChildContext namedContext = new ChildContext(contextName, null, null, child);
					EntityDefinition namedChild = createEntity(entity.getType(),
							createPath(entity.getPropertyPath(), namedContext),
							entity.getSchemaSpace(), entity.getFilter());
					result.add(namedChild);
				}
			}

			synchronized (indexContexts) {
				for (Integer index : indexContexts.get(defaultEntity)) {
					ChildContext indexContext = new ChildContext(null, index, null, child);
					EntityDefinition indexChild = createEntity(entity.getType(),
							createPath(entity.getPropertyPath(), indexContext),
							entity.getSchemaSpace(), entity.getFilter());
					result.add(indexChild);
				}
			}

			synchronized (conditionContexts) {
				for (Condition condition : conditionContexts.get(defaultEntity)) {
					ChildContext conditionContext = new ChildContext(null, null, condition, child);
					EntityDefinition conditionChild = createEntity(entity.getType(),
							createPath(entity.getPropertyPath(), conditionContext),
							entity.getSchemaSpace(), entity.getFilter());
					result.add(conditionChild);
				}
			}
		}

		return result;
	}

	/**
	 * Create a property path
	 * 
	 * @param parentPath the parent path
	 * @param context the child context
	 * @return the property path including the child context
	 */
	private static List<ChildContext> createPath(List<ChildContext> parentPath, ChildContext context) {
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
