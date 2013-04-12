/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.common.service.compatibility;

import java.util.List;

import eu.esdihumboldt.hale.common.align.model.Cell;

/**
 * Listener for compatibility services
 * 
 * @author Sebastian Reinhardt
 */
public interface CompatibilityServiceListener {

	/**
	 * processes the changes if the compatibility mode changed
	 * 
	 * @param isCompatible states if the compatibility is still fully given
	 * @param incompatibleCells the incompatible cells, null if all cells are
	 *            compatible
	 */
	public void compatibilityChanged(boolean isCompatible, List<Cell> incompatibleCells);

}
