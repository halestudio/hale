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

package eu.esdihumboldt.hale.common.align.model.impl;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.model.Entity;

/**
 * Abstract cell explanation implementation.
 * @author Simon Templer
 */
public abstract class AbstractCellExplanation implements CellExplanation {

	/**
	 * @see CellExplanation#getExplanation(Cell)
	 */
	@Override
	public String getExplanation(Cell cell) {
		return getExplanation(cell, false);
	}

	/**
	 * @see CellExplanation#getExplanationAsHtml(Cell)
	 */
	@Override
	public String getExplanationAsHtml(Cell cell) {
		return getExplanation(cell, true);
	}

	/**
	 * Get the explanation string in the specified format.
	 * @param cell the cell to create an explanation for
	 * @param html if the format should be HMTL, otherwise the format is just text
	 * @return the explanation or <code>null</code>
	 */
	protected abstract String getExplanation(Cell cell, boolean html);
	
	/**
	 * Format an entity for inclusion in an explanation.
	 * @param entity the entity
	 * @param html if the format should be HMTL, otherwise the format is just text
	 * @return the formatted entity name
	 */
	protected String formatEntity(Entity entity, boolean html) {
		String name = entity.getDefinition().getDefinition().getDisplayName();
		if (html) {
			return "<span style=\"font-style: italic;\">" + name + "</span>";
		}
		else {
			return "'" + name + "'";
		}
	}

}
