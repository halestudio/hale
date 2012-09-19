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

package eu.esdihumboldt.hale.ui.views.properties.cell;

import org.eclipse.jface.viewers.IFilter;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTreeUtil;

/**
 * The default filter for all filters
 * 
 * @author Patrick Lieb
 */
public abstract class AbstractCellFilter implements IFilter {

	/**
	 * Determine if a cell is invalid and thus should be rejected by the filter.
	 * 
	 * @param input the cell
	 * @return <code>true</code> if the cell should be rejected by the filter,
	 *         <code>false</code> otherwise
	 */
	public abstract boolean isFiltered(Cell input);

	/**
	 * @see IFilter#select(Object)
	 */
	@Override
	public boolean select(Object input) {
		input = TransformationTreeUtil.extractObject(input);

		if (input instanceof Cell) {
			return !isFiltered((Cell) input);
		}

		return false;
	}
}
