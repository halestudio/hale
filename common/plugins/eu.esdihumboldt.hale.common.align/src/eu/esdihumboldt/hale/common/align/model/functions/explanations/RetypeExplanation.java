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

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;

/**
 * Explanation for the retype function.
 * @author Simon Templer
 */
public class RetypeExplanation implements CellExplanation {

	/**
	 * @see CellExplanation#getExplanation(Cell)
	 */
	@Override
	public String getExplanation(Cell cell) {
		Entity source = CellUtil.getFirstEntity(cell.getSource());
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		
		if (source != null && target != null) {
			return MessageFormat.format("Creates a {1} instance for each {0} instance in the source data set.", 
					source.getDefinition().getDefinition().getDisplayName(),
					target.getDefinition().getDefinition().getDisplayName());
		}
		
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.CellExplanation#getExplanationAsHtml(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public String getExplanationAsHtml(Cell cell) {
		// TODO Auto-generated method stub
		return null;
	}

}
