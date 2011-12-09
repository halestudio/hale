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
import java.util.List;

import eu.esdihumboldt.hale.common.align.model.impl.ChildEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Alignment model utility methods.
 * @author Simon Templer
 */
public abstract class AlignmentUtil {

	/**
	 * Determines if the given cell is a type cell.
	 * @param cell the cell
	 * @return if the cell is a type cell
	 */
	public static boolean isTypeCell(Cell cell) {
		// check if cell is a type cell
		return cell.getTarget().values().iterator().next() instanceof Type;
	}
	
	/**
	 * Determines if the given alignment has any type relations.
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
	 * Determines if the given alignment contains a relation between the
	 * given types.
	 * @param alignment the alignment
	 * @param sourceType the source type, may be <code>null</code> for any 
	 *   source type
	 * @param targetType the target type, may be <code>null</code> for any
	 *   target type 
	 * @return if a relation between the given types exists in the alignment
	 */
	public static boolean hasTypeRelation(Alignment alignment, 
			TypeEntityDefinition sourceType, TypeEntityDefinition targetType) {
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
	 * @param cell the cell
	 * @return if the cell is an augmentation
	 */
	public static boolean isAugmentation(Cell cell) {
		// check if cell is an augmentation cell
		return cell.getSource().isEmpty();
	}
	
	/**
	 * Get the parent entity definition for the given entity definition.
	 * @param entity the entity definition
	 * @return the parent entity definition or <code>null</code> if it has no 
	 * parent
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
			return createEntity(entity.getType(), newPath, 
					entity.getSchemaSpace());
		}
	}

	/**
	 * Create an entity definition from a type and a child path
	 * @param type the path parent
	 * @param path the child path 
	 * @param schemaSpace the associated schema space
	 * @return the created entity definition
	 */
	public static EntityDefinition createEntity(TypeDefinition type, 
			List<ChildContext> path, SchemaSpaceID schemaSpace) {
		if (path == null || path.isEmpty()) {
			// entity is a type
			return new TypeEntityDefinition(type, schemaSpace);
		}
		else if (path.get(path.size() - 1).getChild() instanceof PropertyDefinition) {
			// last element in path is a property
			return new PropertyEntityDefinition(type, path, schemaSpace);
		}
		else {
			// last element is a child but no property
			return new ChildEntityDefinition(type, path, schemaSpace);
		}
	}
	
	/**
	 * Get the entity definition with the default instance context which
	 * is a sibling to (or the same as) the given entity definition.
	 * @param entity the entity definition
	 * @return the entity definition with the default context in the last
	 * path element
	 */
	public static EntityDefinition getDefaultEntity(EntityDefinition entity) {
		List<ChildContext> path = entity.getPropertyPath();
		
		if (path == null || path.isEmpty() || path.get(path.size() - 1).getContextName() == null) {
			return entity;
		}
		
		List<ChildContext> newPath = new ArrayList<ChildContext>(path);
		ChildDefinition<?> lastChild = newPath.get(newPath.size() - 1).getChild();
		newPath.remove(newPath.size() - 1);
		newPath.add(new ChildContext(lastChild));
		return createEntity(entity.getType(), newPath, entity.getSchemaSpace());
	}

	/**
	 * Get the entity definition based on the given entity definition with the 
	 * default instance context for each path entry.
	 * @param entity the entity definition
	 * @return the entity definition with the default context in all path 
	 * elements
	 */
	public static EntityDefinition getAllDefaultEntity(
			EntityDefinition entity) {
		List<ChildContext> path = entity.getPropertyPath();
		
		if (path == null || path.isEmpty() || path.get(path.size() - 1).getContextName() == null) {
			return entity;
		}
		
		List<ChildContext> newPath = new ArrayList<ChildContext>();
		for (ChildContext context : path) {
			ChildContext newcontext = new ChildContext(context.getChild());
			newPath.add(newcontext);
		}
		return createEntity(entity.getType(), newPath, entity.getSchemaSpace());
	}

	/**
	 * Derive an entity definition from the given one but with a maximum path 
	 * length.
	 * @param entity the entity definition
	 * @param pathLength the maximum path length
	 * @return the entity definition derived from the given entity definition
	 *   but with the property path shortened if needed, otherwise the given
	 *   definition will be returned 
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
		
		return createEntity(entity.getType(), newPath, entity.getSchemaSpace());
	}

}
