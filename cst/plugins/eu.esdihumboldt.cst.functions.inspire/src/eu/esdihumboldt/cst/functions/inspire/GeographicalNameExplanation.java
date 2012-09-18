/*
 * Copyright (c) 2012 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.inspire;

import java.text.MessageFormat;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;

/**
 * Explanation class for the geographical name function
 * 
 * @author Kevin Mais
 * 
 */
public class GeographicalNameExplanation extends AbstractCellExplanation implements
		GeographicalNameFunction {

	private static final String EXPLANATION_PATTERN = "This function creates the INSPIRE-compliant geographical name based on value of the {0} property and the parameters set by the page. <br />";

	@Override
	protected String getExplanation(Cell cell, boolean html) {
		Entity source = CellUtil.getFirstEntity(cell.getSource());
		Entity target = CellUtil.getFirstEntity(cell.getTarget());

		StringBuilder sb = new StringBuilder();
		sb.append(EXPLANATION_PATTERN);
		sb.append("<br />");
		sb.append("The {1} property consists of the <i>grammatical gender/number, language, name status, nativeness, pronunciation ipa/soundlink, source of name</i> and an amount of <i>spellings</i> based on the quantity of the source properties.<br />");
		sb.append("<br />");
		sb.append("Each spelling is made of values for <i>script</i>, <i>text</i> and <i>transliterationScheme</i>. The <i>text</i> values are the values from the source properties, e.g. {0}. The <i>script/transliterationScheme</i> can be set manually.");

		String result = sb.toString();

		if (html && source != null && target != null) {
			return MessageFormat.format(result, formatEntity(source, true, true),
					formatEntity(target, true, true));
		}
		else {
			result.replaceAll("<br />", "\n");
			result.replaceAll("<i>", "");
			result.replaceAll("</i>", "");
			result.replaceAll("<b>", "");
			result.replaceAll("</b>", "");
			return MessageFormat.format(result, formatEntity(source, false, true),
					formatEntity(target, false, true));
		}
	}

}
