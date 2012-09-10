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

package eu.esdihumboldt.cst.functions.geometric;

import java.text.MessageFormat;
import java.util.List;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;

/**
 * Explanation for {@link NetworkExpansion} cells.
 * 
 * @author Simon Templer
 * @author Kai Schwierczek
 */
public class NetworkExpansionExplanation extends AbstractCellExplanation implements
		NetworkExpansionFunction {

	private static final String EXPLANATION_PATTERN = "Takes a geometry found in the {0} property and creates a buffer geometry. The buffer geometry is assigned to the {1} property in the target type.\n"
			+ "The following expression specifies the buffer size used:\n"
			+ "{2}\n"
			+ "Source property variables in the expression are replaced by the corresponding property value, if the context condition/index matches, otherwise the value isn't set.";

	/**
	 * @see AbstractCellExplanation#getExplanation(Cell, boolean)
	 */
	@Override
	protected String getExplanation(Cell cell, boolean html) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		String expression = CellUtil.getFirstParameter(cell, PARAMETER_BUFFER_WIDTH);
		List<? extends Entity> variables = cell.getSource().get(ENTITY_VARIABLE);
		List<? extends Entity> geom = cell.getSource().get(null);

		if (target != null && expression != null) {
			if (html)
				expression = "<pre>" + expression + "</pre>";
			String explanation = MessageFormat.format(EXPLANATION_PATTERN,
					formatEntity(geom.get(0), html, true), formatEntity(target, html, true),
					expression);
			if (html)
				explanation = explanation.replaceAll("\n", "<br />");
			if (html) {
				StringBuilder sb = new StringBuilder(); // TODO unify the
														// replacement tables by
														// introducing a common
														// method to produce
														// them
				sb.append("<br /><br />Replacement table:<br />");
				sb.append("<table border=\"1\"><tr><th>Variable name</th><th>Value of the following property</th></tr>");
				for (Entity entity : variables)
					sb.append(String.format("<tr><td>%s</td><td>%s</td></tr>",
							getEntityNameWithoutCondition(entity),
							formatEntity(entity, true, false)));
				sb.append("</table>");
				explanation += sb.toString();
			}
			return explanation;
		}

		return null;
	}
}
