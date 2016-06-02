/*
 * Copyright (c) 2016 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.model.impl;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Base class for explanations using individual localized message strings.
 * 
 * @author Simon Templer
 */
public abstract class AbstractResourceBundleCellExplanation extends AbstractCellExplanation {

	/**
	 * Get a message for a specific locale.
	 * 
	 * @param key the message key
	 * @param locale the locale
	 * @return the message string
	 */
	protected String getMessage(String key, Locale locale) {
		return ResourceBundle
				.getBundle(getClass().getName(), locale, getClass().getClassLoader(),
						ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES))
				.getString(key);
	}

}
