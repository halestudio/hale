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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.compatibility;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * The purpose of this class is to check up compatibility of transformation
 * functions dependent on the currently selected export compatibility mode e.g.:
 * HALE/CST, XSLT
 * 
 * @author Sebastian Reinhardt
 */
public interface CompatibilityMode {

	/**
	 * checks the functions id of compatibility
	 * 
	 * @param id the functions id
	 * @param serviceProvider the service provider, may be <code>null</code> if
	 *            none is available in the context
	 * @return true, if the function is compatible
	 */
	public boolean supportsFunction(String id, @Nullable ServiceProvider serviceProvider);

	/**
	 * checks the functions compatibility through its cell
	 * 
	 * @param cell the cell of the function
	 * @return true, if the function is compatible
	 */
	public boolean supportsCell(Cell cell);

}
