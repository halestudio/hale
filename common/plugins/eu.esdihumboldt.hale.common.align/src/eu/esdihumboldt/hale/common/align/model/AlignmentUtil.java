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

package eu.esdihumboldt.hale.common.align.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Objects;

import eu.esdihumboldt.hale.common.align.model.impl.ChildEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Alignment model utility methods.
 * 
 * @author Simon Templer
 */
public abstract class AlignmentUtil {

	/**
	 * Determines if the given cell is a type cell.
	 * 
	 * @param cell
	 *            the cell
	 * @return if the cell is a type cell
	 */
	public static boolean isTypeCell(Cell cell) {
		// check if cell is a type cell
		return cell.getTarget().values().iterator().next() instanceof Type;
	}

	/**
	 * Determines if the given alignment has any type relations.
	 * 
	 * @param alignment
	 *            the alignment
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
	 * @param alignment
	 *            the alignment
	 * @param sourceType
	 *            the source type, may be <code>null</code> for any source type
	 * @param targetType
	 *            the target type, may be <code>null</code> for any target type
	 * @return if a relation between the given types exists in the alignment
	 */
	public static boolean hasTypeRelation(Alignment alignment,
			TypeEntityDefinition sourceType, TypeEntityDefinition targetType) {
		if (sourceType == null && targetType == null) {
			// accept any type relation
			return hasTypeRelation(alignment);
		} else if (sourceType == null) {
			// accept any relation to the given target type
			Collection<? extends Cell> cells = alignment.getCells(targetType);
			return !cells.isEmpty();
		} else if (targetType == null) {
			// accept any relation to the given source type
			Collection<? extends Cell> cells = alignment.getCells(sourceType);
			return !cells.isEmpty();
		} else {
			// accept relations only if they combine both types
			Collection<? extends Cell> targetCells = alignment
					.getCells(targetType);
			Collection<? extends Cell> sourceCells = alignment
					.getCells(sourceType);
			targetCells.retainAll(sourceCells);
			return !targetCells.isEmpty();
		}
	}

	/**
	 * Determines if the given cell is an augmentation.
	 * 
	 * @param cell
	 *            the cell
	 * @return if the cell is an augmentation
	 */
	public static boolean isAugmentation(Cell cell) {
		// check if cell is an augmentation cell
		return cell.getSource() == null || cell.getSource().isEmpty();
	}

	/**
	 * Get the parent entity definition for the given entity definition.
	 * 
	 * @param entity
	 *            the entity definition
	 * @return the parent entity definition or <code>null</code> if it has no
	 *         parent
	 */
	public static EntityDefinition getParent(EntityDefinition entity) {
		List<ChildContext> path = entity.getPropertyPath();

		if (path == null || path.isEmpty()) {
			// entity is a type and has no parent
			return null;
		} else {
			List<ChildContext> newPath = new ArrayList<ChildContext>(path);
			newPath.remove(newPath.size() - 1);
			return createEntity(entity.getType(), newPath,
					entity.getSchemaSpace(), entity.getFilter());
		}
	}

	/**
	 * Create an entity definition from a type and a child path.
	 * 
	 * @param type
	 *            the path parent
	 * @param path
	 *            the child path
	 * @param schemaSpace
	 *            the associated schema space
	 * @param filter
	 *            the entity filter on the type, may be <code>null</code>
	 * @return the created entity definition
	 */
	public static EntityDefinition createEntity(TypeDefinition type,
			List<ChildContext> path, SchemaSpaceID schemaSpace, Filter filter) {
		if (path == null || path.isEmpty()) {
			// entity is a type
			return new TypeEntityDefinition(type, schemaSpace, filter);
		} else if (path.get(path.size() - 1).getChild() instanceof PropertyDefinition) {
			// last element in path is a property
			return new PropertyEntityDefinition(type, path, schemaSpace, filter);
		} else {
			// last element is a child but no property
			return new ChildEntityDefinition(type, path, schemaSpace, filter);
		}
	}

	/**
	 * Get the entity definition with the default instance context which is a
	 * sibling to (or the same as) the given entity definition.
	 * 
	 * @param entity
	 *            the entity definition
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
			if (lastConext.getContextName() == null
					&& lastConext.getIndex() == null
					&& lastConext.getCondition() == null) {
				return entity;
			}
		}

		List<ChildContext> newPath = new ArrayList<ChildContext>(path);
		ChildDefinition<?> lastChild = newPath.get(newPath.size() - 1)
				.getChild();
		newPath.remove(newPath.size() - 1);
		newPath.add(new ChildContext(lastChild));
		return createEntity(entity.getType(), newPath, entity.getSchemaSpace(), 
				entity.getFilter());
	}

	/**
	 * Get the entity definition based on the given entity definition with the
	 * default instance context for each path entry.
	 * 
	 * @param entity
	 *            the entity definition
	 * @return the entity definition with the default context in all path
	 *         elements
	 */
	public static EntityDefinition getAllDefaultEntity(EntityDefinition entity) {
		List<ChildContext> path = entity.getPropertyPath();

		if (path == null || path.isEmpty()
				|| path.get(path.size() - 1).getContextName() == null) {
			return entity;
		}

		List<ChildContext> newPath = new ArrayList<ChildContext>();
		for (ChildContext context : path) {
			ChildContext newcontext = new ChildContext(context.getChild());
			newPath.add(newcontext);
		}
		return createEntity(entity.getType(), newPath, 
				entity.getSchemaSpace(), null);
	}

	/**
	 * Derive an entity definition from the given one but with a maximum path
	 * length.
	 * 
	 * @param entity
	 *            the entity definition
	 * @param pathLength
	 *            the maximum path length
	 * @return the entity definition derived from the given entity definition
	 *         but with the property path shortened if needed, otherwise the
	 *         given definition will be returned
	 */
	public static EntityDefinition deriveEntity(EntityDefinition entity,
			int pathLength) {
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

		return createEntity(entity.getType(), newPath, 
				entity.getSchemaSpace(), entity.getFilter());
	}

	/**
	 * Get a collection of property cells from a type cell
	 * 
	 * @param align
	 *            the alignment to get the property cells from
	 * @param tCell
	 *            the type cell
	 * @return the property cells
	 */
	public static Collection<? extends Cell> getPropertyCellsFromTypeCell(
			Alignment align, Cell tCell) {
		TypeEntityDefinition targetType = (TypeEntityDefinition) tCell
				.getTarget().values().iterator().next().getDefinition();
		Iterator<? extends Entity> it = tCell.getSource().values().iterator();
		Collection<TypeEntityDefinition> typeEntityCollection = new ArrayList<TypeEntityDefinition>();

		while (it.hasNext()) {
			typeEntityCollection.add((TypeEntityDefinition) it.next()
					.getDefinition());
		}

		return align.getPropertyCells(typeEntityCollection, targetType);

	}

	/**
	 * Determines if a given entity definition is a parent of another entity
	 * definition. 
	 * @param parent the parent
	 * @param child the potential child
	 * @return if the first entity definition is a parent of the second or if
	 *   both are equal
	 */
	public static boolean isParent(EntityDefinition parent,
			EntityDefinition child) {
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
		
		if (parentPath.size() >= childPath.size()) {
			// property path for parent is longer or equal, can't be parent of child
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
	 * States if the given entity definition or one of its children is mapped
	 * in the given alignment.
	 * @param entity the entity definition
	 * @param alignment the alignment
	 * @return if the alignment contains a relation where the given entity or
	 *   one of its children (including grand-children etc.) is involved
	 */
	public static boolean entityOrChildMapped(EntityDefinition entity, 
			Alignment alignment) {
		// check for a direct mapping
		if (!alignment.getCells(entity).isEmpty()) {
			return true;
		}
		
		// check for child mappings
		Collection<? extends Cell> typeCells = alignment.getCells(entity.getType(), entity.getSchemaSpace());
		for (Cell cell : typeCells) {
			if (entity.getSchemaSpace() == SchemaSpaceID.SOURCE
					&& cell.getSource() != null
					&& entityOrChildContained(entity, cell.getSource().values())) {
				return true;
			}
			if (entity.getSchemaSpace() == SchemaSpaceID.TARGET
					&& cell.getTarget() != null
					&& entityOrChildContained(entity, cell.getTarget().values())) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Determines if the given entity definition or one of its children is
	 * contained in the given entity candidates.
	 * @param entity the entity definition
	 * @param candidates the entity candidates to test
	 * @return if at least one of the entity candidates is the given entity
	 *   or a child (or grand-child etc.) 
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
	 * @param entityDef the entity definition
	 * @return the entity definition if it is a {@link TypeEntityDefinition},
	 *   otherwise a new type entity definition is created
	 */
	public static TypeEntityDefinition getTypeEntity(EntityDefinition entityDef) {
		if (entityDef instanceof TypeEntityDefinition) {
			return (TypeEntityDefinition) entityDef;
		}
		else {
			return new TypeEntityDefinition(entityDef.getType(), 
					entityDef.getSchemaSpace(), entityDef.getFilter());
		}
	}
	
	/**
	 * Get the context name of the given entity definition.
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
	 * @param entityDef the entity definition
	 * @return the context text or <code>null</code> if it is the default
	 *   context or the context has no text representation
	 */
	public static String getContextText(EntityDefinition entityDef) {
		List<ChildContext> path = entityDef.getPropertyPath();
		if (path != null && !path.isEmpty()) {
			ChildContext lastContext = path.get(path.size() - 1);
			if (lastContext.getIndex() != null) {
				return "[" + lastContext.getIndex() + "]";
			}
			else if (lastContext.getCondition() != null) {
				return getFilterText(
						lastContext.getCondition().getFilter());
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
	 * @param filter the filter, may be <code>null</code>
	 * @return the filter text or <code>null</code>
	 */
	private static String getFilterText(Filter filter) {
		String filterString = FilterDefinitionManager.getInstance().asString(filter);
		if (filterString != null) {
			int pos = filterString.indexOf(':');
			if (pos >= 0 && pos + 1 < filterString.length()) {
				filterString = filterString.substring(pos + 1);
			}
		}
		return filterString;
	}
	
}
