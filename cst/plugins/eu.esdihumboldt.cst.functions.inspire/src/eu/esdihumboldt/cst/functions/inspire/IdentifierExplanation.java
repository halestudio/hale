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
 * Explanation class for the identifier function
 * 
 * @author Kevin Mais
 */
public class IdentifierExplanation extends AbstractCellExplanation implements IdentifierFunction {

	/**
	 * @see AbstractCellExplanation#getExplanation(Cell, boolean)
	 */
	@Override
	protected String getExplanation(Cell cell, boolean html) {
		Entity source = CellUtil.getFirstEntity(cell.getSource());
		Entity target = CellUtil.getFirstEntity(cell.getTarget());

		String country = CellUtil.getFirstParameter(cell, COUNTRY_PARAMETER_NAME);
		String provider = CellUtil.getFirstParameter(cell, DATA_PROVIDER_PARAMETER_NAME);
		String product = CellUtil.getFirstParameter(cell, PRODUCT_PARAMETER_NAME);
		String namespace = Identifier.getNamespace(country, provider, product, target
				.getDefinition().getType());

		String version = CellUtil.getFirstParameter(cell, VERSION);

		StringBuilder sb = new StringBuilder();
		sb.append("The {1} property is populated with an Inspire Identifier composed as follows: <br /><br />");
		sb.append("1. <i>localId</i> contains the value of the {0} property.<br />");
		sb.append("2. The <i>namespace</i> is <b>{2}</b>.<br />");
		if (version != null && !version.isEmpty()) {
			sb.append("3. <i>version</i> is set to <b>{3}</b>.");
		}
		else {
			version = CellUtil.getFirstParameter(cell, VERSION_NIL_REASON);
			if (version != null && !version.isEmpty()) {
				sb.append("3. The reason why the version is not set is given as <b>{3}</b>.");
			}
			else {
				sb.append("3. No reason for the missing version number is given.");
			}
		}

		String result = sb.toString();

		if (source != null) {
			result = MessageFormat.format(result,//
					formatEntity(source, html, true),//
					formatEntity(target, html, true),//
					namespace,//
					version);
		}

		if (!html) {
			result = result.replaceAll("<br />", "\n");
			result = result.replaceAll("<i>", "");
			result = result.replaceAll("</i>", "");
			result = result.replaceAll("<b>", "");
			result = result.replaceAll("</b>", "");
		}

		return result;
	}

}
