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

import java.util.List;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
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
	 * @param entity the entity, may be <code>null</code>
	 * @param html if the format should be HMTL, otherwise the format is just text
	 * @return the formatted entity name or <code>null</code> in case of <code>null</code> input
	 */
	protected String formatEntity(Entity entity, boolean html) {
		if (entity == null)
			return null;
		// get name and standard text
		String name = entity.getDefinition().getDefinition().getDisplayName();
		String text = quoteText(name, html);

		// modify text with filter
		List<ChildContext> path = entity.getDefinition().getPropertyPath();
		// different output than AlignmentUtil in case of property with index condition
		if (path != null && !path.isEmpty() && path.get(path.size() - 1).getIndex() != null) {
			text = path.get(path.size() - 1).getIndex() + ". " + text;
		} else {
			String filterString = AlignmentUtil.getContextText(entity.getDefinition());
			if (filterString != null)
				text += " (matching " + quoteText(filterString, html) + ")";
		}
		return text;
	}

	/**
	 * Checks whether the given entity has an index condition.
	 * 
	 * @param entity the entity to check
	 * @return true, if the entity has an index condition
	 */
	protected boolean hasIndexCondition(Entity entity) {
		List<ChildContext> path = entity.getDefinition().getPropertyPath();
		return path != null && !path.isEmpty() && path.get(path.size() - 1).getIndex() != null;
	}

	/**
	 * Quote or otherwise format (in case of HTML) the given text.
	 * 
	 * @param text the text, may be <code>null</code>
	 * @param html if the format should be HMTL, otherwise the format is just text
	 * @return the quoted text or <code>null</code> in case of <code>null</code> input
	 */
	protected String quoteText(String text, boolean html) {
		if (text == null)
			return null;
		if (html)
			return "<span style=\"font-style: italic;\">" + text + "</span>";
		else
			return "'" + text + "'";
	}
}
