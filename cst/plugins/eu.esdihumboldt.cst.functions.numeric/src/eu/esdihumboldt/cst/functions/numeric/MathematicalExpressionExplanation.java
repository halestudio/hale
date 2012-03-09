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

package eu.esdihumboldt.cst.functions.numeric;

import java.text.MessageFormat;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;

/**
 * Explanation for mathematical expression cells.
 * 
 * @author Kai Schwierczek
 */
public class MathematicalExpressionExplanation implements CellExplanation {
	private static final String EXPLANATION_PATTERN = "Populates the {0} property with a number resulting from the following calculation:\n"
			+ "{1}\n\nSource property names are replaced by the corresponding property value.";

	/**
	 * @see CellExplanation#getExplanation(Cell)
	 */
	@Override
	public String getExplanation(Cell cell) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		String expression = CellUtil.getFirstParameter(cell, MathematicalExpression.PARAMETER_EXPRESSION);
		
		if (target != null && expression != null) {
			return MessageFormat.format(EXPLANATION_PATTERN, 
					"\"" + target.getDefinition().getDefinition().getDisplayName() + "\"",
					expression);
		}
		
		return null;
	}

	/**
	 * @see CellExplanation#getExplanationAsHtml(Cell)
	 */
	@Override
	public String getExplanationAsHtml(Cell cell) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		String expression = CellUtil.getFirstParameter(cell, MathematicalExpression.PARAMETER_EXPRESSION);
		
		if (target != null && expression != null) {
			return MessageFormat.format(EXPLANATION_PATTERN, 
					"<span style=\"font-style: italic;\">" + target.getDefinition().getDefinition().getDisplayName() + "</span>",
					"<span style=\"font-weight: bold;\">" + expression + "</span>").replaceAll("\n", "<br />");
		}
		
		return null;
	}
}
