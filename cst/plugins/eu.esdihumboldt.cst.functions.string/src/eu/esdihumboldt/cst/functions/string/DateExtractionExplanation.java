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

package eu.esdihumboldt.cst.functions.string;

import java.text.MessageFormat;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;

/**
 * Explanation for date extraction cells.
 * 
 * @author Kai Schwierczek
 */
public class DateExtractionExplanation implements CellExplanation {
	/**
	 * @see eu.esdihumboldt.hale.common.align.model.CellExplanation#getExplanation(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public String getExplanation(Cell cell) {
		Entity source = CellUtil.getFirstEntity(cell.getSource());
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		String format = CellUtil.getFirstParameter(cell, DateExtraction.PARAMETER_DATE_FORMAT);
		
		if (target != null && format != null) {
			return MessageFormat.format("Populates the \"{1}\" property with a date created by parsing the \"{0}\" property using the format \"{2}\".", 
					source.getDefinition().getDefinition().getDisplayName(), 
					target.getDefinition().getDefinition().getDisplayName(), format);
		}
		
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.CellExplanation#getExplanationAsHtml(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public String getExplanationAsHtml(Cell cell) {
		return null;
	}
}
