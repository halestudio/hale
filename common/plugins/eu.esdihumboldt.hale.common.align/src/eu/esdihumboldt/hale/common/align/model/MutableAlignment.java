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

import java.net.URI;
import java.util.Collection;

/**
 * Mutable {@link Alignment} which is used where changes to the alignment are
 * allowed.
 * 
 * @author Simon Templer
 */
public interface MutableAlignment extends Alignment {

	/**
	 * Add a cell to the alignment.
	 * 
	 * @param cell the cell to add. It should be already configured, especially
	 *            with the cell target. A cell id may be generated for new cells
	 */
	public void addCell(Cell cell);

	/**
	 * Remove a cell
	 * 
	 * @param cell the cell to remove
	 * @return if the cell was present and removed
	 */
	public boolean removeCell(Cell cell);

	/**
	 * Adds a base alignment.
	 * 
	 * @param prefix the alignment prefix
	 * @param alignment the new base alignment
	 * @param cells the cells to be added from the alignment
	 */
	public void addBaseAlignment(String prefix, URI alignment, Collection<BaseAlignmentCell> cells);

}
