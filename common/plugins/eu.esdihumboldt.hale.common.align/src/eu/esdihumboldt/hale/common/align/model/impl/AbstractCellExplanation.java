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

package eu.esdihumboldt.hale.common.align.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.common.base.Joiner;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Abstract cell explanation implementation.
 * 
 * @author Simon Templer
 */
public abstract class AbstractCellExplanation implements CellExplanation {

	/**
	 * @see CellExplanation#getExplanation(Cell, ServiceProvider)
	 */
	@Override
	public String getExplanation(Cell cell, ServiceProvider provider) {
		return getExplanation(cell, false);
	}

	/**
	 * @see CellExplanation#getExplanationAsHtml(Cell, ServiceProvider)
	 */
	@Override
	public String getExplanationAsHtml(Cell cell, ServiceProvider provider) {
		return getExplanation(cell, true);
	}

	/**
	 * Get the explanation string in the specified format.
	 * 
	 * @param cell the cell to create an explanation for
	 * @param html if the format should be HMTL, otherwise the format is just
	 *            text
	 * @return the explanation or <code>null</code>
	 */
	protected abstract String getExplanation(Cell cell, boolean html);

	/**
	 * Format an entity for inclusion in an explanation.
	 * 
	 * @param entity the entity, may be <code>null</code>
	 * @param html if the format should be HMTL, otherwise the format is just
	 *            text
	 * @param indexInFront whether index conditions should be in front of the
	 *            property name or behind in brackets
	 * @return the formatted entity name or <code>null</code> in case of
	 *         <code>null</code> input
	 */
	protected String formatEntity(Entity entity, boolean html, boolean indexInFront) {
		if (entity == null)
			return null;
		// get name and standard text
		String name = entity.getDefinition().getDefinition().getDisplayName();
		String text = quoteText(name, html);

		// modify text with filter
		List<ChildContext> path = entity.getDefinition().getPropertyPath();
		// different output than AlignmentUtil in case of property with index
		// condition
		if (path != null && !path.isEmpty() && path.get(path.size() - 1).getIndex() != null) {
			if (indexInFront)
				text = formatNumber(path.get(path.size() - 1).getIndex() + 1) + " value of the "
						+ text;
			else
				text += " (the " + formatNumber(path.get(path.size() - 1).getIndex() + 1)
						+ " value)";
		}
		else {
			String filterString = AlignmentUtil.getContextText(entity.getDefinition());
			if (html) {
				filterString = StringEscapeUtils.escapeHtml(filterString);
			}
			if (filterString != null)
				text += " (matching " + quoteText(filterString, html) + ")";
		}
		return text;
	}

	/**
	 * Returns an entity name without condition strings (e.g. "part1.part2").
	 * 
	 * @param entity the entity
	 * @return the entity name
	 */
	protected String getEntityNameWithoutCondition(Entity entity) {
		EntityDefinition entityDef = entity.getDefinition();
		if (entityDef.getPropertyPath() != null && !entityDef.getPropertyPath().isEmpty()) {
			List<String> names = new ArrayList<String>();
			for (ChildContext context : entityDef.getPropertyPath()) {
				names.add(context.getChild().getName().getLocalPart());
			}
			String longName = Joiner.on('.').join(names);
			return longName;
		}
		else
			return entityDef.getDefinition().getDisplayName();
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
	 * @param html if the format should be HMTL, otherwise the format is just
	 *            text
	 * @return the quoted text or <code>null</code> in case of <code>null</code>
	 *         input
	 */
	protected String quoteText(String text, boolean html) {
		if (text == null)
			return null;
		if (html)
			return "<span style=\"font-style: italic;\">" + text + "</span>";
		else
			return "'" + text + "'";
	}

	private String formatNumber(int number) {
		switch (number) {
		case 1:
			return "first";
		case 2:
			return "second";
		case 3:
			return "third";
		case 4:
			return "fourth";
		case 5:
			return "fifth";
		case 6:
			return "sixth";
		default:
			return (number + 1) + ".";
		}
	}
}
