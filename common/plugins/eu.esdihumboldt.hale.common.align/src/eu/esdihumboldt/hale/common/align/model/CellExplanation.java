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

package eu.esdihumboldt.hale.common.align.model;

import java.util.Locale;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Provides a human readable explanation for a cell.
 * 
 * @author Simon Templer
 */
public interface CellExplanation {

	/**
	 * Get the explanation for the given cell.
	 * 
	 * @param cell the cell
	 * @param provider provider for eventual services needed to create the
	 *            explanation, may be <code>null</code>
	 * @return the cell explanation, <code>null</code> if none is available
	 */
	default String getExplanation(Cell cell, ServiceProvider provider) {
		return getExplanation(cell, provider, Locale.getDefault());
	}

	/**
	 * Get the explanation in html format for the given cell
	 * 
	 * @param cell the cell
	 * @param provider provider for eventual services needed to create the
	 *            explanation, my be <code>null</code>
	 * @return the cell explanation in html format, <code>null</code> if none is
	 *         available
	 */
	default String getExplanationAsHtml(Cell cell, ServiceProvider provider) {
		return getExplanationAsHtml(cell, provider, Locale.getDefault());
	}

	/**
	 * Get the explanation for the given cell.
	 * 
	 * @param cell the cell
	 * @param provider provider for eventual services needed to create the
	 *            explanation, may be <code>null</code>
	 * @param locale the locale for the explanation, to be matched if content is
	 *            available
	 * @return the cell explanation, <code>null</code> if none is available
	 */
	public String getExplanation(Cell cell, ServiceProvider provider, Locale locale);

	/**
	 * Get the explanation in html format for the given cell
	 * 
	 * @param cell the cell
	 * @param provider provider for eventual services needed to create the
	 *            explanation, my be <code>null</code>
	 * @param locale the locale for the explanation, to be matched if content is
	 *            available
	 * @return the cell explanation in html format, <code>null</code> if none is
	 *         available
	 */
	public String getExplanationAsHtml(Cell cell, ServiceProvider provider, Locale locale);

	/**
	 * Get the locales supported for explanations.
	 * 
	 * @return the supported locales or <code>null</code> if unknown
	 */
	@Nullable
	public Iterable<Locale> getSupportedLocales();

}
