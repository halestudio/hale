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

package eu.esdihumboldt.cst.functions.groovy;

import java.text.MessageFormat;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;

/**
 * Explanation for groovy cells.
 * 
 * @author Kai Schwierczek
 */
public class GroovyExplanation implements CellExplanation {
	private static final String EXPLANATION_PATTERN = "Populates the {0} property with the result of the following groovy script:\n"
			+ "{1}\nSource property names are bind to their value.";

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.CellExplanation#getExplanation(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public String getExplanation(Cell cell) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		String script = CellUtil.getFirstParameter(cell, GroovyTransformation.PARAMETER_SCRIPT);
		
		if (target != null && script != null) {
			return MessageFormat.format(EXPLANATION_PATTERN, 
					"\"" + target.getDefinition().getDefinition().getDisplayName() + "\"",
					script);
		}
		
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.CellExplanation#getExplanationAsHtml(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public String getExplanationAsHtml(Cell cell) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		String script = CellUtil.getFirstParameter(cell, GroovyTransformation.PARAMETER_SCRIPT);
		
		if (target != null && script != null) {
			return MessageFormat.format(EXPLANATION_PATTERN, 
					"<span style=\"font-style: italic;\">" + target.getDefinition().getDefinition().getDisplayName() + "</span>",
					"<pre>" + script + "</pre>").replaceAll("\n", "<br />");
		}
		
		return null;
	}
}
