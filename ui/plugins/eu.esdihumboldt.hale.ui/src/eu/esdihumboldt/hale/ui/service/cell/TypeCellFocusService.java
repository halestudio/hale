/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.service.cell;

import eu.esdihumboldt.hale.common.align.model.Cell;

/**
 * Cell Service Interface to provide a Cell.
 * 
 * @author Yasmina Kammeyer
 */
public interface TypeCellFocusService {

	/**
	 * Pushes the cell that will be provided to listener
	 * 
	 * @param cell The Cell to set
	 */
	public void setCell(Cell cell);

	/**
	 * Returns the last selected Cell. Can be used for initialization process.
	 * 
	 * @return The last selected Type Cell or null
	 */
	public Cell getLastSelectedTypeCell();

	/**
	 * Adds a listener to service
	 * 
	 * @param listener The listener to add
	 */
	public void addListener(TypeCellFocusListener listener);

	/**
	 * Remove a listener from service
	 * 
	 * @param listener The listener to remove
	 */
	public void removeListener(TypeCellFocusListener listener);
}
