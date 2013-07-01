/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.string;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;

/**
 * Explanation for {@link RegexAnalysis}.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class RegexAnalysisExplanation extends AbstractCellExplanation {

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation#getExplanation(eu.esdihumboldt.hale.common.align.model.Cell,
	 *      boolean)
	 */
	@Override
	protected String getExplanation(Cell cell, boolean html) {
		Entity source = CellUtil.getFirstEntity(cell.getSource());
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		String regex = CellUtil.getFirstParameter(cell, RegexAnalysis.PARAMETER_REGEX_PATTERN).as(
				String.class);
		String outFormat = CellUtil.getFirstParameter(cell, RegexAnalysis.PARAMETER_OUTPUT_FORMAT)
				.as(String.class);

		if (source != null && target != null && outFormat != null && regex != null) {
			String formatEntity_0 = formatEntity(source, html, true);
			String formatEntity_1 = formatEntity(target, html, true);
			String quoteText_2 = quoteText(regex, html);
			String quoteText_3 = quoteText(outFormat, html);
			StringBuilder sb = new StringBuilder();
			if (!html) {
				sb.append("Populates the target property\n");
				sb.append(formatEntity_1);
				sb.append("\nwith the groups captured from the regular expression analysis on the source property\n");
				sb.append(formatEntity_0);
				sb.append("\nThe regular expression analysis is carried out basing on the pattern\n");
				sb.append(quoteText_2);
				sb.append("\nand applying an output format\n");
				sb.append(quoteText_3);
				sb.append("\nto the extracted regular expression groups.");
				sb.append("\n");
				sb.append("\nAn explained example of regular expression groups can be found in the help of Hale.");
				sb.append("\nGeneral information about regular expression groups can be found at: http://www.regular-expressions.info/brackets.html");
			}
			else {
				sb.append("Populates the target property:<br>");
				sb.append("<p><b>" + formatEntity_1 + "</b></p>");
				sb.append("with the groups captured from the regular expression analysis on the source property:<br>");
				sb.append("<p><b>" + formatEntity_0 + "</b></p>");
				sb.append("The regular expression analysis is carried out basing on the pattern:");
				sb.append("<br>");
				sb.append("<p><b>" + quoteText_2 + "</b></p>");
				sb.append("and applying an output format:<br>");
				sb.append("<p><b>" + quoteText_3 + "</b></p>");
				sb.append("to the extracted regular expression groups. <br>");
				sb.append("<br>");
				sb.append("<p>An explained example of regular expression groups can be found in the help of Hale.</p>");
				sb.append("<p>General information about regular expression groups can be found at the url: ");
				sb.append("http://www.regular-expressions.info/brackets.html<br>");
				sb.append("</p>");
			}
			return sb.toString();
		}

		return null;
	}
}
