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

package eu.esdihumboldt.hale.ui.views.properties.cell.explanation;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.ui.views.properties.cell.AbstractCellFilter;

/**
 * Filter for cell explanation section.
 * @author Simon Templer
 */
public class HtmlExplanationCellFilter extends AbstractCellFilter {

	@Override
	public boolean isFiltered(Cell cell) {
		AbstractFunction<?> function = FunctionUtil.getFunction(cell.getTransformationIdentifier());
		if (function != null) {
			CellExplanation explanation =  function.getExplanation();
			if (explanation != null) {
				String text = explanation.getExplanationAsHtml(cell);
				if (text != null) {
					return false;
				}
				text = explanation.getExplanation(cell);
				if (text != null) {
					return false;
				}
			}
		}
		
		return true;
	}

}
