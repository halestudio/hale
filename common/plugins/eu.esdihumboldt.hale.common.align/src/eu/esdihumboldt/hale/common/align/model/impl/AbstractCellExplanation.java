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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.annotation.Nullable;

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

	@Override
	public String getExplanation(Cell cell, ServiceProvider provider, Locale locale) {
		return getExplanation(cell, false, provider, locale);
	}

	@Override
	public String getExplanationAsHtml(Cell cell, ServiceProvider provider, Locale locale) {
		return getExplanation(cell, true, provider, locale);
	}

	/**
	 * Get the explanation string in the specified format.
	 * 
	 * @param cell the cell to create an explanation for
	 * @param html if the format should be HMTL, otherwise the format is just
	 *            text
	 * @param provider the service provider, if available
	 * @param locale the locale for the explanation, to be matched if content is
	 *            available
	 * @return the explanation or <code>null</code>
	 */
	protected abstract String getExplanation(Cell cell, boolean html,
			@Nullable ServiceProvider provider, Locale locale);

	/**
	 * Format an entity for inclusion in an explanation.
	 * 
	 * @param entity the entity, may be <code>null</code>
	 * @param html if the format should be HMTL, otherwise the format is just
	 *            text
	 * @param indexInFront whether index conditions should be in front of the
	 *            property name or behind in brackets
	 * @param locale the locale for the explanation, to be matched if content is
	 *            available
	 * @return the formatted entity name or <code>null</code> in case of
	 *         <code>null</code> input
	 */
	protected String formatEntity(Entity entity, boolean html, boolean indexInFront,
			Locale locale) {
		if (entity == null)
			return null;

		return formatEntity(entity.getDefinition(), html, indexInFront, locale);
	}

	/**
	 * Format an entity for inclusion in an explanation.
	 * 
	 * @param entityDef the entity definition, may be <code>null</code>
	 * @param html if the format should be HMTL, otherwise the format is just
	 *            text
	 * @param indexInFront whether index conditions should be in front of the
	 *            property name or behind in brackets
	 * @param locale the locale for the explanation, to be matched if content is
	 *            available
	 * @return the formatted entity name or <code>null</code> in case of
	 *         <code>null</code> input
	 */
	protected String formatEntity(EntityDefinition entityDef, boolean html, boolean indexInFront,
			Locale locale) {
		if (entityDef == null)
			return null;
		// get name and standard text
		String name = entityDef.getDefinition().getDisplayName();
		String text = quoteName(name, html);

		// modify text with filter
		List<ChildContext> path = entityDef.getPropertyPath();
		// different output than AlignmentUtil in case of property with index
		// condition
		if (path != null && !path.isEmpty() && path.get(path.size() - 1).getIndex() != null) {
			if (indexInFront) {
				text = MessageFormat.format(getBaseMessage("index_pre", locale),
						formatNumber(path.get(path.size() - 1).getIndex() + 1, locale), text);
			}
			else {
				text += " " + MessageFormat.format(getBaseMessage("index_post", locale),
						formatNumber(path.get(path.size() - 1).getIndex() + 1, locale));
			}
		}
		else {
			String filterString = AlignmentUtil.getContextText(entityDef);
			if (html) {
				filterString = StringEscapeUtils.escapeHtml(filterString);
			}
			if (filterString != null)
				text += " " + MessageFormat.format(getBaseMessage("filter", locale),
						quoteText(filterString, html));
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

	/**
	 * Quote or otherwise format (in case of HTML) the given value.
	 * 
	 * @param value the value to quote, may be <code>null</code>
	 * @param html if the format should be HMTL, otherwise the format is just
	 *            text
	 * @return the quoted text or <code>null</code> in case of <code>null</code>
	 *         input
	 */
	protected String quoteValue(Object value, boolean html) {
		if (value == null)
			return null;
		if (html)
			return "<code>" + value + "</code>";
		else
			return "`" + value + "`";
	}

	/**
	 * Quote or otherwise format (in case of HTML) the given name (e.g. an
	 * entity or parameter name).
	 * 
	 * @param name the name to quote, may be <code>null</code>
	 * @param html if the format should be HMTL, otherwise the format is just
	 *            text
	 * @return the quoted text or <code>null</code> in case of <code>null</code>
	 *         input
	 */
	protected String quoteName(String name, boolean html) {
		if (name == null)
			return null;
		if (html)
			return "<em>" + name + "</em>";
		else
			return "'" + name + "'";
	}

	private String formatNumber(int number, Locale locale) {
		switch (number) {
		case 1:
			return getBaseMessage("first", locale);
		case 2:
			return getBaseMessage("second", locale);
		case 3:
			return getBaseMessage("third", locale);
		case 4:
			return getBaseMessage("fourth", locale);
		case 5:
			return getBaseMessage("fifth", locale);
		case 6:
			return getBaseMessage("sixth", locale);
		default:
			return (number + 1) + ".";
		}
	}

	/**
	 * Get a message for a specific locale.
	 * 
	 * @param key the message key
	 * @param locale the locale
	 * @return the message string
	 */
	protected String getMessage(String key, Locale locale) {
		return getMessage(key, locale, getDefaultMessageClass());
	}

	/**
	 * Get a message for a specific locale.
	 * 
	 * @param key the message key
	 * @param locale the locale
	 * @param messageClass the class the messages to retrieve are associated to
	 * @return the message string
	 */
	protected String getMessage(String key, Locale locale, Class<?> messageClass) {
		return ResourceBundle
				.getBundle(messageClass.getName(), locale, messageClass.getClassLoader(),
						ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES))
				.getString(key);
	}

	/**
	 * Get the class used to retrieve messages via
	 * {@link #getMessage(String, Locale)}
	 * 
	 * @return the default message class
	 */
	protected Class<?> getDefaultMessageClass() {
		return getClass();
	}

	/**
	 * Get a message for a specific locale that is stored with the
	 * {@link AbstractCellExplanation} explanation base class.
	 * 
	 * @param key the message key
	 * @param locale the locale
	 * @return the message string
	 */
	private String getBaseMessage(String key, Locale locale) {
		return ResourceBundle
				.getBundle(AbstractCellExplanation.class.getName(), locale,
						AbstractCellExplanation.class.getClassLoader(),
						ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES))
				.getString(key);
	}

	/**
	 * Create a string enumerating the given items.
	 * 
	 * @param items the collection of items
	 * @param locale the locale
	 * @return the joined string
	 */
	protected String enumerateJoin(List<String> items, Locale locale) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < items.size(); i++) {
			result.append(items.get(i));

			if (i == items.size() - 2) {
				result.append(' ');
				result.append(getBaseMessage("and", locale));
				result.append(' ');
			}
			else if (i < items.size() - 2) {
				result.append(", ");
			}
		}
		return result.toString();
	}

	/**
	 * Build a replacement table (HTML only).
	 * 
	 * @param varToProperty variable expressions mapped to the entities that
	 *            replace them
	 * @param locale the locale
	 * @return the replacement table as string
	 */
	protected String buildReplacementTable(Map<String, String> varToProperty, Locale locale) {
		StringBuilder sb = new StringBuilder();
		sb.append("<br /><br />");
		sb.append(getBaseMessage("rt_intro", locale));
		sb.append("<br />");
		sb.append("<table border=\"1\"><tr><th>");
		sb.append(getBaseMessage("rt_variable", locale));
		sb.append("</th><th>");
		sb.append(getBaseMessage("rt_property", locale));
		sb.append("</th></tr>");
		for (Entry<String, String> entry : varToProperty.entrySet()) {
			sb.append(String.format("<tr><td>%s</td><td>%s</td></tr>", entry.getKey(),
					entry.getValue()));
		}
		sb.append("</table>");
		return sb.toString();
	}

}
