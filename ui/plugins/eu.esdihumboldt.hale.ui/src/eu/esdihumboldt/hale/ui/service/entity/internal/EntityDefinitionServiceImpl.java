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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.SetMultimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Condition;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
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
			public void customFunctionsChanged() {
				// custom functions don't affect entity definitions
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

		EntityDefinition result = createWithCondition(sibling, condition);

		if (doAdd) {
			notifyContextAdded(result);
		}

		return result;
	}

	/**
	 * Creates a new entity definition.
	 * 
	 * @param sibling the entity definition to use as base
	 * @param condition the new condition, not <code>null</code>
	 * @return a new entity definition
	 */
	private EntityDefinition createWithCondition(EntityDefinition sibling, Condition condition) {
		EntityDefinition result;

		List<ChildContext> path = sibling.getPropertyPath();
		if (path.isEmpty()) {
			// create type entity definition with filter
			result = createEntity(sibling.getType(), path, sibling.getSchemaSpace(),
					condition.getFilter());
		}
		else {
			List<ChildContext> newPath = new ArrayList<ChildContext>(path);
			ChildContext last = newPath.remove(path.size() - 1);
			// new condition context, w/o name or index context
			newPath.add(new ChildContext(null, null, condition, last.getChild()));
			result = createEntity(sibling.getType(), newPath, sibling.getSchemaSpace(),
					sibling.getFilter());
		}

		return result;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService#editConditionContext(eu.esdihumboldt.hale.common.align.model.EntityDefinition,
	 *      eu.esdihumboldt.hale.common.instance.model.Filter)
	 */
	@Override
	public EntityDefinition editConditionContext(final EntityDefinition sibling, Filter filter) {
		List<ChildContext> path = sibling.getPropertyPath();
		if (sibling.getSchemaSpace() == SchemaSpaceID.TARGET && path.isEmpty()) {
			// not supported for target type entities
			// XXX throw exception instead?
			return null;
		}

		// Check whether there actually is a change. If not, we are done.
		Condition oldCondition = AlignmentUtil.getContextCondition(sibling);
		if (Objects.equal(filter, oldCondition == null ? null : oldCondition.getFilter()))
			return sibling;

		// Create the new entity. Do not add context yet, since the user could
		// still abort the process (see below).
		EntityDefinition newDef = AlignmentUtil.getDefaultEntity(sibling);
		if (filter != null)
			newDef = createWithCondition(sibling, new Condition(filter));

		AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
		Alignment alignment = as.getAlignment();

		// Collect cells to replace.
		// All cells of the EntityDefinition's type can be affected.
		Collection<? extends Cell> potentiallyAffected = alignment.getCells(sibling.getType(),
				sibling.getSchemaSpace());
		Predicate<Cell> associatedCellPredicate = new Predicate<Cell>() {

			@Override
			public boolean apply(Cell input) {
				return input != null && AlignmentUtil.associatedWith(sibling, input, false, true);
			}
		};

		Collection<? extends Cell> affected = new HashSet<Cell>(
				Collections2.filter(potentiallyAffected, associatedCellPredicate));

		// Check whether base alignment cells are affected.
		boolean baseCellsAffected = false;
		Predicate<Cell> baseCellPredicate = new Predicate<Cell>() {

			@Override
			public boolean apply(Cell input) {
				return input != null && input.isBaseCell();
			}
		};
		if (Iterables.find(affected, baseCellPredicate, null) != null) {
			// Check whether the user wants to continue.
			final Display display = PlatformUI.getWorkbench().getDisplay();
			final AtomicBoolean abort = new AtomicBoolean();

			display.syncExec(new Runnable() {

				@Override
				public void run() {
					MessageBox mb = new MessageBox(display.getActiveShell(),
							SWT.YES | SWT.NO | SWT.ICON_QUESTION);
					mb.setMessage(
							"Some base alignment cells reference the entity definition you wish to change.\n"
									+ "The change will only affect cells which aren't from any base alignment.\n\n"
									+ "Do you still wish to continue?");
					mb.setText("Continue?");
					abort.set(mb.open() != SWT.YES);
				}
			});

			if (abort.get())
				return null;

			// Filter base alignment cells out.
			baseCellsAffected = true;
			affected = Collections2.filter(affected, Predicates.not(baseCellPredicate));
		}

		// No more obstacles. Finish!

		// Add condition context if necessary
		if (filter != null)
			addConditionContext(sibling, filter);

		// Replace affected (filtered) cells.
		Map<Cell, MutableCell> replaceMap = new HashMap<Cell, MutableCell>();
		for (Cell cell : affected) {
			DefaultCell newCell = new DefaultCell(cell);
			if (newDef.getSchemaSpace() == SchemaSpaceID.SOURCE)
				newCell.setSource(replace(newCell.getSource(), sibling, newDef));
			else
				newCell.setTarget(replace(newCell.getTarget(), sibling, newDef));
			replaceMap.put(cell, newCell);
		}
		as.replaceCells(replaceMap);

		// Remove old condition context, if it was neither the default context,
		// nor do any base alignment cells still use it.
		if (oldCondition != null && !baseCellsAffected)
			removeContext(sibling);

		return newDef;
	}

	/**
	 * Creates a new ListMultimap with all occurrences of originalDef replaced
	 * by newDef. newDef must be a sibling of originalDef.
	 * 
	 * @param entities the original list
	 * @param originalDef the entity definition to be replaced
	 * @param newDef the entity definition to use
	 * @return a new list
	 */
	private ListMultimap<String, ? extends Entity> replace(
			ListMultimap<String, ? extends Entity> entities, EntityDefinition originalDef,
			EntityDefinition newDef) {
		ListMultimap<String, Entity> newList = ArrayListMultimap.create();
		for (Entry<String, ? extends Entity> entry : entities.entries()) {
			EntityDefinition entryDef = entry.getValue().getDefinition();
			Entity newEntry;
			if (AlignmentUtil.isParent(originalDef, entryDef)) {
				if (entry.getValue() instanceof Type) {
					// entry is a Type, so the changed Definition must be a
					// Type, too.
					newEntry = new DefaultType((TypeEntityDefinition) newDef);
				}
				else if (entry.getValue() instanceof Property) {
					// entry is a Property, check changed Definition.
					if (originalDef.getPropertyPath().isEmpty()) {
						// Type changed.
						newEntry = new DefaultProperty(new PropertyEntityDefinition(
								newDef.getType(), entryDef.getPropertyPath(),
								entryDef.getSchemaSpace(), newDef.getFilter()));
					}
					else {
						// Some element of the property path changed.
						List<ChildContext> newPath = new ArrayList<ChildContext>(
								entryDef.getPropertyPath());
						int lastIndexOfChangedDef = newDef.getPropertyPath().size() - 1;
						newPath.set(lastIndexOfChangedDef,
								newDef.getPropertyPath().get(lastIndexOfChangedDef));
						newEntry = new DefaultProperty(
								new PropertyEntityDefinition(entryDef.getType(), newPath,
										entryDef.getSchemaSpace(), entryDef.getFilter()));
					}
				}
				else {
					throw new IllegalStateException("Entity is neither a Type nor a Property.");
				}
			}
			else {
				newEntry = entry.getValue();
			}
			newList.put(entry.getKey(), newEntry);
		}
		return newList;
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

			addContexts(entityDef, addedContexts);
		}

		return addedContexts;
	}

	@Override
	public void addContexts(EntityDefinition entityDef) {
		Collection<EntityDefinition> addedContexts = new ArrayList<EntityDefinition>();
		synchronized (namedContexts) {
			synchronized (indexContexts) {
				synchronized (conditionContexts) {
					addContexts(entityDef, addedContexts);
				}
			}
		}
		if (!addedContexts.isEmpty()) {
			notifyContextsAdded(addedContexts);
		}
	}

	/**
	 * Add the missing contexts for the given entity definition.
	 * 
	 * @param entityDef the entity definition to add contexts for
	 * @param addedContexts a collection where newly created contexts must be
	 *            added
	 */
	private void addContexts(EntityDefinition entityDef,
			Collection<EntityDefinition> addedContexts) {
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
					boolean added = namedContexts.put(AlignmentUtil.getDefaultEntity(candidate),
							contextName);
					if (added) {
						addedContexts.add(candidate);
					}
				}
				else if (contextIndex != null && contextName == null && contextCondition == null) {
					// add index context
					boolean added = indexContexts.put(AlignmentUtil.getDefaultEntity(candidate),
							contextIndex);
					if (added) {
						addedContexts.add(candidate);
					}
				}
				else if (contextCondition != null && contextName == null && contextIndex == null) {
					// add condition context
					boolean added = conditionContexts.put(AlignmentUtil.getDefaultEntity(candidate),
							contextCondition);
					if (added) {
						addedContexts.add(candidate);
					}
				}
				else {
					throw new IllegalArgumentException("Illegal combination of instance contexts");
				}
			}
		}
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
