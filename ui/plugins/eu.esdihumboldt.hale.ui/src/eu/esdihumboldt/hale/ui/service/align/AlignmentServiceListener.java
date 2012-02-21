/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.service.align;

import eu.esdihumboldt.hale.common.align.model.Cell;

/**
 * Listener for alignment service events
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface AlignmentServiceListener {
	
	/**
	 * Called when the alignment has been cleared
	 */
	public void alignmentCleared();
	
	/**
	 * Called when cells have been added
	 * @param cells the cells that have been added
	 */
	public void cellsAdded(Iterable<Cell> cells);
	
	/**
	 * Called when an existing cell has been replaced by another.
	 * @param oldCell the old cell that has been replaced
	 * @param newCell the new cell that has replaced the other
	 */
	public void cellReplaced(Cell oldCell, Cell newCell);
	
	/**
	 * Called when an existing cell has been removed
	 * @param cell the cell that has been removed
	 */
	public void cellRemoved(Cell cell);

}
