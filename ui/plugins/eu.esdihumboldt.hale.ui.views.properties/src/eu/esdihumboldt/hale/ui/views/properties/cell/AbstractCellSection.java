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

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTreeUtil;
import eu.esdihumboldt.hale.ui.views.properties.AbstractSingleObjectSection;

/**
 * Cell section base class.
 * @author Simon Templer
 */
public abstract class AbstractCellSection extends
		AbstractSingleObjectSection {

	private Cell cell;
	
	/**
	 * @see AbstractSingleObjectSection#setInput(Object)
	 */
	@Override
	protected void setInput(Object input) {
		input = TransformationTreeUtil.extractObject(input);

		if (input instanceof Cell) {
			setCell((Cell) input);
		}
	}

	/**
	 * Set the input cell.
	 * @param input the cell
	 */
	private void setCell(Cell input) {
		this.cell = input;
	}

	/**
	 * Get the current cell.
	 * @return the cell
	 */
	public Cell getCell() {
		return cell;
	}

}
