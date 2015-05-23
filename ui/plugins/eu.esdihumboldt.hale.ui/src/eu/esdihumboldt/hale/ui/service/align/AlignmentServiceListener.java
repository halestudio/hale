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
	 * 
	 * @param cells the cells that have been added
	 */
	public void cellsAdded(Iterable<Cell> cells);

	/**
	 * Called when an existing cell has been replaced by another.
	 * 
	 * @param cells a mapping from replaced cell to new cell
	 */
	public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells);

	/**
	 * Called when existing cells have been removed
	 * 
	 * @param cells the cells that have been removed
	 */
	public void cellsRemoved(Iterable<Cell> cells);

	/**
	 * Called when existing cells have been modified
	 * 
	 * @param cells the cells that have been modified
	 * @param propertyName the name of the property that changed
	 */
	public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName);

	/**
	 * Called when the custom function definitions have changed
	 * (added/removed/replaced).
	 */
	public void customFunctionsChanged();

	/**
	 * Called when the alignment had some unspecified update.
	 */
	public void alignmentChanged();

}
