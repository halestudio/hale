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

package eu.esdihumboldt.cst.functions.string;

import java.text.SimpleDateFormat;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.core.parameter.Validator;

/**
 * Validator for date format strings.
 * 
 * @author Kai Schwierczek
 */
public class DateExtractionFormatValidator implements Validator {

	/**
	 * @see eu.esdihumboldt.hale.common.core.parameter.Validator#validate(java.lang.String)
	 */
	@Override
	public String validate(String value) {
		try {
			new SimpleDateFormat(value);
		} catch (Exception e) {
			return e.getLocalizedMessage();
		}
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.parameter.Validator#setParameters(com.google.common.collect.ListMultimap)
	 */
	@Override
	public void setParameters(ListMultimap<String, String> parameters) {
		// no parameters
	}
}
