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

package eu.esdihumboldt.hale.common.align.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Default alignment implementation
 * @author Simon Templer
 */
public class DefaultAlignment implements Alignment, MutableAlignment {
	
	//FIXME simple collection should be replaced later on
	private final Collection<MutableCell> cells = new ArrayList<MutableCell>();
	private final Collection<MutableCell> typeCells = new ArrayList<MutableCell>();

	/**
	 * @see MutableAlignment#addCell(MutableCell)
	 */
	@Override
	public void addCell(MutableCell cell) {
		internalAdd(cell);
	}
	
	private void internalAdd(MutableCell cell) {
		cells.add(cell);
		
		// check if cell is a type cell
		if (AlignmentUtil.isTypeCell(cell)) {
			typeCells.add(cell);
		}
	}

	/**
	 * @see Alignment#getCells(EntityDefinition)
	 */
	@Override
	public Collection<? extends Cell> getCells(EntityDefinition entityDefinition) {
		List<Cell> cells = new ArrayList<Cell>(); 
		
		for (Cell cell : getCells()) {
			// determine if the cell is associated to the entity definition
			boolean isAssociated;
			switch (entityDefinition.getSchemaSpace()) {
			case SOURCE:
				isAssociated = associatedWith(cell.getSource(), entityDefinition);
				break;
			case TARGET:
				isAssociated = associatedWith(cell.getTarget(), entityDefinition);
				break;
			default:
				isAssociated = associatedWith(cell.getSource(), entityDefinition)
						|| associatedWith(cell.getTarget(), entityDefinition); 
			}
			
			if (isAssociated) {
				cells.add(cell);
			}
		}
		
		return cells;
	}
	
	/**
	 * Determines if the given entity definition is contained in the given
	 * entity list.
	 * @param entities the (named) entity list
	 * @param entityDef the entity definition
	 * @return if the entity definition is contained in the given entities
	 */
	private boolean associatedWith(
			ListMultimap<String, ? extends Entity> entities,
			EntityDefinition entityDef) {
		if (entities != null) {
			for (Entity entity : entities.values()) {
				if (entityDef.equals(entity.getDefinition())) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * @see Alignment#getPropertyCells(Iterable, TypeEntityDefinition)
	 */
	@Override
	public Collection<? extends Cell> getPropertyCells(
			Iterable<TypeEntityDefinition> sourceTypes,
			TypeEntityDefinition targetType) {
		List<Cell> result = new ArrayList<Cell>();
		
		for (Cell cell : cells) {
			if (!AlignmentUtil.isTypeCell(cell)) {
				// cell is a property cell
				if (sourceTypes == null || AlignmentUtil.isAugmentation(cell)) {
					// cell is an augmentation or we accept any source type
					if (associatedWithType(cell.getTarget(), 
							Collections.singleton(targetType))) {
						// cell is associated to the target type
						result.add(cell);
					}
				}
				else {
					// cell is a property mapping
					if (associatedWithType(cell.getSource(), 
							sourceTypes)
						&& associatedWithType(cell.getTarget(), 
							Collections.singleton(targetType))) {
						// cell is associated to a relation between a source
						// type and the target type
						result.add(cell);
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Determines if any of the given entities is associated to at least one of
	 * the given type entity definitions. 
	 * @param entities the entities
	 * @param types the type entity definitions
	 * @return if there is an entity associated to one of the types
	 */
	private boolean associatedWithType(
			ListMultimap<String, ? extends Entity> entities,
			Iterable<TypeEntityDefinition> types) {
		for (Entity entity : entities.values()) {
			TypeDefinition entityType = entity.getDefinition().getType();
			for (TypeEntityDefinition type : types) {
				if (entityType.equals(type.getDefinition())) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * @see Alignment#getCells()
	 */
	@Override
	public Collection<MutableCell> getCells() {
		return Collections.unmodifiableCollection(cells);
	}

	/**
	 * @see MutableAlignment#removeCell(Cell)
	 */
	@Override
	public boolean removeCell(Cell cell) {
		boolean removed = cells.remove(cell);
		if (removed) {
			typeCells.remove(cell);
		}
		return removed;
	}

	/**
	 * @see Alignment#getTypeCells()
	 */
	@Override
	public Collection<MutableCell> getTypeCells() {
		return Collections.unmodifiableCollection(typeCells);
	}

}
