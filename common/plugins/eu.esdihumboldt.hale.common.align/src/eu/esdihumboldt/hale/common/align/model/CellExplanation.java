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

/**
 * Provides a human readable explanation for a cell.
 * 
 * @author Simon Templer
 */
public interface CellExplanation {

	/**
	 * Get the explanation for the given cell.
	 * 
	 * @param cell the cell
	 * @return the cell explanation, <code>null</code> if none is available
	 */
	public String getExplanation(Cell cell);

	/**
	 * Get the explanation in html format for the given cell
	 * 
	 * @param cell the cell
	 * @return the cell explanation in html format, <code>null</code> if none is
	 *         available
	 */
	public String getExplanationAsHtml(Cell cell);

}
