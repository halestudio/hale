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
import java.util.List;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.functions.FormattedStringFunction;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;

/**
 * Explanation for formatted string cells.
 * @author Simon Templer
 */
public class FormattedStringExplanation extends AbstractCellExplanation implements FormattedStringFunction {
	private static final String EXPLANATION_PATTERN = "Populates the {0} property with a string formatted according to this pattern:\n"
			+ "{1}\nSource property names in curly braces are replaced by the corresponding property value, if the context condition/index matches, otherwise the value isn't set.";

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation#getExplanation(eu.esdihumboldt.hale.common.align.model.Cell, boolean)
	 */
	@Override
	protected String getExplanation(Cell cell, boolean html) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		String pattern = CellUtil.getFirstParameter(cell, PARAMETER_PATTERN);
		List<? extends Entity> sources = cell.getSource().get(ENTITY_VARIABLE);

		if (target != null && pattern != null) {
			if (html)
				pattern = "<pre>" + pattern + "</pre>";
			String explanation = MessageFormat.format(EXPLANATION_PATTERN, 
					formatEntity(target, html, true), pattern);
			if (html)
				explanation = explanation.replaceAll("\n", "<br />");
			if (html) {
				StringBuilder sb = new StringBuilder();
				sb.append("<br>Replacement table:<br>");
				sb.append("<table border=\"1\"><tr><th>Variable name</th><th>Value of the following property</th></tr>");
				for (Entity entity : sources)
					sb.append(String.format("<tr><td>%s</td><td>%s</td></tr>", '{' + getEntityNameWithoutCondition(entity) + '}',
							formatEntity(entity, true, false)));
				sb.append("</table>");
				explanation += sb.toString();
			}
			return explanation;
		}

		return null;
	}
}
