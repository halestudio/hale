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
	public boolean addCell(MutableCell cell) {
		//FIXME when are cells equal?! FIXME
		if (cells.contains(cell)) {
			cells.remove(cell);
			internalAdd(cell);
			return true;
		}
		else {
			internalAdd(cell);
			return false;
		}
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
			//XXX any way to determine if it's source or target related?
			if (associatedWith(cell.getSource(), entityDefinition)
					|| associatedWith(cell.getTarget(), entityDefinition)) {
				cells.add(cell);
			}
		}
		
		return cells;
	}
	
	private boolean associatedWith(
			ListMultimap<String, ? extends Entity> entities,
			EntityDefinition entityDef) {
		for (Entity entity : entities.values()) {
			if (entityDef.equals(entity.getDefinition())) {
				return true;
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
