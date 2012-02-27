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

package eu.esdihumboldt.hale.common.align.model.functions.explanations;

import java.text.MessageFormat;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.functions.FormattedStringFunction;

/**
 * Explanation for formatted string cells.
 * @author Simon Templer
 */
public class FormattedStringExplanation implements CellExplanation, FormattedStringFunction {
	
	private static final String EXPLANATION_PATTERN = "Populates the {0} property with a string formatted according to this pattern:\n"
			+ "{1}\n\nSource property names in curly braces are replaced by the corresponding property value.";

	/**
	 * @see CellExplanation#getExplanation(Cell)
	 */
	@Override
	public String getExplanation(Cell cell) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		String pattern = CellUtil.getFirstParameter(cell, PARAMETER_PATTERN);
		
		if (target != null && pattern != null) {
			return MessageFormat.format(EXPLANATION_PATTERN, 
					"'" + target.getDefinition().getDefinition().getDisplayName() + "'",
					pattern);
		}
		
		return null;
	}

	/**
	 * @see CellExplanation#getExplanationAsHtml(Cell)
	 */
	@Override
	public String getExplanationAsHtml(Cell cell) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		String pattern = CellUtil.getFirstParameter(cell, PARAMETER_PATTERN);
		
		if (target != null && pattern != null) {
			return MessageFormat.format(EXPLANATION_PATTERN, 
					"<span style=\"font-style: italic;\">" + target.getDefinition().getDefinition().getDisplayName() + "</span>",
					"<span style=\"font-weight: bold;\">" + pattern + "</span>").replaceAll("\n", "<br />");
		}
		
		return null;
	}

}
