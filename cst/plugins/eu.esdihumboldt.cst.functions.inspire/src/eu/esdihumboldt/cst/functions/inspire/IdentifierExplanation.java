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

package eu.esdihumboldt.cst.functions.inspire;

import java.text.MessageFormat;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;

/**
 * Explanation class for the identifier function
 * 
 * @author Kevin Mais
 *
 */
public class IdentifierExplanation extends AbstractCellExplanation implements IdentifierFunction {

	private static final String EXPLANATION_PATTERN = "This function creates the INSPIRE-compliant identifiers based on localID of the {0} property and the parameters set by the page: <br />";
	
	/**
	 * @see eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation#getExplanation(eu.esdihumboldt.hale.common.align.model.Cell, boolean)
	 */
	@Override
	protected String getExplanation(Cell cell, boolean html) {
		Entity source = CellUtil.getFirstEntity(cell.getSource());
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		
		ListMultimap<String, String> params = cell.getTransformationParameters();
		String version = params.get(VERSION).get(0);
		String targetName = target.getDefinition().getType().getDisplayName();
		
		StringBuilder sb = new StringBuilder();
		sb.append(EXPLANATION_PATTERN);
		sb.append("The {1} property is composed as follows: <br />");
		sb.append("1.<b>localId</b> contains the value of the {0} property <br />" +
				"2.<b>namespace</b> consists of the values of " + "<i>" + COUNTRY_PARAMETER_NAME + "</i>" +", " + "<i>" + DATA_PROVIDER_PARAMETER_NAME + "</i>" + ", " + "<i>" + PRODUCT_PARAMETER_NAME + "</i>" + " and the name of <i>" + targetName + "</i> property" +"<br />");
		if(version != ""&& version != " " && version != null && !version.isEmpty()) {
			sb.append("3.<b>" + VERSION + "</b> contains the value set for itself.");
		} else {
			sb.append("3.<b>" + VERSION_NIL_REASON + "</b> contains the value set for itself.");
		}
		
		String result = sb.toString();
		
		if (html && source != null) {
			return MessageFormat.format(result, formatEntity(source, true, true), formatEntity(target, true, true));
		}
		
		else {
			result.replaceAll("<br />", "\n");
			result.replaceAll("<i>", "");
			result.replaceAll("</i>", "");
			result.replaceAll("<b>", "");
			result.replaceAll("</b>", "");
			return MessageFormat.format(result, formatEntity(source, false, true), formatEntity(target, false, true));
		}
	}

}
