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

package eu.esdihumboldt.hale.ui.service.align;

import java.util.Map;

import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunction;
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
	 * Add a custom function.
	 * 
	 * @param function the custom function to add
	 */
	public void addCustomPropertyFunction(CustomPropertyFunction function);

	/**
	 * Remove the custom function with the given identifier.
	 * 
	 * @param id the function identifier
	 */
	public void removeCustomPropertyFunction(String id);

	/**
	 * Adds the cells contained in the given alignment to the current alignment.
	 * <br>
	 * <br>
	 * If cells with the same entities and transformations already exist they
	 * will be replaced. THIS IS NOT TRUE, yet at least.
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
	 * Replace cells with a new cells.
	 * 
	 * @param cells a mapping from old cells that should be removed from the
	 *            alignment to new cells that should be added to the alignment
	 */
	public void replaceCells(Map<? extends Cell, MutableCell> cells);

	/**
	 * Removes the given cells
	 * 
	 * @param cells the cells to remove
	 */
	public void removeCells(Cell... cells);

	/**
	 * Sets a property for the cell.
	 * 
	 * @param cellId the cell id to set the property from.
	 * @param propertyName the name of the property to set.
	 * @param property the value of the property.
	 */
	public void setCellProperty(String cellId, String propertyName, Object property);

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

	/**
	 * Adds a base alignment.
	 * 
	 * @param loader the loader of the base alignment
	 * @return whether a base alignment was successfully added
	 */
	public boolean addBaseAlignment(BaseAlignmentLoader loader);
}
