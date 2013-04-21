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

import java.text.MessageFormat;

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
			return MessageFormat
					.format("Populates the {1} property with regex-analysing the {0} property using the regular expression {2} and applying the output format {3} to the contained regex groups.",
							formatEntity(source, html, true), formatEntity(target, html, true),
							quoteText(regex, html), quoteText(outFormat, html));
		}

		return null;
	}
}
