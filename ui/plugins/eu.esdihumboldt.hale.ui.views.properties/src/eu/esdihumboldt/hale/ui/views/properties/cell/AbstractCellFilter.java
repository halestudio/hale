/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
