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

package eu.esdihumboldt.hale.common.align.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.impl.ChildEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.util.groovy.paths.Path;

/**
 * Alignment model utility methods.
 * 
 * @author Simon Templer
 */
public abstract class AlignmentUtil {

	private static final ALogger log = ALoggerFactory.getLogger(AlignmentUtil.class);

	/**
	 * Determines if the given cell is a type cell.
	 * 
	 * @param cell the cell
	 * @return if the cell is a type cell
	 */
	public static boolean isTypeCell(Cell cell) {
		// check if cell is a type cell
		return cell.getTarget().values().iterator().next() instanceof Type;
	}

	/**
	 * Determines if the given alignment has any type relations.
	 * 
	 * @param alignment the alignment
	 * @return if any type cells are present in the alignment
	 */
	public static boolean hasTypeRelation(Alignment alignment) {
		for (Cell cell : alignment.getCells()) {
			if (isTypeCell(cell)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines if the given alignment contains a relation between the given
	 * types.
	 * 
	 * @param alignment the alignment
	 * @param sourceType the source type, may be <code>null</code> for any
	 *            source type
	 * @param targetType the target type, may be <code>null</code> for any
	 *            target type
	 * @return if a relation between the given types exists in the alignment
	 */
	public static boolean hasTypeRelation(Alignment alignment, TypeEntityDefinition sourceType,
			TypeEntityDefinition targetType) {
		if (sourceType == null && targetType == null) {
			// accept any type relation
			return hasTypeRelation(alignment);
		}
		else if (sourceType == null) {
			// accept any relation to the given target type
			Collection<? extends Cell> cells = alignment.getCells(targetType);
			return !cells.isEmpty();
		}
		else if (targetType == null) {
			// accept any relation to the given source type
			Collection<? extends Cell> cells = alignment.getCells(sourceType);
			return !cells.isEmpty();
		}
		else {
			// accept relations only if they combine both types
			Collection<? extends Cell> targetCells = alignment.getCells(targetType);
			Collection<? extends Cell> sourceCells = alignment.getCells(sourceType);
			targetCells.retainAll(sourceCells);
			return !targetCells.isEmpty();
		}
	}

	/**
	 * Determines if the given cell is an augmentation.
	 * 
	 * @param cell the cell
	 * @return if the cell is an augmentation
	 */
	public static boolean isAugmentation(Cell cell) {
		// check if cell is an augmentation cell
		return cell.getSource() == null || cell.getSource().isEmpty();
	}

	/**
	 * Get the parent entity definition for the given entity definition.
	 * 
	 * @param entity the entity definition
	 * @return the parent entity definition or <code>null</code> if it has no
	 *         parent
	 */
	public static EntityDefinition getParent(EntityDefinition entity) {
		List<ChildContext> path = entity.getPropertyPath();

		if (path == null || path.isEmpty()) {
			// entity is a type and has no parent
			return null;
		}
		else {
			List<ChildContext> newPath = new ArrayList<ChildContext>(path);
			newPath.remove(newPath.size() - 1);
			return createEntity(entity.getType(), newPath, entity.getSchemaSpace(),
					entity.getFilter());
		}
	}

	/**
	 * Get the default child of the given entity.
	 * 
	 * @param entity the parent entity
	 * @param childName the child name
	 * @return the child entity or <code>null</code> if no child with the given
	 *         name exists
	 */
	public static EntityDefinition getChild(EntityDefinition entity, QName childName) {
		ChildDefinition<?> child = DefinitionUtil.getChild(entity.getDefinition(), childName);
		if (child == null) {
			return null;
		}

		List<ChildContext> path = new ArrayList<ChildContext>(entity.getPropertyPath());
		path.add(new ChildContext(child));
		return createEntity(entity.getType(), path, entity.getSchemaSpace(), entity.getFilter());
	}

	/**
	 * Create an entity definition from a type and a child path.
	 * 
	 * @param type the path parent
	 * @param path the child path
	 * @param schemaSpace the associated schema space
	 * @param filter the entity filter on the type, may be <code>null</code>
	 * @return the created entity definition
	 */
	public static EntityDefinition createEntity(TypeDefinition type, List<ChildContext> path,
			SchemaSpaceID schemaSpace, Filter filter) {
		if (path == null || path.isEmpty()) {
			// entity is a type
			return new TypeEntityDefinition(type, schemaSpace, filter);
		}
		else if (path.get(path.size() - 1).getChild() instanceof PropertyDefinition) {
			// last element in path is a property
			return new PropertyEntityDefinition(type, path, schemaSpace, filter);
		}
		else {
			// last element is a child but no property
			return new ChildEntityDefinition(type, path, schemaSpace, filter);
		}
	}

	/**
	 * Create an entity definition from a definition path. Child contexts will
	 * all be defaults contexts.
	 * 
	 * @param path the definition path, the topmost element has to represent a
	 *            {@link TypeDefinition}, all other elements must be
	 *            {@link ChildDefinition}s
	 * @param schemaSpace the associated schema space
	 * @param filter the entity filter on the type, may be <code>null</code>
	 * @return the created entity definition
	 */
	public static EntityDefinition createEntity(Path<Definition<?>> path, SchemaSpaceID schemaSpace,
			Filter filter) {
		List<Definition<?>> defs = path.getElements();

		// create entity definition
		Definition<?> top = defs.get(0);
		if (!(top instanceof TypeDefinition))
			throw new IllegalArgumentException("Topmost accessor must represent a type definition");

		List<ChildDefinition<?>> childPath = Lists.transform(defs.subList(1, defs.size()),
				new Function<Definition<?>, ChildDefinition<?>>() {

					@Override
					public ChildDefinition<?> apply(Definition<?> input) {
						if (input instanceof ChildDefinition<?>)
							return (ChildDefinition<?>) input;
						throw new IllegalArgumentException(
								"All definitions in child accessors must be ChildDefinitions");
					}
				});
		// childPath will be copied in there, no need to do it here
		return createEntityFromDefinitions((TypeDefinition) top, childPath, schemaSpace, filter);
	}

	/**
	 * Create an entity definition from a definition path. Child contexts will
	 * all be defaults contexts.
	 * 
	 * @param type the path parent
	 * @param path the child path
	 * @param schemaSpace the associated schema space
	 * @param filter the entity filter on the type, may be <code>null</code>
	 * @return the created entity definition
	 */
	public static EntityDefinition createEntityFromDefinitions(TypeDefinition type,
			List<? extends ChildDefinition<?>> path, SchemaSpaceID schemaSpace, Filter filter) {
		ArrayList<ChildContext> contextPath = new ArrayList<>(path.size());
		// use a copy for efficiency later
		contextPath.addAll(Lists.transform(path, new Function<ChildDefinition<?>, ChildContext>() {

			@Override
			public ChildContext apply(ChildDefinition<?> input) {
				return new ChildContext(input);
			}
		}));
		return createEntity(type, contextPath, schemaSpace, filter);
	}

	/**
	 * Get the entity definition with the default instance context which is a
	 * sibling to (or the same as) the given entity definition.
	 * 
	 * @param entity the entity definition
	 * @return the entity definition with the default context in the last path
	 *         element
	 */
	public static EntityDefinition getDefaultEntity(EntityDefinition entity) {
		List<ChildContext> path = entity.getPropertyPath();

		if (path == null || path.isEmpty()) {
			if (entity.getFilter() == null) {
				return entity;
			}
			// sibling of type w/o filter
			return createEntity(entity.getType(), path, entity.getSchemaSpace(), null);
		}
		else {
			ChildContext lastConext = path.get(path.size() - 1);
			if (lastConext.getContextName() == null && lastConext.getIndex() == null
					&& lastConext.getCondition() == null) {
				return entity;
			}
		}

		List<ChildContext> newPath = new ArrayList<ChildContext>(path);
		ChildDefinition<?> lastChild = newPath.get(newPath.size() - 1).getChild();
		newPath.remove(newPath.size() - 1);
		newPath.add(new ChildContext(lastChild));
		return createEntity(entity.getType(), newPath, entity.getSchemaSpace(), entity.getFilter());
	}

	/**
	 * Get the entity definition based on the given entity definition with the
	 * default instance context for each path entry.
	 * 
	 * @param entity the entity definition
	 * @return the entity definition with the default context in all path
	 *         elements
	 */
	public static EntityDefinition getAllDefaultEntity(EntityDefinition entity) {
		List<ChildContext> path = entity.getPropertyPath();

		if (path == null || path.isEmpty() || path.get(path.size() - 1).getContextName() == null) {
			return entity;
		}

		List<ChildContext> newPath = new ArrayList<ChildContext>();
		for (ChildContext context : path) {
			ChildContext newcontext = new ChildContext(context.getChild());
			newPath.add(newcontext);
		}
		return createEntity(entity.getType(), newPath, entity.getSchemaSpace(), null);
	}

	/**
	 * Derive an entity definition from the given one but with a maximum path
	 * length.
	 * 
	 * @param entity the entity definition
	 * @param pathLength the maximum path length
	 * @return the entity definition derived from the given entity definition
	 *         but with the property path shortened if needed, otherwise the
	 *         given definition will be returned
	 */
	public static EntityDefinition deriveEntity(EntityDefinition entity, int pathLength) {
		if (pathLength < 0) {
			pathLength = 0;
		}

		List<ChildContext> path = entity.getPropertyPath();

		if (path == null || path.size() <= pathLength) {
			return entity;
		}

		List<ChildContext> newPath = new ArrayList<ChildContext>(pathLength);

		for (int i = 0; i < pathLength; i++) {
			newPath.add(path.get(i));
		}

		return createEntity(entity.getType(), newPath, entity.getSchemaSpace(), entity.getFilter());
	}

	/**
	 * Determines if a given entity definition is a parent of another entity
	 * definition or if both are equal.
	 * 
	 * @param parent the parent
	 * @param child the potential child
	 * @return if the first entity definition is a parent of the second or if
	 *         both are equal
	 */
	public static boolean isParent(EntityDefinition parent, EntityDefinition child) {
		if (!parent.getType().equals(child.getType())) {
			// if the types do not match, there can't be a relation
			return false;
		}

		// check type context
		if (!Objects.equal(parent.getFilter(), child.getFilter())) {
			// if the filters do not match, there can't be a relation
			return false;
		}

		// check the property paths
		List<ChildContext> parentPath = parent.getPropertyPath();
		List<ChildContext> childPath = child.getPropertyPath();

		if (parentPath.size() > childPath.size()) {
			// property path for parent is longer, can't be parent of child
			return false;
		}

		// check parent path elements for equality with child path
		for (int i = 0; i < parentPath.size(); i++) {
			ChildContext parentContext = parentPath.get(i);
			ChildContext childContext = childPath.get(i);
			if (!parentContext.equals(childContext)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * States if the given entity definition or one of its children is mapped in
	 * the given alignment.
	 * 
	 * @param entity the entity definition
	 * @param alignment the alignment
	 * @return if the alignment contains a relation where the given entity or
	 *         one of its children (including grand-children etc.) is involved
	 */
	public static boolean entityOrChildMapped(EntityDefinition entity, Alignment alignment) {
		// check for a direct mapping
		if (!alignment.getCells(entity).isEmpty()) {
			return true;
		}

		// check for child mappings
		Collection<? extends Cell> typeCells = alignment.getCells(entity.getType(),
				entity.getSchemaSpace());
		for (Cell cell : typeCells) {
			if (entity.getSchemaSpace() == SchemaSpaceID.SOURCE && cell.getSource() != null
					&& entityOrChildContained(entity, cell.getSource().values())) {
				return true;
			}
			if (entity.getSchemaSpace() == SchemaSpaceID.TARGET && cell.getTarget() != null
					&& entityOrChildContained(entity, cell.getTarget().values())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines if the given entity definition or one of its children is
	 * contained in the given entity candidates.
	 * 
	 * @param entity the entity definition
	 * @param candidates the entity candidates to test
	 * @return if at least one of the entity candidates is the given entity or a
	 *         child (or grand-child etc.)
	 */
	public static boolean entityOrChildContained(EntityDefinition entity,
			Iterable<? extends Entity> candidates) {
		for (Entity candidate : candidates) {
			EntityDefinition def = candidate.getDefinition();
			if (isParent(entity, def)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Get the type entity definition of the given entity definition.
	 * 
	 * @param entityDef the entity definition
	 * @return the entity definition if it is a {@link TypeEntityDefinition},
	 *         otherwise a new type entity definition is created
	 */
	public static TypeEntityDefinition getTypeEntity(EntityDefinition entityDef) {
		if (entityDef instanceof TypeEntityDefinition) {
			return (TypeEntityDefinition) entityDef;
		}
		else {
			return new TypeEntityDefinition(entityDef.getType(), entityDef.getSchemaSpace(),
					entityDef.getFilter());
		}
	}

	/**
	 * Get the context name of the given entity definition.
	 * 
	 * @param candidate the entity definition
	 * @return the context name, <code>null</code> for the default context
	 */
	public static Integer getContextName(EntityDefinition candidate) {
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
	 * Get the context index of the given entity definition.
	 * 
	 * @param candidate the entity definition
	 * @return the context name, <code>null</code> for the default context
	 */
	public static Integer getContextIndex(EntityDefinition candidate) {
		List<ChildContext> path = candidate.getPropertyPath();

		if (path == null || path.isEmpty()) {
			// type currently always a default context
			return null;
		}
		else {
			return path.get(path.size() - 1).getIndex();
		}
	}

	/**
	 * Get the context condition of the given entity definition.
	 * 
	 * @param candidate the entity definition
	 * @return the context name, <code>null</code> for the default context
	 */
	public static Condition getContextCondition(EntityDefinition candidate) {
		List<ChildContext> path = candidate.getPropertyPath();

		if (path == null || path.isEmpty()) {
			// wrap the type filter in a condition
			Filter filter = candidate.getFilter();
			if (filter == null) {
				return null;
			}
			else {
				return new Condition(filter);
			}
		}
		else {
			return path.get(path.size() - 1).getCondition();
		}
	}

	/**
	 * Get a text representation for the entity definition context.
	 * 
	 * @param entityDef the entity definition
	 * @return the context text or <code>null</code> if it is the default
	 *         context or the context has no text representation
	 */
	public static String getContextText(EntityDefinition entityDef) {
		List<ChildContext> path = entityDef.getPropertyPath();
		if (path != null && !path.isEmpty()) {
			ChildContext lastContext = path.get(path.size() - 1);
			if (lastContext.getIndex() != null) {
				return "[" + lastContext.getIndex() + "]";
			}
			else if (lastContext.getCondition() != null) {
				return getFilterText(lastContext.getCondition().getFilter());
			}
		}
		else {
			// type filter
			return getFilterText(entityDef.getFilter());
		}

		return null;
	}

	/**
	 * Get the text to display for a filter.
	 * 
	 * @param filter the filter, may be <code>null</code>
	 * @return the filter text or <code>null</code>
	 */
	public static String getFilterText(Filter filter) {
		String filterString = FilterDefinitionManager.getInstance().asString(filter);
		if (filterString != null) {
			int pos = filterString.indexOf(':');
			if (pos >= 0 && pos + 1 < filterString.length()) {
				filterString = filterString.substring(pos + 1);
			}
		}
		return filterString;
	}

	/**
	 * Determines if the given entity is a default entity.
	 * 
	 * @param entity the entity to check
	 * @return if the entity is a default entity
	 */
	public static boolean isDefaultEntity(EntityDefinition entity) {
		if (entity.getFilter() != null) {
			return false;
		}

		for (ChildContext context : entity.getPropertyPath()) {
			if (context.getCondition() != null || context.getContextName() != null
					|| context.getIndex() != null) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Assures that an entity is a {@link TypeEntityDefinition},
	 * {@link PropertyEntityDefinition} or {@link ChildEntityDefinition} and
	 * that the inherent classification is correct.
	 * 
	 * @param entity the entity definition
	 * @return the entity that is guaranteed to be a
	 *         {@link TypeEntityDefinition}, {@link PropertyEntityDefinition} or
	 *         {@link ChildEntityDefinition}
	 */
	public static EntityDefinition normalizeEntity(EntityDefinition entity) {
		if (entity instanceof TypeEntityDefinition) {
			return entity;
		}
		else if (entity instanceof PropertyEntityDefinition) {
			// check if use of PED is correct
			if (entity.getDefinition() instanceof ChildDefinition<?>
					&& ((ChildDefinition<?>) entity.getDefinition()).asGroup() != null) {
				// should be a CED
				return new ChildEntityDefinition(entity.getType(), entity.getPropertyPath(),
						entity.getSchemaSpace(), entity.getFilter());
			}
			else if (entity.getPropertyPath().isEmpty()) {
				return new TypeEntityDefinition(entity.getType(), entity.getSchemaSpace(),
						entity.getFilter());
			}
			else {
				return entity;
			}
		}
		else if (entity instanceof ChildEntityDefinition) {
			// check if use of CED is correct
			if (entity.getDefinition() instanceof ChildDefinition<?>
					&& ((ChildDefinition<?>) entity.getDefinition()).asProperty() != null) {
				// should be a PED
				return new PropertyEntityDefinition(entity.getType(), entity.getPropertyPath(),
						entity.getSchemaSpace(), entity.getFilter());
			}
			else if (entity.getPropertyPath().isEmpty()) {
				return new TypeEntityDefinition(entity.getType(), entity.getSchemaSpace(),
						entity.getFilter());
			}
			else {
				return entity;
			}
		}
		else {
			if (entity.getPropertyPath().isEmpty()) {
				return new TypeEntityDefinition(entity.getType(), entity.getSchemaSpace(),
						entity.getFilter());
			}
			else {
				if (entity.getDefinition() instanceof ChildDefinition<?>) {
					ChildDefinition<?> child = (ChildDefinition<?>) entity.getDefinition();
					if (child.asProperty() != null) {
						// should be a PED
						return new PropertyEntityDefinition(entity.getType(),
								entity.getPropertyPath(), entity.getSchemaSpace(),
								entity.getFilter());
					}
					else if (child.asGroup() != null) {
						// should be a CED
						return new ChildEntityDefinition(entity.getType(), entity.getPropertyPath(),
								entity.getSchemaSpace(), entity.getFilter());
					}
					else {
						throw new IllegalArgumentException("Illegal entity definition");
					}
				}
				else {
					throw new IllegalArgumentException("Illegal entity definition");
				}
			}
		}
	}

	/**
	 * Match a property condition against a property value.
	 * 
	 * @param condition the property condition
	 * @param value the property value
	 * @param parent the parent of the property value, may be <code>null</code>
	 *            if there is none
	 * @return if the value matched the property condition
	 */
	public static boolean matchCondition(Condition condition, Object value, Object parent) {
		// create dummy instance
		MutableInstance dummy = new DefaultInstance(null, null);
		// add value as property
		dummy.addProperty(new QName("value"), value);
		// add parent value as property
		if (parent != null) {
			dummy.addProperty(new QName("parent"), parent);
		}

		return condition.getFilter().match(dummy);
	}

	/**
	 * Returns a cell like the given property cell with all source and target
	 * types matching those off the given type cell.<br>
	 * If the types already match they are unchanged. If the types are sub types
	 * of the types of the type cell they are changed. If no change is necessary
	 * the cell itself is returned.
	 * 
	 * @param propertyCell the property cell to update
	 * @param typeCell the type cell with the target types
	 * @param strict If false and the target type cell has no sources or target,
	 *            the property cell is updated to have the sources/target in
	 *            their declaring type. If true, said properties are left
	 *            unchanged. Does not matter for complete type cells, since they
	 *            have sources and a target.
	 * @return the updated cell or <code>null</code> if an update isn't possible
	 */
	public static Cell reparentCell(Cell propertyCell, Cell typeCell, boolean strict) {
		ListMultimap<String, Entity> sources = ArrayListMultimap.create();
		ListMultimap<String, Entity> targets = ArrayListMultimap.create();
		boolean updateNecessary = false;

		// XXX are updates to the property path needed?
		// Currently not, since ChildDefinitions are compared by their names
		// only.

		// TARGETS

		Entity targetEntity = CellUtil.getFirstEntity(typeCell.getTarget());
		if (targetEntity != null) {
			TypeDefinition typeCellTargetType = ((Type) targetEntity).getDefinition()
					.getDefinition();
			for (Entry<String, ? extends Entity> target : propertyCell.getTarget().entries()) {
				TypeDefinition propertyCellTargetType = target.getValue().getDefinition().getType();
				if (propertyCellTargetType.equals(typeCellTargetType))
					targets.put(target.getKey(), target.getValue());
				else if (DefinitionUtil.isSuperType(typeCellTargetType, propertyCellTargetType)) {
					PropertyEntityDefinition oldDef = (PropertyEntityDefinition) target.getValue()
							.getDefinition();
					targets.put(target.getKey(),
							new DefaultProperty(new PropertyEntityDefinition(typeCellTargetType,
									oldDef.getPropertyPath(), SchemaSpaceID.TARGET, null)));
					updateNecessary = true;
				}
				else {
					// a cell with targets in more than one type
					return null;
				}
			}
		}
		else if (!strict)
			updateNecessary |= reparentToDeclaring(propertyCell.getTarget(), targets);
		else
			targets.putAll(propertyCell.getTarget());

		// SOURCES

		if (propertyCell.getSource() != null && !propertyCell.getSource().isEmpty()) {
			if (typeCell.getSource() != null && !typeCell.getSource().isEmpty()) {
				// collect source entity definitions
				Collection<TypeEntityDefinition> typeCellSourceTypes = new ArrayList<TypeEntityDefinition>();
				for (Entity entity : typeCell.getSource().values())
					typeCellSourceTypes.add((TypeEntityDefinition) entity.getDefinition());

				for (Entry<String, ? extends Entity> source : propertyCell.getSource().entries()) {
					TypeEntityDefinition propertyCellSourceType = getTypeEntity(
							source.getValue().getDefinition());
					if (typeCellSourceTypes.contains(propertyCellSourceType))
						sources.put(source.getKey(), source.getValue());
					else {
						boolean matchFound = false;
						// try to find a matching source
						// XXX what if multiple sources match?
						// currently all are added
						// maybe the whole cell should be duplicated?
						for (TypeEntityDefinition typeCellSourceType : typeCellSourceTypes) {
							if (DefinitionUtil.isSuperType(typeCellSourceType.getDefinition(),
									propertyCellSourceType.getDefinition())
									&& (propertyCellSourceType.getFilter() == null
											|| propertyCellSourceType.getFilter()
													.equals(typeCellSourceType.getFilter()))) {
								if (matchFound)
									log.warn(
											"Inherited property cell source matches multiple sources of type cell.");
								matchFound = true;
								PropertyEntityDefinition oldDef = (PropertyEntityDefinition) source
										.getValue().getDefinition();
								sources.put(source.getKey(),
										new DefaultProperty(new PropertyEntityDefinition(
												typeCellSourceType.getDefinition(),
												oldDef.getPropertyPath(), SchemaSpaceID.SOURCE,
												typeCellSourceType.getFilter())));
								updateNecessary = true;
								// XXX break; if only one match should be added
							}
						}
						if (!matchFound) {
							// a cell with a source that does not match the type
							// cell
							return null;
						}
					}
				}
			}
			else if (!strict)
				updateNecessary |= reparentToDeclaring(propertyCell.getSource(), sources);
			else
				targets.putAll(propertyCell.getTarget());
		}

		if (updateNecessary) {
			MutableCell copy = new DefaultCell(propertyCell);
			copy.setSource(sources);
			copy.setTarget(targets);
			propertyCell = copy;
		}

		return propertyCell;
	}

	/**
	 * Copies the properties from original to modified, modifying properties to
	 * their declaring type, if the type has no filter set.
	 * 
	 * @param original the original Multimap of properties
	 * @param modified the target Multimap
	 * @return true, if a property was changed, false otherwise
	 */
	private static boolean reparentToDeclaring(ListMultimap<String, ? extends Entity> original,
			ListMultimap<String, Entity> modified) {
		boolean changed = false;
		for (Entry<String, ? extends Entity> oEntity : original.entries()) {
			PropertyEntityDefinition property = (PropertyEntityDefinition) oEntity.getValue()
					.getDefinition();
			ChildDefinition<?> childDef = property.getPropertyPath().get(0).getChild();
			if (Objects.equal(childDef.getDeclaringGroup(), childDef.getParentType())
					|| property.getFilter() != null)
				modified.put(oEntity.getKey(), oEntity.getValue());
			else if (childDef.getDeclaringGroup() instanceof TypeDefinition) {
				modified.put(oEntity.getKey(),
						new DefaultProperty(new PropertyEntityDefinition(
								(TypeDefinition) childDef.getDeclaringGroup(),
								property.getPropertyPath(), property.getSchemaSpace(), null)));
				changed = true;
			}
			else {
				// declaring group of first level property no type
				// definition?
				// simply add it without change, shouldn't happen
				modified.put(oEntity.getKey(), oEntity.getValue());
			}
		}
		return changed;
	}

	/**
	 * Checks whether the given entity (or one of its children) is associated
	 * with the given cell (considering inheritance).
	 * 
	 * @param entity the entity to check
	 * @param cell the cell to check the entity against
	 * @param allowInheritance whether inheritance is allowed
	 * @param orChildren will also check against the entities children
	 * @return whether the entity is associated with the cell
	 */
	public static boolean associatedWith(EntityDefinition entity, Cell cell,
			boolean allowInheritance, boolean orChildren) {
		ListMultimap<String, ? extends Entity> entities;
		switch (entity.getSchemaSpace()) {
		case SOURCE:
			entities = cell.getSource();
			break;
		case TARGET:
			entities = cell.getTarget();
			break;
		default:
			throw new IllegalStateException(
					"Entity definition with illegal schema space encountered");
		}

		if (entities == null)
			return false;

		for (Entity e : entities.values()) {
			EntityDefinition def = e.getDefinition();
			if (allowInheritance) {
				if (DefinitionUtil.isSuperType(entity.getType(), def.getType())
						&& (def.getFilter() == null
								|| def.getFilter().equals(entity.getFilter()))) {
					// type is a match according to inheritance, make sure to
					// have common type
					def = createEntity(entity.getType(), def.getPropertyPath(),
							def.getSchemaSpace(), entity.getFilter());
				}
			}
			if ((orChildren && isParent(entity, def)) || entity.equals(def))
				return true;
		}

		return false;
	}

	/**
	 * Returns the values of the given instance which match the given property
	 * entity definition.
	 * 
	 * @param instance the instance to collect values from
	 * @param definition the property
	 * @param onlyValues whether to only return values, or to return whatever
	 *            can be found (including groups/instances)
	 * @return all values of the given property
	 */
	public static Multiset<Object> getValues(Instance instance, PropertyEntityDefinition definition,
			boolean onlyValues) {
		Multiset<Object> result = HashMultiset.create();
		addValues(instance, definition.getPropertyPath(), result, onlyValues);
		return result;
	}

	/**
	 * Add the values found on the given path to the given set.
	 * 
	 * @param group the parent group
	 * @param path the path on the group
	 * @param collectedValues the set to add the values to
	 * @param onlyValues whether to only return values, or to return whatever
	 *            can be found (including groups/instances)
	 */
	public static void addValues(Group group, List<ChildContext> path,
			Multiset<Object> collectedValues, boolean onlyValues) {
		if (path == null || path.isEmpty()) {
			// group/instance at end of path
			if (onlyValues) {
				// only include instance values
				if (group instanceof Instance) {
					Object value = ((Instance) group).getValue();
					if (value != null) {
						collectedValues.add(value);
					}
				}
			}
			else {
				// include the group/instance as is
				collectedValues.add(group);
			}
			// empty path - retrieve value from instance
		}
		else {
			// go down the path
			ChildContext context = path.get(0);
			List<ChildContext> subPath = path.subList(1, path.size());

			Object[] values = group.getProperty(context.getChild().getName());

			if (values != null) {
				// apply the possible source contexts
				if (context.getIndex() != null) {
					// select only the item at the index
					int index = context.getIndex();
					if (index < values.length) {
						values = new Object[] { values[index] };
					}
					else {
						values = new Object[] {};
					}
				}
				if (context.getCondition() != null) {
					// select only values that match the condition
					List<Object> matchedValues = new ArrayList<Object>();
					for (Object value : values) {
						if (AlignmentUtil.matchCondition(context.getCondition(), value, group)) {
							matchedValues.add(value);
						}
					}
					values = matchedValues.toArray();
				}

				// check all values
				for (Object value : values) {
					if (value instanceof Group) {
						addValues((Group) value, subPath, collectedValues, onlyValues);
					}
					else if (subPath.isEmpty()) {
						// normal value and at the end of the path
						if (value != null) {
							collectedValues.add(value);
						}
					}
				}
			}
		}
	}

	/**
	 * Get children of an {@link EntityDefinition} without context conditions
	 * 
	 * @param entityDef the entity definition
	 * @return Collection of entity definitions
	 */
	public static Collection<? extends EntityDefinition> getChildrenWithoutContexts(
			EntityDefinition entityDef) {
		List<ChildContext> path = entityDef.getPropertyPath();
		Collection<? extends ChildDefinition<?>> children;

		if (path == null || path.isEmpty()) {
			// entity is a type, children are the type children
			children = entityDef.getType().getChildren();
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
			EntityDefinition defaultEntity = AlignmentUtil.createEntity(entityDef.getType(),
					createPath(entityDef.getPropertyPath(), context), entityDef.getSchemaSpace(),
					entityDef.getFilter());
			result.add(defaultEntity);
		}

		return result;
	}

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
