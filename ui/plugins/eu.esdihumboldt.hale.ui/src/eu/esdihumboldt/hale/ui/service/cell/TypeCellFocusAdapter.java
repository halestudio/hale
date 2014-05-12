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
 * The Adapter of the listener.
 * 
 * @author Yasmina Kammeyer
 */
public class TypeCellFocusAdapter implements TypeCellFocusListener {

	/**
	 * @see eu.esdihumboldt.hale.ui.service.cell.TypeCellFocusListener#dataChanged(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public void dataChanged(Cell cell) {
		// Override me
	}

}
