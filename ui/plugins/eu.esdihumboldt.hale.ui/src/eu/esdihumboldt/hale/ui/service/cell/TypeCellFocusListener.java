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
 * Cell listener Interface. Listens for {@link TypeCellFocusService} events.
 * 
 * @author Yasmina Kammeyer
 */
public interface TypeCellFocusListener {

	/**
	 * Called when the data of service changed.
	 * 
	 * @param cell The new data
	 */
	public void dataChanged(Cell cell);

}
