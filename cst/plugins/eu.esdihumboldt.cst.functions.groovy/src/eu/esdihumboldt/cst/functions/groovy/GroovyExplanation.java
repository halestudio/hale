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
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;

/**
 * Explanation for groovy cells.
 * 
 * @author Kai Schwierczek
 */
public class GroovyExplanation extends AbstractCellExplanation {
	private static final String EXPLANATION_PATTERN = "Populates the {0} property with the result of the following groovy script:\n"
			+ "{1}\nSource property names are bind to their value.";

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation#getExplanation(eu.esdihumboldt.hale.common.align.model.Cell, boolean)
	 */
	@Override
	protected String getExplanation(Cell cell, boolean html) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		String script = CellUtil.getFirstParameter(cell, GroovyTransformation.PARAMETER_SCRIPT);

		if (target != null && script != null) {
			if (html)
				script = "<pre>" + script + "</pre>";
			String explanation = MessageFormat.format(EXPLANATION_PATTERN, formatEntity(target, html),
					script);
			if (html)
				explanation = explanation.replaceAll("\n", "<br />");
			return explanation;
		}

		return null;
	}
}
