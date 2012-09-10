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

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;

/**
 * The {@link AlignmentService} provides access to the currently loaded
 * alignment.
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public interface AlignmentService {

	/**
	 * Get the current alignment
	 * 
	 * @return the entire {@link Alignment} as currently represented in the
	 *         Alignment Model.
	 */
	public Alignment getAlignment();

	/**
	 * Adds the cells contained in the given alignment to the current alignment.
	 * If cells with the same entities and transformations already exist they
	 * will be replaced.
	 * 
	 * @param alignment the alignment to add
	 */
	public void addOrUpdateAlignment(MutableAlignment alignment);

	/**
	 * Adds the given cell to the current alignment.
	 * 
	 * @param cell the cell to add
	 */
	public void addCell(MutableCell cell);

	/**
	 * Replace a cell with a new cell.
	 * 
	 * @param oldCell the old cell that should be removed from the alignment
	 * @param newCell the new cell that should be added to the alignment
	 */
	public void replaceCell(Cell oldCell, MutableCell newCell);

	/**
	 * Removes the given cell
	 * 
	 * @param cell the cell to remove
	 */
	public void removeCell(Cell cell);

	/**
	 * Invoke this operation if you want to clear out all alignments stored.
	 * This method is required when one wants to start working on a new
	 * alignment.
	 */
	public void clean();

	/**
	 * Adds a listener to the service
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(AlignmentServiceListener listener);

	/**
	 * Removes a listener to the service
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(AlignmentServiceListener listener);

}
